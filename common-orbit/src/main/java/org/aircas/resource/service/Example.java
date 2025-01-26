package org.aircas.resource.service;

import java.util.List;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.aircas.resource.model.GroundStation;
import org.aircas.resource.model.OrbitalParameters;
import org.aircas.resource.model.SatelliteState;
import org.aircas.resource.service.impl.SatelliteVisibilityServiceImpl;
import org.aircas.resource.util.OrbitUtil;


public class Example {

  public static void main(String[] args) {
    // 初始化Orekit数据（确保在使用前已经初始化）
    OrbitUtil.loadOrekitEnv();
    // 创建服务实例
    // IERS_2010是国际地球自转和参考系统服务(IERS)发布的2010年地球定向参数标准
    // 用于定义地球固连参考系(ITRF)与天球参考系(ICRF)之间的转换关系
    // 包含岁差、章动、极移和UT1-UTC等地球定向参数

    // 可用的参考系统:

    // 1. EME2000 (J2000) - 惯性参考系
    // 基于2000.0历元的平赤道和平春分点,常用于轨道计算
    Frame eme2000Frame = FramesFactory.getEME2000();

//    // 2. GCRF - 天球参考系
//    // 国际天球参考系,用于高精度天文定位
//    Frame gcrfFrame = FramesFactory.getGCRF();
//
//    // 3. CIRF - 瞬时天球参考系
//    // 考虑了岁差和章动效应的瞬时参考系
//    Frame cirfFrame = FramesFactory.getCIRF(IERSConventions.IERS_2010, true);
//
//    // 4. TIRF - 瞬时地球参考系
//    // 考虑了极移的瞬时地固系
//    Frame tirfFrame = FramesFactory.getTIRF(IERSConventions.IERS_2010, true);
//
//    // 5. ICRF - 国际天球参考系
//    // 基本惯性系,定义了约300个河外射电源的位置
//    Frame icrfFrame = FramesFactory.getICRF();
//
//    // 6. MOD - 历元平黄道参考系
//    // 考虑了岁差效应的平黄道系
//    Frame modFrame = FramesFactory.getMOD(IERSConventions.IERS_2010);

    // 7. TOD - 瞬时黄道参考系
    // 考虑了岁差和章动的真实黄道系
//    Frame todFrame = FramesFactory.getTOD(IERSConventions.IERS_2010);
//    Frame                      earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
    SatelliteVisibilityService service = new SatelliteVisibilityServiceImpl(eme2000Frame);

    // 创建轨道参数
    OrbitalParameters orbitalParams = new OrbitalParameters(
        7000000.0, // 半长轴(米)
        0.001,     // 偏心率
        98.0,      // 倾角(度)
        90.0,      // 近地点幅角(度)
        0.0,       // 升交点赤经(度)
        0.0        // 平近点角(度)
    );

    // 创建地面站
    GroundStation station = new GroundStation(
        39.9,  // 纬度(度)
        116.3, // 经度(度)
        43.0   // 高度(米)
    );

    // 设置时间范围
    AbsoluteDate startDate = new AbsoluteDate();
    AbsoluteDate endDate   = startDate.shiftedBy(24 * 3600.0 * 7); // 24小时后

    // 计算可见性
    List<SatelliteState> states = service.calculateVisibleStates(
        orbitalParams,
        station,
        startDate,
        endDate,
        eme2000Frame,
        0, // 最小仰角
        50  // 最大仰角
    );

    // 输出结果
    for (SatelliteState state : states) {
      System.out.printf("时间: %s\n", state.getDate());
      System.out.printf("仰角: %.2f°\n", state.getElevation());
      System.out.printf("位置(m): X=%.2f, Y=%.2f, Z=%.2f\n",
          state.getPosition().getX(),
          state.getPosition().getY(),
          state.getPosition().getZ());
      System.out.printf("速度(m/s): Vx=%.2f, Vy=%.2f, Vz=%.2f\n\n",
          state.getVelocity().getX(),
          state.getVelocity().getY(),
          state.getVelocity().getZ());
    }
  }
}
