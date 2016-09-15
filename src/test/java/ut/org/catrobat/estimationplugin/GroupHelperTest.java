package ut.org.catrobat.estimationplugin;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.groups.GroupManager;
import org.catrobat.estimationplugin.helper.GroupHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentAccessor.class})
public class GroupHelperTest {

    @Test
    public void testGetCountOfGroup() {
        PowerMockito.mockStatic(ComponentAccessor.class);
        GroupManager groupManager = Mockito.mock(GroupManager.class);
        ComponentAccessor componentAccessor = Mockito.mock(ComponentAccessor.class);
        Mockito.when(componentAccessor.getGroupManager()).thenReturn(groupManager);
        Group group = Mockito.mock(Group.class);
        Mockito.when(groupManager.getGroup("Test")).thenReturn(group);
        Collection<User> userCollection = Mockito.mock(Collection.class);
        Mockito.when(groupManager.getUsersInGroup(group)).thenReturn(userCollection);
        Mockito.when(userCollection.size()).thenReturn(1337);
        int count = GroupHelper.getCountOfGroup("Test");
        assertEquals(1337, count);
    }
}
