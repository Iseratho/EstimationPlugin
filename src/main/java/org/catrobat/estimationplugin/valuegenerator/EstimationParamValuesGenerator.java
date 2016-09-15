package org.catrobat.estimationplugin.valuegenerator;

import com.atlassian.configurable.ValuesGenerator;
import org.catrobat.estimationplugin.misc.TimeConsidered;

import java.util.HashMap;
import java.util.Map;

public class EstimationParamValuesGenerator implements ValuesGenerator{

    public Map<String, String> getValues(Map userParams) {
        Map estimationParams = new HashMap<String, String>();
        estimationParams.put(TimeConsidered.WHOLE_TIME, "whole time");
        estimationParams.put(TimeConsidered.LAST_DAYS, "last x days");
        estimationParams.put(TimeConsidered.LAST_WEEKS, "last x weeks");
        estimationParams.put(TimeConsidered.LAST_MONTHS, "last x months");
        estimationParams.put(TimeConsidered.LAST_YEARS, "last x years");
        estimationParams.put(TimeConsidered.SAME_AS_YEAR_BEFORE, "same period as last year");
        return estimationParams;
    }
}
