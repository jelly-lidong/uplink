package org.aircas.orbit.cal;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 初始化覆盖矩阵，角度间隔为2°
        CoverageMatrix matrix = new CoverageMatrix(2.0);

        // 假设某次观测任务预估覆盖以下角度条件
        List<AngleCondition> conditions = new ArrayList<>();
        conditions.add(new AngleCondition(10.5, 45.0, 30.0, 60.0));
        conditions.add(new AngleCondition(20.0, 30.0, 90.0, 120.0));

        // 计算贡献率（预估阶段）
        double contribution = ContributionCalculator.calculateContribution(matrix, conditions);
        System.out.println("预估贡献率: " + contribution);

        // 实际观测完成后更新覆盖矩阵
        for (AngleCondition cond : conditions) {
            matrix.updateEntry(
                    cond.getPhiL(), cond.getThetaL(),
                    cond.getPhiS(), cond.getThetaS()
            );
        }

        // 再次计算贡献率（此时次数已更新）
        contribution = ContributionCalculator.calculateContribution(matrix, conditions);
        System.out.println("更新后贡献率: " + contribution);
    }
}
