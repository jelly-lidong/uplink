package org.aircas.orbit.visible.handler.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.EarthAtmosphereExclusionDetector;
import org.aircas.orbit.visible.handler.AbstractEventDetectorHandler;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * 地球大气层排除事件处理器
 *
 * <p>
 * 该类用于计算卫星观测目标时，光线是否穿过地球大气层的情况。当光线需要穿过
 * 大气层时，由于大气折射和散射的影响，会降低观测质量。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测光线是否穿过大气层</li>
 * <li>考虑大气层高度的影响</li>
 * <li>生成不受大气影响的可观测时间区间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学遥感任务规划</li>
 * <li>天文观测任务</li>
 * <li>空间目标监视</li>
 * </ul>
 * </p>
 */
@Slf4j
public class EarthAtmosphereExclusionEventHandler extends AbstractEventDetectorHandler {

    private final        double           minAtmosphereAngle;  // 大气层高度（米）
    private final        OneAxisEllipsoid earth;     // 地球模型
    private static final double           DEFAULT_ATMOSPHERE_ALTITUDE = 100000.0; // 默认大气层高度100km
    private static final double           MIN_ELEVATION_ANGLE         = 5.0;  // 最小仰角（度）
    private static final double           DEFAULT_MIN_WINDOW_DURATION = 60.0; // 默认最短窗口时长（秒）


    /**
     * 构造函数
     *
     * @param minAtmosphereAngle 大气层高度（米）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     */
    public EarthAtmosphereExclusionEventHandler(int maxIter, double maxCheck, double threshold, double minAtmosphereAngle, double minWindowDuration) {
        super(maxCheck, threshold, maxIter, minWindowDuration);

        if (minAtmosphereAngle < 0) {
            throw new IllegalArgumentException("大气层高度必须大于或等于0米");
        }

        this.minAtmosphereAngle = minAtmosphereAngle;

        // 初始化地球模型
        Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        this.earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING, earthFrame);
    }

    @Override
    public String getName() {
        return "地球大气层排除约束";
    }

    @Override
    public String getExclusionInfo() {
        return String.format("大气层高度: %.2f千米", minAtmosphereAngle / 1000.0);
    }

    /**
     * 创建地球大气层排除事件检测器
     *
     * @param targetPropagator 目标传播器
     * @param timeIntervals 时间区间列表
     * @return 事件检测器
     */
    @Override
    protected EventDetector createDetector(Propagator targetPropagator, List<TimeInterval> timeIntervals) {

        return new EarthAtmosphereExclusionDetector(targetPropagator, minAtmosphereAngle, maxIter, AdaptableInterval.of(maxCheck), threshold, createDefaultHandler(timeIntervals), earth);
    }


    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        // 先调用父类的计算方法
        List<TimeInterval> calculatedIntervals = super.calculate(satellitePropagator, targetPropagator, intervals);

        // 过滤无效的时间窗口
        return filterShortWindows(calculatedIntervals);
    }


}