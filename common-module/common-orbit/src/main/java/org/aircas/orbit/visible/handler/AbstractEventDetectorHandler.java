package org.aircas.orbit.visible.handler;

import static java.util.concurrent.TimeUnit.DAYS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeWindow;
import org.aircas.orbit.util.PropagatorCreator;
import org.aircas.orbit.visible.TimeWinCallback;
import org.hipparchus.ode.events.Action;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;

@Slf4j
public abstract class AbstractEventDetectorHandler extends EventDetectorHandler {

    protected final double maxCheck;  // 检查间隔
    protected final double threshold; // 检测阈值
    protected final int maxIter;     // 最大迭代次数
    protected final double minWindowDuration; // 最短窗口时长（秒）

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int QUEUE_CAPACITY = 1000;

    protected AbstractEventDetectorHandler(double maxCheck, double threshold, int maxIter, double minWindowDuration) {
        this.maxCheck = maxCheck;
        this.threshold = threshold;
        this.maxIter = maxIter;

        if (minWindowDuration < 0) {
            throw new IllegalArgumentException("最短窗口时长不能为负值");
        }
        this.minWindowDuration = minWindowDuration;
    }

    /**
     * 并行计算单个时间窗口
     */
    private class WindowCalculationTask implements Runnable {

        private final Propagator satellitePropagator;
        private final Propagator targetPropagator;
        private final TimeWindow timeWindow;
        private final TimeWinCallback winCallback;

        public WindowCalculationTask(Propagator satellitePropagator, Propagator targetPropagator, TimeWindow timeWindow, TimeWinCallback winCallback) {
            this.winCallback = winCallback;
            this.satellitePropagator = satellitePropagator;//PropagatorCreator.clonePropagator(satellitePropagator);
            this.targetPropagator = targetPropagator; //PropagatorCreator.clonePropagator(targetPropagator);
            this.timeWindow = timeWindow;
        }


        @Override
        public void run() {
            List<TimeWindow> tmpWins = new ArrayList<>();
            try {
                satellitePropagator.clearEventsDetectors();

                AbsoluteDate startDate = timeWindow.getStartDate();
                AbsoluteDate endDate = timeWindow.getEndDate();

                // 创建检测器
                EventDetector detector = createDetector(targetPropagator, tmpWins, winCallback);

                // 在传播之前验证初始状态
                SpacecraftState initialState = satellitePropagator.getInitialState();
                validateState(initialState, "初始状态");

                // 检查初始状态
                if (detector.g(initialState) > 0) {
                    TimeWindow newInterval = new TimeWindow();
                    newInterval.setStartDate(startDate);
                    tmpWins.add(newInterval);
                }

                satellitePropagator.addEventDetector(detector);

                // 传播到结束时间，并在过程中验证状态
                SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);
                validateState(finalState, "最终状态");

                // 安全地设置结束时间
                if (detector.g(finalState) > 0 && !tmpWins.isEmpty()) {
                    TimeWindow timeWindow = tmpWins.get(tmpWins.size() - 1);
                    if (timeWindow.getEndDate() == null) {
                        timeWindow.setEndDate(finalState.getDate());
                        if (isValidWindow(timeWindow)) {
                            winCallback.notify(timeWindow);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("计算时间窗口发生错误: {}", e.getMessage());
                // 不抛出异常，返回空结果
                tmpWins.clear();
            }
        }

        /**
         * 验证航天器状态
         */
        private void validateState(SpacecraftState state, String phase) {
            try {
                Orbit orbit = state.getOrbit();
                if (orbit instanceof KeplerianOrbit) {
                    KeplerianOrbit kep = (KeplerianOrbit) orbit;
                    double ecc = kep.getE();
                    if (ecc < 0 || Double.isNaN(ecc)) {
                        throw new IllegalStateException(String.format("%s的偏心率无效: %f", phase, ecc));
                    }
                }
            } catch (Exception e) {
                log.error("状态验证失败: {}", e.getMessage());
                throw new RuntimeException("状态验证失败", e);
            }
        }
    }


    @Override
    public void calculate(Propagator satellitePropagator, Propagator targetPropagator, TimeWindow inputTimeWindow, TimeWinCallback callback) throws RuntimeException {
        ExecutorService executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_CAPACITY), new ThreadPoolExecutor.CallerRunsPolicy());
        // 创建所有任务
        // 计算每个任务的窗口数量,按照天数划分
        int timeUnit = 1;
        AbsoluteDate startDate = inputTimeWindow.getStartDate();
        AbsoluteDate endDate = startDate.shiftedBy(timeUnit, TimeUnit.DAYS);

        List<Future<?>> futures = new ArrayList<>();
        while (endDate.isBefore(inputTimeWindow.getEndDate())) {
            TimeWindow interval = new TimeWindow();
            interval.setStartDate(startDate);
            interval.setEndDate(endDate);
            futures.add( executorService.submit(new WindowCalculationTask(satellitePropagator, targetPropagator, interval, callback)));
            startDate = endDate;
            endDate = startDate.shiftedBy(timeUnit, TimeUnit.DAYS);
        }

        if (endDate.isAfterOrEqualTo(inputTimeWindow.getEndDate())) {
            TimeWindow interval = new TimeWindow();
            interval.setStartDate(startDate);
            interval.setEndDate(inputTimeWindow.getEndDate());
            futures.add(executorService.submit(new WindowCalculationTask(satellitePropagator, targetPropagator, interval, callback)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
//        log.warn("完成所有窗口计算：【{}】 ", getName());
        executorService.shutdown();
    }

    /**
     * 创建具体的事件检测器
     */
    protected abstract EventDetector createDetector(Propagator targetPropagator, List<TimeWindow> timeIntervals, TimeWinCallback winCallback);

    /**
     * 创建默认的事件处理器
     */
    protected EventHandler createDefaultHandler(List<TimeWindow> timeIntervals, TimeWinCallback winCallback) {
        return (s, detector, increasing) -> {
            //log.info("{},increasing:{}",getName(),increasing);
            if (increasing) {
                TimeWindow newInterval = new TimeWindow();
                newInterval.setStartDate(s.getDate());
                timeIntervals.add(newInterval);
            } else if (!timeIntervals.isEmpty()) {
                TimeWindow lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                lastInterval.setEndDate(s.getDate());
                if (isValidWindow(lastInterval)) {
                    winCallback.notify(lastInterval);
                }
            }
            return Action.CONTINUE;
        };
    }

    /**
     * 验证时间窗口是否满足最短持续时间要求
     *
     * @param interval 时间区间
     * @return 是否满足要求
     */
    protected boolean isValidWindow(TimeWindow interval) {
        if (interval == null || interval.getStartDate() == null || interval.getEndDate() == null) {
            return false;
        }
        double duration = interval.getEndDate().durationFrom(interval.getStartDate());
        return duration >= minWindowDuration;
    }


}