package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 月球排除事件检测器
 *
 * <p>
 * 该检测器用于检测卫星观测目标时是否受到月球干扰。
 * 当卫星-目标连线与卫星-月球连线之间的夹角小于设定阈值时，
 * 表示月球可能会干扰卫星对目标的观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星观测目标时的月球干扰</li>
 * <li>计算卫星-目标-月球三者之间的角度关系</li>
 * <li>支持设置月球排除角阈值</li>
 * </ul>
 * </p>
 */
public class LunarExclusionDetector extends BaseObservationDetector<LunarExclusionDetector> {

    private final double avoidanceAngle;  // 月球排除角（弧度）

    /**
     * 构造函数
     *
     * @param targetPropagator 目标传播器
     * @param avoidanceAngle 月球排除角（度）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔
     * @param threshold 检测阈值
     * @param handler 事件处理器
     */
    public LunarExclusionDetector(Propagator targetPropagator, double avoidanceAngle, int maxIter, AdaptableInterval maxCheck, double threshold, EventHandler handler) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);
        this.avoidanceAngle = Math.toRadians(avoidanceAngle);
    }

    @Override
    protected LunarExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new LunarExclusionDetector(targetPropagator, Math.toDegrees(avoidanceAngle), newMaxIter, newMaxCheck, newThreshold, newHandler);
    }

    /**
     * 评估月球干扰条件
     *
     * @param s 航天器状态
     * @return 如果角度大于排除角返回正值，否则返回负值
     */
    @Override
    public double g(SpacecraftState s) {
        // 获取月球在J2000坐标系中的位置
        Vector3D moonPosition = CelestialBodyFactory.getMoon().getPVCoordinates(s.getDate(), inertialFrame).getPosition();

        // 获取观测卫星位置
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();

        // 计算卫星指向月球的向量
        Vector3D satelliteToMoon = moonPosition.subtract(satellitePosition);

        // 获取卫星指向目标的向量
        Vector3D satelliteToTarget = getRelativePosition(s);

        // 计算两个方向向量之间的夹角
        double angle = calculateAngle(satelliteToMoon, satelliteToTarget);

        // 返回角度与排除角的差值
        // 如果返回值为正，表示目标不在月球干扰区域内
        // 如果返回值为负，表示目标在月球干扰区域内
        return angle - avoidanceAngle;
    }
}