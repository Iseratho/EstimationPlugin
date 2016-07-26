package org.catrobat.estimationplugin.valuegenerator;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.estimationplugin.jql.IssueListCreator;

import java.util.HashMap;
import java.util.Map;

public class IssueValuesGenerator implements ValuesGenerator {

    @Override
    public Map getValues(Map map) {

        Map<String, String> issueMap = new HashMap<String, String>();
        SearchProvider searchProvider = ComponentAccessor.getComponent(SearchProvider.class);
        ApplicationUser applicationUser = ComponentAccessor.getComponent(ApplicationUser.class);
        IssueListCreator issueListCreator = new IssueListCreator(searchProvider, applicationUser);
        return issueMap;
    }
}
