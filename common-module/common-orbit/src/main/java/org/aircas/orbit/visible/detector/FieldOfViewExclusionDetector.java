package org.aircas.orbit.visible.detector;

import lombok.Getter;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 视场角排除事件检测器
 *
 * <p>
 * 该检测器用于检测目标是否在卫星的视场内。
 * 当卫星-目标连线与卫星视场中心轴之间的夹角小于设定阈值时，
 * 表示目标在视场内，可以进行观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测目标是否在卫星视场内</li>
 * <li>考虑卫星姿态的影响</li>
 * <li>支持设置视场角阈值</li>
 * </ul>
 * </p>
 */
@Getter
public class FieldOfViewExclusionDetector extends BaseObservationDetector<FieldOfViewExclusionDetector> {

    private final double   fieldOfViewAngle;     // 视场角（弧度）
    private final Vector3D boresightDirection; // 视场中心轴方向（在卫星体坐标系中）

    /**
     * 构造函数
     *
     * @param targetPropagator 目标传播器
     * @param fieldOfViewAngle 视场角（度）
     * @param boresightDirection 视场中心轴方向（在卫星体坐标系中）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔
     * @param threshold 检测阈值
     * @param handler 事件处理器
     */
    public FieldOfViewExclusionDetector(Propagator targetPropagator, double fieldOfViewAngle, Vector3D boresightDirection, int maxIter, AdaptableInterval maxCheck, double threshold,
        EventHandler handler) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);

        if (fieldOfViewAngle <= 0 || fieldOfViewAngle >= 180) {
            throw new IllegalArgumentException("视场角必须在(0, 180)度范围内");
        }

        this.fieldOfViewAngle   = Math.toRadians(fieldOfViewAngle);
        this.boresightDirection = boresightDirection.normalize();
    }

    /**
     * 使用默认视场中心轴方向的构造函数（默认指向+Z轴）
     */
    public FieldOfViewExclusionDetector(Propagator targetPropagator, double fieldOfViewAngle, int maxIter, AdaptableInterval maxCheck, double threshold, EventHandler handler) {
        this(targetPropagator, fieldOfViewAngle, Vector3D.PLUS_K, maxIter, maxCheck, threshold, handler);
    }

    @Override
    protected FieldOfViewExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new FieldOfViewExclusionDetector(targetPropagator, Math.toDegrees(fieldOfViewAngle), boresightDirection, newMaxIter, newMaxCheck, newThreshold, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        // 获取卫星姿态
        Attitude attitude = s.getAttitude();

        // 获取从卫星体坐标系到惯性系的旋转
        Rotation rotation = attitude.getRotation();

        // 将视场中心轴方向转换到惯性系
        Vector3D inertialBoresight = rotation.applyTo(boresightDirection);

        // 获取目标相对位置（在惯性系中）
        Vector3D relativePosition = getRelativePosition(s);
        Vector3D targetDirection  = relativePosition.normalize();

        // 计算目标方向与视场中心轴的夹角
        double angle = Vector3D.angle(targetDirection, inertialBoresight);

        // 返回夹角与视场角一半的差值
        // 如果返回值为负，表示目标在视场内
        // 如果返回值为正，表示目标在视场外
        return angle - fieldOfViewAngle / 2.0;
    }

    /**
     * 获取检测器名称
     */
    public String getName() {
        return "FieldOfViewExclusionDetector";
    }

    /**
     * 获取检测器描述
     */
    public String getDescription() {
        return String.format("视场角排除检测器 (视场角: %.2f°)", Math.toDegrees(fieldOfViewAngle));
    }

    /**
     * 检查给定方向是否在视场内
     *
     * @param direction 待检查的方向（在卫星体坐标系中）
     * @return 是否在视场内
     */
    public boolean isInFieldOfView(Vector3D direction) {
        double angle = Vector3D.angle(direction.normalize(), boresightDirection);
        return angle <= fieldOfViewAngle / 2.0;
    }

    /**
     * 获取视场角（度）
     */
    public double getFieldOfViewAngleDegrees() {
        return Math.toDegrees(fieldOfViewAngle);
    }

    /**
     * 获取到视场边界的角度
     *
     * @param direction 待检查的方向（在卫星体坐标系中）
     * @return 到视场边界的角度（度），在视场内为负值，在视场外为正值
     */
    public double getAngleToBoundary(Vector3D direction) {
        double angle = Vector3D.angle(direction.normalize(), boresightDirection);
        return Math.toDegrees(angle - fieldOfViewAngle / 2.0);
    }
}