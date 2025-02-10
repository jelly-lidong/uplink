package org.aircas.orbit.transfer;

import org.aircas.orbit.util.OrbitUtil;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * 低推理轨道转移算法：双椭圆转移
 */
public class BiEllipticTransfer {
    private static final double MU_EARTH = 398600.4418e9; // 地球引力常数 (m^3/s^2)

    public static void main(String[] args) throws Exception {
        // 初始化 Orekit 环境
        OrbitUtil.loadOrekitEnv();

        // 定义参考坐标系和初始时间
        Frame frame = FramesFactory.getICRF();
        AbsoluteDate initialDate = new AbsoluteDate(2022, 1, 1, 0, 0, 0.0, TimeScalesFactory.getUTC());

        // 创建初始轨道 (LEO)
        KeplerianOrbit initialOrbit = new KeplerianOrbit(
            7000e3,     // 半长轴
            0.0,        // 偏心率
            0.0,        // 倾角
            0.0,        // 升交点赤经
            0.0,        // 近地点幅角
            0.0,        // 真近点角
            PositionAngleType.MEAN,
            frame,
            initialDate,
            MU_EARTH
        );

        // 创建目标轨道 (GEO)
        KeplerianOrbit targetOrbit = new KeplerianOrbit(
            42164e3,    // 半长轴
            0.0,        // 偏心率
            0.0,        // 倾角
            0.0,        // 升交点赤经
            0.0,        // 近地点幅角
            0.0,        // 真近点角
            PositionAngleType.MEAN,
            frame,
            initialDate,
            MU_EARTH
        );

        // 计算双椭圆转移参数
        double[] transferParams = computeBiEllipticTransfer(initialOrbit, targetOrbit, frame);
        
        System.out.println("双椭圆转移结果：");
        System.out.printf("第一次增速 ΔV1: %.2f m/s%n", transferParams[0]);
        System.out.printf("第二次增速 ΔV2: %.2f m/s%n", transferParams[1]);
        System.out.printf("第三次增速 ΔV3: %.2f m/s%n", transferParams[2]);
        System.out.printf("总转移时间: %.2f s%n", transferParams[3]);
        System.out.printf("总转移距离: %.2f m%n", transferParams[4]);
    }

    public static double[] computeBiEllipticTransfer(KeplerianOrbit initialOrbit, KeplerianOrbit targetOrbit, Frame frame) {
        double r1 = initialOrbit.getA();  // 初始轨道半长轴
        double r2 = targetOrbit.getA();   // 目标轨道半长轴
        double rB = 2.5 * r2;             // 中间轨道远点距离（通常取为目标轨道半长轴的2-3倍）

        // 创建第一个转移椭圆轨道（从初始轨道到中间轨道）
        KeplerianOrbit firstTransferOrbit = new KeplerianOrbit(
            (r1 + rB) / 2.0,              // 半长轴
            (rB - r1) / (rB + r1),        // 偏心率
            0.0, 0.0, 0.0, 0.0,
            PositionAngleType.MEAN,
            frame,
            initialOrbit.getDate(),
            MU_EARTH
        );

        // 创建第二个转移椭圆轨道（从中间轨道到目标轨道）
        KeplerianOrbit secondTransferOrbit = new KeplerianOrbit(
            (r2 + rB) / 2.0,              // 半长轴
            (rB - r2) / (rB + r2),        // 偏心率
            0.0, 0.0, 0.0, 0.0,
            PositionAngleType.MEAN,
            frame,
            initialOrbit.getDate(),
            MU_EARTH
        );

        // 计算三次速度变化
        double v1 = Math.sqrt(MU_EARTH / r1);  // 初始轨道速度
        double v2 = Math.sqrt(MU_EARTH * (2.0 / r1 - 2.0 / (r1 + rB)));  // 第一个转移轨道近地点速度
        double v3 = Math.sqrt(MU_EARTH * (2.0 / rB - 2.0 / (r1 + rB)));  // 第一个转移轨道远地点速度
        double v4 = Math.sqrt(MU_EARTH * (2.0 / rB - 2.0 / (r2 + rB)));  // 第二个转移轨道远地点速度
        double v5 = Math.sqrt(MU_EARTH * (2.0 / r2 - 2.0 / (r2 + rB)));  // 第二个转移轨道近地点速度
        double v6 = Math.sqrt(MU_EARTH / r2);  // 最终轨道速度

        // 计算三次增速
        double deltaV1 = Math.abs(v2 - v1);
        double deltaV2 = Math.abs(v4 - v3);
        double deltaV3 = Math.abs(v6 - v5);

        // 计算转移时间
        double t1 = Math.PI * Math.sqrt(Math.pow((r1 + rB) / 2.0, 3) / MU_EARTH);  // 第一段转移时间
        double t2 = Math.PI * Math.sqrt(Math.pow((r2 + rB) / 2.0, 3) / MU_EARTH);  // 第二段转移时间
        double totalTime = t1 + t2;

        // 计算总转移距离（近似值）
        double totalDistance = Math.PI * (firstTransferOrbit.getA() + secondTransferOrbit.getA());

        return new double[]{deltaV1, deltaV2, deltaV3, totalTime, totalDistance};
    }
} 