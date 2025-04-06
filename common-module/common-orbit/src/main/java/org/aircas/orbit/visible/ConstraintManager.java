package org.aircas.orbit.visible;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.orekit.propagation.Propagator;
import org.orekit.time.AbsoluteDate;

/**
 * 约束管理器
 *
 * <p>
 * 该类用于管理和执行一系列约束条件,用于筛选有效的观测时间窗口。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 *   <li>管理多个约束条件</li>
 *   <li>支持串行和并行执行约束检查</li>
 *   <li>提供进度回调机制</li>
 *   <li>对时间窗口进行过滤和合并</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 *   <li>空间目标观测任务规划</li>
 *   <li>多约束条件下的时间窗口计算</li>
 *   <li>卫星可见性分析</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 *   <li>使用CopyOnWriteArrayList保证线程安全</li>
 *   <li>支持链式调用API</li>
 *   <li>可配置并行执行以提高性能</li>
 *   <li>提供阈值控制以优化性能</li>
 * </ul>
 * </p>
 */

@Slf4j
@Data
public class ConstraintManager {

    /** 约束条件列表 */
    private final List<EventDetectorHandler> constraints;

    /** 是否启用并行执行 */
    private boolean parallelExecution;

    /** 时间窗口数量阈值,超过此值时考虑使用并行处理 */
    private static final int INTERVAL_THRESHOLD = 1000;

    /** 约束条件数量阈值,超过此值时考虑使用并行处理 */
    private static final int CONSTRAINT_THRESHOLD = 3;

    /** 并行处理时的分组大小 */
    private static final int GROUP_SIZE = 2;

    /** 停止计算的标志 */
    private volatile boolean stopRequested = false;

    public interface ProgressCallback {

        void onStart(int totalConstraints);

        void onConstraintComplete(String constraintName, int current, int total, List<TimeInterval> results);

        void onComplete(List<TimeInterval> finalResults);
    }

    public ConstraintManager() {
        this.constraints       = new CopyOnWriteArrayList<>();
        this.parallelExecution = false;
        log.info("初始化约束管理器");
    }

    public ConstraintManager addConstraint(EventDetectorHandler constraint) {
        log.info("添加第{}个约束条件: {}, 约束信息: {}", constraints.size() + 1, constraint.getName(), constraint.getExclusionInfo());
        constraints.add(constraint);
        return this;
    }

    public ConstraintManager removeConstraint(EventDetectorHandler constraint) {
        constraints.remove(constraint);
        log.info("移除约束条件: {}, 当前约束条件数量: {}", constraint.getName(), constraints.size());
        return this;
    }

    public ConstraintManager clearConstraints() {
        constraints.clear();
        log.info("清空所有约束条件");
        return this;
    }


    /**
     * 请求停止约束计算
     */
    public void requestStop() {
        log.info("收到停止约束计算请求");
        this.stopRequested = true;
    }

    /**
     * 重置停止标志
     */
    public void resetStopFlag() {
        log.info("重置停止标志");
        this.stopRequested = false;
    }

    /**
     * 检查是否应该停止计算
     */
    private boolean shouldStop() {
        if (stopRequested) {
            log.info("检测到停止请求，终止约束计算");
            return true;
        }
        return false;
    }

    public ConstraintManager setParallel(boolean parallel) {
        this.parallelExecution = parallel;
        log.info("设置并行执行模式: {}", parallel);
        return this;
    }

    public List<TimeInterval> executeConstraints(
        Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> initialIntervals) {
        return executeConstraints(satellitePropagator, targetPropagator, initialIntervals, null);
    }

    public List<TimeInterval> executeConstraints(
        Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> initialIntervals,
        ProgressCallback callback) {
        TimeInterval timeInterval = initialIntervals.get(0);
        log.info("开始执行约束条件检查, 初始时间区间: {} 至 {}, 约束条件数量: {}", timeInterval.getStartDate(), timeInterval.getEndDate(), constraints.size());

        if (callback != null) {
            callback.onStart(constraints.size());
        }

        if (shouldUseParallel(initialIntervals)) {
            log.info("使用并行模式执行约束条件检查");
            return executeParallel(satellitePropagator, targetPropagator, initialIntervals, callback);
        } else {
            log.info("使用串行模式执行约束条件检查");
            return executeSerial(satellitePropagator, targetPropagator, initialIntervals, callback);
        }
    }

    /**
     * 判断是否应该使用并行执行
     *
     * <p>
     * 根据以下条件判断是否使用并行执行:
     * <ul>
     *   <li>parallelExecution标志为true</li>
     *   <li>时间区间数量超过阈值INTERVAL_THRESHOLD</li>
     *   <li>约束条件数量超过阈值CONSTRAINT_THRESHOLD</li>
     *   <li>系统可用处理器核心数大于1</li>
     * </ul>
     * </p>
     *
     * @param intervals 待处理的时间区间列表
     * @return 是否应该使用并行执行
     */
    private boolean shouldUseParallel(List<TimeInterval> intervals) {
        boolean result = parallelExecution
            && intervals.size() > INTERVAL_THRESHOLD
            && constraints.size() > CONSTRAINT_THRESHOLD
            && Runtime.getRuntime().availableProcessors() > 1;

        log.info("并行执行条件检查 - 并行标志: {}, 区间数量: {}, 约束数量: {}, CPU核心数: {}, 最终结果: {}",
            parallelExecution, intervals.size(), constraints.size(),
            Runtime.getRuntime().availableProcessors(), result);

        return result;
    }

