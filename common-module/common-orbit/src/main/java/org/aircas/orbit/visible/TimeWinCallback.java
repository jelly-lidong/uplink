package org.aircas.orbit.visible;

import org.aircas.orbit.model.TimeWindow;

@FunctionalInterface
public interface TimeWinCallback {

    /**
     * 回调方法，用于通知外部当前约束计算出的有效的时间窗口
     * @param timeInterval 有效的时间窗口
     */
    void notify(TimeWindow timeInterval);

}
