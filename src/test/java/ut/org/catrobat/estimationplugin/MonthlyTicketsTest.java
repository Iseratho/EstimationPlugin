package ut.org.catrobat.estimationplugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.user.ApplicationUser;
import org.catrobat.estimationplugin.calc.MonthlyTickets;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.catrobat.estimationplugin.misc.ReportParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Matchers;

import com.atlassian.jira.datetime.DateTimeFormatter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ComponentAccessor.class)
public class MonthlyTicketsTest {

    private MonthlyTickets monthlyTickets;
    private IssueListCreator issueListCreator;

    private List<String> finishedIssuesStatus = new ArrayList<String>();

    @Before
    public void setUp() throws Exception {
        Issue issue = Mockito.mock(Issue.class);
        new MockComponentWorker().addMock(Issue.class, issue).init();

        finishedIssuesStatus.add("Closed");

        PowerMockito.mockStatic(ComponentAccessor.class);
        ChangeHistoryManager changeHistoryManager = Mockito.mock(ChangeHistoryManager.class);

        Mockito.when(ComponentAccessor.getChangeHistoryManager()).thenReturn(changeHistoryManager);

        SearchProvider searchProvider = Mockito.mock(SearchProvider.class);
        ApplicationUser applicationUser = Mockito.mock(ApplicationUser.class);

        DateTimeFormatterFactory formatterFactory = Mockito.mock(DateTimeFormatterFactory.class);
        DateTimeFormatter dateTimeFormatter = Mockito.mock(DateTimeFormatter.class);
        Mockito.when(formatterFactory.formatter()).thenReturn(dateTimeFormatter);
        Mockito.when(dateTimeFormatter.withStyle(DateTimeStyle.ISO_8601_DATE)).thenReturn(dateTimeFormatter);
        Mockito.when(dateTimeFormatter.format(Matchers.any())).thenReturn("teststring");
        CustomFieldManager customFieldManager = Mockito.mock(CustomFieldManager.class);
        Mockito.when(ComponentAccessor.getCustomFieldManager()).thenReturn(customFieldManager);
        ReportParams reportParams = new ReportParams(searchProvider, applicationUser, formatterFactory);
        reportParams.setConfigureParams(false, new Long(0), new Long(0));
        monthlyTickets = new MonthlyTickets(reportParams);
        issueListCreator = Mockito.mock(IssueListCreator.class);
        List<Issue> finishedIssueList = new ArrayList<>();
        Issue issue1 = Mockito.mock(Issue.class);

        finishedIssueList.add(issue1);
        Mockito.when(issueListCreator.getIssueListForStatus(Matchers.any(), Matchers.any(), Matchers.anyBoolean())).thenReturn(finishedIssueList);

        Mockito.when(issue1.getCreated()).thenReturn(new Timestamp(new Date().getTime()));
        Mockito.when(issue1.getResolutionDate()).thenReturn(new Timestamp(new Date().getTime()));

        ChangeHistory changeHistory1 = Mockito.mock(ChangeHistory.class);
        Mockito.when(changeHistory1.getTimePerformed()).thenReturn(new Timestamp(new Date().getTime()));

        List<ChangeHistory> changeHistoryList = new ArrayList<>();
        changeHistoryList.add(changeHistory1);
        Mockito.when(changeHistoryManager.getChangeHistories(Matchers.any())).thenReturn(changeHistoryList);

        monthlyTickets.setIssueListCreator(issueListCreator);
    }

    @Test
    public void testCalculateTicketsPerMonth() throws  SearchException {
        monthlyTickets.calculateTicketsPerMonth(new Date(), new Date());
        return;
    }

    @Test
    public void testGetTicketsPerMonth() throws SearchException {
        monthlyTickets.setStartDate(new Date());
        monthlyTickets.setEndDate(new Date());
        Map<String, Object> data = monthlyTickets.getTicketsPerMonth();
        assertNotNull(data);
        assert(data.size() == 5);
        assertNotNull(data.get("ticketsPerMonthList"));
        assertNotNull(data.get("ticketsPerMonthLabels"));
        assertNotNull(data.get("ticketsPerMonthCount"));
        assertNotNull(data.get("startdate"));
        assertNotNull(data.get("enddate"));

        assertTrue(data.get("ticketsPerMonthList") instanceof List);
        assertTrue(data.get("ticketsPerMonthLabels") instanceof String);
        assertTrue(data.get("ticketsPerMonthCount") instanceof List);
        assertTrue(data.get("startdate") instanceof Date);
        assertTrue(data.get("enddate") instanceof Date);
    }
}
