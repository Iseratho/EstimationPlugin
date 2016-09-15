package ut.org.catrobat.estimationplugin;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.catrobat.estimationplugin.helper.StatisticsHelper;
import org.junit.Test;

import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class StatisticsHelperTest {

    @Test
    public void testGetBellCurveMap() {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        summaryStatistics.addValue(1);
        summaryStatistics.addValue(3);
        summaryStatistics.addValue(7);
        summaryStatistics.addValue(3);
        summaryStatistics.addValue(1);
        StatisticsHelper.MAX_STEPS = 3;
        SortedMap<Long, Double> bellCurveMap = StatisticsHelper.getBellCurveMap(summaryStatistics, false);
        assertEquals(3, bellCurveMap.size());
    }
}