    /**
     * 串行执行约束检查
     *
     * <p>
     * 该方法按顺序依次执行每个约束条件的检查。
     * 主要步骤:
     * <ul>
     *   <li>依次遍历每个约束条件</li>
     *   <li>使用当前约束过滤时间区间</li>
     *   <li>更新剩余的有效时间区间</li>
     * </ul>
     * </p>
     *
     * <p>
     * 执行流程:
     * <ul>
     *   <li>创建时间区间副本作为工作集</li>
     *   <li>顺序执行每个约束的计算</li>
     *   <li>通过回调报告执行进度</li>
     *   <li>如果某步骤后无剩余区间则提前终止</li>
     * </ul>
     * </p>
     *
     * @param satellitePropagator 卫星轨道传播器
     * @param targetPropagator 目标轨道传播器
     * @param initialIntervals 初始时间区间列表
     * @param callback 进度回调接口
     * @return 满足所有约束的时间区间列表
     */
    private List<TimeInterval> executeSerial(
        Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> initialIntervals,
        ProgressCallback callback) {

        log.info("开始串行执行约束检查");
        // 创建输入列表的副本
        List<TimeInterval> currentIntervals = new ArrayList<>(initialIntervals);
        int                total            = constraints.size();

        for (int i = 0; i < constraints.size(); i++) {
            // 检查是否应该停止
            if (shouldStop()) {
                log.warn("约束计算被手动停止，在第 {}/{} 个约束处终止", i + 1, total);
                if (callback != null) {
                    callback.onComplete(currentIntervals);
                }
                return currentIntervals;
            }

            EventDetectorHandler constraint = constraints.get(i);
            log.info("执行约束条件 --> {}", constraint.getName());

            // 每次迭代都使用新的列表存储结果
            List<TimeInterval> result = constraint.calculate(
                satellitePropagator,
                targetPropagator,
                new ArrayList<>(currentIntervals)  // 传入当前区间的副本
            );

            // 更新当前区间
            currentIntervals = new ArrayList<>(result);

            if (callback != null) {
                callback.onConstraintComplete(constraint.getName(), i + 1, total, currentIntervals);
            }

            if (currentIntervals.isEmpty()) {
                log.warn("约束条件[{}]执行后无剩余时间区间, 提前终止检查", constraint.getName());
                break;
            }
        }

        if (callback != null) {
            callback.onComplete(currentIntervals);
        }

        log.info("串行执行约束检查完成, 最终时间区间数量: {}", currentIntervals.size());
        return currentIntervals;
    }

    /**
     * 并行执行约束检查
     * <p>
     * 该方法将约束分组并行执行,主要步骤如下:
     * <ul>
     *   <li>将约束条件分组</li>
     *   <li>对每组约束并行执行计算</li>
     *   <li>计算每组结果的交集</li>
     *   <li>通过回调报告执行进度</li>
     * </ul>
     * </p>
     *
     * @param satellitePropagator 卫星轨道传播器
     * @param targetPropagator 目标轨道传播器
     * @param initialIntervals 初始时间区间列表
     * @param callback 进度回调接口
     * @return 满足所有约束的时间区间列表
     */
    private List<TimeInterval> executeParallel(
        Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> initialIntervals,
        ProgressCallback callback) {

        log.info("开始并行执行约束检查");
        // 将约束分组
        List<List<EventDetectorHandler>> groups = groupConstraints();
        log.info("约束条件分组完成, 共 {} 组", groups.size());

        // 创建初始区间的副本作为工作集
        List<TimeInterval> currentIntervals = new ArrayList<>(initialIntervals);
        // 记录已处理的约束数量
        int processedConstraints = 0;

        // 遍历每个约束组
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            // 检查是否应该停止
            if (shouldStop()) {
                log.warn("约束计算被手动停止，在第 {} 组约束处终止", groupIndex + 1);
                if (callback != null) {
                    callback.onComplete(currentIntervals);
                }
                return currentIntervals;
            }

            List<EventDetectorHandler> group = groups.get(groupIndex);
            log.info("开始处理第 {} 组约束条件, 组内约束数量: {}", groupIndex + 1, group.size());

            // 创建final变量供lambda表达式使用
            List<TimeInterval> finalCurrentIntervals = currentIntervals;
            // 并行执行当前组的所有约束
            List<List<TimeInterval>> groupResults = group.parallelStream()
                .map(constraint -> {
                    log.info("并行执行约束条件: {}", constraint.getName());
                    List<TimeInterval> result = constraint.calculate(
                        satellitePropagator,
                        targetPropagator,
                        finalCurrentIntervals
                    );
                    return result;
                })
                .collect(Collectors.toList());

            // 计算当前组所有结果的交集
            currentIntervals = findIntersection(groupResults);
            log.info("第 {} 组约束条件处理完成, 交集计算后剩余时间区间数量: {}", groupIndex + 1, currentIntervals.size());

            // 如果设置了回调,报告进度
            if (callback != null) {
                processedConstraints += group.size();
                for (EventDetectorHandler constraint : group) {
                    callback.onConstraintComplete(
                        constraint.getName(),
                        processedConstraints,
                        constraints.size(),
                        currentIntervals
                    );
                }
            }

            // 如果没有剩余区间则提前终止
            if (currentIntervals.isEmpty()) {
                log.warn("第 {} 组约束条件处理后无剩余时间区间, 提前终止检查", groupIndex + 1);
                break;
            }
        }

