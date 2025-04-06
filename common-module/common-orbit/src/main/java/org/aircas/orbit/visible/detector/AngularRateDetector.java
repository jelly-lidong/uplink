package org.aircas.orbit.visible.detector;

import lombok.Getter;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

/**
 * 角速率检测器
 *
 * <p>
 * 该检测器用于检测目标相对于观测卫星的角速率是否超过阈值。
 * 计算目标在观测卫星视场中的方位角和俯仰角的变化率，
 * 当任一角速率超过设定阈值时，表示目标运动过快，不适合观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>计算目标的方位角和俯仰角变化率</li>
 * <li>检测角速率是否超过阈值</li>
 * <li>支持设置最大角速率阈值</li>
 * </ul>
 * </p>
 */
@Getter
public class AngularRateDetector extends BaseObservationDetector<AngularRateDetector> {

    private final double       maxAngularRate;   // 最大角速率 (rad/s)
    private       double       lastAzimuth;           // 上一时刻的方位角
    private       double       lastElevation;         // 上一时刻的俯仰角
    private       AbsoluteDate lastDate;        // 上一时刻的时间
    private       Vector3D     lastDirection;        // 上一时刻的指向向量

    /**
     * 构造函数
     *
     * @param targetPropagator 目标传播器
     * @param maxAngularRate 最大角速率（度/秒）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔
     * @param threshold 检测阈值
     * @param handler 事件处理器
     */
    public AngularRateDetector(Propagator targetPropagator, double maxAngularRate, int maxIter, AdaptableInterval maxCheck, double threshold, EventHandler handler) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);
        this.maxAngularRate = Math.toRadians(maxAngularRate);
        this.lastDate       = null;
        this.lastDirection  = null;
    }

    @Override
    protected AngularRateDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new AngularRateDetector(targetPropagator, Math.toDegrees(maxAngularRate), newMaxIter, newMaxCheck, newThreshold, newHandler);
    }

    /**
     * 计算方位角和俯仰角
     * @param relativePosition 相对位置向量
     * @return 方位角和俯仰角数组 [方位角, 俯仰角]
     */
    private double[] calculateAzEl(Vector3D relativePosition) {
        // 计算相对位置在东北天坐标系下的方位角和俯仰角
        double x = relativePosition.getX();
        double y = relativePosition.getY();
        double z = relativePosition.getZ();

        // 计算方位角（从北向东为正）
        double azimuth = Math.atan2(y, x);
        // 确保方位角在0到2π之间
        if (azimuth < 0) {
            azimuth += 2 * Math.PI;
        }

        // 计算俯仰角
        double r         = Math.sqrt(x * x + y * y);
        double elevation = Math.atan2(z, r);

        return new double[]{azimuth, elevation};
    }

    @Override
    public double g(SpacecraftState s) {
        // 获取相对位置向量
        Vector3D relativePosition = getRelativePosition(s);
        Vector3D currentDirection = relativePosition.normalize();

        // 计算当前时刻的方位角和俯仰角
        double[] azEl             = calculateAzEl(relativePosition);
        double   currentAzimuth   = azEl[0];
        double   currentElevation = azEl[1];

        if (lastDate == null || lastDirection == null) {
            // 第一次调用，初始化上一时刻的数据
            lastAzimuth   = currentAzimuth;
            lastElevation = currentElevation;
            lastDate      = s.getDate();
            lastDirection = currentDirection;
            return -1.0;
        }

        // 计算时间间隔（秒）
        double deltaT = s.getDate().durationFrom(lastDate);

        // 计算方位角变化（需要处理跨越360度的情况）
        double deltaAzimuth = currentAzimuth - lastAzimuth;
        if (deltaAzimuth > Math.PI) {
            deltaAzimuth -= 2 * Math.PI;
        } else if (deltaAzimuth < -Math.PI) {
            deltaAzimuth += 2 * Math.PI;
        }

        // 计算俯仰角变化
        double deltaElevation = currentElevation - lastElevation;

        // 计算角速率
        double azimuthRate   = Math.abs(deltaAzimuth / deltaT);
        double elevationRate = Math.abs(deltaElevation / deltaT);

        // 更新上一时刻的数据
        lastAzimuth   = currentAzimuth;
        lastElevation = currentElevation;
        lastDate      = s.getDate();
        lastDirection = currentDirection;

        // 返回超出最大角速率的量
        // 如果任一角速率超过限制，返回正值
        return Math.max(azimuthRate - maxAngularRate, elevationRate - maxAngularRate);
    }


}