package org.catrobat.estimationplugin.reports;

import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.apache.log4j.Logger;
import org.catrobat.estimationplugin.calc.EstimationCalculator;
import org.catrobat.estimationplugin.helper.GroupHelper;
import org.catrobat.estimationplugin.misc.ReportParams;

import java.util.Map;

public class EstimationReport extends AbstractReport {

    private static final Logger log = Logger.getLogger(EstimationReport.class);
    private final SearchProvider searchProvider;
    private final DateTimeFormatterFactory formatterFactory;
    private final ProjectManager projectManager;
    private final IssueManager issueManager;
    private final I18nHelper helper;
    private final PluginSettingsFactory pluginSettingsFactory;

    public EstimationReport(SearchProvider searchProvider, ProjectManager projectManager, I18nHelper helper,
                            IssueManager issueManager, DateTimeFormatterFactory formatterFactory,
                            PluginSettingsFactory pluginSettingsFactory) {
        this.searchProvider = searchProvider;
        this.projectManager = projectManager;
        this.helper = helper;
        this.issueManager = issueManager;
        this.formatterFactory = formatterFactory;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
        ApplicationUser remoteUser = action.getLoggedInApplicationUser();
        Long projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
        Long filterId = new Long(0);
        String filterOrProjectId = ParameterUtils.getStringParam(params, "projectid");

        Long numprog = ParameterUtils.getLongParam(params, "numprog");

        String userGroup = ParameterUtils.getStringParam(params, "usergroup");
        if (userGroup != "none considered" && userGroup != "test" && numprog == 0) {
            numprog = (long) GroupHelper.getCountOfGroup(userGroup);
        }

        ReportParams reportParams = new ReportParams(searchProvider, remoteUser, formatterFactory);

        if (filterOrProjectId.startsWith("project-")) {
            projectId = Long.parseLong(filterOrProjectId.replaceFirst("project-", ""));
            reportParams.setConfigureParams(false, projectId, numprog);
        } else if (filterOrProjectId.startsWith("filter-")) {
            filterId = Long.parseLong(filterOrProjectId.replaceFirst("filter-", ""));
            reportParams.setConfigureParams(true, filterId, numprog);
        } else if(filterOrProjectId.equals("")) {
            projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
            reportParams.setConfigureParams(false, projectId, numprog);
        } else {
            throw new AssertionError("neither project nor filter id");
        }

        String timeConsidered = ParameterUtils.getStringParam(params, "basedon");
        Long lastX = ParameterUtils.getLongParam(params, "lastx");
        reportParams.setTimeConsideredParams(timeConsidered, lastX);

        EstimationCalculator estimationCalculator = new EstimationCalculator(reportParams);
        Map<String, Object> velocityParams;

        velocityParams = estimationCalculator.calculateOutputParams();

        velocityParams.put("projectName", projectManager.getProjectObj(projectId).getName());
        velocityParams.put("countMember", numprog);
        velocityParams.put("probability", new Float(0));
        velocityParams.put("deviation", new Float(0));

        return descriptor.getHtml("view", velocityParams);
    }

    public void validate(ProjectActionSupport action, Map params) {
        Long numprog = ParameterUtils.getLongParam(params, "numprog");
        Long projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
        String userGroup = ParameterUtils.getStringParam(params, "usergroup");

        if (numprog == null || numprog.longValue() < 0)
            action.addError("interval", action.getText("estimation-report.interval.invalid"));
        if (projectId == null)
            action.addError("selectedProjectId", action.getText("estimation-report.projectid.invalid"));
        if (userGroup != "none" && GroupHelper.getCountOfGroup(userGroup) < 1)
            action.addError("usergroup", "error no users in group");
        //TODO: check whether user group contains members
    }
}
