package org.aircas.orbit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.util.OrbitUtil;
import org.aircas.orbit.util.PropagatorCreator;
import org.aircas.orbit.visible.ConstraintManager;
import org.aircas.orbit.visible.handler.impl.DistanceExclusionEventHandler;
import org.aircas.orbit.visible.handler.impl.EarthAtmosphereExclusionEventHandler;
import org.aircas.orbit.visible.handler.impl.LunarExclusionEventHandler;
import org.aircas.orbit.visible.handler.impl.SolarExclusionEventHandler;
import org.orekit.files.ccsds.ndm.odm.KeplerianElements;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

@Slf4j
public class MainApp {

    public static void main(String[] args) {
        OrbitUtil.loadOrekitEnv();
        extracted();
    }


    private static void extracted() {
        // 定义轨道参数
        NumericalPropagator satellitePropagator = getNumericalPropagator();

        // 定义目标TLE
        TLEPropagator targetPropagator = getTlePropagator();

        // 创建约束管理器
        ConstraintManager manager = new ConstraintManager();

        int maxIter = 100;
        double maxCheck = 30;
        double threshold = 0.001;

        // 添加约束
        manager.addConstraint(new SolarExclusionEventHandler(maxIter, maxCheck, threshold, 90.0, 60))
                .addConstraint(new LunarExclusionEventHandler(maxIter, maxCheck, threshold, 60.0, 60))
                .addConstraint(new EarthAtmosphereExclusionEventHandler(maxIter, maxCheck, threshold, 0, 60))
                .addConstraint(new DistanceExclusionEventHandler(maxIter, maxCheck, threshold, 50000, 1000000, 60));
        log.info("约束信息");
        log.info("最小太阳遮蔽角：{}",90.0);
        log.info("最小月球遮蔽角：{}",60.0);
        log.info("最小地气光角度：{}",0);
        log.info("最近距离：{} km,最远距离：{}",50,1000);
        log.info("最短事件窗口：{}",60);
        log.info("--------------------------------------------------------------------------------");
        // 设置初始时间段
        AbsoluteDate startDate = new AbsoluteDate(2025, 4, 5, 0, 0, 0.0, TimeScalesFactory.getUTC());
        AbsoluteDate endDate = new AbsoluteDate(2025, 4, 10, 0, 0, 0.0, TimeScalesFactory.getUTC());
        log.info("开始时间：{}",startDate.toString().substring(0,19));
        log.info("结束时间：{}",endDate.toString().substring(0,19));
        log.info("--------------------------------------------------------------------------------");
        // 执行约束检查（带进度回调）
        List<TimeWindow> timeWindows = manager.executeConstraints(satellitePropagator, targetPropagator, new TimeWindow(startDate, endDate), new ConstraintManager.ProgressCallback() {
            @Override
            public void onStart(int totalConstraints) {
                log.warn("开始执行约束检查，共{}个约束", totalConstraints);
            }

            @Override
            public void onConstraintComplete(String name, int current, int total, List<TimeWindow> results) {
                //log.warn("【{}】执行完成 ({}/{})，共 {} 个时间窗口", name, current, total, results.size());
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWindow(TimeWindow window) {
                //log.info("有效时间窗口：{} 至 {}，共{}秒", window.getStartDate().toString().substring(0, 19), window.getEndDate().toString().substring(0, 19), window.getEndDate().durationFrom(window.getStartDate(),
                //  TimeUnit.SECONDS));
            }
        });
        for (TimeWindow window : timeWindows) {
            log.info("有效时间窗口：{} 至 {}，共{}秒", window.getStartDate().toString().substring(0, 19), window.getEndDate().toString().substring(0, 19), window.getEndDate().durationFrom(window.getStartDate(),
                    TimeUnit.SECONDS));
        }
        log.info("end....");
    }


    private static TLEPropagator getTlePropagator() {
        String line1 = "1 25544U 98067A   20344.52777778  .00001264  00000-0  29611-4 0  9993";
        String line2 = "2 25544  51.6460  21.4613 0007417  45.5487 314.5526 15.49112347256347";
        TLE targetTLE = new TLE(line1, line2);
        log.info("目标卫星两行报");
        log.info("line1: {}", line1);
        log.info("line2: {}", line2);
        log.info("--------------------------------------------------------------------------------");
        TLEPropagator targetPropagator = TLEPropagator.selectExtrapolator(targetTLE);
        return targetPropagator;
    }


    private static NumericalPropagator getNumericalPropagator() {
        KeplerianElements keplerianElements = new KeplerianElements();
        keplerianElements.setA(6945045.1);
        keplerianElements.setE(0.0014876690693199635);
        keplerianElements.setI(Math.toRadians(97.72345918416977));
        keplerianElements.setPa(Math.toRadians(96.93742203712463));
        keplerianElements.setRaan(Math.toRadians(122.43375301361084));
        keplerianElements.setMeanMotion(Math.toRadians(289.12911636829376));
        keplerianElements.setEpoch(new AbsoluteDate(2025, 4, 5, 0, 0, 0.0, TimeScalesFactory.getUTC()));
        keplerianElements.setAnomalyType(PositionAngleType.MEAN);
        log.info("任务卫星轨道六根数");
        log.info("半长轴: {}", keplerianElements.getA());
        log.info("偏心率: {}", keplerianElements.getE());
        log.info("轨道倾角: {}", Math.toDegrees(keplerianElements.getI()));
        log.info("近地点幅角: {}", Math.toDegrees(keplerianElements.getPa()));
        log.info("升交点赤经: {}", Math.toDegrees(keplerianElements.getRaan()));
        log.info("平近点角: {}", Math.toDegrees(keplerianElements.getMeanMotion()));
        log.info("历元时间: {}", keplerianElements.getEpoch());
        log.info("--------------------------------------------------------------------------------");
        return PropagatorCreator.createNumericalPropagator(keplerianElements);
    }
}
