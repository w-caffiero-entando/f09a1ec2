package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import java.util.Collection;
import org.entando.entando.aps.system.services.TreeNodeHelper;

public class PageTreeNodeHelper extends TreeNodeHelper<IPage> {

    private final IPageManager pageManager;

    public PageTreeNodeHelper(IPageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public IPage getTreeNode(String nodeCode) {
        return this.pageManager.getDraftPage(nodeCode);
    }

    @Override
    protected boolean isNodeAllowed(IPage page, Collection<String> groupCodes) {
        return groupCodes.contains(page.getGroup()) || groupCodes.contains(Group.ADMINS_GROUP_NAME);
    }
}
