package org.aircas.resource.model;

import lombok.Data;

@Data
public class GroundStation {
  private final double latitude;  // 纬度(度)
  private final double longitude; // 经度(度)
  private final double altitude;  // 高度(米)

  public GroundStation(double latitude, double longitude, double altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  // Getters
  public double getLatitude() { return latitude; }
  public double getLongitude() { return longitude; }
  public double getAltitude() { return altitude; }
}
