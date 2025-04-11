package org.aircas.orbit.cal;

import lombok.Data;

@Data
public class AngleCondition {
    private final double phiL;
    private final double thetaL;
    private final double phiS;
    private final double thetaS;

    public AngleCondition(double phiL, double thetaL, double phiS, double thetaS) {
        this.phiL = phiL;
        this.thetaL = thetaL;
        this.phiS = phiS;
        this.thetaS = thetaS;
    }
}
