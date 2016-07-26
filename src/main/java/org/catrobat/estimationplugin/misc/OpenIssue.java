package org.catrobat.estimationplugin.misc;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import org.catrobat.estimationplugin.helper.HtmlHelper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class OpenIssue {

    private Issue issue;

    private String estimation;
    private String estimationSML;

    private static CustomField estimationField;
    private static CustomField estimationSMLField;

    static {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        estimationField = customFieldManager.getCustomFieldObjectByName("Estimated Effort");
        estimationSMLField = customFieldManager.getCustomFieldObjectByName("Effort");
    }

    public OpenIssue(Issue issue) {
        this.issue = issue;
        extractData();
    }

    private void extractData() {
        if (issue.getCustomFieldValue(OpenIssue.estimationField) != null && issue.getCustomFieldValue(OpenIssue.estimationField) instanceof Option) {
            estimation = ((Option) issue.getCustomFieldValue(estimationField)).getValue();
        }
        if (issue.getCustomFieldValue(OpenIssue.estimationSMLField) != null && issue.getCustomFieldValue(OpenIssue.estimationSMLField) instanceof Option) {
            estimationSML = ((Option) issue.getCustomFieldValue(estimationSMLField)).getValue();
        }
    }

    public String getEstimation() {
        return estimation;
    }

    public Timestamp getCreated() {
        return issue.getCreated();
    }

    public String getHTMLLink() {
        String url = "/jira/browse/" + issue.getKey(); //TODO change to jira base url
        return HtmlHelper.getHtmlLink(url, issue.getKey(), issue.getDescription());
    }


    static class CreatedComparator implements Comparator<OpenIssue> {

        @Override
        public int compare(OpenIssue o1, OpenIssue o2) {
            long t1 = o1.getCreated().getTime();
            long t2 = o2.getCreated().getTime();
            if (t2 > t1) {
                return 1;
            } else if (t1 > t2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
