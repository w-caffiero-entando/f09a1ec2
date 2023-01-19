package org.entando.entando.apsadmin.portal.model;

import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.aps.system.services.pagemodel.PageModelManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageModelActionTest {

    @Mock
    private PageModelManager pageModelManager;

    @InjectMocks
    @Spy
    private PageModelAction action;

    @Test
    void testShowDetailsNullPageModel() {
        Mockito.doReturn("text").when(action).getText(Mockito.anyString());
        Assertions.assertEquals("pageModelList", action.showDetails());
    }

    @Test
    void testGetPageModelDetailsJson() {
        String pageModelCode = "page_model";
        PageModel expectedModel = new PageModel();
        expectedModel.setCode(pageModelCode);
        Mockito.when(pageModelManager.getPageModel(pageModelCode)).thenReturn(expectedModel);
        action.setCode(pageModelCode);
        PageModel pageModel = action.getPageModelDetailsJson();
        Assertions.assertEquals(expectedModel, pageModel);
    }
}
