package org.aircas.orbit.util;

import com.common.model.entity.resource.Satellite;
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
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

public class PropagatorCreator {


  public static NumericalPropagator createNumericalPropagator(Satellite satellite) {
    KeplerianElements keplerianElements = new KeplerianElements();
    keplerianElements.setA(satellite.getArgumentOfPerigee());
    keplerianElements.setE(satellite.getEccentricity());
    keplerianElements.setI(Math.toRadians(satellite.getInclination()));
    keplerianElements.setPa(Math.toRadians(satellite.getArgumentOfPerigee()));
    keplerianElements.setRaan(Math.toRadians(satellite.getRightAscension()));
    keplerianElements.setMeanMotion(Math.toRadians(satellite.getMeanAnomaly()));
    keplerianElements.setEpoch(new AbsoluteDate(satellite.getEpochTime().toString(), TimeScalesFactory.getUTC()));
    keplerianElements.setAnomalyType(PositionAngleType.MEAN);
    return createNumericalPropagator(keplerianElements);
  }

  /**
   * 通过轨道六根数获取数值传播器
   * 数值传播器的工作原理:
   * 1. 首先根据六根数(半长轴a、偏心率e、轨道倾角i、近地点幅角ω、升交点赤经Ω、平近点角M)构建开普勒轨道
   * 2. 基于开普勒轨道创建初始航天器状态
   * 3. 配置数值传播器,包括:
   *    - 设置自适应步长积分器(最小步长、最大步长、位置容差)
   *    - 选择合适的轨道类型(偏心率接近0时使用CARTESIAN类型)
   *    - 添加重力场等摄动力模型
   * 4. 通过数值积分方法求解轨道运动方程,得到任意时刻的轨道状态
   *
   * @param keplerianElements 开普勒轨道六根数
   * @return 数值传播器
   */
  public static NumericalPropagator createNumericalPropagator(KeplerianElements keplerianElements) {
    // 惯性参考系
    final Frame inertialFrame = FramesFactory.getEME2000();

    double            a           = keplerianElements.getA();
    double            e           = keplerianElements.getE();
    double            i           = keplerianElements.getI();
    double            omega       = keplerianElements.getPa();
    double            raan        = keplerianElements.getRaan();
    double            lM          = keplerianElements.getMeanMotion();
    AbsoluteDate      initialDate = keplerianElements.getEpoch();
    PositionAngleType anomalyType = keplerianElements.getAnomalyType();
    // 创建开普勒轨道对象
    final Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, anomalyType,
        inertialFrame, initialDate, Constants.EGM96_EARTH_MU);

    // 初始状态定义
    final SpacecraftState initialState = new SpacecraftState(initialOrbit);

    // 第3步: 配置数值传播器
    // ----------------------------------------------------
    // 自适应步长积分器，最小步长为0.001秒，最大步长为60秒
    // 步长设置的作用:
    // 1. 最小步长(minStep)
    //    - 限制积分器使用的最小时间间隔
    //    - 防止步长过小导致计算量过大
    //    - 避免数值不稳定性
    //    - 通常设置为毫秒级(0.001秒)
    //
    // 2. 最大步长(maxStep)
    //    - 限制积分器使用的最大时间间隔
    //    - 确保计算精度不会因步长过大而降低
    //    - 保证能捕捉到轨道快速变化
    //    - 通常设置为几十秒到几分钟(如60秒)
    //
    // 积分器会在这两个限制之间自适应调整步长:
    // - 轨道变化剧烈时使用较小步长
    // - 轨道平稳时使用较大步长
    // 这种自适应机制可以在保证精度的同时提高计算效率
    final double minStep           = 0.001; // 最小步长，单位：秒
    final double maxStep           = 60.0; // 最大步长，单位：秒
    // 位置容差(positionTolerance)用于控制数值积分的精度:
    // 1. 它定义了轨道传播过程中允许的最大位置误差,单位为米
    // 2. 积分器会根据这个容差自动调整步长:
    //    - 当预测误差大于容差时,会减小步长以提高精度
    //    - 当预测误差远小于容差时,会增大步长以提高效率
    // 3. 较小的容差值(如1-10米)会提供更精确的结果,但计算量更大
    // 4. 较大的容差值(如100米以上)计算更快,但精度降低
    // 5. 容差值的选择需要在计算效率和精度要求之间权衡
    final double positionTolerance = 10.0; // 位置容差，单位：米
    OrbitType    propagationType   = OrbitType.KEPLERIAN; // 传播类型
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
