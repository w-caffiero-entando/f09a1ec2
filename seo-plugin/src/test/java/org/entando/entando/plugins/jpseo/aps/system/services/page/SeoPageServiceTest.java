package org.entando.entando.plugins.jpseo.aps.system.services.page;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.agiletec.aps.BaseTestCase;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPageManager;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SeoPageServiceTest extends BaseTestCase {

    private IPageService pageService;
    private IPageManager pageManager;

    @Test
    public void testGetBuiltInSeoPage() throws Exception {
        PageDto page = this.pageService.getPage("service", IPageService.STATUS_DRAFT);
        assertEquals("service", page.getCode());
    }

    @BeforeEach
    private void init() throws Exception {
        try {
            pageService = (IPageService) this.getApplicationContext().getBean("SeoPageService");
            pageManager = (IPageManager) this.getApplicationContext().getBean(SystemConstants.PAGE_MANAGER);
        } catch (Exception e) {
            throw e;
        }
    }
}