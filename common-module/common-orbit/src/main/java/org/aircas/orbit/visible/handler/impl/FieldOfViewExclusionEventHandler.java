package org.aircas.orbit.visible.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.hipparchus.ode.events.Action;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.orbit.visible.detector.FieldOfViewExclusionDetector;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.aircas.orbit.model.TimeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * 视场角排除事件计算器
 *
 * <p>
 * 该类用于计算目标在卫星的视场内的可观测时间区间。
 * </p>
 */
@Slf4j
public class FieldOfViewExclusionEventHandler extends EventDetectorHandler {

    private final double fieldOfViewAngle; // 视场角
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次
    private final double threshold; // 精度

    public FieldOfViewExclusionEventHandler(double fieldOfViewAngle, int maxIter, double maxCheck, double threshold) {
        this.fieldOfViewAngle = fieldOfViewAngle;
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
        this.threshold = threshold;
    }

  @Override
  public String getName() {
    return "视场角排除";
  }

  @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();

            // 创建视场角排除事件检测器
            FieldOfViewExclusionDetector detector = new FieldOfViewExclusionDetector(targetPropagator, fieldOfViewAngle,
                    AdaptableInterval.of(maxCheck), threshold, maxIter, (s, eventDetector, increasing) -> {
                if (increasing) {
                    TimeInterval newInterval = new TimeInterval();
                    newInterval.setStartDate(s.getDate());
                    timeIntervals.add(newInterval);
                } else {
                    TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                    lastInterval.setEndDate(s.getDate());
                }
                return Action.CONTINUE;
            });
            SpacecraftState initialState = satellitePropagator.propagate(startDate);
            if (detector.g(initialState) > 0 && (timeIntervals.isEmpty())) {
                System.out.println("开始时间: " + startDate);
                log.debug("初始状态符合条件: date:{}", initialState.getDate());
                TimeInterval newInterval = new TimeInterval();
                newInterval.setStartDate(startDate);
                timeIntervals.add(newInterval);
            }

            satellitePropagator.addEventDetector(detector);

            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);
            log.debug("最终状态: date:{}", finalState.getDate());
            log.debug("最终状态符合条件: date:{}", finalState.getDate());
            timeIntervals.get(timeIntervals.size() - 1).setEndDate(finalState.getDate());
        }

        return timeIntervals;
    }
}
