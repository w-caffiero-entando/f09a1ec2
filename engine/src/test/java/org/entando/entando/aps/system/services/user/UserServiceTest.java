/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.system.services.authorization.Authorization;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.system.services.user.UserGroupPermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.services.IDtoBuilder;
import org.entando.entando.aps.system.services.assertionhelper.UserGroupPermissionAssertionHelper;
import org.entando.entando.aps.system.services.mockhelper.AuthorizationMockHelper;
import org.entando.entando.aps.system.services.mockhelper.UserMockHelper;
import org.entando.entando.aps.system.services.user.model.UserDto;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.entando.entando.aps.system.services.userprofile.model.IUserProfile;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.web.common.model.RestListRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserManager userManager;
    @Mock
    private IUserProfileManager userProfileManager;
    @Mock
    private IDtoBuilder<UserDetails, UserDto> dtoBuilder;

    @InjectMocks
    private UserService userService;

    private UserDetails userDetails = UserMockHelper.mockUser();
    private List<Authorization> authorizationList = AuthorizationMockHelper.mockAuthorizationList(3);

    @Test
    void getMyGroupPermissionsTest() {

        userDetails.addAuthorizations(authorizationList);

        List<UserGroupPermissions> expectedList = authorizationList.stream()
                .map(authorization -> new UserGroupPermissions(authorization.getGroup().getName(),
                        authorization.getRole().getPermissions()))
                .collect(Collectors.toList());

        List<UserGroupPermissions> actualList = userService.getMyGroupPermissions(userDetails);
        UserGroupPermissionAssertionHelper.assertUserGroupPermissions(expectedList, actualList);
    }

    @Test
    void getMyGroupPermissionsWithNoAuthorizationsTest() {

        List<UserGroupPermissions> actualList = userService.getMyGroupPermissions(userDetails);
        assertEquals(0, actualList.size());
    }

    @Test
    void getMyGroupPermissionsWithNullUserShouldReturnEmptyListTest() {

        List<UserGroupPermissions> actualList = userService.getMyGroupPermissions(null);
        assertEquals(0, actualList.size());
    }

    @Test
    void shouldLoadUserFromProfileIfUserDoesNotExistInKeycloak() throws EntException {
        Mockito.when(userManager.getUsernames()).thenReturn(new ArrayList<>(List.of("admin", "keycloakOnly")));
        Mockito.when(userProfileManager.searchId(Mockito.any())).thenReturn(List.of("admin", "oldUser"));
        mockExistingUser("admin");
        Mockito.when(userProfileManager.getProfile("oldUser")).thenReturn(Mockito.mock(IUserProfile.class));
        userService.getUsers(new RestListRequest(), "1");
        ArgumentCaptor<List<UserDetails>> usersCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(dtoBuilder, Mockito.times(1)).convert(usersCaptor.capture());
        List<UserDetails> users = usersCaptor.getValue();
        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals("admin", users.get(0).getUsername());
        Assertions.assertFalse(users.get(0).isDisabled());
        Assertions.assertEquals("oldUser", users.get(1).getUsername());
        Assertions.assertTrue(users.get(1).isDisabled());
    }

    @Test
    void shouldLoadOnlyUsersWithoutProfileIfWithProfileFlagIsDisabled() throws EntException {
        Mockito.when(userManager.getUsernames()).thenReturn(new ArrayList<>(List.of("admin", "keycloakOnly")));
        Mockito.when(userProfileManager.searchId(Mockito.any())).thenReturn(List.of("admin"));
        mockExistingUser("keycloakOnly");
        userService.getUsers(new RestListRequest(), "0");
        ArgumentCaptor<List<UserDetails>> usersCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(dtoBuilder, Mockito.times(1)).convert(usersCaptor.capture());
        List<UserDetails> users = usersCaptor.getValue();
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals("keycloakOnly", users.get(0).getUsername());
        Assertions.assertFalse(users.get(0).isDisabled());
    }

    @Test
    void shouldLoadBothUserWithProfileAndWithoutProfileIfWithProfileFlagIsNotSet() throws EntException {
        Mockito.when(userManager.getUsernames()).thenReturn(new ArrayList<>(List.of("admin", "keycloakOnly")));
        Mockito.when(userProfileManager.searchId(Mockito.any())).thenReturn(List.of("admin", "oldUser"));
        mockExistingUser("admin");
        mockExistingUser("keycloakOnly");
        Mockito.when(userProfileManager.getProfile("oldUser")).thenReturn(Mockito.mock(IUserProfile.class));
        userService.getUsers(new RestListRequest(), null);
        ArgumentCaptor<List<UserDetails>> usersCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(dtoBuilder, Mockito.times(1)).convert(usersCaptor.capture());
        List<UserDetails> users = usersCaptor.getValue();
        Assertions.assertEquals(3, users.size());
        Assertions.assertEquals("admin", users.get(0).getUsername());
        Assertions.assertFalse(users.get(0).isDisabled());
        Assertions.assertEquals("keycloakOnly", users.get(1).getUsername());
        Assertions.assertFalse(users.get(1).isDisabled());
        Assertions.assertEquals("oldUser", users.get(2).getUsername());
        Assertions.assertTrue(users.get(2).isDisabled());
    }

    private void mockExistingUser(String username) throws EntException {
        UserDetails user = Mockito.mock(UserDetails.class);
        Mockito.when(user.getUsername()).thenReturn(username);
        Mockito.when(userManager.getUser(username)).thenReturn(user);
    }
}
