package org.aircas.orbit.visible.detector;

import lombok.Getter;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 距离排除检测器
 *
 * <p>
 * 该检测器用于检测观测卫星与目标之间的距离是否在有效观测范围内。
 * 当距离小于最小距离或大于最大距离时，表示不适合观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星与目标之间的距离</li>
 * <li>判断距离是否在有效观测范围内</li>
 * <li>支持设置最小和最大观测距离</li>
 * </ul>
 * </p>
 */
@Getter
public class DistanceExclusionDetector extends BaseObservationDetector<DistanceExclusionDetector> {

    private final double minDistance; // 最小观测距离（米）
    private final double maxDistance; // 最大观测距离（米）

    /**
     * 构造函数
     *
     * @param targetPropagator 目标传播器
     * @param minDistance 最小观测距离（米）
     * @param maxDistance 最大观测距离（米）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔
     * @param threshold 检测阈值
     * @param handler 事件处理器
     */
    public DistanceExclusionDetector(Propagator targetPropagator, double minDistance, double maxDistance, int maxIter, AdaptableInterval maxCheck, double threshold, EventHandler handler) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);

        if (minDistance >= maxDistance) {
            throw new IllegalArgumentException("最小距离必须小于最大距离");
        }
        if (minDistance < 0) {
            throw new IllegalArgumentException("最小距离不能为负值");
        }

        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    protected DistanceExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new DistanceExclusionDetector(targetPropagator, minDistance, maxDistance, newMaxIter, newMaxCheck, newThreshold, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        // 获取相对位置向量
        Vector3D relativePosition = getRelativePosition(s);

        // 计算实际距离
        double distance = relativePosition.getNorm();

        // 判断距离是否在有效范围内
        if (distance < minDistance) {
            // 距离小于最小距离，返回正值表示不满足条件
            return minDistance - distance;
        } else if (distance > maxDistance) {
            // 距离大于最大距离，返回正值表示不满足条件
            return distance - maxDistance;
        } else {
            // 距离在有效范围内，返回负值表示满足条件
            // 返回到边界的最小距离的相反数
            return -Math.min(distance - minDistance, maxDistance - distance);
        }
    }

    /**
     * 获取检测器名称
     */
    public String getName() {
        return "DistanceExclusionDetector";
    }

    /**
     * 获取检测器描述
     */
    public String getDescription() {
        return String.format("距离排除检测器 (范围: %.2f km - %.2f km)", minDistance / 1000.0, maxDistance / 1000.0);
    }

    /**
     * 检查给定距离是否在有效范围内
     *
     * @param distance 待检查的距离（米）
     * @return 是否在有效范围内
     */
    public boolean isDistanceValid(double distance) {
        return distance >= minDistance && distance <= maxDistance;
    }

    /**
     * 获取到有效范围边界的距离
     *
     * @param distance 当前距离
     * @return 到最近边界的距离（米），在范围内返回0
     */
    public double getDistanceToBoundary(double distance) {
        if (distance < minDistance) {
            return minDistance - distance;
        } else if (distance > maxDistance) {
            return distance - maxDistance;
        }
        return 0.0;
    }

    /**
     * 获取最小观测距离（千米）
     */
    public double getMinDistanceKm() {
        return minDistance / 1000.0;
    }

    /**
     * 获取最大观测距离（千米）
     */
    public double getMaxDistanceKm() {
        return maxDistance / 1000.0;
    }
}