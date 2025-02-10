package org.aircas.orbit.transfer;

import java.io.IOException;
import org.aircas.orbit.util.OrbitUtil;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;


public class HohmannTransfer {

  private static final double MU_EARTH = 398600.4418e9; // Earth’s gravitational parameter (km^3/s^2)

  public static void main(String[] args) throws OrekitException, IOException {

    // 初始化 Orekit 环境
    OrbitUtil.loadOrekitEnv();

    // 1. 定义起始轨道（低地轨道 LEO）
    // 定义六根数
    double semiMajorAxis1 = 7000e3;    // 半长轴 (a)
    double eccentricity1  = 0.0;        // 偏心率 (e)
    double inclination1   = 0.0;         // 轨道倾角 (i)
    double raan           = 0.0;                 // 升交点赤经 (Ω)
    double argPerigee     = 0.0;           // 近地点幅角 (ω)
    double trueAnomaly    = 0.0;          // 真近点角 (ν)

    // 使用默认地心惯性坐标系
    Frame        frame       = FramesFactory.getICRF();
    AbsoluteDate initialDate = new AbsoluteDate(2022, 1, 1, 0, 0, 0.0, TimeScalesFactory.getUTC());

    // 使用六根数创建开普勒轨道
    KeplerianOrbit initialOrbit = new KeplerianOrbit(
        semiMajorAxis1,    // 半长轴
        eccentricity1,     // 偏心率
        inclination1,      // 轨道倾角
        raan,              // 升交点赤经
        argPerigee,        // 近地点幅角
        trueAnomaly,       // 真近点角
        PositionAngleType.MEAN,// 角度类型
        frame,             // 参考坐标系
        initialDate,       // 历元
        MU_EARTH);         // 引力常数

    System.out.println("Initial Orbit: " + initialOrbit);

    // 2. 定义目标轨道（静止轨道 GEO）
    double semiMajorAxis2 = 42164e3;    // 半长轴 (a)，静止轨道高度
    double eccentricity2  = 0.0;        // 偏心率 (e) 
    double inclination2   = 0.0;        // 轨道倾角 (i)
    double raan2          = 0.0;        // 升交点赤经 (Ω)
    double argPerigee2    = 0.0;        // 近地点幅角 (ω)
    double trueAnomaly2   = 0.0;        // 真近点角 (ν)

    // 使用六根数创建目标轨道
    KeplerianOrbit targetOrbit = new KeplerianOrbit(
        semiMajorAxis2,    // 半长轴
        eccentricity2,     // 偏心率  
        inclination2,      // 轨道倾角
        raan2,             // 升交点赤经
        argPerigee2,       // 近地点幅角
        trueAnomaly2,      // 真近点角
        PositionAngleType.MEAN, // 角度类型
        frame,             // 参考坐标系
        initialDate,       // 历元
        MU_EARTH);         // 引力常数

    System.out.println("Target Orbit: " + targetOrbit);

    double[] doubles = computeHohmanTransfer(initialOrbit, targetOrbit, frame);
    System.out.println("DeltaV1: " + doubles[0] + " m/s");
    System.out.println("DeltaV2: " + doubles[1] + " m/s");
    System.out.println("Transfer Time: " + doubles[2] + " s");
    System.out.println("Transfer Distance: " + doubles[3] + " m");
  }

  private static double[] computeHohmanTransfer(KeplerianOrbit initialOrbit, KeplerianOrbit targetOrbit, Frame frame) {
    // 3. 计算霍曼转移轨道参数
    // 半长轴 = (起始轨道半长轴 + 目标轨道半长轴) / 2
    double semiMajorAxisTransfer = (initialOrbit.getA() + targetOrbit.getA()) / 2.0;

    // 偏心率 = (目标轨道半长轴 - 起始轨道半长轴) / (目标轨道半长轴 + 起始轨道半长轴)
    double eccentricityTransfer = (targetOrbit.getA() - initialOrbit.getA()) / (initialOrbit.getA() + targetOrbit.getA());

    // 创建转移轨道（椭圆轨道）
    KeplerianOrbit transferOrbit = new KeplerianOrbit(
        semiMajorAxisTransfer,  // 半长轴
        eccentricityTransfer,   // 偏心率
        0.0,                    // 轨道倾角
        0.0,                    // 升交点赤经
        0.0,                    // 近地点幅角
        0.0,                    // 真近点角
        PositionAngleType.MEAN, // 角度类型
        frame,                  // 参考坐标系
        initialOrbit.getDate(),            // 历元
        MU_EARTH);              // 引力常数

    System.out.println("Transfer Orbit: " + transferOrbit);

    // 4. 计算增速（Δv）
    // 计算从起始轨道到转移轨道的增速
    double deltaV1 = computeDeltaV(initialOrbit, transferOrbit, true, frame);
    System.out.println("DeltaV1 (from initial orbit to transfer orbit): " + deltaV1 + " m/s");

    // 计算从转移轨道到目标轨道的增速
    double deltaV2 = computeDeltaV(transferOrbit, targetOrbit, false, frame);
    System.out.println("DeltaV2 (from transfer orbit to target orbit): " + deltaV2 + " m/s");
    // 5. 计算转移时间（t）
    // 计算转移时间（t）
    double t = (initialOrbit.getA() - targetOrbit.getA()) / (2.0 * Math.sqrt(MU_EARTH));
    System.out.println("Transfer Time: " + t + " s");

    // 6. 计算转移距离（d）
    // 计算转移距离（d）
    double d = (initialOrbit.getA() + targetOrbit.getA()) / 2.0 * t;
    System.out.println("Transfer Distance: " + d + " m");

    // 返回转移参数
    return new double[]{deltaV1, deltaV2, t, d};
  }

  // 计算复杂的增速（Δv）
  private static double computeDeltaV(KeplerianOrbit fromOrbit, KeplerianOrbit toOrbit, boolean isFirstDeltaV, Frame frame) {
    double r1 = fromOrbit.getA(); // 起始轨道的半长轴（对于圆形轨道，半长轴即轨道半径）
    double r2 = toOrbit.getA(); // 目标轨道的半长轴（对于圆形轨道，半长轴即轨道半径）

    // 计算起始轨道的速度
    double v1 = Math.sqrt(MU_EARTH / r1); // 圆形轨道的速度

    // 计算转移轨道的近地点或远地点的速度（使用椭圆轨道公式）
    double vTransfer = Math.sqrt(MU_EARTH * (2.0 / r1 - 1.0 / fromOrbit.getA()));

    // 计算目标轨道的速度
    double v2 = Math.sqrt(MU_EARTH / r2); // 圆形轨道的速度

    // 如果是第一次增速（从起始轨道到转移轨道）
    if (isFirstDeltaV) {
      return Math.abs(vTransfer - v1); // 从起始轨道到转移轨道的增速
    }
    // 否则是第二次增速（从转移轨道到目标轨道）
    else {
      return Math.abs(v2 - vTransfer); // 从转移轨道到目标轨道的增速
    }
  }
}

