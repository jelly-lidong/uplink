package org.aircas.orbit.visible.handler.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.MainApp;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.util.OrbitUtil;
import org.aircas.orbit.visible.TimeWinCallback;
import org.aircas.orbit.visible.detector.SolarExclusionEventDetector;
import org.aircas.orbit.visible.handler.AbstractEventDetectorHandler;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.AdaptableInterval;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * 太阳排除事件处理器
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
public class SolarExclusionEventHandler extends AbstractEventDetectorHandler {

    private final double minSolarThresholdAngle;  // 最小太阳遮蔽角（度）
    private final double minWindowDuration;       // 最短窗口时长（秒）

    /**
     * 构造函数
     *
     * @param minSolarThresholdAngle 最小太阳遮蔽角（度）
     * @param minWindowDuration 最短窗口时长（秒）
     * @param maxIter 最大迭代次数
     * @param maxCheck 最大检查间隔（秒）
     * @param threshold 检测阈值
     */
    public SolarExclusionEventHandler(int maxIter, double maxCheck, double threshold, double minSolarThresholdAngle, double minWindowDuration) {
        super(maxCheck, threshold, maxIter, minWindowDuration);

        if (minSolarThresholdAngle <= 0 || minSolarThresholdAngle >= 180) {
            throw new IllegalArgumentException("太阳遮蔽角必须在(0, 180)度范围内");
        }
        if (minWindowDuration < 0) {
            throw new IllegalArgumentException("最短窗口时长不能为负值");
        }

        this.minSolarThresholdAngle = minSolarThresholdAngle;
        this.minWindowDuration      = minWindowDuration;
    }

    @Override
    public String getName() {
        return "最小太阳遮蔽角约束";
    }

    @Override
    public String getExclusionInfo() {
        return String.format("最小太阳遮蔽角约束: %.2f度, 最短窗口: %.1f秒", minSolarThresholdAngle, minWindowDuration);
    }

    /**
     * 创建太阳排除事件检测器
     *
     * @param targetPropagator 目标传播器
     * @param timeIntervals    时间区间列表
     * @param winCallback
     * @return 事件检测器
     */
    @Override
    protected EventDetector createDetector(Propagator targetPropagator, List<TimeWindow> timeIntervals, TimeWinCallback winCallback) {
        return new SolarExclusionEventDetector(targetPropagator, minSolarThresholdAngle, maxIter, AdaptableInterval.of(maxCheck), threshold, createDefaultHandler(timeIntervals,winCallback));
    }

    public static void main(String[] args) {
        OrbitUtil.loadOrekitEnv();
        SolarExclusionEventHandler handler   = new SolarExclusionEventHandler(100, 60, 0.1, 90, 60);
        AbsoluteDate              startDate = new AbsoluteDate(2025, 4, 5, 0, 0, 0.0, TimeScalesFactory.getUTC());
        AbsoluteDate               endDate   = new AbsoluteDate(2025, 4, 5, 17, 0, 0.0, TimeScalesFactory.getUTC());



        // 定义轨道参数
        NumericalPropagator satellitePropagator = MainApp.getNumericalPropagator();

        // 定义目标TLE
        TLEPropagator targetPropagator = MainApp.getTlePropagator();
        handler.calculate(satellitePropagator, targetPropagator, new TimeWindow(startDate, endDate), new TimeWinCallback() {
            @Override
            public void notify(TimeWindow timeInterval) {
                log.warn("时间窗口: {} - {}", timeInterval.getStartDate(), timeInterval.getEndDate());
            }
        });
    }

}