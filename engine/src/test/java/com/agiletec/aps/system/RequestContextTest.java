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

import freemarker.template.Configuration;
import freemarker.template.TemplateModel;
import org.entando.entando.aps.system.services.controller.executor.ExecutorBeanContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RequestContextTest {

    @Test
    void shouldSetExecutorBeanContainer() {
        RequestContext reqCtx = new RequestContext();
        ExecutorBeanContainer ebc = new ExecutorBeanContainer(
                Mockito.mock(Configuration.class), Mockito.mock(TemplateModel.class));
        reqCtx.setExecutorBeanContainer(ebc);
        Assertions.assertEquals(ebc, reqCtx.getExecutorBeanContainer());
    }
}
