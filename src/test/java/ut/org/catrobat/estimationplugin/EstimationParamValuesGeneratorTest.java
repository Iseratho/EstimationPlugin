package ut.org.catrobat.estimationplugin;

import org.catrobat.estimationplugin.calc.EstimationCalculator;
import org.catrobat.estimationplugin.misc.TimeConsidered;
import org.catrobat.estimationplugin.valuegenerator.EstimationParamValuesGenerator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EstimationParamValuesGeneratorTest {

    @Test
    public void testGetValues() {
        Map map = Mockito.mock(Map.class);
        EstimationParamValuesGenerator valuesGenerator = new EstimationParamValuesGenerator();
        Map values = valuesGenerator.getValues(map);
        assertEquals("whole time", values.get(TimeConsidered.WHOLE_TIME));
        assertEquals("last x days", values.get(TimeConsidered.LAST_DAYS));
        assertEquals("last x weeks", values.get(TimeConsidered.LAST_WEEKS));
        assertEquals("last x months", values.get(TimeConsidered.LAST_MONTHS));
        assertEquals("last x years", values.get(TimeConsidered.LAST_YEARS));
        assertEquals("same period as last year", values.get(TimeConsidered.SAME_AS_YEAR_BEFORE));
    }
}
