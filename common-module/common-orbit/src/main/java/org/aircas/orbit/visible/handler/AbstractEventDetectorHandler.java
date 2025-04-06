package org.aircas.orbit.visible.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aircas.orbit.model.TimeInterval;
import org.hipparchus.ode.ODEIntegrator;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.forces.ForceModel;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;

@Slf4j
public abstract class AbstractEventDetectorHandler extends EventDetectorHandler {

    protected final double maxCheck;  // 检查间隔
    protected final double threshold; // 检测阈值
    protected final int    maxIter;     // 最大迭代次数
    protected final double minWindowDuration; // 最短窗口时长（秒）

    private static final int  CORE_POOL_SIZE  = Runtime.getRuntime().availableProcessors();
    private static final int  MAX_POOL_SIZE   = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int  QUEUE_CAPACITY  = 1000;

    // 创建线程池
    private static final ExecutorService executorService = new ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(QUEUE_CAPACITY),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    protected AbstractEventDetectorHandler(double maxCheck,
        double threshold,
        int maxIter,
        double minWindowDuration) {
        this.maxCheck  = maxCheck;
        this.threshold = threshold;
        this.maxIter   = maxIter;

        if (minWindowDuration < 0) {
            throw new IllegalArgumentException("最短窗口时长不能为负值");
        }
        this.minWindowDuration = minWindowDuration;
    }

    /**
     * 并行计算单个时间窗口
     */
    private class WindowCalculationTask implements Callable<List<TimeInterval>> {

        private final Propagator   satellitePropagator;
        private final Propagator   targetPropagator;
        private final TimeInterval interval;

        public WindowCalculationTask(Propagator satellitePropagator,
            Propagator targetPropagator,
            TimeInterval interval) {
            try {
                this.satellitePropagator = createNewPropagator(satellitePropagator);
                this.targetPropagator    = createNewPropagator(targetPropagator);
                this.interval            = interval;
            } catch (Exception e) {
                throw new RuntimeException("创建传播器失败", e);
            }
        }

        /**
         * 创建新的传播器实例
         */
        private Propagator createNewPropagator(Propagator original) {
            try {
                if (original instanceof NumericalPropagator) {
                    // 获取初始状态
                    SpacecraftState initialState = original.getInitialState();

                    // 创建新的数值传播器，使用默认积分器
                    NumericalPropagator newPropagator = new NumericalPropagator(createDefaultIntegrator());

                    // 设置初始状态
                    newPropagator.setInitialState(initialState);

                    // 复制所有力学模型
                    for (ForceModel force : ((NumericalPropagator) original).getAllForceModels()) {
                        newPropagator.addForceModel(force);
                    }

                    // 设置轨道类型和位置角类型
                    newPropagator.setOrbitType(initialState.getOrbit().getType());

                    return newPropagator;

                } else if (original instanceof KeplerianPropagator) {
                    // 处理开普勒传播器
                    return new KeplerianPropagator(original.getInitialState().getOrbit());

                } else {
                    // 对于其他类型的传播器，创建基于初始状态的开普勒传播器
                    return new KeplerianPropagator(original.getInitialState().getOrbit());
                }
            } catch (Exception e) {
                log.error("创建传播器失败: {}", e.getMessage());
                throw new RuntimeException("创建传播器失败", e);
            }
        }

        /**
         * 创建默认积分器
         */
        private ODEIntegrator createDefaultIntegrator() {
            // 使用DormandPrince853积分器，这是一个常用的高精度积分器
            double minStep               = 0.001;    // 最小步长（秒）
            double maxStep               = 300.0;    // 最大步长（秒）
            double scalAbsoluteTolerance = 1.0e-8;
            double scalRelativeTolerance = 1.0e-8;

            return new DormandPrince853Integrator(
                minStep, maxStep,
                scalAbsoluteTolerance, scalRelativeTolerance);
        }

