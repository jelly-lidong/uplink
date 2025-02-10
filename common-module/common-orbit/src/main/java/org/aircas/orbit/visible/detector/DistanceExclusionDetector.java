package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.handlers.EventHandler;

public class DistanceExclusionDetector extends AbstractDetector<DistanceExclusionDetector> {

    private final Propagator targetPropagator;
    private final double minDistance; // 最小距离
    private final double maxDistance; // 最大距离

    public DistanceExclusionDetector(Propagator targetPropagator, double minDistance, double maxDistance,
                                     AdaptableInterval maxCheck, double threshold, int maxIter, EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.targetPropagator = targetPropagator;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    protected DistanceExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new DistanceExclusionDetector(targetPropagator, minDistance, maxDistance, newMaxCheck, newThreshold, newMaxIter, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        Vector3D targetPosition = targetPropagator.propagate(s.getDate()).getPVCoordinates(FramesFactory.getEME2000()).getPosition();

        double distance = satellitePosition.subtract(targetPosition).getNorm();

        // 返回距离与最小距离和最大距离的差值
        if (distance < minDistance) {
            return minDistance - distance; // 小于最小距离
        } else if (distance > maxDistance) {
            return distance - maxDistance; // 大于最大距离
        } else {
            return 0; // 在范围内
        }
    }

    public static void main(String[] args) {
        // 示例代码可以在这里实现，类似于其他检测器的主方法
    }
}
