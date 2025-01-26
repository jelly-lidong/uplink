package org.aircas.resource.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.aircas.resource.model.GroundStation;
import org.aircas.resource.model.OrbitalParameters;
import org.aircas.resource.model.SatelliteState;
import org.aircas.resource.service.SatelliteVisibilityService;
import org.orekit.utils.Constants;

public class SatelliteVisibilityServiceImpl implements SatelliteVisibilityService {

  private final OneAxisEllipsoid earth;

  public SatelliteVisibilityServiceImpl(Frame earthFrame) {
    this.earth = new OneAxisEllipsoid(
        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        Constants.WGS84_EARTH_FLATTENING,
        earthFrame);
  }

  @Override
  public List<SatelliteState> calculateVisibleStates(
      OrbitalParameters orbitalParams,
      GroundStation station,
      AbsoluteDate startDate,
      AbsoluteDate endDate,
      Frame frame,
      double minElevation,
      double maxElevation) {

    List<SatelliteState> results = new ArrayList<>();

    // 创建地面站位置
    GeodeticPoint stationPosition = new GeodeticPoint(
        Math.toRadians(station.getLatitude()),
        Math.toRadians(station.getLongitude()),
        station.getAltitude());

    TopocentricFrame stationFrame = new TopocentricFrame(earth, stationPosition, "Ground Station");

    // 创建初始轨道 - 使用 EME2000 参考系
    Frame eme2000 = FramesFactory.getEME2000();
    Orbit initialOrbit = new KeplerianOrbit(
        orbitalParams.getSemiMajorAxis(),
        orbitalParams.getEccentricity(),
        Math.toRadians(orbitalParams.getInclination()),
        Math.toRadians(orbitalParams.getArgumentOfPerigee()),
        Math.toRadians(orbitalParams.getRaan()),
        Math.toRadians(orbitalParams.getMeanAnomaly()),
        PositionAngleType.TRUE,
        eme2000,  // 使用 EME2000 而不是 ITRF
        startDate,
        Constants.WGS84_EARTH_MU);

    // 创建传播器
    Propagator propagator = new KeplerianPropagator(initialOrbit);

    // 设置仰角检测器
    double minElevationRad = Math.toRadians(minElevation);
    double maxElevationRad = Math.toRadians(maxElevation);

    // 设置步长为1秒的步长处理器
    propagator.getMultiplexer().add(1., state -> {
      Vector3D position  = state.getPVCoordinates().getPosition();
      double   elevation = stationFrame.getElevation(position, frame, state.getDate());

      if (elevation >= minElevationRad && elevation <= maxElevationRad) {
        results.add(new SatelliteState(
            state.getDate(),
            position,
            state.getPVCoordinates().getVelocity(),
            Math.toDegrees(elevation)
        ));
      }
    });
    // 传播轨道
    propagator.propagate(startDate, endDate);

    return results;
  }
}
