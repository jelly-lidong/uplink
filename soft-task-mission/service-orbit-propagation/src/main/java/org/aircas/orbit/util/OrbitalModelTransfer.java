package org.aircas.orbit.util;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinates;

public class OrbitalModelTransfer {

  public static void main(String[] args) {

  }

  /**
   * 卫星参数转换之速度位置矢量转轨道六根数
   */
  public static KeplerianOrbit pvCoordinatesToKeplerianOrbit(PVCoordinates pvCoordinates, AbsoluteDate initialDate) {
    return new KeplerianOrbit(pvCoordinates, FramesFactory.getEME2000(), initialDate, Constants.WGS84_EARTH_MU);
  }


  public static PVCoordinates keplerianOrbitToPvCoordinates(KeplerianOrbit keplerianOrbit) {
    // 计算PV坐标
    PVCoordinates pvCoordinates = keplerianOrbit.getPVCoordinates(FramesFactory.getEME2000());
    // 获取位置和速度
    double x = pvCoordinates.getPosition().getX(); // 转换为千米
    double y = pvCoordinates.getPosition().getY(); // 转换为千米
    double z = pvCoordinates.getPosition().getZ(); // 转换为千米
    double vx = 0.0316227468 * pvCoordinates.getVelocity().getX() / 1000.0; // 转换为千米/秒
    double vy = 0.0316227468 * pvCoordinates.getVelocity().getY() / 1000.0; // 转换为千米/秒
    double vz = 0.0316227468 * pvCoordinates.getVelocity().getZ() / 1000.0; // 转换为千米/秒
    Vector3D pVector3D = new Vector3D(x, y, z);
    Vector3D vVector3D = new Vector3D(vx, vy, vz);
    return new PVCoordinates(pVector3D, vVector3D);
  }
}
