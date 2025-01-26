package org.aircas.resource.util;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;

public class CalculatorUtil {
    /**
     * 计算太阳遮蔽角
     * <p>
     * 太阳排除角是指卫星-目标连线与卫星-太阳连线之间的夹角。
     * 此角度用于评估太阳是否会干扰卫星对目标的观测。
     * </p>
     *
     * @param orbit 卫星轨道参数
     * @param targetTLE      目标卫星的TLE轨道根数
     * @param date          计算时刻
     * @return 太阳排除角(弧度)
     */
    public static double calculateSolarExclusionAngle(Orbit orbit, TLE targetTLE, AbsoluteDate date) {
        // 获取太阳的位置
        Frame inertialFrame = FramesFactory.getEME2000();
        Vector3D sunPosition = CelestialBodyFactory.getSun().getPVCoordinates(date, inertialFrame).getPosition();

        // 获取卫星的位置
        PVCoordinates satellitePV = orbit.getPVCoordinates(date, inertialFrame);
        Vector3D satellitePosition = satellitePV.getPosition();

        // 获取目标的位置
        TLEPropagator targetPropagator = TLEPropagator.selectExtrapolator(targetTLE);
        PVCoordinates targetPV = targetPropagator.propagate(date).getPVCoordinates(inertialFrame);
        Vector3D targetPosition = targetPV.getPosition();

        // 计算卫星与太阳的夹角
        Vector3D satelliteToSun = sunPosition.subtract(satellitePosition);
        Vector3D satelliteToTarget = targetPosition.subtract(satellitePosition);

        // 返回角度（弧度）
        return Vector3D.angle(satelliteToSun, satelliteToTarget);
    }

    /**
     * 计算卫星相对地面站的仰角
     *
     * @param state        卫星状态
     * @param stationFrame 地面站站心坐标系
     * @return 仰角（度）
     */
    public static double calculateElevation(SpacecraftState state, TopocentricFrame stationFrame) {
        double elevation = stationFrame.getElevation(
                state.getPVCoordinates().getPosition(),
                state.getFrame(),
                state.getDate());
        return FastMath.toDegrees(elevation);
    }
}
