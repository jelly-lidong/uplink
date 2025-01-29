package org.aircas.orbit.visible.event.impl;

import org.hipparchus.ode.events.Action;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.time.AbsoluteDate;
import org.aircas.orbit.visible.detector.EarthAtmosphereExclusionDetector;
import org.aircas.orbit.visible.event.EventDetectorCalculator;
import org.aircas.orbit.model.TimeInterval;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import java.util.ArrayList;
import java.util.List;

/**
 * 地球大气层排除事件链节点
 *
 * <p>
 * 该类用于检测目标是否在地球大气层背景中。当目标不在地球大气层背景中时,
 * 表示可以对目标进行观测。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>检测目标是否在地球大气层背景中</li>
 * <li>计算目标与地球大气层的几何关系</li>
 * <li>生成目标可观测的时间区间</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>空间目标观测任务规划</li>
 * <li>地基光学观测</li>
 * <li>空间态势感知</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 * <li>基于Orekit事件检测框架实现</li>
 * <li>使用WGS84地球椭球体模型</li>
 * <li>考虑100km高度的大气层</li>
 * </ul>
 * </p>
 */
public class EarthAtmosphereExclusionEventCalculator extends EventDetectorCalculator {
    private final int maxIter;
    private final double maxCheck; // 每分钟检查一次
    private final double threshold; // 精度

    public EarthAtmosphereExclusionEventCalculator(int maxIter, double maxCheck, double threshold) {
        this.maxIter = maxIter;
        this.maxCheck = maxCheck;
        this.threshold = threshold;
    }

  @Override
  public String getName() {
    return "地气光遮蔽角";
  }

  @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator, Propagator targetPropagator, List<TimeInterval> intervals) {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            AbsoluteDate startDate = interval.getStartDate();
            AbsoluteDate endDate = interval.getEndDate();
            // 定义地球模型
            OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                    Constants.WGS84_EARTH_FLATTENING,
                    FramesFactory.getITRF(IERSConventions.IERS_2010, true));

            final TimeInterval[] newInterval = new TimeInterval[1];
            EarthAtmosphereExclusionDetector detector = new EarthAtmosphereExclusionDetector(FramesFactory.getEME2000(),
                    targetPropagator, earth, AdaptableInterval.of(maxCheck), threshold, maxIter, (s, detector1, increasing) -> {
                if (increasing) {
                    newInterval[0] = new TimeInterval();
                    newInterval[0].setStartDate(s.getDate());
                    System.out.println("目标不在地球和大气层背景中开始时间: " + s.getDate());
                } else {
                    newInterval[0].setEndDate(s.getDate());
                    if (newInterval[0].getStartDate() != null && newInterval[0].getEndDate() != null) {
                        timeIntervals.add(newInterval[0]);
                    }
                    System.out.println("目标不在地球和大气层背景中结束时间: " + s.getDate());
                }
                return Action.CONTINUE;
            });
            System.out.println("偏心率: " + satellitePropagator.getInitialState().getOrbit().getE());

            // 将事件检测器添加到传播器
            satellitePropagator.addEventDetector(detector);

            // 检查初始状态
            SpacecraftState initialState = satellitePropagator.propagate(startDate);
            if (detector.g(initialState) > 0) {
                newInterval[0] = new TimeInterval();
                newInterval[0].setStartDate(startDate);
            }

            // 传播轨道
            SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);

            // 检查结束状态
            if (newInterval[0] != null && newInterval[0].getStartDate() != null && newInterval[0].getEndDate() == null && detector.g(finalState) > 0) {
                newInterval[0].setEndDate(endDate);
                timeIntervals.add(newInterval[0]);
            }
        }

        return timeIntervals;
    }
}
