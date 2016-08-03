package org.catrobat.estimationplugin.calc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.catrobat.estimationplugin.misc.FinishedIssueList;
import org.catrobat.estimationplugin.misc.OpenIssueList;

import java.util.*;

public class BackwardCalculator {

    private IssueListCreator issueListCreator;

    private List<String> openIssuesStatus = new ArrayList<String>();
    private List<String> finishedIssuesStatus = new ArrayList<String>();

    public BackwardCalculator(SearchProvider searchProvider, ApplicationUser user) {
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
    }

    public Map<String, Object> prepareMap(OpenIssueList openIssueList, FinishedIssueList finishedIssueList, Date deadline) {
        Map<String, Object> data = new HashMap<String, Object>();
        long accomplishable = finishedIssueList.calculateCountOfAccomplishableTicketsTillDate(deadline);
        List<String> oldestAccomplishableIssues = openIssueList.getOldestIssuesLinks((int)accomplishable);
        data.put("issuesToBeFinishedAsHtml", oldestAccomplishableIssues);
        data.put("log", issueListCreator.getQueryLog());
        data.put("count", accomplishable);
        data.put("removeCount", openIssueList.getOpenIssueCount() - accomplishable);
        return data;
    }

    public Map<String, Object> calculateOutputParams(Long projectOrFilterId, boolean isFilter, Date deadline) throws SearchException {
        List<Issue> openIssueList = issueListCreator.getIssueListForStatus(projectOrFilterId, openIssuesStatus, isFilter);
        List<Issue> finishedIssueList = issueListCreator.getIssueListForStatus(projectOrFilterId, finishedIssuesStatus, isFilter);
        OpenIssueList openIssueListClass = new OpenIssueList(openIssueList);
        FinishedIssueList finishedIssueListClass = new FinishedIssueList(finishedIssueList);
        return prepareMap(openIssueListClass, finishedIssueListClass, deadline);
    }
}
