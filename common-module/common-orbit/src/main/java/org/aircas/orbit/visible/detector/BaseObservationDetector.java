package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.handlers.EventHandler;

/**
 * 观测约束检测器基类
 * 提取所有检测器的共同属性和方法
 */
public abstract class BaseObservationDetector<T extends BaseObservationDetector<T>> extends AbstractDetector<T> {

    protected final Frame      inertialFrame;
    protected final Propagator targetPropagator;

    protected BaseObservationDetector(Propagator targetPropagator, AdaptableInterval maxCheck, double threshold, int maxIter, EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.inertialFrame    = FramesFactory.getEME2000();
        this.targetPropagator = targetPropagator;
    }

    /**
     * 获取目标相对观测卫星的位置向量
     */
    protected Vector3D getRelativePosition(SpacecraftState state) {
        Vector3D observerPosition = state.getPVCoordinates().getPosition();
        Vector3D targetPosition   = targetPropagator.propagate(state.getDate()).getPVCoordinates(inertialFrame).getPosition();
        return targetPosition.subtract(observerPosition);
    }

    /**
     * 计算两个向量之间的夹角
     */
    protected double calculateAngle(Vector3D v1, Vector3D v2) {
        return Vector3D.angle(v1, v2);
    }

    /**
     * 检测条件评估
     * 返回正值表示满足条件，负值表示不满足条件
     */
    @Override
    public abstract double g(SpacecraftState s);
}