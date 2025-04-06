package org.aircas.orbit.visible.handler.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.aircas.orbit.visible.detector.SolarExclusionEventDetector;
import org.aircas.orbit.visible.handler.EventDetectorHandler;
import org.hipparchus.ode.events.Action;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;

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
@Slf4j
public class SolarExclusionEventHandler extends EventDetectorHandler {

  private final double minSolarThresholdAngle;
  private final double minWindowDuration; // 最短窗口时长（秒）
  private final int    maxIter;
  private final double maxCheck;
  private final double threshold;

  public SolarExclusionEventHandler(double minSolarThresholdAngle, double minWindowDuration, int maxIter,
      double maxCheck, double threshold) {
    this.minSolarThresholdAngle = minSolarThresholdAngle;
    this.maxIter                = maxIter;
    this.maxCheck               = maxCheck;
    this.threshold              = threshold;
    this.minWindowDuration      = minWindowDuration;
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public List<TimeInterval> calculate(Propagator satellitePropagator,
      Propagator targetPropagator, List<TimeInterval> intervals) {
    List<TimeInterval> timeIntervals = new ArrayList<>();
    for (TimeInterval interval : intervals) {
      satellitePropagator.clearEventsDetectors();
      AbsoluteDate startDate = interval.getStartDate();
      AbsoluteDate endDate   = interval.getEndDate();

      SolarExclusionEventDetector detector = new SolarExclusionEventDetector(
          FramesFactory.getEME2000(),
          targetPropagator, minSolarThresholdAngle, maxIter,
          AdaptableInterval.of(maxCheck), threshold,
          (s, eventDetector, increasing) -> {
            log.debug("检测到太阳排除事件: {},date:{}", increasing ? "开始" : "结束", s.getDate());
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
