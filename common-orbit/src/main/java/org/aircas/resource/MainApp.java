package org.aircas.resource;

import org.orekit.files.ccsds.ndm.odm.KeplerianElements;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.aircas.resource.event.EventDetectorCalculator;
import org.aircas.resource.event.impl.EarthAtmosphereExclusionEventCalculator;
import org.aircas.resource.event.impl.SolarExclusionEventCalculator;
import org.aircas.resource.model.TimeInterval;
import org.aircas.resource.util.OrbitUtil;
import org.aircas.resource.util.PropagatorCreator;

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

        EventDetectorCalculator.add(new EarthAtmosphereExclusionEventCalculator(100,1.,0.001));
        EventDetectorCalculator.add(new SolarExclusionEventCalculator(5.,100,1,0.001));

        List<TimeInterval> timeIntervalList = new ArrayList<>();
        AbsoluteDate startDate = new AbsoluteDate(2023, 10, 1, 0, 0, 0.0, TimeScalesFactory.getUTC());
        AbsoluteDate endDate = startDate.shiftedBy(86400 * 30); // 传播一天
        timeIntervalList.add(new TimeInterval(startDate, endDate));
        EventDetectorCalculator.getEventDetectorCalculators().forEach(eventDetectorCalculator -> {
            List<TimeInterval> result = eventDetectorCalculator.calculate(numericalPropagator, targetPropagator, timeIntervalList);
            timeIntervalList.clear();
            timeIntervalList.addAll(result);
        });
    }

    private static NumericalPropagator getNumericalPropagator() {
        KeplerianElements keplerianElements = new KeplerianElements();
        keplerianElements.setA(7000e3);
        keplerianElements.setE(0.);
        keplerianElements.setI(Math.toRadians(98.7));
        keplerianElements.setPa(Math.toRadians(0));
        keplerianElements.setRaan(Math.toRadians(0));
        keplerianElements.setMeanMotion(Math.toRadians(0));
        keplerianElements.setEpoch(new AbsoluteDate(2023, 10, 1, 0, 0, 0.0, TimeScalesFactory.getUTC()));
        keplerianElements.setAnomalyType(PositionAngleType.MEAN);

        return PropagatorCreator.createNumericalPropagator(keplerianElements);
    }
}
