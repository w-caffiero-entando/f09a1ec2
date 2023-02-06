package org.entando.entando.apsadmin.portal.rs.validator;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.pagemodel.PageModel;
import com.agiletec.apsadmin.portal.PageConfigAction;
import org.entando.entando.apsadmin.portal.rs.model.SwapWidgetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SwapWidgetRequestValidatorTest {

    @Mock
    private IPageModelManager pageModelManager;

    @InjectMocks
    private SwapWidgetRequestValidator validator;

    @Test
    void testValidateRequestInvalidDest() {
        String pageCode = "page_code";
        String pageModelCode = "page_model";

        IPage page = Mockito.mock(IPage.class);
        PageMetadata pageMetadata = new PageMetadata();
        pageMetadata.setModelCode(pageModelCode);
        Mockito.when(page.getMetadata()).thenReturn(pageMetadata);

        PageModel pageModel = new PageModel();
        Mockito.when(pageModelManager.getPageModel(pageModelCode)).thenReturn(pageModel);

        PageConfigAction action = Mockito.mock(PageConfigAction.class);
        Mockito.when(action.getPage(pageCode)).thenReturn(page);
        Mockito.when(action.getText("error.request.dest.invalid")).thenReturn("Invalid");

        SwapWidgetRequest request = new SwapWidgetRequest();
        request.setPageCode(pageCode);
        request.setSrc(0);
        request.setDest(1);

        validator.validateRequest(request, action);

        Mockito.verify(action, Mockito.times(1)).addActionError("Invalid");
    }
}
