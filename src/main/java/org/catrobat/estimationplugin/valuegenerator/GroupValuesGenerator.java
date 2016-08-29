package org.catrobat.estimationplugin.valuegenerator;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.groups.GroupManager;
import org.catrobat.estimationplugin.helper.GroupHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroupValuesGenerator implements ValuesGenerator{

    @Override
    public Map getValues(Map map) {
        Map<String, String> groupMap = new HashMap<String, String>();
        //groupMap.put("none considered", "none considered");
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        Collection<Group> groupsCollection = groupManager.getAllGroups();
        for (Group group : groupsCollection) {
            String groupName = group.getName();
            int count = GroupHelper.getCountOfGroup(groupName);
            if (count > 0) {
                groupMap.put(groupName, groupName);
            }
        }
        //groupMap.put("test", "test");
        return groupMap;
    }
}

