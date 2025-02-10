package org.aircas.orbit.handler;

import org.hipparchus.util.FastMath;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;

import java.util.Locale;

/**
 * 专用步长处理器。
 * <p>此类扩展了步长处理器，以便在给定步长时在输出流上打印。<p>
 * 作者：
 * - Pascal Parraud
 */
public class StepHandler implements OrekitFixedStepHandler {

    /**
     * 简单构造函数。
     */
    public StepHandler() {
        // 私有构造函数
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final SpacecraftState s0, final AbsoluteDate t, final double step) {
        // 初始化时输出表头
        System.out.println("          date                a           e" +
                "           i         ω          Ω" +
                "          ν");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStep(final SpacecraftState currentState) {
        // 获取当前状态的开普勒轨道
        final KeplerianOrbit o = (KeplerianOrbit) OrbitType.KEPLERIAN.convertType(currentState.getOrbit());
        // 输出当前状态的轨道参数
        System.out.format(Locale.US, "%s %12.3f %10.8f %10.6f %10.6f %10.6f %10.6f%n",
                currentState.getDate(),
                o.getA(), o.getE(),
                FastMath.toDegrees(o.getI()),
                FastMath.toDegrees(o.getPerigeeArgument()),
                FastMath.toDegrees(o.getRightAscensionOfAscendingNode()),
                FastMath.toDegrees(o.getTrueAnomaly()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish(final SpacecraftState finalState) {
        // 输出结束信息
        System.out.println("this was the last step ");
        System.out.println();
    }

}
