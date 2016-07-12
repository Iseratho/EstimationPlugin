package ut.org.catrobat.estimationplugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.ConditionBuilder;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import org.catrobat.estimationplugin.jql.IssueListCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentAccessor.class, JqlQueryBuilder.class})
public class IssueListCreatorTest {

    private IssueListCreator issueListCreator;
    Issue testIssue;

    List<String> status;
    Long aLong = new Long(0);
    Date testDate = new Date();
    JqlClauseBuilder jqlClauseBuilderMock;

    @Before
    public void setUp() throws SearchException {
        PowerMockito.mockStatic(ComponentAccessor.class);
        PowerMockito.mockStatic(JqlQueryBuilder.class);

        //ComponentAccessor componentAccessor = Mockito.mock(ComponentAccessor.class, Mockito.RETURNS_DEEP_STUBS);

        jqlClauseBuilderMock = Mockito.mock(JqlClauseBuilder.class);
        JqlQueryBuilder jqlQueryBuilderMock = Mockito.mock(JqlQueryBuilder.class);
        Query queryMock = Mockito.mock(Query.class);
        ConditionBuilder conditionBuilderMock = Mockito.mock(ConditionBuilder.class);
        Mockito.when(jqlQueryBuilderMock.where()).thenReturn(jqlClauseBuilderMock);
        Mockito.when(JqlQueryBuilder.newBuilder()).thenReturn(jqlQueryBuilderMock);
        Mockito.when(JqlQueryBuilder.newBuilder(Matchers.any())).thenReturn(jqlQueryBuilderMock);

        Mockito.when(jqlClauseBuilderMock.project(Matchers.anyLong())).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.savedFilter()).thenReturn(conditionBuilderMock);
        Mockito.when(conditionBuilderMock.eq(Matchers.anyLong())).thenReturn(jqlClauseBuilderMock);
        Mockito.when(conditionBuilderMock.eq(Matchers.anyString())).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.and()).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.or()).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.sub()).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.endsub()).thenReturn(jqlClauseBuilderMock);
        Mockito.when(jqlClauseBuilderMock.status()).thenReturn(conditionBuilderMock);
        Mockito.when(jqlClauseBuilderMock.buildQuery()).thenReturn(queryMock);
        Mockito.when(jqlClauseBuilderMock.resolutionDateBetween(Matchers.any(Date.class), Matchers.any(Date.class))).thenReturn(jqlClauseBuilderMock);

        SearchProvider searchProvider = Mockito.mock(SearchProvider.class);
        ApplicationUser applicationUser = Mockito.mock(ApplicationUser.class);
        SearchResults searchResults = Mockito.mock(SearchResults.class);

        Mockito.when(searchProvider.search(queryMock, applicationUser, PagerFilter.getUnlimitedFilter())).thenReturn(searchResults);
        //Mockito.when(searchProvider.search(Matchers.any(), Matchers.eq(applicationUser), Matchers.eq(PagerFilter.getUnlimitedFilter()))).thenReturn(searchResults);
        List<Issue> issueList = new ArrayList<>();
        issueList.add(testIssue);
        Mockito.when(searchResults.getIssues()).thenReturn(issueList);

        issueListCreator = new IssueListCreator(searchProvider, applicationUser);
        status = new ArrayList<>();
        status.add("Hallo");
        status.add("Test");
    }

    @Test
    public void testProject() throws SearchException {
        List<String> status = new ArrayList<>();
        status.add("Hallo");
        status.add("Test");
        //List<Issue> issueList = issueListCreator.getIssueListForStatus(aLong, status, false);
        long test = issueListCreator.getMonthlyResolution(aLong, status, false, testDate, testDate);

        Mockito.verify(jqlClauseBuilderMock, Mockito.atLeast(1)).project(Matchers.anyLong());
        Mockito.verify(jqlClauseBuilderMock, Mockito.never()).savedFilter();
    }

    @Test
    public void testFilter() throws SearchException {
        List<String> status = new ArrayList<>();
        status.add("Hallo");
        status.add("Test");
        //List<Issue> issueList = issueListCreator.getIssueListForStatus(aLong, status, false);
        long test = issueListCreator.getMonthlyResolution(aLong, status, true, testDate, testDate);

        Mockito.verify(jqlClauseBuilderMock, Mockito.never()).project(Matchers.anyLong());
        Mockito.verify(jqlClauseBuilderMock, Mockito.atLeast(1)).savedFilter();
    }

    @Test
    public void testZero() throws SearchException {
        List<String> status = new ArrayList<>();
        //List<Issue> issueList = issueListCreator.getIssueListForStatus(aLong, status, false);
        long test = issueListCreator.getMonthlyResolution(aLong, status, true, testDate, testDate);

        Mockito.verify(jqlClauseBuilderMock, Mockito.never()).status();
    }

    @Test
    public void testOnce() throws SearchException {
        List<String> status = new ArrayList<>();
        status.add("sdkfk");
        //List<Issue> issueList = issueListCreator.getIssueListForStatus(aLong, status, false);
        long test = issueListCreator.getMonthlyResolution(aLong, status, true, testDate, testDate);

        Mockito.verify(jqlClauseBuilderMock, Mockito.atLeastOnce()).status();
    }

    @Test
    public void testMultiple() throws SearchException {
        List<String> status = new ArrayList<>();
        status.add("sdkfk");
        status.add("sdkfk");
        status.add("sdkfk");
        //List<Issue> issueList = issueListCreator.getIssueListForStatus(aLong, status, false);
        long test = issueListCreator.getMonthlyResolution(aLong, status, true, testDate, testDate);

        Mockito.verify(jqlClauseBuilderMock, Mockito.atLeast(3)).status();
    }
}
