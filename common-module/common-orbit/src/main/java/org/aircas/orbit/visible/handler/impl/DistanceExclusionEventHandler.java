package org.aircas.orbit.visible.handler.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.DistanceExclusionDetector;
import org.aircas.orbit.visible.handler.AbstractEventDetectorHandler;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;

/**
 * 距离排除事件处理器
 *
 * <p>
 * 该类用于计算卫星与目标之间的距离约束。当卫星与目标之间的距离在指定范围内时，
 * 才认为可以进行有效观测。这对于确保观测质量和仪器性能非常重要。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星与目标之间的距离</li>
 * <li>验证距离是否在有效观测范围内</li>
 * <li>生成满足距离约束的可观测时间区间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学成像任务</li>
 * <li>雷达观测任务</li>
 * <li>近距离交会操作</li>
 * </ul>
 * </p>
 */
@Slf4j
public class DistanceExclusionEventHandler extends AbstractEventDetectorHandler {

    private final        double minDistance;    // 最小观测距离（米）
    private final        double maxDistance;    // 最大观测距离（米）
    private static final double DEFAULT_MIN_WINDOW_DURATION = 60.0; // 默认最短窗口时长（秒）

    /**
     * 构造函数
     *
     * @param minDistance 最小观测距离（米）
     * @param maxDistance 最大观测距离（米）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     */
    public DistanceExclusionEventHandler(int maxIter, double maxCheck, double threshold, double minDistance, double maxDistance, double maxWindowDuration) {
        super(maxCheck, threshold, maxIter, maxWindowDuration);

        if (minDistance <= 0) {
            throw new IllegalArgumentException("最小观测距离必须大于0米");
        }
        if (maxDistance <= minDistance) {
            throw new IllegalArgumentException("最大观测距离必须大于最小观测距离");
        }

        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public String getName() {
        return "距离排除约束";
    }

    @Override
    public String getExclusionInfo() {
        return String.format("观测距离范围: %.2f - %.2f千米,最短窗口时长: %.2f秒", minDistance / 1000.0, maxDistance / 1000.0, minWindowDuration);
    }

    /**
     * 创建距离排除事件检测器
     *
     * @param targetPropagator 目标传播器
     * @param timeIntervals 时间区间列表
     * @return 事件检测器
     */
    @Override
    protected EventDetector createDetector(Propagator targetPropagator, List<TimeInterval> timeIntervals) {
        return new DistanceExclusionDetector(targetPropagator, minDistance, maxDistance, maxIter, AdaptableInterval.of(maxCheck), threshold, createDefaultHandler(timeIntervals));
    }

    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        // 先调用父类的计算方法
        List<TimeInterval> calculatedIntervals = super.calculate(satellitePropagator, targetPropagator, intervals);

        // 过滤无效的时间窗口
        return filterShortWindows(calculatedIntervals);
    }

}