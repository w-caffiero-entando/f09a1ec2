package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import org.entando.entando.aps.system.services.page.model.PageDto;

public interface IPageAuthorizationService {

    String PAGE_AUTHORIZATION_SERVICE_BEAN_NAME = "PageAuthorizationService";

    boolean canView(UserDetails user, String pageCode);

    boolean canView(UserDetails user, String pageCode, boolean allowFreeGroup);

    boolean canEdit(UserDetails user, String pageCode);

    List<PageDto> filterList(UserDetails user, List<PageDto> toBeFiltered);

    List<String> getGroupCodesForReading(UserDetails user);

    List<String> getGroupCodesForEditing(UserDetails user);
}
