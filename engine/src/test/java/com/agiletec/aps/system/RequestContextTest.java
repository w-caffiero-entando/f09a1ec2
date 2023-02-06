package com.agiletec.aps.system;

import org.apache.ws.commons.schema.constants.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestContextTest {

    @Test
    void shouldAddAndGetCurrentFrameOrWidgetParamsToReqCtxThreadLocal(){
        final String paramValue = "testvalue";
        final String keyTest = "reqKeyTest";
        RequestContext reqCtx = new RequestContext();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, paramValue);
        Assertions.assertEquals(paramValue, ReqCtxThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.addExtraParam(keyTest, paramValue);
        Assertions.assertNull(ReqCtxThreadLocal.get(keyTest));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(keyTest));

        ReqCtxThreadLocal.init();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, paramValue);
        Assertions.assertEquals(paramValue, ReqCtxThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_WIDGET));

    }

    @Test
    void shouldRemoveFrameOrWidgetParamsToReqCtxThreadLocal(){
        final String paramValue = "testvalue";
        final String keyTest = "reqKeyTest";
        RequestContext reqCtx = new RequestContext();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, paramValue);
        Assertions.assertEquals(paramValue, ReqCtxThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
        Assertions.assertNull(ReqCtxThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.addExtraParam(keyTest, paramValue);
        reqCtx.removeExtraParam(keyTest);
        Assertions.assertNull(reqCtx.getExtraParam(keyTest));

    }
}
