package org.aircas.resource.task;

import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.aircas.resource.constraint.Constraint;
import org.aircas.resource.model.TimeInterval;

import java.util.ArrayList;
import java.util.List;

public class ObservationTask {
    private final List<Constraint> constraints;
    private final Propagator satellitePropagator;
    private final Propagator targetPropagator;
    private final AbsoluteDate startDate;
    private final AbsoluteDate endDate;

    public ObservationTask(Propagator satellitePropagator, Propagator targetPropagator, AbsoluteDate startDate, AbsoluteDate endDate) {
        this.constraints = new ArrayList<>();
        this.satellitePropagator = satellitePropagator;
        this.targetPropagator = targetPropagator;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public List<TimeInterval> validate() {
        List<TimeInterval> validIntervals = new ArrayList<>();
        AbsoluteDate currentTime = startDate;

        while (currentTime.compareTo(endDate) < 0) {
            SpacecraftState state = satellitePropagator.propagate(currentTime);
            boolean allConstraintsSatisfied = true;

            for (Constraint constraint : constraints) {
                if (!constraint.isSatisfied(state, targetPropagator)) {
                    allConstraintsSatisfied = false;
                    break;
                }
            }

            if (allConstraintsSatisfied) {
                // 记录有效时间段
                validIntervals.add(new TimeInterval(currentTime, currentTime.shiftedBy(1.0))); // 记录当前时间段
            }

            currentTime = currentTime.shiftedBy(1.0); // 移动到下一个时间点
        }

        return validIntervals;
    }
}
