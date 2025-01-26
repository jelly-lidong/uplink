package org.aircas.resource.event.impl;

import org.hipparchus.ode.events.Action;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.resource.detector.FieldOfViewExclusionDetector;
import org.aircas.resource.event.EventDetectorCalculator;
import org.aircas.resource.model.TimeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * 视场角排除事件计算器
 *
 * <p>
 * 该类用于计算目标在卫星的视场内的可观测时间区间。
 * </p>
 */
public class FieldOfViewExclusionEventCalculator extends EventDetectorCalculator {

    private final double fieldOfViewAngle; // 视场角
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次
    private final double threshold; // 精度

    public FieldOfViewExclusionEventCalculator(double fieldOfViewAngle, int maxIter, double maxCheck, double threshold) {
        this.fieldOfViewAngle = fieldOfViewAngle;
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
        this.threshold = threshold;
    }

    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();

            // 创建视场角排除事件检测器
            FieldOfViewExclusionDetector detector = new FieldOfViewExclusionDetector(targetPropagator, fieldOfViewAngle,
                    AdaptableInterval.of(maxCheck), threshold, maxIter, (s, detector1, increasing) -> {
                if (increasing) {
                    System.out.println("目标在视场内开始时间: " + s.getDate());
                } else {
                    System.out.println("目标在视场内结束时间: " + s.getDate());
                }
                return Action.CONTINUE;
            });

            // 将事件检测器添加到传播器
            satellitePropagator.addEventDetector(detector);

            // 检查初始状态
            SpacecraftState initialState = satellitePropagator.propagate(startDate);
            if (detector.g(initialState) > 0) {
                TimeInterval newInterval = new TimeInterval();
                newInterval.setStartDate(startDate);
                timeIntervals.add(newInterval);
            }

            // 传播轨道
            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);

            // 检查结束状态
            if (detector.g(finalState) > 0) {
                TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                lastInterval.setEndDate(endDate);
            }
        }

        return timeIntervals;
    }
}
