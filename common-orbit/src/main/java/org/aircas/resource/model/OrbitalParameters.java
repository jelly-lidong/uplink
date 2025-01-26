package org.aircas.resource.model;

import lombok.Data;

@Data
public class OrbitalParameters {
  private final double semiMajorAxis;    // 半长轴(米)
  private final double eccentricity;      // 偏心率
  private final double inclination;       // 轨道倾角(度)
  private final double argumentOfPerigee; // 近地点幅角(度)
  private final double raan;             // 升交点赤经(度)
  private final double meanAnomaly;      // 平近点角(度)

  public OrbitalParameters(double semiMajorAxis, double eccentricity, double inclination,
      double argumentOfPerigee, double raan, double meanAnomaly) {
    this.semiMajorAxis = semiMajorAxis;
    this.eccentricity = eccentricity;
    this.inclination = inclination;
    this.argumentOfPerigee = argumentOfPerigee;
    this.raan = raan;
    this.meanAnomaly = meanAnomaly;
  }

}
