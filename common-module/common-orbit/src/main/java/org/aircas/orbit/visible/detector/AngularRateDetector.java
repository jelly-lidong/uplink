package org.aircas.orbit.visible.detector;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AbstractDetector;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetectionSettings;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

public class AngularRateDetector extends AbstractDetector<AngularRateDetector> {

    private final Propagator targetPropagator;
    private final double maxAngularRate;   // 最大转角速率 (rad/s)
    private double lastAzimuth;    // 上一时刻的方位角
    private double lastElevation;  // 上一时刻的俯仰角
    private AbsoluteDate lastDate; // 上一时刻的时间

    public AngularRateDetector(Propagator targetPropagator, 
                              double maxAngularRate,
                              AdaptableInterval maxCheck,
                              double threshold, 
                              int maxIter, 
                              EventHandler handler) {
        super(new EventDetectionSettings(maxCheck, threshold, maxIter), handler);
        this.targetPropagator = targetPropagator;
        this.maxAngularRate = maxAngularRate;
        this.lastDate = null;
    }

    @Override
    protected AngularRateDetector create(AdaptableInterval newMaxCheck, 
                                       double newThreshold, 
                                       int newMaxIter, 
                                       EventHandler newHandler) {
        return new AngularRateDetector(targetPropagator, 
            maxAngularRate,
                                     newMaxCheck, 
                                     newThreshold, 
                                     newMaxIter, 
                                     newHandler);
    }

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
        double r = Math.sqrt(x * x + y * y);
        double elevation = Math.atan2(z, r);
        
        return new double[]{azimuth, elevation};
    }

    @Override
    public double g(SpacecraftState s) {
        // 获取观测卫星和目标卫星的位置
        Vector3D observerPosition = s.getPVCoordinates().getPosition();
        Vector3D targetPosition = targetPropagator.propagate(s.getDate())
                                .getPVCoordinates(FramesFactory.getEME2000()).getPosition();

        // 计算相对位置向量
        Vector3D relativePosition = targetPosition.subtract(observerPosition);
        
        // 计算当前时刻的方位角和俯仰角
        double[] azEl = calculateAzEl(relativePosition);
        double currentAzimuth = azEl[0];
        double currentElevation = azEl[1];
        
        if (lastDate == null) {
            // 第一次调用，初始化上一时刻的数据
            lastAzimuth = currentAzimuth;
            lastElevation = currentElevation;
            lastDate = s.getDate();
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
        double azimuthRate = Math.abs(deltaAzimuth / deltaT);
        double elevationRate = Math.abs(deltaElevation / deltaT);

        // 更新上一时刻的数据
        lastAzimuth = currentAzimuth;
        lastElevation = currentElevation;
        lastDate = s.getDate();

        // 返回超出最大角速率的量
        // 如果任一角速率超过限制，返回正值
        return Math.max(azimuthRate - maxAngularRate,
                       elevationRate - maxAngularRate);
    }

    public static void main(String[] args) {
        // 示例代码可以在这里实现
    
    }
} 