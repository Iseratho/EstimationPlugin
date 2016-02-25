package org.catrobat.estimationplugin.calc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.ofbiz.core.entity.GenericValue;

import java.sql.Timestamp;
import java.util.*;


public class EstimationCalculator {

    private final ProjectManager projectManager;

    private float ticketsPerDay;
    private float averageTicketDurationDays;

    private List<String> openIssuesStatus = new ArrayList<String>();
    private List<String> finishedIssuesStatus = new ArrayList<String>();
    private CustomField estimationField;
    private CustomField estimationSMLField;
    private long defaultEstimate;

    private IssueListCreator issueListCreator;
    private List<Issue> openIssueList;
    private List<Issue> finishedIssueList;

    public EstimationCalculator(ProjectManager projectManager, SearchProvider searchProvider, ApplicationUser user) {
        this.projectManager = projectManager;
        issueListCreator = new IssueListCreator(searchProvider, user);

        loadSettings();
    }

    private void loadSettings() {
        // TODO: change initialisation to Admin
        openIssuesStatus.add("Backlog");
        openIssuesStatus.add("Open");
        openIssuesStatus.add("In Progress");
        finishedIssuesStatus.add("Closed");
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        estimationField = customFieldManager.getCustomFieldObjectByName("Estimated Effort");
        estimationSMLField = customFieldManager.getCustomFieldObjectByName("Effort");
        defaultEstimate = 5;
    }

    public void calculateTicketsPerDay()
    {
        ticketsPerDay = getFinishedIssueCount()/((float)getProjectDurationFromStart());
        averageTicketDurationDays = getDaysTicketsWhereOpened()/((float)getFinishedIssueCount());
    }

    public int uncertainty()
    {
        return 7;
    }

    public long getProjectDurationFromStart() {
        long start = getProjectStartInMillis();
        Date today = new Date();
        long days = (today.getTime() - start)/(1000 * 60 * 60 * 24);
        return  days;
    }

    public long getProjectStartInMillis() {
        List<Issue> issues = finishedIssueList;
        ListIterator<Issue> issueIterator = issues.listIterator();
        Timestamp minCreated = issueIterator.next().getCreated();
        while (issueIterator.hasNext()) {
            Issue currentIssue = issueIterator.next();
            Timestamp created = currentIssue.getCreated();
            if (minCreated.getTime() - created.getTime() > 0) {
                minCreated = created;
            }
        }
        return minCreated.getTime();
    }

    public Date getProjectStartDate() {
        return new Date(getProjectStartInMillis());
    }


    public Map<String, Object> calculateOutputParams(Long projectId, boolean isProject) throws SearchException
    {
        if (isProject == false) {

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("openIssues", "");
            data.put("openCost", "");
            data.put("finishDate", "");
            data.put("uncertainty", "");
            data.put("ticketsPerDay", "");
            data.put("costMap", "");
            data.put("smlMap", "");
            data.put("test", "");
            data.put("test2", "");
            data.put("avgDaysOpened", "");
            data.put("avgFinishDate", "");
            data.put("projectStart", "");

            return data;

        }
        openIssueList = issueListCreator.getIssueListForStatus(projectId, openIssuesStatus);
        finishedIssueList = issueListCreator.getIssueListForStatus(projectId, finishedIssuesStatus);

        calculateTicketsPerDay();
        int uncertainty = uncertainty();

        Date today = new Date();
        long openIssues = getOpenIssueCount();
        long openCost = getOpenIssueCost();
        float daysToFinish = openIssues / ticketsPerDay;
        int daysToFinishRounded = Math.round(daysToFinish);
        Calendar finishDate = Calendar.getInstance();
        finishDate.setTime(today);
        finishDate.add(Calendar.DATE, daysToFinishRounded);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("openIssues", openIssues);
        data.put("openCost", openCost);
        data.put("finishDate", finishDate.getTime());
        data.put("uncertainty", uncertainty);
        String ticketsPerDay = String.valueOf(getFinishedIssueCount()) +
                "/" +  String.valueOf(getDaysTicketsWhereOpened() + "/" + String.valueOf(getProjectDurationFromStart()));
        data.put("ticketsPerDay", ticketsPerDay);
        data.put("costMap", getMapOfEffortsFromIssueListForCustomField(openIssueList, estimationField));
        data.put("smlMap", getMapOfEffortsFromIssueListForCustomField(openIssueList, estimationSMLField));
        data.put("avgDaysOpened", averageTicketDurationDays);
        finishDate.setTime(today);
        finishDate.add(Calendar.DATE,Math.round(averageTicketDurationDays));
        data.put("avgFinishDate", finishDate.getTime());
        data.put("projectStart", getProjectStartDate());

        return data;
    }

    private Timestamp getDatePutIntoBacklog(Issue issue) {
        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        List<ChangeHistory> issueHistory = changeHistoryManager.getChangeHistories(issue);
        for(ChangeHistory changeHistory : issueHistory) {
            List<GenericValue> changeItem = changeHistory.getChangeItems();
            for(GenericValue genericValue : changeItem) {
                String field = genericValue.getString("field");
                String oldstring = genericValue.getString("oldstring");
                String newstring = genericValue.getString("newstring");
                if (field.equals("status") && oldstring.equals("Issues Pool") && newstring.equals("Backlog")) {
                    Timestamp changedToIssuePool = changeHistory.getTimePerformed();
                    return changedToIssuePool;
                }
            }
        }
        // TODO: this is ugly fix, so items which where never put into backlog, have 0 days worked on
        return issue.getResolutionDate();
    }

    // TODO: change to be based on date put into backlog
    private long getDaysTicketsWhereOpened() {
        List<Issue> issues = finishedIssueList;
        ListIterator<Issue> issueIterator = issues.listIterator();
        long daysOpened = 0;
        while (issueIterator.hasNext()) {
            Issue currentIssue = issueIterator.next();
            //Timestamp created = currentIssue.getCreated();
            Timestamp created = getDatePutIntoBacklog(currentIssue);
            Timestamp resolved = currentIssue.getResolutionDate();
            if (created != null && resolved != null) {
                long diffMilliseconds = resolved.getTime() - created.getTime();
                long days = diffMilliseconds / (1000 * 60 * 60 * 24);
                daysOpened += days;
            }
        }
        return daysOpened;
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

    private long getOpenIssueCost() {
        Map<String, Long> map = getMapOfEffortsFromIssueListForCustomField(openIssueList, estimationField);
        long sumEstimates = 0;
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            long key;
            try {
                key = Long.parseLong(entry.getKey());
            }catch(NumberFormatException nfe) {
                key = defaultEstimate;
            }
            sumEstimates += key * entry.getValue();
        }
        return sumEstimates;
    }

    private long getOpenIssueCount() {
        return openIssueList.size();
    }

    private long getFinishedIssueCount() {
        return finishedIssueList.size();
    }
}
