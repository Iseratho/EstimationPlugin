package org.catrobat.estimationplugin.misc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.user.ApplicationUser;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReportParams {

    // Jira specific params
    private SearchProvider searchProvider;
    private ApplicationUser remoteUser;
    private DateTimeFormatterFactory formatterFactory;

    // Params from configure page
    private boolean isFilter;
    private Long projectOrFilterId;
    private Long numProgrammers;
    private TimeConsidered timeConsidered;
    private long lastx;
    private TicketsOrCost ticketsOrCost;

    // Params from admin interface
    private boolean removeOutliersEnabled;
    private int removeOutliersDays;
    private List<String> removeOutliersStatus = new ArrayList<>();

    private List<String> openIssuesStatus = new ArrayList<>();
    private List<String> finishedIssuesStatus = new ArrayList<>();
    private CustomField estimationField;
    private CustomField estimationSMLField;

    // Debug
    private boolean numProgrammersEnabled;

    public ReportParams(SearchProvider searchProvider, ApplicationUser remoteUser, DateTimeFormatterFactory formatterFactory) {
        this.searchProvider = searchProvider;
        this.remoteUser = remoteUser;
        this.formatterFactory = formatterFactory;
        loadReportParamsFromAdminInterface();
        loadStaticVariables();
    }

    /*
    public ReportParams(SearchProvider searchProvider, ApplicationUser remoteUser, DateTimeFormatterFactory formatterFactory, Map params) {
        this.searchProvider = searchProvider;
        this.remoteUser = remoteUser;
        this.formatterFactory = formatterFactory;
        loadReportParamsFromAdminInterface();
        loadStaticVariables();
        loadConfigureParams(params);
    }*/

    public void setConfigureParams(boolean isFilter, Long projectOrFilterId, Long numProgrammers) {
        this.isFilter = isFilter;
        this.projectOrFilterId = projectOrFilterId;
        this.numProgrammers = numProgrammers;
    }

    public void setTimeConsideredParams(String timeConsidered, Long lastx) {
        this.timeConsidered = TimeConsidered.valueOf(timeConsidered);
        this.lastx = lastx;
    }

    /*public void loadConfigureParams(Map params) {

    }*/

    private void loadReportParamsFromAdminInterface() {
        // TODO: connection to Admin interface
        openIssuesStatus.add("Backlog");
        openIssuesStatus.add("Open");
        openIssuesStatus.add("In Progress");
        finishedIssuesStatus.add("Closed");
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        estimationField = customFieldManager.getCustomFieldObjectByName("Estimated Effort");
        estimationSMLField = customFieldManager.getCustomFieldObjectByName("Effort");

        removeOutliersEnabled = true;
        removeOutliersDays = 365;
        removeOutliersStatus.add("Backlog");
    }

    private void loadStaticVariables() {
        numProgrammersEnabled = true;
        ticketsOrCost = TicketsOrCost.TICKETS;
    }

    public SearchProvider getSearchProvider() {
        return searchProvider;
    }

    public ApplicationUser getRemoteUser() {
        return remoteUser;
    }

    public DateTimeFormatterFactory getFormatterFactory() {
        return formatterFactory;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public Long getProjectOrFilterId() {
        return projectOrFilterId;
    }

    public Long getNumProgrammers() {
        return numProgrammers;
    }

    public boolean isRemoveOutliersEnabled() {
        return removeOutliersEnabled;
    }

    public int getRemoveOutliersDays() {
        return removeOutliersDays;
    }

    public List<String> getRemoveOutliersStatus() {
        return removeOutliersStatus;
    }

    public List<String> getOpenIssuesStatus() {
        return openIssuesStatus;
    }

    public List<String> getFinishedIssuesStatus() {
        return finishedIssuesStatus;
    }

    public CustomField getEstimationField() {
        return estimationField;
    }

    public CustomField getEstimationSMLField() {
        return estimationSMLField;
    }

    public boolean isNumProgrammersEnabled() {
        return numProgrammersEnabled;
    }

    public TimeConsidered getTimeConsidered() {
        return timeConsidered;
    }

    public long getLastx() {
        return lastx;
    }

}
