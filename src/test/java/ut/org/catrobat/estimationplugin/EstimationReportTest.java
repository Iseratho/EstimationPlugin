package ut.org.catrobat.estimationplugin;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.catrobat.estimationplugin.helper.GroupHelper;
import org.catrobat.estimationplugin.reports.EstimationReport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentAccessor.class, JqlQueryBuilder.class})
public class EstimationReportTest {

    @Before
    public void setup() {
        PowerMockito.mockStatic(ComponentAccessor.class);

        GroupManager groupManager = Mockito.mock(GroupManager.class);
        ComponentAccessor componentAccessor = Mockito.mock(ComponentAccessor.class);
        Mockito.when(componentAccessor.getGroupManager()).thenReturn(groupManager);
        Group group = Mockito.mock(Group.class);
        Mockito.when(groupManager.getGroup("Test")).thenReturn(group);
        Collection<User> userCollection = Mockito.mock(Collection.class);
        Mockito.when(groupManager.getUsersInGroup(group)).thenReturn(userCollection);
        Mockito.when(userCollection.size()).thenReturn(1337);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testValidate() {
        SearchProvider searchProvider = Mockito.mock(SearchProvider.class);
        ProjectManager projectManager = Mockito.mock(ProjectManager.class);
        I18nHelper i18nHelper = Mockito.mock(I18nHelper.class);
        IssueManager issueManager = Mockito.mock(IssueManager.class);
        DateTimeFormatterFactory formatterFactory = Mockito.mock(DateTimeFormatterFactory.class);
        PluginSettingsFactory pluginSettingsFactory = Mockito.mock(PluginSettingsFactory.class);
        EstimationReport estimationReport = new EstimationReport(searchProvider, projectManager, i18nHelper,
                issueManager, formatterFactory, pluginSettingsFactory);
        ProjectActionSupport projectActionSupport = Mockito.mock(ProjectActionSupport.class);
        Map params = new HashMap();
        params.put("numprog", new Long(-1));

        estimationReport.validate(projectActionSupport, params);

        Mockito.verify(projectActionSupport, Mockito.atLeastOnce()).addError("interval", projectActionSupport.getText("estimation-report.projectid.invalid"));
    }

    @Test(expected=Exception.class)
    public void testSomething() throws Exception {

        //EstimationReport testClass = new EstimationReport();

        throw new Exception("EstimationReport has no tests!");

    }

}
