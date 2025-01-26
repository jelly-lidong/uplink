package org.aircas.resource.service;

import java.util.List;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.aircas.resource.model.GroundStation;
import org.aircas.resource.model.OrbitalParameters;
import org.aircas.resource.model.SatelliteState;

public interface SatelliteVisibilityService {

  /**
   * 计算指定时间范围内满足仰角要求的卫星状态
   *
   * @param orbitalParams 卫星轨道参数
   * @param station       地面站参数
   * @param startDate     开始时间
   * @param endDate       结束时间
   * @param frame         参考框架
   * @param minElevation  最小仰角(度)
   * @param maxElevation  最大仰角(度)
   * @return 满足仰角要求的卫星状态列表
   */
  List<SatelliteState> calculateVisibleStates(
      OrbitalParameters orbitalParams,
      GroundStation station,
      AbsoluteDate startDate,
      AbsoluteDate endDate,
      Frame frame,
      double minElevation,
      double maxElevation
  );
}
