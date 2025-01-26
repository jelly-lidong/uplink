package org.aircas.resource.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 视场角排除事件检测器
 *
 * <p>
 * 该检测器用于检测目标是否在卫星的视场内。
 * 当卫星-目标连线与卫星-视场边界之间的夹角小于设定阈值时,
 * 表示目标在视场内，可以进行观测。
 * </p>
 */
public class FieldOfViewExclusionDetector extends AbstractDetector<FieldOfViewExclusionDetector> {

    private final Propagator targetPropagator;
    private final double fieldOfViewAngle; // 视场角

    public FieldOfViewExclusionDetector(Propagator targetPropagator, double fieldOfViewAngle,
                                         AdaptableInterval maxCheck, double threshold, int maxIter, EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.targetPropagator = targetPropagator;
        this.fieldOfViewAngle = fieldOfViewAngle;
    }

    @Override
    protected FieldOfViewExclusionDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new FieldOfViewExclusionDetector(targetPropagator, fieldOfViewAngle, newMaxCheck, newThreshold, newMaxIter, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        Vector3D targetPosition = targetPropagator.propagate(s.getDate()).getPVCoordinates(FramesFactory.getEME2000()).getPosition();

        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);
        double angle = Vector3D.angle(satelliteToTarget, Vector3D.PLUS_I); // 假设卫星朝向为X轴

        // 返回角度与视场角的差值
        return angle - Math.toRadians(fieldOfViewAngle);
    }

}