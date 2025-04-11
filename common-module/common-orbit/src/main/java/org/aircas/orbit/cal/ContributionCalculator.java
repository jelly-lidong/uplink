package org.aircas.orbit.cal;

import java.util.List;

public class ContributionCalculator {
    public static double calculateContribution(CoverageMatrix matrix, List<AngleCondition> conditions) {
        double totalContribution = 0.0;
        for (AngleCondition cond : conditions) {
            int count = matrix.getCount(
                    cond.getPhiL(), cond.getThetaL(),
                    cond.getPhiS(), cond.getThetaS()
            );
            totalContribution += 1.0 / (count + 1);
        }
        return totalContribution;
    }
}
