package org.entando.entando.aps.system.services.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.group.IGroupManager;
import org.entando.entando.aps.system.services.group.model.GroupDto;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.group.model.GroupRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.group.GroupUtilizer;
import java.util.Optional;
import org.entando.entando.aps.system.services.component.ComponentUsageEntity;
import org.entando.entando.aps.system.services.component.IComponentDto;
import org.entando.entando.aps.system.services.group.model.GroupDtoBuilder;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private IGroupManager groupManager;
    
    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    public void setUp() throws Exception {
        this.groupService = new GroupService(groupManager, new GroupDtoBuilder(), null);
        this.groupService.setApplicationContext(this.applicationContext);
    }

    @Test
    void should_raise_exception_on_delete_reserved_group() {
        Group group = new Group();
        group.setName(Group.ADMINS_GROUP_NAME);
        when(groupManager.getGroup(group.getName())).thenReturn(group);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.removeGroup(group.getName());
        });
    }

    @Test
    void addExistingGroupShouldThrowValidationConflictException() {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.addGroup(groupReq);
        });
    }

    @Test
    void addExistingGroupWithDifferentDescriptionsShouldThrowValidationConflictException() {

        Group existingGroup = GroupTestHelper.stubTestGroup();
        existingGroup.setDescription("Description old");
        GroupRequest groupReq = GroupTestHelper.stubTestGroupRequest();

        when(groupManager.getGroup(anyString())).thenReturn(existingGroup);
        Assertions.assertThrows(ValidationConflictException.class, () -> {
            this.groupService.addGroup(groupReq);
        });
    }
    
    @Test
    void shouldFindComponentDto() throws Exception {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test description");
        when(groupManager.getGroup(group.getName())).thenReturn(group);
        Mockito.when(this.groupManager.getGroup("test")).thenReturn(group);
        Optional<IComponentDto> dto = this.groupService.getComponentDto("test");
        assertThat(dto).isNotEmpty();
        Assertions.assertTrue(dto.get() instanceof GroupDto);
        Assertions.assertEquals(ComponentUsageEntity.TYPE_GROUP, dto.get().getType());
    }
    
    @Test
    void shouldDeleteComponent() throws EntException {
        Group mock = Mockito.mock(Group.class);
        when(groupManager.getGroup("test")).thenReturn(mock);
        when(applicationContext.getBeanNamesForType(GroupUtilizer.class)).thenReturn(new String[0]);
        this.groupService.deleteComponent("test");
        verify(groupManager, times(1)).removeGroup(Mockito.any(Group.class));
    }

}
