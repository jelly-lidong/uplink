package org.aircas.orbit.visible.event.impl;

import org.hipparchus.ode.events.Action;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.orbit.visible.detector.SolarExclusionEventDetector;
import org.aircas.orbit.visible.event.EventDetectorCalculator;
import org.aircas.orbit.model.TimeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * 太阳排除事件链节点
 *
 * <p>
 * 该类用于检测卫星观测目标时是否受到太阳干扰。当卫星-目标连线与卫星-太阳连线之间的夹角
 * 大于设定阈值时,表示可以对目标进行观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测卫星观测目标时的太阳干扰</li>
 * <li>计算卫星-目标-太阳三者之间的角度关系</li>
 * <li>生成目标可观测的时间区间</li>
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
 * <li>支持设置太阳排除角阈值</li>
 * </ul>
 * </p>
 */
public class SolarExclusionEventCalculator extends EventDetectorCalculator {
    private final double thresholdAngle;
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次
    private final double threshold; // 精度

    public SolarExclusionEventCalculator(double thresholdAngle, int maxIter, double maxCheck, double threshold) {
        this.thresholdAngle = thresholdAngle;
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
        this.threshold = threshold;
    }

  @Override
  public String getName() {
    return "太阳遮蔽角";
  }

  @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            satellitePropagator.clearEventsDetectors();
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();

            final TimeInterval[] newInterval = new TimeInterval[1];
            SolarExclusionEventDetector detector = new SolarExclusionEventDetector(FramesFactory.getEME2000(),
                    targetPropagator, thresholdAngle, maxIter, AdaptableInterval.of(maxCheck), threshold, (s, detector1, increasing) -> {
                if (increasing) {
                    newInterval[0] = new TimeInterval();
                    newInterval[0].setStartDate(s.getDate());
                    System.out.println("【event】太阳遮蔽角大于15度开始时间: " + s.getDate());
                } else {
                    if (newInterval[0] != null && newInterval[0].getStartDate() != null) {
                        newInterval[0].setEndDate(s.getDate());
                        timeIntervals.add(newInterval[0]);
                    }
                    System.out.println("【event】太阳遮蔽角大于15度结束时间: " + s.getDate());
                }
                return Action.CONTINUE;
            });

            // 将事件检测器添加到传播器
            satellitePropagator.addEventDetector(detector);

            System.out.println("传播轨道开始时间: " + startDate);
            // 检查初始状态
            SpacecraftState initialState = satellitePropagator.propagate(startDate);
            if (detector.g(initialState) > 0) {
                System.out.println("太阳遮蔽角大于15度开始时间: " + startDate);
                newInterval[0] = new TimeInterval();
                newInterval[0].setStartDate(startDate);
            }

            // 传播轨道
            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);

            // 检查结束状态
            if (newInterval[0] != null && newInterval[0].getStartDate() != null && newInterval[0].getEndDate() == null && detector.g(finalState) > 0) {
                System.out.println("太阳遮蔽角大于15度结束时间: " + endDate);
                newInterval[0].setEndDate(endDate);
                timeIntervals.add(newInterval[0]);
            }
        }
        return timeIntervals;
    }
}
