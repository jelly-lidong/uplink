package org.aircas.resource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatelliteState {

  private AbsoluteDate date;
  private Vector3D     position;
  private Vector3D     velocity;
  private double       elevation;

}
