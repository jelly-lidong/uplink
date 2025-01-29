package org.aircas.orbit.task;


import com.common.model.entity.resource.Satellite;
import com.common.model.entity.task.TaskConstraint;
import com.common.model.entity.task.TaskInfo;
import java.util.List;
import org.aircas.orbit.util.PropagatorCreator;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.springframework.util.CollectionUtils;

public class SpaceTargetObservingTask implements Runnable {

  private final TaskInfo taskInfo;

  public SpaceTargetObservingTask(TaskInfo taskInfo) {
    this.taskInfo = taskInfo;
  }

  @Override
  public void run() {

    List<Satellite>      taskSatellites   = taskInfo.getTaskSatellites();
    if (CollectionUtils.isEmpty(taskSatellites)) {
      return;
    }
    List<Satellite>      targetSatellites = taskInfo.getTargetSatellites();
    if (CollectionUtils.isEmpty(targetSatellites)) {
      return;
    }
    AbsoluteDate  startTime = new AbsoluteDate(taskInfo.getStartTime().toString(), TimeScalesFactory.getUTC());
    AbsoluteDate  end = new AbsoluteDate(taskInfo.getEndTime().toString(), TimeScalesFactory.getUTC());

    Satellite satellite = taskSatellites.get(0);
    NumericalPropagator taskSatellitePropagator = PropagatorCreator.createNumericalPropagator(satellite);
    List<TaskConstraint> taskConstraints  = taskInfo.getTaskConstraints();
    for (Satellite targetSatellite : targetSatellites) {
      NumericalPropagator propagator = PropagatorCreator.createNumericalPropagator(targetSatellite);


    }



  }
}
