package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.user.UserDetails;
import org.entando.entando.aps.system.services.TreeNodeHelper;

public class PageTreeNodeHelper extends TreeNodeHelper<IPage> {

    private final IPageManager pageManager;
    private final IPageAuthorizationService authorizationService;
    private final UserDetails user;

    public PageTreeNodeHelper(IPageManager pageManager, IPageAuthorizationService authorizationService, UserDetails user) {
        this.pageManager = pageManager;
        this.authorizationService = authorizationService;
        this.user = user;
    }

    @Override
    public IPage getTreeNode(String nodeCode) {
        return this.pageManager.getDraftPage(nodeCode);
    }

    @Override
    protected boolean isNodeAllowed(IPage page) {
        return this.authorizationService.canEdit(user, page.getCode());
    }
}
