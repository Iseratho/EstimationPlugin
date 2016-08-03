package org.catrobat.estimationplugin.reports;

import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.project.ProjectManager;
import org.catrobat.estimationplugin.calc.BackwardCalculator;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EstimationPluginBackward extends AbstractReport {

    private final ProjectManager projectManager;
    private final SearchProvider searchProvider;
    private final DateTimeFormatterFactory formatterFactory;
    public EstimationPluginBackward(ProjectManager projectManager, SearchProvider searchProvider, DateTimeFormatterFactory formatterFactory) {
        this.projectManager = projectManager;
        this.searchProvider = searchProvider;
        this.formatterFactory = formatterFactory;
    }

    public String generateReportHtml(ProjectActionSupport projectActionSupport, Map params) throws Exception {
        Map<String, Object> velocityParams = new HashMap<>();
        Long projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
        Date finishDate = ParameterUtils.getDateParam(params, "finishDate", Locale.ENGLISH);
        //TODO make work for non current project or filter

        ApplicationUser applicationUser = projectActionSupport.getLoggedInApplicationUser();
        BackwardCalculator backwardCalculator = new BackwardCalculator(searchProvider, applicationUser);

        Long filterId = new Long(0);
        String filterOrProjectId = ParameterUtils.getStringParam(params, "projectid");
        if (filterOrProjectId.startsWith("project-")) {
            projectId = Long.parseLong(filterOrProjectId.replaceFirst("project-", ""));
            velocityParams = backwardCalculator.calculateOutputParams(projectId, false, finishDate);
        } else if (filterOrProjectId.startsWith("filter-")) {
            filterId = Long.parseLong(filterOrProjectId.replaceFirst("filter-", ""));
            velocityParams = backwardCalculator.calculateOutputParams(filterId, true, finishDate);
        } else if(filterOrProjectId.equals("")) {
            projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
            velocityParams = backwardCalculator.calculateOutputParams(projectId, false, finishDate);
        } else {
            throw new AssertionError("neither project nor filter id");
        }

        backwardCalculator.calculateOutputParams(projectId, false, finishDate);


        velocityParams.put("projectName", projectManager.getProjectObj(projectId).getName());
        return descriptor.getHtml("view", velocityParams);
    }

    public void validate(ProjectActionSupport action, Map params) {
        Date finishDate = ParameterUtils.getDateParam(params, "finishDate", Locale.ENGLISH);
        Long probability = ParameterUtils.getLongParam(params, "probability");

        if (finishDate == null || finishDate.before(new Date())) {
            action.addError("finishDate", "Date must be after today");
            //action.addError("interval", action.getText("estimation-report.interval.invalid"));
        }
        if (probability == null || probability.longValue() <= 0 || probability.longValue() >= 100) {
            action.addError("probability", "Error: value must be between 0 and 100");
            //action.addError("selectedProjectId", action.getText("estimation-report.projectid.invalid"));
        }
    }
}