        // 执行完成回调
        if (callback != null) {
            callback.onComplete(currentIntervals);
        }

        log.info("并行执行约束检查完成, 最终时间区间数量: {}", currentIntervals.size());
        return currentIntervals;
    }

    /**
     * 将约束条件分组
     * 根据预设的GROUP_SIZE大小将约束条件分成多个组
     * 每组最多包含GROUP_SIZE个约束条件
     * 最后一组可能包含少于GROUP_SIZE个约束条件
     *
     * @return 分组后的约束条件列表, 每个子列表代表一个组
     */
    private List<List<EventDetectorHandler>> groupConstraints() {
        log.info("开始对约束条件进行分组, 组大小: {}", GROUP_SIZE);
        // 存储所有分组的列表
        List<List<EventDetectorHandler>> groups = new ArrayList<>();
        // 当前正在处理的分组
        List<EventDetectorHandler> currentGroup = new ArrayList<>();

        // 遍历所有约束条件进行分组
        for (EventDetectorHandler constraint : constraints) {
            // 将约束添加到当前组
            currentGroup.add(constraint);

            // 当前组达到预设大小时,将其添加到groups并清空当前组
            if (currentGroup.size() >= GROUP_SIZE) {
                groups.add(new ArrayList<>(currentGroup));
                currentGroup.clear();
            }
        }

        // 处理最后一个未满的分组
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }

        log.info("约束条件分组完成, 共分为 {} 组", groups.size());
        return groups;
    }


    /**
     * 计算多个时间区间列表的交集
     *
     * @param intervalLists 多个时间区间列表的集合
     * @return 所有时间区间列表的交集
     */
    private List<TimeInterval> findIntersection(List<List<TimeInterval>> intervalLists) {
        // 如果输入列表为空，返回空列表
        if (intervalLists.isEmpty()) {
            log.info("输入的时间区间列表为空");
            return new ArrayList<>();
        }

        log.info("开始计算 {} 个时间区间列表的交集", intervalLists.size());
        // 初始化结果为第一个时间区间列表的副本
        List<TimeInterval> result = new ArrayList<>(intervalLists.get(0));

        // 依次与后续的时间区间列表计算交集
        for (int i = 1; i < intervalLists.size(); i++) {
            // 计算当前结果与下一个列表的交集
            result = computeIntersection(result, intervalLists.get(i));
            log.info("第 {} 次交集计算完成, 剩余时间区间数量: {}", i, result.size());

            // 如果交集为空，提前结束循环
            if (result.isEmpty()) {
                log.info("交集为空, 提前结束计算");
                break;
            }
        }

        return result;
    }

    /**
     * 计算两个时间区间列表的交集
     *
     * @param intervals1 第一个时间区间列表
     * @param intervals2 第二个时间区间列表
     * @return 两个列表的交集
     */
    private List<TimeInterval> computeIntersection(List<TimeInterval> intervals1, List<TimeInterval> intervals2) {
        log.info("计算两个时间区间列表的交集, 列表1大小: {}, 列表2大小: {}", intervals1.size(), intervals2.size());
        List<TimeInterval> result = intervals1.stream()
            .flatMap(interval1 -> intervals2.stream()
                .map(interval2 -> {
                    AbsoluteDate startDate = interval1.getStartDate().isAfter(interval2.getStartDate()) ?
                        interval1.getStartDate() : interval2.getStartDate();
                    AbsoluteDate endDate = interval1.getEndDate().isBefore(interval2.getEndDate()) ?
                        interval1.getEndDate() : interval2.getEndDate();

                    if (startDate.isBefore(endDate)) {
                        TimeInterval intersection = new TimeInterval();
                        intersection.setStartDate(startDate);
                        intersection.setEndDate(endDate);
                        return intersection;
                    }
                    return null;
                }))
            .filter(interval -> interval != null)
            .collect(Collectors.toList());

        log.info("交集计算完成, 结果时间区间数量: {}", result.size());
        return result;
    }
}