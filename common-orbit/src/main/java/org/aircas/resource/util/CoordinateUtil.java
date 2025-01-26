package org.aircas.resource.util;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.files.ccsds.ndm.odm.KeplerianElements;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.Transform;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

public class CoordinateUtil {

    /**
     * 将位置速度从源坐标系转换到目标坐标系
     *
     * @param position  位置向量 (m)
     * @param velocity  速度向量 (m/s)
     * @param fromFrame 源坐标系
     * @param toFrame   目标坐标系
     * @param date      转换时刻
     * @return 转换后的位置速度 [位置(m), 速度(m/s)]
     */
    public static Vector3D[] transformPV(Vector3D position, Vector3D velocity,
                                         Frame fromFrame, Frame toFrame, AbsoluteDate date) {
        // 获取坐标系转换关系
        Transform transform = fromFrame.getTransformTo(toFrame, date);

        // 转换位置和速度
        Vector3D transformedPosition = transform.transformPosition(position);
        Vector3D transformedVelocity = transform.transformVector(velocity);

        return new Vector3D[]{transformedPosition, transformedVelocity};
    }

    /**
     * 将位置从源坐标系转换到目标坐标系
     *
     * @param position  位置向量 (m)
     * @param fromFrame 源坐标系
     * @param toFrame   目标坐标系
     * @param date      转换时刻
     * @return 转换后的位置向量 (m)
     */
    public static Vector3D transformPosition(Vector3D position,
                                             Frame fromFrame, Frame toFrame, AbsoluteDate date) {
        // 获取坐标系转换关系
        Transform transform = fromFrame.getTransformTo(toFrame, date);

        // 转换位置
        return transform.transformPosition(position);
    }

    /**
     * 将速度从源坐标系转换到目标坐标系
     *
     * @param velocity  速度向量 (m/s)
     * @param fromFrame 源坐标系
     * @param toFrame   目标坐标系
     * @param date      转换时刻
     * @return 转换后的速度向量 (m/s)
     */
    public static Vector3D transformVelocity(Vector3D velocity,
                                             Frame fromFrame, Frame toFrame, AbsoluteDate date) {
        // 获取坐标系转换关系
        Transform transform = fromFrame.getTransformTo(toFrame, date);

        // 转换速度
        return transform.transformVector(velocity);
    }


    /**
     * 将轨道六根数转换为经纬高坐标
     */
    public static GeodeticPoint orbitalToGeodetic(KeplerianElements keplerianElements) {
        double a = keplerianElements.getA();
        double e = keplerianElements.getE();
        double i = keplerianElements.getI();
        double omega = keplerianElements.getPa();
        double raan = keplerianElements.getRaan();
        double lv = keplerianElements.getMeanMotion();
        Frame frame = FramesFactory.getEME2000();
        AbsoluteDate date = keplerianElements.getEpoch();
        // 构建开普勒轨道
        KeplerianOrbit orbit = new KeplerianOrbit(a, e, i, omega, raan, lv,
                PositionAngleType.MEAN,
                frame, date,
                Constants.WGS84_EARTH_MU);

        // 获取位置矢量
        Vector3D position = orbit.getPVCoordinates().getPosition();

        // 转换到地固系
        Vector3D positionITRF = transformPosition(position, frame,
                FramesFactory.getITRF(IERSConventions.IERS_2010, true),
                date);

        Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        // 创建地球椭球体
        OneAxisEllipsoid earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                earthFrame);

        // 使用ITRF作为地固系

        GeodeticPoint point = earth.transform(positionITRF, earthFrame, date);
        return new GeodeticPoint(point.getLatitude(), point.getLongitude(), point.getAltitude());
    }


}
