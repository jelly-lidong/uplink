package org.aircas.orbit.visible.detector;

import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.MainApp;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.util.OrbitUtil;
import org.aircas.orbit.visible.TimeWinCallback;
import org.aircas.orbit.visible.handler.impl.SolarExclusionEventHandler;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * 太阳排除事件检测器
 *
 * <p>
 * 该检测器用于检测卫星观测目标时是否受到太阳干扰。
 * 当卫星-目标连线与卫星-太阳连线之间的夹角小于设定阈值时,
 * 表示太阳可能会干扰卫星对目标的观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星观测目标时的太阳干扰</li>
 * <li>计算卫星-目标-太阳三者之间的角度关系</li>
 * <li>支持设置太阳排除角阈值</li>
 * <li>提供事件处理机制</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>光学成像任务规划</li>
 * <li>卫星对地观测任务</li>
 * <li>空间目标监视跟踪</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 * <li>基于Orekit事件检测框架实现</li>
 * <li>使用J2000惯性坐标系</li>
 * <li>支持TLE格式的目标轨道数据</li>
 * </ul>
 * </p>
 */
@Slf4j
public class SolarExclusionEventDetector extends BaseObservationDetector<SolarExclusionEventDetector> {

    private final double thresholdAngle;

    public SolarExclusionEventDetector(Propagator targetPropagator, double thresholdAngle, int maxIter, AdaptableInterval maxCheck, double threshold, EventHandler handler) {
        super(targetPropagator, maxCheck, threshold, maxIter, handler);
        this.thresholdAngle = thresholdAngle;
    }

    @Override
    protected SolarExclusionEventDetector create(AdaptableInterval newMaxCheck, double newThreshold, int newMaxIter, EventHandler newHandler) {
        return new SolarExclusionEventDetector(targetPropagator, thresholdAngle, newMaxIter, newMaxCheck, newThreshold, newHandler);
    }

    @Override
    public double g(SpacecraftState s) {
        AbsoluteDate date          = s.getDate();
        Vector3D sunPosition = CelestialBodyFactory.getSun().getPVCoordinates(date, inertialFrame).getPosition();
        Vector3D satellitePosition = s.getPVCoordinates().getPosition();
        Vector3D satelliteToSun = sunPosition.subtract(satellitePosition);
        Vector3D satelliteToTarget = getRelativePosition(s);

        double angle = calculateAngle(satelliteToSun, satelliteToTarget);
        //log.info("太阳遮蔽角：{}",Math.toDegrees(angle) - thresholdAngle);
        return Math.toDegrees(angle) - thresholdAngle;
    }


    public static void main(String[] args) {
        OrbitUtil.loadOrekitEnv();
        AbsoluteDate               startDate = new AbsoluteDate(2025, 4, 5, 0, 0, 0.0, TimeScalesFactory.getUTC());
        AbsoluteDate               endDate   = new AbsoluteDate(2025, 4, 5, 17, 0, 0.0, TimeScalesFactory.getUTC());

        // 定义轨道参数
        NumericalPropagator satellitePropagator = MainApp.getNumericalPropagator();
        // 定义目标TLE
        TLEPropagator targetPropagator = MainApp.getTlePropagator();

        SolarExclusionEventDetector detector = new SolarExclusionEventDetector(targetPropagator, 60, 100, AdaptableInterval.of(60), 0.1, new EventHandler() {
            @Override
            public Action eventOccurred(SpacecraftState s, EventDetector detector, boolean increasing) {
                if (increasing){
                    log.info("开始：{}", s.getDate());
                }else{
                    log.info("结束：{}", s.getDate());
                }
                return Action.CONTINUE;
            }
        });

        // 在传播之前验证初始状态
        SpacecraftState initialState = satellitePropagator.getInitialState();

        // 检查初始状态
        if (detector.g(initialState) > 0) {
            log.error("开始：{}",initialState.getDate());
        }

        satellitePropagator.addEventDetector(detector);

        // 传播到结束时间，并在过程中验证状态
        SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);
        if (detector.g(finalState) > 0) {
            log.error("结束：{}",finalState.getDate());
        }
    }
}
