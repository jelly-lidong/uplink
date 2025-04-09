package org.aircas.orbit.visible;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.orekit.propagation.Propagator;

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

    /**
     * 约束条件列表
     */
    private Node<EventDetectorHandler> constraintNode;

    /**
     * 是否启用并行执行
     */
    private boolean parallelExecution;

    /**
     * 时间窗口数量阈值,超过此值时考虑使用并行处理
     */
    private static final int INTERVAL_THRESHOLD = 1000;

    /**
     * 约束条件数量阈值,超过此值时考虑使用并行处理
     */
    private static final int CONSTRAINT_THRESHOLD = 3;

    /**
     * 并行处理时的分组大小
     */
    private static final int GROUP_SIZE = 2;

    /**
     * 停止计算的标志
     */
    private volatile boolean stopRequested = false;

    private static class Node<E> {

        E                         item;
        ConstraintManager.Node<E> next;

        Node(E element, ConstraintManager.Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }

    public interface ProgressCallback {

        void onStart(int totalConstraints);

        void onConstraintComplete(String constraintName, int current, int total, List<TimeWindow> results);

        void onFinished();

        void onWindow(TimeWindow window);
    }

    public ConstraintManager() {
        this.parallelExecution = false;
        log.info("初始化约束管理器");
    }

    public ConstraintManager addConstraint(EventDetectorHandler constraint) {
        if (this.constraintNode == null) {
            this.constraintNode = new Node<>(constraint, null);
        } else {
            Node<EventDetectorHandler> current = this.constraintNode;
            while (current.next != null) {
                current = current.next;
            }
            current.next = new Node<>(constraint, null);
        }
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


    public void executeConstraints(Propagator satellitePropagator, Propagator targetPropagator, TimeWindow timeWindow, ProgressCallback callback) {
        executeSerial(constraintNode, satellitePropagator, targetPropagator, timeWindow, callback);
//        result.sort(Comparator.comparing(TimeWindow::getStartDate));
//        callback.onComplete(mergeConsecutiveWindows(result));
    }

    /**
     * 执行约束条件
     *
     * @param satellitePropagator 卫星轨道传播器
     * @param targetPropagator    目标轨道传播器
     * @param timeWindow          初始时间区间列表
     * @param progressCallback    进度回调接口
     * @return 满足所有约束的时间区间列表
     */
    private void executeSerial(Node<EventDetectorHandler> constraintNode, Propagator satellitePropagator, Propagator targetPropagator, TimeWindow timeWindow, ProgressCallback progressCallback
    ) {
//        log.info("开始执行约束计算{}", constraintNode.item.getName());
        constraintNode.item.calculate(satellitePropagator, targetPropagator, timeWindow, timeInterval -> {
//                    log.info("{} 约束条件计算得到一个窗口: {} - {}", constraintNode.item.getName(), timeInterval.getStartDate(), timeInterval.getEndDate());
                if (constraintNode.next != null) {
                    // 递归调用下一个约束条件
                    executeSerial(constraintNode.next, satellitePropagator, targetPropagator, timeInterval, progressCallback);
                } else {
                    // 如果没有下一个约束条件,则将结果添加到窗口列表中
//                        log.info("{} 约束条件计算得到一个窗口: {} - {}", constraintNode.item.getName(), timeInterval.getStartDate(), timeInterval.getEndDate());
                    progressCallback.onWindow(timeInterval);
                }
            }  // 传入当前区间的副本
        );
    }

    /**
     * 合并连续的时间窗口
     * 如果两个窗口的结束时间和开始时间相同，则将它们合并为一个窗口
     *
     * @param windows 待合并的时间窗口列表
     * @return 合并后的时间窗口列表
     */
    private List<TimeWindow> mergeConsecutiveWindows(List<TimeWindow> windows) {
        if (windows.size() < 2) {
            return windows;
        }

        List<TimeWindow> mergedWindows = new ArrayList<>();
        TimeWindow       currentWindow = windows.get(0);

        for (int i = 1; i < windows.size(); i++) {
            TimeWindow nextWindow = windows.get(i);
            // 检查当前窗口的结束时间是否等于下一个窗口的开始时间
            if (currentWindow.getEndDate().equals(nextWindow.getStartDate())) {
                // 合并窗口，更新当前窗口的结束时间
                currentWindow.setEndDate(nextWindow.getEndDate());
            } else {
                // 如果不连续，保存当前窗口并开始新的窗口
                mergedWindows.add(currentWindow);
                currentWindow = nextWindow;
            }
        }
        // 添加最后一个窗口
        mergedWindows.add(currentWindow);

        return mergedWindows;
    }

}