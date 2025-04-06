package org.aircas.orbit.visible.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.LunarExclusionDetector;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.hipparchus.ode.events.Action;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;
import java.util.List;

/**
 * 月球排除事件处理器
 * <p>
 * 该类用于计算目标是否在月球避让角范围内的可观测时间区间。
 * </p>
 */
@Slf4j
public class LunarExclusionEventHandler extends EventDetectorHandler {

    private final double avoidanceAngle; // 月球避让角（弧度）
    private final double threshold; // 阈值
    private final int maxIter; // 最大迭代次数
    private final double maxCheck; // 检查间隔（秒）

    public LunarExclusionEventHandler(double avoidanceAngle, double maxCheck, double threshold,int maxIter) {
        this.avoidanceAngle = avoidanceAngle;
        this.threshold = threshold;
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
    }

    @Override
    public String getName() {
        return "月光遮蔽角";
    }

    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        
        for (TimeInterval interval : intervals) {
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();

            // 创建月球排除检测器
            LunarExclusionDetector detector = new LunarExclusionDetector(
                    targetPropagator,
                    avoidanceAngle,
                    AdaptableInterval.of(maxCheck),
                    threshold,
                    maxIter,
                    (s, eventDetector, increasing) -> {
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