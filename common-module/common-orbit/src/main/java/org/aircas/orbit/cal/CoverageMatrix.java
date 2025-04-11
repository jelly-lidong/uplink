package org.aircas.orbit.cal;

import java.util.HashMap;
import java.util.Map;

public class CoverageMatrix {
    private final Map<Long, Integer> matrix;
    private final double angleInterval;

    public CoverageMatrix(double angleInterval) {
        this.matrix = new HashMap<>();
        this.angleInterval = angleInterval;
    }

    private long computeKey(double phiL, double thetaL, double phiS, double thetaS) {
        int iPhiL = (int) Math.floor(phiL / angleInterval);
        int iThetaL = (int) Math.floor(thetaL / angleInterval);
        int iPhiS = (int) Math.floor(phiS / angleInterval);
        int iThetaS = (int) Math.floor(thetaS / angleInterval);
        return ((long) iPhiL << 24) | ((long) iThetaL << 16) | ((long) iPhiS << 8) | iThetaS;
    }

    public int getCount(double phiL, double thetaL, double phiS, double thetaS) {
        long key = computeKey(phiL, thetaL, phiS, thetaS);
        return matrix.getOrDefault(key, 0);
    }

    public void updateEntry(double phiL, double thetaL, double phiS, double thetaS) {
        long key = computeKey(phiL, thetaL, phiS, thetaS);
        matrix.put(key, matrix.getOrDefault(key, 0) + 1);
    }
}

