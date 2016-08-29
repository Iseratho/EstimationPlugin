package org.catrobat.estimationplugin.calc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.catrobat.estimationplugin.issue.FinishedIssueList;
import org.catrobat.estimationplugin.issue.OpenIssueList;
import org.catrobat.estimationplugin.misc.ReportParams;

import java.util.*;

public class BackwardCalculator {

    private IssueListCreator issueListCreator;



    private ReportParams reportParams;

    public BackwardCalculator(ReportParams reportParams) {
        this.reportParams = reportParams;
        issueListCreator = new IssueListCreator(reportParams.getSearchProvider(), reportParams.getRemoteUser());
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

    public Map<String, Object> calculateOutputParams(Date deadline) throws SearchException {
        List<Issue> openIssueList = issueListCreator.getIssueListForStatus(reportParams.getProjectOrFilterId(), reportParams.getOpenIssuesStatus(), reportParams.isFilter());
        List<Issue> finishedIssueList = issueListCreator.getIssueListForStatus(reportParams.getProjectOrFilterId(), reportParams.getFinishedIssuesStatus(), reportParams.isFilter());
        OpenIssueList openIssueListClass = new OpenIssueList(openIssueList);
        FinishedIssueList finishedIssueListClass = new FinishedIssueList(finishedIssueList);
        return prepareMap(openIssueListClass, finishedIssueListClass, deadline);
    }
}
