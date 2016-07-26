package org.catrobat.estimationplugin.helper;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.SortedMap;
import java.util.TreeMap;


public class StatisticsHelper {

    public static int MAX_STEPS = 31;

    public static SortedMap<Long, Double> getBellCurveMap(SummaryStatistics summaryStatistics, boolean cumulative) {
        SortedMap<Long, Double> bellCurveMap = new TreeMap<>();
        final double mean = summaryStatistics.getMean();
        final double sd = summaryStatistics.getStandardDeviation();
        long usedStepSize;
        double stepSize = sd / MAX_STEPS;
        if (stepSize < 1) {
            usedStepSize = 1;
        }
        else {
            usedStepSize = (int)Math.ceil(stepSize);
        }

        NormalDistribution normalDistribution = new NormalDistribution(mean, sd);
        long startValue = Math.round(mean) - ((MAX_STEPS-1)/2)*usedStepSize;
        long endValue = Math.round(mean) + ((MAX_STEPS-1)/2)*usedStepSize;
        for(long i = startValue; i < endValue; i+=usedStepSize) {
            double stepProbability;
            if (cumulative) {
                stepProbability = normalDistribution.cumulativeProbability(i) * 100;
            } else {
                stepProbability = (normalDistribution.cumulativeProbability(i) - normalDistribution.cumulativeProbability(i - usedStepSize)) * 100;
            }
            bellCurveMap.put(i, stepProbability);
        }
        return bellCurveMap;
    }

    public static SortedMap<Long, Double> getBellCurveMap(SummaryStatistics summaryStatistics) {
        return getBellCurveMap(summaryStatistics, false);
    }
}
