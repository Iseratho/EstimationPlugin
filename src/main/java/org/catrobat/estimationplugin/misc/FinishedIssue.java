package org.catrobat.estimationplugin.misc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.user.ApplicationUser;
import org.ofbiz.core.entity.GenericValue;

import java.sql.Timestamp;
import java.util.*;

public class FinishedIssue {

    private Issue issue;

    private Date created;
    private Date workStarted;
    private Date workFinished;
    private long workDuration;

    public FinishedIssue(Issue issue) {
        this.issue = issue;
        extractData();
    }

    private void extractData() {
        created = new Date(issue.getCreated().getTime());
        workStarted = getDateOfStatusTransition("Issues Pool", "Backlog");
        workFinished = new Date(issue.getResolutionDate().getTime());
        workDuration = workFinished.getTime() - workStarted.getTime();
    }

    private Date getDateOfStatusTransition(String oldStatus, String newStatus) {
        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        List<ChangeHistory> issueHistory = changeHistoryManager.getChangeHistories(issue);
        for(ChangeHistory changeHistory : issueHistory) {
            List<GenericValue> changeItem = changeHistory.getChangeItems();
            for(GenericValue genericValue : changeItem) {
                String field = genericValue.getString("field");
                String oldstring = genericValue.getString("oldstring");
                String newstring = genericValue.getString("newstring");
                if (field.equals("status") && oldstring.equals(oldStatus) && newstring.equals(newStatus)) {
                    Timestamp changedToIssuePool = changeHistory.getTimePerformed();
                    return new Date(changedToIssuePool.getTime());
                }
            }
        }
        // TODO: this is ugly fix, so items which where never put into backlog, have 0 days worked on
        return new Date(issue.getResolutionDate().getTime());
    }

    public Date getCreated() {
        return created;
    }

    public long getWorkDuration() {
        return workDuration;
    }

    public String getAssignee() {
        return issue.getAssigneeId();
    }

    public Set<ApplicationUser> getUsersParticipating() {
        Set<ApplicationUser> participatingUsers = new HashSet<>();
        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        List<ChangeHistory> issueHistory = changeHistoryManager.getChangeHistories(issue);
        for(ChangeHistory changeHistory : issueHistory) {
            ApplicationUser user = changeHistory.getAuthorObject();
            participatingUsers.add(user);
        }
        return participatingUsers;
    }

    public int getUsersParticipatingCount() {
        return getUsersParticipating().size();
    }

    @DebugAnnotation
    public String getChangeHistoryString() {
        String changeHistoryString = "";
        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        List<ChangeHistory> issueHistory = changeHistoryManager.getChangeHistories(issue);
        for(ChangeHistory changeHistory : issueHistory) {
            List<GenericValue> changeItem = changeHistory.getChangeItems();
            for(GenericValue genericValue : changeItem) {
                changeHistoryString += genericValue.toString() + "\n";
            }
        }
        return changeHistoryString;
    }


}
