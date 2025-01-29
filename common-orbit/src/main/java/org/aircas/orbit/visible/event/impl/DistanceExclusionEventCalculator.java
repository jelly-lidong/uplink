package org.aircas.orbit.visible.event.impl;

import org.hipparchus.ode.events.Action;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.orbit.visible.detector.DistanceExclusionDetector;
import org.aircas.orbit.visible.event.EventDetectorCalculator;
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
public class DistanceExclusionEventCalculator extends EventDetectorCalculator {

    private final double minDistance; // 最小距离
    private final double maxDistance; // 最大距离

    private final double threshold; // 视场角
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次

    public DistanceExclusionEventCalculator(double minDistance, double maxDistance, double threshold, int maxIter, double maxCheck) {
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
            DistanceExclusionDetector distanceDetector = new DistanceExclusionDetector(targetPropagator, minDistance, maxDistance,
                    AdaptableInterval.of(maxCheck), threshold, maxIter, (s, detector, increasing) -> {
                if (increasing) {
                    System.out.println("目标与卫星的距离在范围内开始时间: " + s.getDate());
                } else {
                    System.out.println("目标与卫星的距离在范围内结束时间: " + s.getDate());
                }
                return Action.CONTINUE;
            });

            // 将事件检测器添加到传播器
            satellitePropagator.addEventDetector(distanceDetector);

            // 检查初始状态
            SpacecraftState initialState = satellitePropagator.propagate(startDate);
            if (distanceDetector.g(initialState) == 0) {
                TimeInterval newInterval = new TimeInterval();
                newInterval.setStartDate(startDate);
                timeIntervals.add(newInterval);
            }

            // 传播轨道
            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);

            // 检查结束状态
            if (distanceDetector.g(finalState) == 0) {
                TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                lastInterval.setEndDate(endDate);
            }
        }

        return timeIntervals;
    }
}