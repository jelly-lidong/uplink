package org.aircas.orbit.visible.detector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 地球大气层排除事件检测器
 * 
 * <p>
 * 该类用于检测目标是否在地球大气层背景中。当目标不在地球大气层背景中时,
 * 表示可以对目标进行观测。
 * </p>
 * 
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测目标是否在地球大气层背景中</li>
 * <li>计算目标与地球大气层的几何关系</li>
 * <li>生成目标可观测的时间区间</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 技术细节:
 * <ul>
 * <li>基于Orekit事件检测框架实现</li>
 * <li>使用WGS84地球椭球体模型</li>
 * <li>考虑大气层高度的影响</li>
 * </ul>
 * </p>
 */
@Slf4j
@Getter
public class EarthAtmosphereExclusionDetector extends BaseObservationDetector<EarthAtmosphereExclusionDetector> {

    private final OneAxisEllipsoid earth;
    private final double           minAtmosphereAngle; // 大气层高度（米）
    private static final double    DEFAULT_ATMOSPHERE_HEIGHT = 100000.0; // 默认大气层高度100km

    /**
     * 构造函数
     *
     * @param targetPropagator 目标传播器
     * @param minAtmosphereAngle 大气层高度（米）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔
     * @param threshold 检测阈值
     * @param handler 事件处理器
     * @param earth 地球模型
     */
    public EarthAtmosphereExclusionDetector(
            Propagator targetPropagator,
            double minAtmosphereAngle,
            int maxIter,
            AdaptableInterval maxCheck,
            double threshold,
            EventHandler handler,
            OneAxisEllipsoid earth) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);

        this.minAtmosphereAngle = minAtmosphereAngle;
        this.earth              = earth;
    }

    /**
     * 使用默认大气层高度的构造函数
     */
    public EarthAtmosphereExclusionDetector(
            Propagator targetPropagator,
            int maxIter,
            AdaptableInterval maxCheck,
            double threshold,
            EventHandler handler,
            OneAxisEllipsoid earth) {
        this(targetPropagator, DEFAULT_ATMOSPHERE_HEIGHT, maxIter, maxCheck, threshold, handler, earth);
    }

    @Override
    protected EarthAtmosphereExclusionDetector create(
            AdaptableInterval newMaxCheck,
            double newThreshold,
            int newMaxIter,
            EventHandler newHandler) {
        return new EarthAtmosphereExclusionDetector(
            targetPropagator, minAtmosphereAngle,
            newMaxIter,
            newMaxCheck,
            newThreshold,
            newHandler,
            earth);
    }

    @Override
    public double g(SpacecraftState s) {
        // 获取观测卫星位置
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        
        // 获取目标位置
        Vector3D targetPosition = getRelativePosition(s).add(satellitePosition);
        
        // 获取地心位置（在惯性系中）
        Vector3D earthPosition = earth.getBodyFrame()
            .getTransformTo(inertialFrame, s.getDate())
            .transformPosition(Vector3D.ZERO);

        // 计算卫星到目标的向量
        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);

        // 计算卫星到地心的向量
        Vector3D satelliteToEarth = earthPosition.subtract(satellitePosition);

        // 计算目标与地心方向的夹角
        double angle = Math.toDegrees(Vector3D.angle(satelliteToTarget, satelliteToEarth));

        log.info("目标-卫星-地心的夹角: {}°", angle);
        // 计算地球和大气层的视半径
        double earthRadius = earth.getEquatorialRadius();

        // 计算从卫星位置看到的地球大气层的视半径
        double apparentRadius = Math.toDegrees(Math.asin(earthRadius / satelliteToEarth.getNorm()));

        // 如果夹角大于视半径，表示目标不在地球大气层背景中
        // 返回正值表示满足观测条件，负值表示不满足
        return angle - apparentRadius - minAtmosphereAngle;
    }

    /**
     * 获取检测器名称
     */
    public String getName() {
        return "EarthAtmosphereExclusionDetector";
    }

    /**
     * 获取检测器描述
     */
    public String getDescription() {
        return String.format("地球大气层排除检测器 (大气层高度: %.2f km)", 
            minAtmosphereAngle / 1000.0);
    }

    /**
     * 检查给定点是否在地球大气层背景中
     *
     * @param satellitePosition 卫星位置
     * @param targetPosition 目标位置
     * @param date 观测时间
     * @return 是否在地球大气层背景中
     */
    public boolean isInAtmosphereBackground(
            Vector3D satellitePosition,
            Vector3D targetPosition,
            org.orekit.time.AbsoluteDate date) {
        
        Vector3D earthPosition = earth.getBodyFrame()
            .getTransformTo(inertialFrame, date)
            .transformPosition(Vector3D.ZERO);

        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);
        Vector3D satelliteToEarth = earthPosition.subtract(satellitePosition);

        double angle = Vector3D.angle(satelliteToTarget, satelliteToEarth);
        double totalRadius = earth.getEquatorialRadius() + minAtmosphereAngle;
        double apparentRadius = Math.asin(totalRadius / satelliteToEarth.getNorm());

        return angle <= apparentRadius;
    }

    /**
     * 获取大气层高度（千米）
     */
    public double getAtmosphereHeightKm() {
        return minAtmosphereAngle / 1000.0;
    }
}