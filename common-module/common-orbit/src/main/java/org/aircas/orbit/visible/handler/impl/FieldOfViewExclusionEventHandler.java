package org.aircas.orbit.visible.handler.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.FieldOfViewExclusionDetector;
import org.aircas.orbit.visible.handler.AbstractEventDetectorHandler;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;

/**
 * 视场角排除事件处理器
 *
 * <p>
 * 该类用于计算目标在卫星的视场内的可观测时间区间。当目标在卫星视场角范围内时，
 * 表示可以对目标进行观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测目标是否在卫星视场内</li>
 * <li>考虑卫星姿态的影响</li>
 * <li>生成目标可观测的时间区间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学成像任务规划</li>
 * <li>空间目标监视跟踪</li>
 * <li>卫星对地观测</li>
 * </ul>
 * </p>
 */
@Slf4j
public class FieldOfViewExclusionEventHandler extends AbstractEventDetectorHandler {

    private final        double   fieldOfViewAngle;     // 视场角（度）
    private final        Vector3D boresightDirection; // 视场中心轴方向
    private static final double   DEFAULT_MIN_WINDOW_DURATION = 30.0; // 默认最短窗口时长（秒）

    /**
     * 构造函数
     *
     * @param fieldOfViewAngle 视场角（度）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     */
    public FieldOfViewExclusionEventHandler(double fieldOfViewAngle, double minWindowDuration, int maxIter, double maxCheck, double threshold) {
        this(fieldOfViewAngle, minWindowDuration, Vector3D.PLUS_K, maxIter, maxCheck, threshold);
    }

    /**
     * 构造函数
     *
     * @param fieldOfViewAngle 视场角（度）
     * @param boresightDirection 视场中心轴方向
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     */
    public FieldOfViewExclusionEventHandler(double fieldOfViewAngle, double minWindowDuration, Vector3D boresightDirection, int maxIter, double maxCheck, double threshold) {
        super(maxCheck, threshold, maxIter, minWindowDuration);

        if (fieldOfViewAngle <= 0 || fieldOfViewAngle >= 180) {
            throw new IllegalArgumentException("视场角必须在(0, 180)度范围内");
        }

        this.fieldOfViewAngle   = fieldOfViewAngle;
        this.boresightDirection = boresightDirection.normalize();
    }

    @Override
    public String getName() {
        return "视场角排除约束";
    }

    @Override
    public String getExclusionInfo() {
        return String.format("视场角: %.2f度", fieldOfViewAngle);
    }

    /**
     * 创建视场角排除事件检测器
     *
     * @param targetPropagator 目标传播器
     * @param timeIntervals 时间区间列表
     * @return 事件检测器
     */
    @Override
    protected EventDetector createDetector(Propagator targetPropagator, List<TimeInterval> timeIntervals) {
        return new FieldOfViewExclusionDetector(targetPropagator, fieldOfViewAngle, boresightDirection, maxIter, AdaptableInterval.of(maxCheck), threshold, createDefaultHandler(timeIntervals));
    }


    /**
     * 验证时间窗口是否有效
     *
     * @param interval 时间区间
     * @return 是否有效
     */
    public boolean isWindowValid(TimeInterval interval) {
        if (interval == null || interval.getStartDate() == null || interval.getEndDate() == null) {
            return false;
        }

        // 检查时间窗口持续时间
        double duration = interval.getEndDate().durationFrom(interval.getStartDate());
        return duration >= DEFAULT_MIN_WINDOW_DURATION;
    }

    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        // 先调用父类的计算方法
        List<TimeInterval> calculatedIntervals = super.calculate(satellitePropagator, targetPropagator, intervals);

        // 过滤无效的时间窗口
        return filterShortWindows(calculatedIntervals);
    }

}