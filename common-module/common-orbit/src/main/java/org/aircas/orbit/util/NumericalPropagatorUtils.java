package org.aircas.orbit.util;

import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.forces.ForceModel;
import org.orekit.forces.drag.DragForce;
import org.orekit.forces.drag.IsotropicDrag;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.OceanTides;
import org.orekit.forces.gravity.Relativity;
import org.orekit.forces.gravity.SolidTides;
import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.radiation.IsotropicRadiationSingleCoefficient;
import org.orekit.forces.radiation.SolarRadiationPressure;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.models.earth.atmosphere.DTM2000;
import org.orekit.models.earth.atmosphere.data.MarshallSolarActivityFutureEstimation;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数值轨道预报器工具类
 *
 * @author jelly-lidong
 * @date 2025-04-13
 */
public class NumericalPropagatorUtils {

    private static final Logger log = LoggerFactory.getLogger(NumericalPropagatorUtils.class);

    /**
     * 创建数值轨道预报器
     *
     * @param keplerianElements 开普勒轨道根数
     * @return 配置完成的数值预报器
     */
    public static NumericalPropagator createNumericalPropagator(KeplerianElements keplerianElements) {
        // 惯性参考系
        final Frame inertialFrame = FramesFactory.getEME2000();

        // 从开普勒根数中获取轨道参数
        double            a           = keplerianElements.getA();
        double            e           = keplerianElements.getE();
        double            i           = keplerianElements.getI();
        double            omega       = keplerianElements.getPa();
        double            raan        = keplerianElements.getRaan();
        double            lM          = keplerianElements.getMeanMotion();
        AbsoluteDate      initialDate = keplerianElements.getEpoch();
        PositionAngleType anomalyType = keplerianElements.getAnomalyType();

        // 创建开普勒轨道对象
        final Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, anomalyType, inertialFrame, initialDate, Constants.EGM96_EARTH_MU);

        // 初始状态定义
        final SpacecraftState initialState = new SpacecraftState(initialOrbit);

        // 配置数值传播器
        final double minStep           = 0.001;  // 最小步长，单位：秒
        final double maxStep           = 60.0;   // 最大步长，单位：秒
        final double positionTolerance = 10.0;  // 位置容差，单位：米

        // 选择传播类型
        OrbitType propagationType = OrbitType.KEPLERIAN;
        if (keplerianElements.getE() < 0.001) {
            propagationType = OrbitType.CARTESIAN;
        }

        // 计算容差
        final double[][] tolerances = NumericalPropagator.tolerances(positionTolerance, initialOrbit, propagationType);

        // 创建自适应步长积分器
        final DormandPrince853Integrator integrator = new DormandPrince853Integrator(minStep, maxStep, tolerances[0], tolerances[1]);

        // 创建数值传播器
        final NumericalPropagator propagator = new NumericalPropagator(integrator);
        propagator.setOrbitType(propagationType);

        // 添加各种摄动力模型
        try {
            // 1. 地球引力场摄动
            final NormalizedSphericalHarmonicsProvider provider = GravityFieldFactory.getNormalizedProvider(70, 70);
            final ForceModel holmesFeatherstone = new HolmesFeatherstoneAttractionModel(FramesFactory.getITRF(IERSConventions.IERS_2010, true), provider);
            propagator.addForceModel(holmesFeatherstone);

            // 2. 第三体引力摄动
            ThirdBodyAttraction moonAttraction = new ThirdBodyAttraction(CelestialBodyFactory.getMoon());
            propagator.addForceModel(moonAttraction);

            ThirdBodyAttraction sunAttraction = new ThirdBodyAttraction(CelestialBodyFactory.getSun());
            propagator.addForceModel(sunAttraction);
            // 3. 大气阻力
            final DTM2000 atmosphere = new DTM2000(
                new MarshallSolarActivityFutureEstimation(MarshallSolarActivityFutureEstimation.DEFAULT_SUPPORTED_NAMES.DEFAULT_SUPPORTED_RANGE, MarshallSolarActivityFutureEstimation.StrengthLevel.MEDIUM),
                CelestialBodyFactory.getSun(), FramesFactory.getITRF(IERSConventions.IERS_2010, true));

            // 设置航天器参数
            final double dragCoefficient = 2.2;  // 阻力系数
            final double crossSection    = 1.0;     // 迎风面积（平方米）

            final DragForce dragForce = new DragForce(atmosphere, new IsotropicDrag(crossSection, dragCoefficient));
            propagator.addForceModel(dragForce);

            // 4. 太阳辐射压力
            final double cr = 1.8;  // 反射系数
            final IsotropicRadiationSingleCoefficient spacecraft = new IsotropicRadiationSingleCoefficient(crossSection, cr);
            final SolarRadiationPressure solarRadiationPressure = new SolarRadiationPressure(CelestialBodyFactory.getSun(), Constants.WGS84_EARTH_EQUATORIAL_RADIUS, spacecraft);
            propagator.addForceModel(solarRadiationPressure);

            // 5. 固体潮汐
            final SolidTides solidTides = new SolidTides(IERSConventions.IERS_2010, FramesFactory.getITRF(IERSConventions.IERS_2010, true), TideSystem.PERMANENT_TIDE, CelestialBodyFactory.getMoon(),
                CelestialBodyFactory.getSun());
            propagator.addForceModel(solidTides);

            // 6. 海洋潮汐
            final OceanTides oceanTides = new OceanTides(IERSConventions.IERS_2010, FramesFactory.getITRF(IERSConventions.IERS_2010, true), 6, 6,  // 度数和阶数
                CelestialBodyFactory.getMoon(), CelestialBodyFactory.getSun());
            propagator.addForceModel(oceanTides);

            // 7. 相对论效应
            final Relativity relativity = new Relativity(Constants.EGM96_EARTH_MU);
            propagator.addForceModel(relativity);

        } catch (Exception e) {
            log.error("添加摄动力模型失败: {}", e.getMessage());
            e.printStackTrace();
        }

        // 设置初始状态
        propagator.setInitialState(initialState);

        return propagator;
    }

    /**
     * 开普勒轨道根数数据类
     */
    public static class KeplerianElements {

        private double            a;           // 半长轴 (米)
        private double            e;           // 偏心率
        private double            i;           // 轨道倾角 (弧度)
        private double            pa;          // 近地点幅角 (弧度)
        private double            raan;        // 升交点赤经 (弧度)
        private double            meanMotion;  // 平均运动 (弧度)
        private AbsoluteDate      epoch; // 历元
        private PositionAngleType anomalyType; // 角度类型

        // Constructor
        public KeplerianElements(double a, double e, double i, double pa, double raan, double meanMotion, AbsoluteDate epoch, PositionAngleType anomalyType) {
            this.a           = a;
            this.e           = e;
            this.i           = i;
            this.pa          = pa;
            this.raan        = raan;
            this.meanMotion  = meanMotion;
            this.epoch       = epoch;
            this.anomalyType = anomalyType;
        }

        // Getters
        public double getA() {
            return a;
        }

        public double getE() {
            return e;
        }

        public double getI() {
            return i;
        }

        public double getPa() {
            return pa;
        }

        public double getRaan() {
            return raan;
        }

        public double getMeanMotion() {
            return meanMotion;
        }

        public AbsoluteDate getEpoch() {
            return epoch;
        }

        public PositionAngleType getAnomalyType() {
            return anomalyType;
        }
    }
}
