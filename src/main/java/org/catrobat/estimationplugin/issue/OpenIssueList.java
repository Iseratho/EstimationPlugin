package org.catrobat.estimationplugin.issue;

import com.atlassian.jira.issue.Issue;
import org.catrobat.estimationplugin.helper.DateHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OpenIssueList {

    private List<OpenIssue> openIssueList;

    private long defaultEstimate;

    public OpenIssueList(List<Issue> issueList) {
        openIssueList = new LinkedList<OpenIssue>();
        for(Issue issue : issueList) {
            OpenIssue openIssue = new OpenIssue(issue);
            openIssueList.add(openIssue);
        }

        defaultEstimate = 5;
    }

    public List<OpenIssue> getOpenIssueList() {
        return openIssueList;
    }

    public long getOpenIssueCount() {
        return openIssueList.size();
    }

    private Map<String, Long> getMapOfEffortsFromIssueListForCustomField(List<OpenIssue> issueList) {
        ListIterator<OpenIssue> issueIterator = issueList.listIterator();
        Map<String, Long> map = new HashMap<String, Long>();
        while (issueIterator.hasNext()) {
            OpenIssue currentIssue = issueIterator.next();
            String value = currentIssue.getEstimation();
            map.putIfAbsent(value, new Long(0));
            map.put(value, map.get(value) + 1);
        }
        return map;
    }

    public long getOpenIssueCost() {
        Map<String, Long> map = getMapOfEffortsFromIssueListForCustomField(openIssueList);
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

    public List<String> getOldestIssuesLinks(int amount) {
        List<OpenIssue> openIssues = openIssueList; //TODO check whether does is a deep or shallow copy
        openIssues.sort(new OpenIssue.CreatedComparator());
        List<String> oldestIssues = new ArrayList<>();
        for (int i = 0; i < amount && i < openIssues.size(); i++) { //TODO solve with iterator
            oldestIssues.add(openIssues.get(i).getHTMLLink());
        }
        return oldestIssues;
    }

    public void removeOutliers(long daysSince, List<String> statusList) {
        List<OpenIssue> openIssues = new ArrayList<>(openIssueList);
        for (OpenIssue openIssue : openIssueList) {
            for(String statusString : statusList) {
                Date inStatusSince = openIssue.getDatePutIntoStatus(statusString);
                long diff = DateHelper.getDateDiff(inStatusSince, new Date(), TimeUnit.DAYS);
                if (diff > daysSince) {
                    openIssues.remove(openIssue);
                    break;
                }
            }
        }
        this.openIssueList = openIssues;
    }
}
