package org.entando.entando.apsadmin.user;

import static org.mockito.ArgumentMatchers.any;

import com.agiletec.aps.system.services.user.IUserManager;
import java.util.ArrayList;
import java.util.List;
import org.entando.entando.aps.system.services.userprofile.IUserProfileManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileFinderActionTest {

    @Mock
    private IUserManager userManager;
    @Mock
    private IUserProfileManager userProfileManager;

    @InjectMocks
    private UserProfileFinderAction action;

    @Test
    void shouldReturnBothUsersWithProfileAndWithoutProfile() throws Exception {
        Mockito.when(userManager.searchUsernames(null)).thenReturn(new ArrayList<>(List.of("admin", "editor")));
        Mockito.when(userProfileManager.searchId(any())).thenReturn(new ArrayList<>(List.of("admin", "oldUser")));
        List<String> usernames = action.getSearchResult();
        Assertions.assertEquals(3, usernames.size());
        Assertions.assertEquals("admin", usernames.get(0));
        Assertions.assertEquals("editor", usernames.get(1));
        Assertions.assertEquals("oldUser", usernames.get(2));
    }

    @Test
    void shouldReturnOnlyUsersWithProfile() throws Exception {
        Mockito.when(userProfileManager.searchId(any())).thenReturn(List.of("admin", "oldUser"));
        action.setWithProfile(1);
        List<String> usernames = action.getSearchResult();
        Assertions.assertEquals(2, usernames.size());
        Assertions.assertEquals("admin", usernames.get(0));
        Assertions.assertEquals("oldUser", usernames.get(1));
    }

    @Test
    void shouldReturnOnlyUsersWithoutProfile() throws Exception {
        Mockito.when(userManager.searchUsernames(null)).thenReturn(List.of("admin", "editor"));
        Mockito.when(userProfileManager.searchId(any())).thenReturn(List.of("admin", "oldUser"));
        action.setWithProfile(0);
        List<String> usernames = action.getSearchResult();
        Assertions.assertEquals(1, usernames.size());
        Assertions.assertEquals("editor", usernames.get(0));
    }
}
