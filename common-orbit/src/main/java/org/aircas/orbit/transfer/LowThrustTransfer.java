package org.aircas.orbit.transfer;

import org.aircas.orbit.util.OrbitUtil;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * 低推力轨道转移优化
 * 特点：
 * 1. 适用于电推进等低推力推进系统
 * 2. 采用螺旋式轨道转移策略
 * 3. 通过最优控制理论优化推力方向
 * 4. 相比化学推进：
 *    - 总燃料消耗更少
 *    - 转移时间更长
 *    - 轨道改变更平滑
 */
public class LowThrustTransfer {
    private static final double MU_EARTH = 398600.4418e9; // 地球引力常数 (m^3/s^2)
    private static final double G0 = 9.80665; // 标准重力加速度 (m/s^2)
    
    // 推进系统参数
    private static final double THRUST = 0.5; // 推力大小 (N)
    private static final double ISP = 3000.0; // 比冲 (s)
    private static final double MASS_INITIAL = 1000.0; // 初始质量 (kg)

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

        // 计算低推力转移参数
        TransferResult result = computeLowThrustTransfer(initialOrbit, targetOrbit);
        
        // 输出结果
        System.out.println("低推力转移结果：");
        System.out.printf("总推进剂消耗: %.2f kg%n", result.propellantMass);
        System.out.printf("转移时间: %.2f days%n", result.transferTime / (24 * 3600));
        System.out.printf("平均加速度: %.6f m/s²%n", result.averageAcceleration);
        System.out.printf("总速度变化: %.2f m/s%n", result.deltaV);
        System.out.printf("轨道圈数: %.1f%n", result.numberOfOrbits);
    }

    public static TransferResult computeLowThrustTransfer(KeplerianOrbit initialOrbit, KeplerianOrbit targetOrbit) {
        double r1 = initialOrbit.getA();
        double r2 = targetOrbit.getA();
        
        // 计算平均加速度
        double averageAcceleration = THRUST / MASS_INITIAL;
        
        // 计算理想速度变化（ΔV）
        double deltaV = Math.sqrt(MU_EARTH / r1) * (Math.sqrt(2 * r2 / (r1 + r2)) - 1);
        
        // 计算推进剂消耗
        double propellantMass = MASS_INITIAL * (1 - Math.exp(-deltaV / (ISP * G0)));
        
        // 计算转移时间
        // 使用近似公式：t = ΔV / a，其中a是平均加速度
        double transferTime = deltaV / averageAcceleration;
        
        // 计算转移过程中的轨道圈数
        // 使用近似公式：N = 2π * (r2 - r1) / (λ * r1)
        // 其中λ是每圈轨道半径的相对变化率
        double lambda = 2 * Math.PI * averageAcceleration / Math.sqrt(MU_EARTH / Math.pow(r1, 3));
        double numberOfOrbits = 2 * Math.PI * (r2 - r1) / (lambda * r1);
        
        return new TransferResult(
            propellantMass,
            transferTime,
            averageAcceleration,
            deltaV,
            numberOfOrbits
        );
    }
    
    /**
     * 转移结果数据类
     */
    private static class TransferResult {
        final double propellantMass;      // 推进剂消耗 (kg)
        final double transferTime;         // 转移时间 (s)
        final double averageAcceleration;  // 平均加速度 (m/s²)
        final double deltaV;               // 总速度变化 (m/s)
        final double numberOfOrbits;       // 转移轨道圈数

        TransferResult(double propellantMass, double transferTime, 
                      double averageAcceleration, double deltaV, double numberOfOrbits) {
            this.propellantMass = propellantMass;
            this.transferTime = transferTime;
            this.averageAcceleration = averageAcceleration;
            this.deltaV = deltaV;
            this.numberOfOrbits = numberOfOrbits;
        }
    }
} 