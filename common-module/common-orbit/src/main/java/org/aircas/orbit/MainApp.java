package org.aircas.orbit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.aircas.orbit.visible.handler.impl.LunarExclusionEventHandler;
import org.orekit.files.ccsds.ndm.odm.KeplerianElements;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.EphemerisGenerator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.aircas.orbit.visible.handler.impl.EarthAtmosphereExclusionEventHandler;
import org.aircas.orbit.visible.handler.impl.SolarExclusionEventHandler;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.util.OrbitUtil;
import org.aircas.orbit.util.PropagatorCreator;

import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        OrbitUtil.loadOrekitEnv();
        extracted();
//        try {
//            Propagator satellitePropagator = getNumericalPropagator();
//
//            // 定义目标TLE
//            String line1 = "1 25544U 98067A   20344.52777778  .00001264  00000-0  29611-4 0  9993";
//            String line2 = "2 25544  51.6460  21.4613 0007417  45.5487 314.5526 15.49112347256347";
//            TLE targetTLE = new TLE(line1, line2);
//            TLEPropagator targetPropagator = TLEPropagator.selectExtrapolator(targetTLE);
//
//            AbsoluteDate startDate = new AbsoluteDate(2023, 10, 1, 0, 0, 0.0, TimeScalesFactory.getUTC());
//            // 创建观测任务
//            ObservationTask task = new ObservationTask(satellitePropagator, targetPropagator, startDate, startDate.shiftedBy(86400)); // 传播一天
//
//            // 添加约束
//            task.addConstraint(new GeometricConstraint(30.0, 20.0)); // 视场角30度，目标角度20度
//            task.addConstraint(new TimeConstraint(startDate, startDate.shiftedBy(86400))); // 0到60秒内，当前时间30秒
//            task.addConstraint(new EnvironmentalConstraint(true)); // 晴天
//
//            // 验证约束
//            List<TimeInterval> validIntervals = task.validate();
//            for (TimeInterval interval : validIntervals) {
//                System.out.println("有效时间段: " + interval.getStartDate() + " 到 " + interval.getEndDate());
//            }
//
//        } catch (OrekitException e) {
//            System.err.println(e.getLocalizedMessage());
//        }


    }


    private static void extracted() {
        // 定义轨道参数
        NumericalPropagator numericalPropagator = getNumericalPropagator();

        // 定义目标TLE
        String line1 = "1 25544U 98067A   20344.52777778  .00001264  00000-0  29611-4 0  9993";
        String line2 = "2 25544  51.6460  21.4613 0007417  45.5487 314.5526 15.49112347256347";
        TLE targetTLE = new TLE(line1, line2);
        TLEPropagator targetPropagator = TLEPropagator.selectExtrapolator(targetTLE);

//        EventDetectorHandler.add(new SolarExclusionEventHandler(5.,60,100,1,0.001));
//        EventDetectorHandler.add(new LunarExclusionEventHandler(5.,1,0.001,100));
        EventDetectorHandler.add(new EarthAtmosphereExclusionEventHandler(100,1.,0.001));

        List<TimeInterval> timeIntervalList = new ArrayList<>();
        AbsoluteDate startDate = new AbsoluteDate(2025, 4, 6, 0, 0, 0.0, TimeScalesFactory.getUTC());
        AbsoluteDate endDate = startDate.shiftedBy(86400 * 2); // 传播一天
        timeIntervalList.add(new TimeInterval(startDate, endDate));
        EventDetectorHandler.getEVENT_DETECTOR_HANDLERS().forEach(eventDetectorCalculator -> {
            List<TimeInterval> result = eventDetectorCalculator.calculate(numericalPropagator, targetPropagator, timeIntervalList);
            timeIntervalList.clear();
            timeIntervalList.addAll(result);
        });

        System.out.println("有效时间段: ");
        for (TimeInterval interval : timeIntervalList) {
            System.out.println(interval.getStartDate()    + " 到 " + interval.getEndDate());
        }
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

        return PropagatorCreator.createNumericalPropagator(keplerianElements);
    }
}
