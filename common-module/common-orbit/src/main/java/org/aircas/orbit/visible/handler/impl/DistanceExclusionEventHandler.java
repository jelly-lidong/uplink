package org.aircas.orbit.visible.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.hipparchus.ode.events.Action;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.orbit.visible.detector.DistanceExclusionDetector;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.aircas.orbit.model.TimeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * 距离约束事件计算器
 *
 * <p>
 * 该类用于计算目标与卫星之间的距离是否在设定范围内的可观测时间区间。
 * </p>
 */
@Slf4j
public class DistanceExclusionEventHandler extends EventDetectorHandler {

    private final double minDistance; // 最小距离
    private final double maxDistance; // 最大距离

    private final double threshold; // 视场角
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次

    public DistanceExclusionEventHandler(double minDistance, double maxDistance, double threshold, int maxIter, double maxCheck) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.threshold = threshold;
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
    }

  @Override
  public String getName() {
    return "距离";
  }

  @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();

            // 创建距离排除事件检测器
            DistanceExclusionDetector detector = new DistanceExclusionDetector(targetPropagator, minDistance, maxDistance,
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