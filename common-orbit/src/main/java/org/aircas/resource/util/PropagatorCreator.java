package org.aircas.resource.util;

import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.files.ccsds.ndm.odm.KeplerianElements;
import org.orekit.forces.ForceModel;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

public class PropagatorCreator {
    /**
     * 通过轨道六根数获取数值传播器
     *
     * @return 数值传播器
     */
    public static NumericalPropagator createNumericalPropagator(KeplerianElements keplerianElements) {
        // 惯性参考系
        final Frame inertialFrame = FramesFactory.getEME2000();

        double a = keplerianElements.getA();
        double e = keplerianElements.getE();
        double i = keplerianElements.getI();
        double omega = keplerianElements.getPa();
        double raan = keplerianElements.getRaan();
        double lM = keplerianElements.getMeanMotion();
        AbsoluteDate initialDate = keplerianElements.getEpoch();
        PositionAngleType anomalyType = keplerianElements.getAnomalyType();
        // 创建开普勒轨道对象
        final Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, anomalyType,
                inertialFrame, initialDate, Constants.EGM96_EARTH_MU);

        System.out.println("偏心率: " + initialOrbit.getE());
        // 初始状态定义
        final SpacecraftState initialState = new SpacecraftState(initialOrbit);

        // 第3步: 配置数值传播器
        // ----------------------------------------------------
        // 自适应步长积分器，最小步长为0.001秒，最大步长为1000秒
        final double minStep = 0.001; // 最小步长，单位：秒
        final double maxStep = 1000.0; // 最大步长，单位：秒
        final double positionTolerance = 10.0; // 位置容差，单位：米
        OrbitType propagationType = OrbitType.KEPLERIAN; // 传播类型
        if (keplerianElements.getE() < 0.001) {
            /*
             * 当偏心率设为0时，出现了Jacobian matrix for type KEPLERIAN is singular with current orbit的错误。
             * 这通常是由于在数值传播器中设置的误差容限与轨道参数不匹配导致的。对于圆轨道（偏心率为0），需要特别注意误差容限的设置。
             * 解决方案
             * 调整误差容限：
             * 在创建NumericalPropagator时，误差容限需要根据轨道的特性进行调整。对于圆轨道，建议使用OrbitType.CARTESIAN来设置误差容限，因为它对圆轨道更稳定。
             */
            propagationType = OrbitType.CARTESIAN;
        }
        // 计算容差
        final double[][] tolerances =
                NumericalPropagator.tolerances(positionTolerance, initialOrbit, propagationType);
        // 创建自适应步长积分器
        final AdaptiveStepsizeIntegrator integrator =
                new DormandPrince853Integrator(minStep, maxStep, tolerances[0], tolerances[1]);

        // 创建数值传播器
        final NumericalPropagator propagator = new NumericalPropagator(integrator);
        propagator.setOrbitType(propagationType);

        // 力模型（简化为扰动重力场）
        final NormalizedSphericalHarmonicsProvider provider =
                GravityFieldFactory.getNormalizedProvider(10, 10);
        final ForceModel holmesFeatherstone =
                new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010,
                        true),
                        provider);

        // 将力模型添加到传播器中
        propagator.addForceModel(holmesFeatherstone);

        // 在传播器中设置初始状态
        propagator.setInitialState(initialState);

        // 设置步长处理器
        //propagator.getMultiplexer().add(60., new TutorialStepHandler());
        return propagator;
    }

}