        @Override
        public List<TimeInterval> call() {
            List<TimeInterval> results = new ArrayList<>();
            try {
                satellitePropagator.clearEventsDetectors();

                AbsoluteDate startDate = interval.getStartDate();
                AbsoluteDate endDate   = interval.getEndDate();

                // 创建检测器
                EventDetector detector = createDetector(targetPropagator, results);

                // 在传播之前验证初始状态
                SpacecraftState initialState = satellitePropagator.getInitialState();
                validateState(initialState, "初始状态");

                // 检查初始状态
                if (detector.g(initialState) > 0) {
                    TimeInterval newInterval = new TimeInterval();
                    newInterval.setStartDate(startDate);
                    results.add(newInterval);
                }

                satellitePropagator.addEventDetector(detector);

                // 传播到结束时间，并在过程中验证状态
                SpacecraftState finalState = satellitePropagator.propagate(startDate, endDate);
                validateState(finalState, "最终状态");

                // 安全地设置结束时间
                if (!results.isEmpty()) {
                    results.get(results.size() - 1).setEndDate(finalState.getDate());
                } else if (detector.g(finalState) > 0) {
                    TimeInterval newInterval = new TimeInterval();
                    newInterval.setStartDate(startDate);
                    newInterval.setEndDate(finalState.getDate());
                    results.add(newInterval);
                }

            } catch (Exception e) {
                log.error("计算时间窗口发生错误: {}", e.getMessage());
                // 不抛出异常，返回空结果
                results.clear();
            }
            return results;
        }

        /**
         * 验证航天器状态
         */
        private void validateState(SpacecraftState state, String phase) {
            try {
                Orbit orbit = state.getOrbit();
                if (orbit instanceof KeplerianOrbit) {
                    KeplerianOrbit kep = (KeplerianOrbit) orbit;
                    double         ecc = kep.getE();
                    if (ecc < 0 || Double.isNaN(ecc)) {
                        throw new IllegalStateException(
                            String.format("%s的偏心率无效: %f", phase, ecc));
                    }
                }
            } catch (Exception e) {
                log.error("状态验证失败: {}", e.getMessage());
                throw new RuntimeException("状态验证失败", e);
            }
        }
    }


    @Override
    public List<TimeInterval> calculate(Propagator satellitePropagator,
        Propagator targetPropagator,
        List<TimeInterval> intervals) {
        try {
            // 创建所有任务
            List<Callable<List<TimeInterval>>> tasks = intervals.stream()
                .map(interval -> new WindowCalculationTask(
                    satellitePropagator,
                    targetPropagator,
                    interval))
                .collect(Collectors.toList());

            // 并行执行所有任务
            List<Future<List<TimeInterval>>> futures = executorService.invokeAll(tasks);

            // 收集结果
            List<TimeInterval> results = new ArrayList<>();
            for (Future<List<TimeInterval>> future : futures) {
                try {
                    List<TimeInterval> intervalList = future.get();
                    results.addAll(intervalList);
                } catch (ExecutionException e) {
                    log.error("获取计算结果时发生错误: {}", e.getMessage(), e);
                }
            }

            // 按时间排序
            results.sort((a, b) -> a.getStartDate().compareTo(b.getStartDate()));

            // 过滤短时间窗口
            return filterShortWindows(results);

        } catch (InterruptedException e) {
            log.error("并行计算被中断: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    /**
     * 创建具体的事件检测器
     */
    protected abstract EventDetector createDetector(Propagator targetPropagator,
        List<TimeInterval> timeIntervals);

    /**
     * 创建默认的事件处理器
     */
    protected EventHandler createDefaultHandler(List<TimeInterval> timeIntervals) {
        return (s, detector, increasing) -> {
            if (increasing) {
                TimeInterval newInterval = new TimeInterval();
                newInterval.setStartDate(s.getDate());
                timeIntervals.add(newInterval);
            } else if (!timeIntervals.isEmpty()) {
                TimeInterval lastInterval = timeIntervals.get(timeIntervals.size() - 1);
                lastInterval.setEndDate(s.getDate());
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
    protected boolean isWindowDurationValid(TimeInterval interval) {
        if (interval == null || interval.getStartDate() == null || interval.getEndDate() == null) {
            return false;
        }
        double duration = interval.getEndDate().durationFrom(interval.getStartDate());
        return duration >= minWindowDuration;
    }

    /**
     * 过滤掉不满足最短持续时间要求的时间窗口
     *
     * @param intervals 时间区间列表
     * @return 过滤后的时间区间列表
     */
    protected List<TimeInterval> filterShortWindows(List<TimeInterval> intervals) {
        List<TimeInterval> filteredIntervals = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            if (isWindowDurationValid(interval)) {
                filteredIntervals.add(interval);
            } else {
                log.debug("过滤掉短时间窗口: {} -> {}",
                    interval.getStartDate(), interval.getEndDate());
            }
        }
        return filteredIntervals;
    }

}