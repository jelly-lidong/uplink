package org.aircas.orbit.visible.handler.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.LunarExclusionDetector;
import org.aircas.orbit.visible.handler.AbstractEventDetectorHandler;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;

/**
 * 月球排除事件处理器
 *
 * <p>
 * 该类用于检测卫星观测目标时是否受到月球干扰。当卫星-目标连线与卫星-月球连线之间的夹角
 * 大于设定阈值时，表示可以对目标进行观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星观测目标时的月球干扰</li>
 * <li>计算卫星-目标-月球三者之间的角度关系</li>
 * <li>生成目标可观测的时间区间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学成像任务规划</li>
 * <li>空间目标监视跟踪</li>
 * <li>月球干扰避让</li>
 * </ul>
 * </p>
 */
@Slf4j
public class LunarExclusionEventHandler extends AbstractEventDetectorHandler {

    private final        double avoidanceAngle;  // 月球避让角（度）
    private static final double DEFAULT_MIN_WINDOW_DURATION = 60.0; // 默认最短窗口时长（秒）

    /**
     * 构造函数
     *
     * @param avoidanceAngle 月球避让角（度）
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     * @param maxIter 最大迭代次数
     */
    public LunarExclusionEventHandler(
        int maxIter,
        double maxCheck,
        double threshold,
        double avoidanceAngle,
        double minWindowDuration
    ) {
        super(maxCheck, threshold, maxIter, minWindowDuration);

        if (avoidanceAngle <= 0 || avoidanceAngle >= 180) {
            throw new IllegalArgumentException("月球避让角必须在(0, 180)度范围内");
        }

        this.avoidanceAngle = avoidanceAngle;
    }

    @Override
    public String getName() {
        return "最小月光遮蔽角约束";
    }

    @Override
    public String getExclusionInfo() {
        return String.format("月球避让角: %.2f度", avoidanceAngle);
    }

    /**
     * 创建月球排除事件检测器
     *
     * @param targetPropagator 目标传播器
     * @param timeIntervals 时间区间列表
     * @return 事件检测器
     */
    @Override
    protected EventDetector createDetector(Propagator targetPropagator,
        List<TimeInterval> timeIntervals) {
        return new LunarExclusionDetector(
            targetPropagator,
            avoidanceAngle,
            maxIter,
            AdaptableInterval.of(maxCheck),
            threshold,
            createDefaultHandler(timeIntervals)
        );
    }


    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> intervals) {
        // 先调用父类的计算方法
        List<TimeInterval> calculatedIntervals = super.calculate(
            satellitePropagator, targetPropagator, intervals);

        // 过滤无效的时间窗口
        return filterShortWindows(calculatedIntervals);
    }

}