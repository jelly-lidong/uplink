package org.aircas.orbit.visible.handler;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.visible.TimeWinCallback;
import org.orekit.propagation.Propagator;

/**
 * 事件检测器链节点基类
 *
 * <p>
 * 该类是事件检测器链模式的基类,用于构建可扩展的事件检测器链。每个节点可以处理特定类型的事件检测,
 * 并将结果传递给下一个节点继续处理。
 * </p>
 *
 * <p>
 * 主要功能:
 * <ul>
 * <li>定义事件检测器链的基本结构</li>
 * <li>支持多个事件检测器的串联</li>
 * <li>处理卫星和目标的轨道传播</li>
 * </ul>
 * </p>
 *
 * <p>
 * 使用场景:
 * <ul>
 * <li>空间目标观测任务规划</li>
 * <li>多条件约束的事件检测</li>
 * <li>复杂观测条件的组合判断</li>
 * </ul>
 * </p>
 *
 * <p>
 * 技术细节:
 * <ul>
 * <li>采用责任链设计模式</li>
 * <li>基于Orekit事件检测框架</li>
 * <li>支持可扩展的事件检测器链</li>
 * </ul>
 * </p>
 */

public abstract class EventDetectorHandler {

    @Getter
    private static final List<EventDetectorHandler> EVENT_DETECTOR_HANDLERS = new ArrayList<>();

    public static void add(EventDetectorHandler eventDetectorHandler) {
        EVENT_DETECTOR_HANDLERS.add(eventDetectorHandler);
    }


    public abstract String getName();

    public abstract String getExclusionInfo();

    /**
     * 处理事件检测
     *
     * @param satellitePropagator 卫星传播器
     * @param targetPropagator    目标传播器
     * @param timeWindow           时间区间
     * @return 处理后的时间区间
     */
    public abstract void calculate(Propagator satellitePropagator, Propagator targetPropagator, TimeWindow timeWindow, TimeWinCallback callback) throws RuntimeException;
}
