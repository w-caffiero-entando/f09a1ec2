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
package com.agiletec.aps.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestContextTest {

    @Test
    void shouldAddAndGetCurrentFrameOrWidgetParamsToReqCtxThreadLocal(){
        final String paramValue = "testvalue";
        final String keyTest = "reqKeyTest";
        RequestContext reqCtx = new RequestContext();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, paramValue);
        Assertions.assertEquals(paramValue, EntThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.addExtraParam(keyTest, paramValue);
        Assertions.assertNull(EntThreadLocal.get(keyTest));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(keyTest));

        EntThreadLocal.clear();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET, paramValue);
        Assertions.assertEquals(paramValue, EntThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_WIDGET));

    }

    @Test
    void shouldRemoveFrameOrWidgetParamsToReqCtxThreadLocal(){
        final String paramValue = "testvalue";
        final String keyTest = "reqKeyTest";
        RequestContext reqCtx = new RequestContext();
        reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME, paramValue);
        Assertions.assertEquals(paramValue, EntThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));
        Assertions.assertEquals(paramValue, reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.removeExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME);
        Assertions.assertNull(EntThreadLocal.get(SystemConstants.EXTRAPAR_CURRENT_FRAME));

        reqCtx.addExtraParam(keyTest, paramValue);
        reqCtx.removeExtraParam(keyTest);
        Assertions.assertNull(reqCtx.getExtraParam(keyTest));

    }
}
