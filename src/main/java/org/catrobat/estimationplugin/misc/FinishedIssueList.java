package org.catrobat.estimationplugin.misc;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.catrobat.estimationplugin.helper.DateHelper;

import java.util.*;

public class FinishedIssueList {

    private List<FinishedIssue> finishedIssueList;

    public FinishedIssueList(List<Issue> issueList) {
        finishedIssueList = new LinkedList<FinishedIssue>();
        for(Issue issue : issueList) {
            FinishedIssue openIssue = new FinishedIssue(issue);
            finishedIssueList.add(openIssue);
        }
    }

    public long getFinishedIssueCount() {
        return finishedIssueList.size();
    }

    public SummaryStatistics getDurationStatistics() {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for(FinishedIssue finishedIssue : finishedIssueList) {
            summaryStatistics.addValue(finishedIssue.getWorkDuration());
        }
        return summaryStatistics;
    }

    public DescriptiveStatistics getDurationStatisticsDescriptive() {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        for(FinishedIssue finishedIssue : finishedIssueList) {
            descriptiveStatistics.addValue(finishedIssue.getWorkDuration());
        }
        return descriptiveStatistics;
    }

    // TODO: change to be based on date put into backlog
    public long getDaysTicketsWhereOpened() {
        List<FinishedIssue> issues = finishedIssueList;
        ListIterator<FinishedIssue> issueIterator = issues.listIterator();
        long daysOpened = 0;
        while (issueIterator.hasNext()) {
            FinishedIssue currentIssue = issueIterator.next();
            daysOpened += currentIssue.getWorkDuration();
        }
        return DateHelper.convertMillisToDays(daysOpened);
    }

    public Date getProjectStartDate() {
        List<FinishedIssue> issues = finishedIssueList;
        ListIterator<FinishedIssue> issueIterator = issues.listIterator();
        if (!issueIterator.hasNext()) {
            // TODO: make sure you never come here
            return new Date();
        }
        long minCreated = issueIterator.next().getCreated().getTime();
        while (issueIterator.hasNext()) {
            FinishedIssue currentIssue = issueIterator.next();
            long created = currentIssue.getCreated().getTime();
            if (minCreated - created > 0) {
                minCreated = created;
            }
        }
        return new Date(minCreated);
    }

    public long calculateCountOfAccomplishableTicketsTillDate(Date deadline) {
        long daysTillDeadline = DateHelper.daysUntil(deadline);
        double ticketsPerDay = getTicketsPerDay();
        return (long)Math.floor(daysTillDeadline * ticketsPerDay);
    }

    public long getProjectDurationFromStart() {
        long start = getProjectStartDate().getTime();
        Date today = new Date();
        long days = (today.getTime() - start)/(1000 * 60 * 60 * 24);
        return days;
    }

    public double getTicketsPerDay() {
        return getFinishedIssueCount()/((double)getProjectDurationFromStart());
    }

    public double getAverageTicketDurationDays() {
        return getDaysTicketsWhereOpened()/((double)getFinishedIssueCount());
    }

    public int getAssigneeCount() {
        Set<String> users = new HashSet<String>();
        for(FinishedIssue finishedIssue : finishedIssueList) {
            users.add(finishedIssue.getAssignee());
        }
        return users.size();
    }

    public double getIssueOverlappingFactor() {
        long projectDuration = getProjectDurationFromStart();
        long workDuration = 0;
        for(FinishedIssue finishedIssue : finishedIssueList) {
            workDuration += finishedIssue.getWorkDuration();
        }
        return DateHelper.convertMillisToDays(workDuration)/(double)projectDuration;
    }

    @DebugAnnotation
    public String getChangeHistoryExample() {
        return finishedIssueList.get(0).getChangeHistoryString();
    }

    @DebugAnnotation
    public String getStringOfParticipatingUsersCount() {
        String stringOfParticipatingUsers = "";
        for(FinishedIssue finishedIssue : finishedIssueList) {
            stringOfParticipatingUsers += finishedIssue.getUsersParticipatingCount() + " + ";
        }
        return stringOfParticipatingUsers;
    }

    public double getAverageWorkingStudents() {
        long projectDuration = getProjectDurationFromStart();
        long workDurationTimesMembers = 0;
        for(FinishedIssue finishedIssue : finishedIssueList) {
            workDurationTimesMembers += finishedIssue.getWorkDuration() * finishedIssue.getUsersParticipatingCount();
        }
        return (DateHelper.convertMillisToDays(workDurationTimesMembers)/(double)projectDuration)/getIssueOverlappingFactor();
    }

    public long getTotalWorkDurationBasedOnAverageDurationPerStudent() {
        long totalWorkDurationPerStudent = 0;
        for(FinishedIssue finishedIssue : finishedIssueList) {
            totalWorkDurationPerStudent += finishedIssue.getWorkDuration() / finishedIssue.getUsersParticipatingCount();
        }
        return DateHelper.convertMillisToDays(totalWorkDurationPerStudent);
    }

    public double getAverageWorkDurationPerStudent() {
        return getTotalWorkDurationBasedOnAverageDurationPerStudent()/(double)getFinishedIssueCount();
    }
}
