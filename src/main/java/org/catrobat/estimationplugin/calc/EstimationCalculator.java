package org.catrobat.estimationplugin.calc;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.google.gson.Gson;
import org.catrobat.estimationplugin.helper.DateHelper;
import org.catrobat.estimationplugin.helper.StatisticsHelper;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.catrobat.estimationplugin.issue.FinishedIssueList;
import org.catrobat.estimationplugin.issue.OpenIssueList;
import org.catrobat.estimationplugin.misc.ReportParams;

import java.util.*;


public class EstimationCalculator {
    private final DateTimeFormatter dateTimeFormatter;
    private IssueListCreator issueListCreator;

    private FinishedIssueList finishedIssueListClass;
    private OpenIssueList openIssueListClass;

    private Map<String, Long> costMap; //debug only
    private Map<String, Long> smlMap; //debug only

    private ReportParams reportParams;

    public EstimationCalculator(ReportParams reportParams) {
        this.reportParams = reportParams;
        issueListCreator = new IssueListCreator(reportParams.getSearchProvider(), reportParams.getRemoteUser());
        this.dateTimeFormatter = reportParams.getFormatterFactory().formatter().withStyle(DateTimeStyle.ISO_8601_DATE);
    }

    public Map<String, Object> prepareMap() {
        Date today = new Date();
        long openIssues = openIssueListClass.getOpenIssueCount();
        long openCost = openIssueListClass.getOpenIssueCost();
        double daysToFinish = openIssues / finishedIssueListClass.getTicketsPerDay();
        if (reportParams.isNumProgrammersEnabled()) {
            daysToFinish = daysToFinish * finishedIssueListClass.getAverageWorkingStudents() / reportParams.getNumProgrammers();
        }
        long daysToFinishRounded = Math.round(daysToFinish);
        Calendar finishDate = Calendar.getInstance();
        finishDate.setTime(today);
        finishDate.add(Calendar.DATE, (int)daysToFinishRounded);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("openIssues", openIssues);
        data.put("openCost", openCost);
        data.put("finishDate", dateTimeFormatter.format(finishDate.getTime()));

        // Uncertainty is probably false
        data.put("uncertainty", DateHelper.convertMillisToDays((long)finishedIssueListClass.getDurationStatistics().getStandardDeviation())/7);
        String ticketsPerDay = String.valueOf(finishedIssueListClass.getFinishedIssueCount()) +
                "/" +  String.valueOf(finishedIssueListClass.getDaysTicketsWhereOpened() + "/" + String.valueOf(finishedIssueListClass.getProjectDurationFromStart()));
        data.put("ticketsPerDay", ticketsPerDay);
        data.put("costMap", costMap);
        data.put("smlMap", smlMap);
        double averageTicketDurationDays = finishedIssueListClass.getAverageTicketDurationDays();
        data.put("avgDaysOpened", averageTicketDurationDays);

        // 50% probability
        data.put("avgDaysOpenedNew", DateHelper.convertMillisToDays((long)finishedIssueListClass.getDurationStatistics().getMean()));
        finishDate.setTime(today);
        finishDate.add(Calendar.DATE,(int)Math.round(averageTicketDurationDays));
        data.put("avgFinishDate", dateTimeFormatter.format(finishDate.getTime()));
        data.put("projectStart", dateTimeFormatter.format(finishedIssueListClass.getProjectStartDate()));
        data.put("openIssueList", openIssueListClass.getOpenIssueList());
        data.put("queryLog", issueListCreator.getQueryLog());

        SortedMap bellCurveMap = StatisticsHelper.getBellCurveMap(finishedIssueListClass.getDurationStatistics());

        List<Date> dateList = DateHelper.convertLongCollectionToDateList(bellCurveMap.keySet());
        String json = new Gson().toJson(dateList);
        data.put("bellCurveMapKeys", json);
        data.put("bellCurveMapValues", bellCurveMap.values());

        data.put("assigneeCount", finishedIssueListClass.getAssigneeCount());
        data.put("overlapFactor", finishedIssueListClass.getIssueOverlappingFactor());

        data.put("testing", finishedIssueListClass.getAverageWorkingStudents());
        return data;
    }

    public Map<String, Object> calculateOutputParams() throws SearchException {
        List<Issue> openIssueList = issueListCreator.getIssueListForStatus(reportParams.getProjectOrFilterId(), reportParams.getOpenIssuesStatus(), reportParams.isFilter());
        List<Issue> finishedIssueList = issueListCreator.getIssueListForStatus(reportParams.getProjectOrFilterId(), reportParams.getFinishedIssuesStatus(), reportParams.isFilter());
        finishedIssueListClass = new FinishedIssueList(finishedIssueList);
        openIssueListClass = new OpenIssueList(openIssueList);
        if (reportParams.isRemoveOutliersEnabled()) {
            openIssueListClass.removeOutliers(reportParams.getRemoveOutliersDays(), reportParams.getRemoveOutliersStatus());
        }
        costMap = getMapOfEffortsFromIssueListForCustomField(openIssueList, reportParams.getEstimationField());
        smlMap = getMapOfEffortsFromIssueListForCustomField(openIssueList, reportParams.getEstimationSMLField());

        return prepareMap();
    }

    private Map<String, Long> getMapOfEffortsFromIssueListForCustomField(List<Issue> issueList, CustomField customField) {
        ListIterator<Issue> issueIterator = issueList.listIterator();
        Map<String, Long> map = new HashMap<String, Long>();
        while (issueIterator.hasNext()) {
            Issue currentIssue = issueIterator.next();
            if (currentIssue.getCustomFieldValue(customField) != null && currentIssue.getCustomFieldValue(customField) instanceof Option) {
                String value = ((Option) currentIssue.getCustomFieldValue(customField)).getValue();
                map.putIfAbsent(value, new Long(0));
                map.put(value, map.get(value) + 1);
            }
        }
        return map;
    }
}
