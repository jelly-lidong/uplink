package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * 月亮排除检测器
 * 用于检测目标是否在月亮干扰范围内
 */
public class LunarExclusionDetector extends AbstractDetector<LunarExclusionDetector> {

    private final Propagator targetPropagator;
    private final double avoidanceAngle;  // 月亮避让角（弧度）
    private final PVCoordinatesProvider moon;  // 月球位置提供器
    private final Frame frame;  // 参考坐标系

    public LunarExclusionDetector(final Propagator targetPropagator,
                                 final double avoidanceAngle,
                                 final AdaptableInterval maxCheck,
                                 final double threshold,
                                 final int maxIter,
                                 final EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.targetPropagator = targetPropagator;
        this.avoidanceAngle = avoidanceAngle;
        this.frame = FramesFactory.getEME2000();
        try {
            this.moon = CelestialBodyFactory.getMoon();
        } catch (Exception e) {
            throw new RuntimeException("无法获取月球位置信息", e);
        }
    }

    @Override
    protected LunarExclusionDetector create(final AdaptableInterval newMaxCheck,
                                          final double newThreshold,
                                          final int newMaxIter,
                                          final EventHandler newHandler) {
        return new LunarExclusionDetector(targetPropagator, avoidanceAngle,
                newMaxCheck, newThreshold, newMaxIter, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        final AbsoluteDate date = s.getDate();
        
        // 获取观测卫星位置
        final Vector3D observerPosition = s.getPVCoordinates().getPosition();
        
        // 获取目标位置
        final Vector3D targetPosition = targetPropagator
                .propagate(date)
                .getPVCoordinates(frame)
                .getPosition();
        
        // 获取月球位置
        final Vector3D moonPosition = moon.getPVCoordinates(date, frame).getPosition();

        // 计算观测卫星到目标的方向向量
        final Vector3D observerToTarget = targetPosition.subtract(observerPosition).normalize();
        
        // 计算观测卫星到月球的方向向量
        final Vector3D observerToMoon = moonPosition.subtract(observerPosition).normalize();

        // 计算目标方向与月球方向的夹角
        double angle = Vector3D.angle(observerToTarget, observerToMoon);

        // 返回角度差值
        // 当返回值小于0时，表示目标在月球避让角范围内
        // 当返回值大于0时，表示目标在月球避让角范围外
        return angle - avoidanceAngle;
    }

} 