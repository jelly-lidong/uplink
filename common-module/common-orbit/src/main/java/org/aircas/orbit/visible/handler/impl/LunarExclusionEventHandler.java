package org.aircas.orbit.visible.handler.impl;

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
            LunarExclusionDetector lunarDetector = new LunarExclusionDetector(
                    targetPropagator,
                    avoidanceAngle,
                    AdaptableInterval.of(maxCheck),
                    threshold,
                    maxIter,
                    (s, detector, increasing) -> {
                        if (increasing) {
                            // 目标离开月球避让区，开始可见
                            TimeInterval newInterval = new TimeInterval();
                            newInterval.setStartDate(s.getDate());
                            timeIntervals.add(newInterval);
                        } else {
                            // 目标进入月球避让区，结束可见
                            if (!timeIntervals.isEmpty()) {
                                TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                                lastInterval.setEndDate(s.getDate());
                            }
                        }
                        return Action.CONTINUE;
                    });

            // 将检测器添加到传播器
            satellitePropagator.addEventDetector(lunarDetector);

            // 检查初始状态
            SpacecraftState initialState = satellitePropagator.propagate(startDate);



            // 传播轨道
            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);

            // 检查结束状态
            if (lunarDetector.g(finalState) > 0) {
                // 结束状态不在月球避让区内
                if (!timeIntervals.isEmpty()) {
                    TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                    if (lastInterval.getEndDate() == null) {
                        lastInterval.setEndDate(endDate);
                    }
                }
            }
        }

        return timeIntervals;
    }
} 