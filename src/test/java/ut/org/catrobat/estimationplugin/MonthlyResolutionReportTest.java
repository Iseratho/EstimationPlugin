package ut.org.catrobat.estimationplugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.catrobat.estimationplugin.calc.MonthlyTickets;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.catrobat.estimationplugin.reports.MonthlyResolutionReport;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentAccessor.class, MonthlyTickets.class})
public class MonthlyResolutionReportTest {

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {

    }

    @Ignore
    public void testGenerateReportHtml() throws Exception {
        PowerMockito.mockStatic(ComponentAccessor.class);
        SearchProvider searchProvider = Mockito.mock(SearchProvider.class);
        ProjectManager projectManager = Mockito.mock(ProjectManager.class);
        I18nHelper i18nHelper = Mockito.mock(I18nHelper.class);
        IssueManager issueManager = Mockito.mock(IssueManager.class);
        DateTimeFormatterFactory dateTimeFormatterFactory = Mockito.mock(DateTimeFormatterFactory.class);
        PluginSettingsFactory pluginSettingsFactory = Mockito.mock(PluginSettingsFactory.class);

        DateTimeFormatter dateTimeFormatter = Mockito.mock(DateTimeFormatter.class);
        Mockito.when(dateTimeFormatterFactory.formatter()).thenReturn(dateTimeFormatter);
        Mockito.when(dateTimeFormatter.withStyle(DateTimeStyle.ISO_8601_DATE)).thenReturn(dateTimeFormatter);

        ProjectActionSupport projectActionSupport = Mockito.mock(ProjectActionSupport.class);
        Map map = new HashMap<>();
        map.put("startDate", (new Date().toString()));
        map.put("endDate", (new Date().toString()));

        MonthlyResolutionReport monthlyResolutionReport = new MonthlyResolutionReport(searchProvider, projectManager, i18nHelper,
                issueManager, dateTimeFormatterFactory, pluginSettingsFactory);

        MonthlyTickets monthlyTickets = PowerMockito.mock(MonthlyTickets.class);

        Map<String, Object> velocityParams = new HashMap();
        Mockito.when(monthlyTickets.getTicketsPerMonth(Matchers.anyLong(), Matchers.anyBoolean())).thenReturn(velocityParams);

        String html = monthlyResolutionReport.generateReportHtml(projectActionSupport, map);
    }

}
