/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.tags;

import static javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.entando.entando.aps.system.services.controller.executor.ExecutorBeanContainer;
import org.entando.entando.test_utils.UnitTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FreemarkerTemplateParameterTagTest {
    
    @Mock
    private PageContext pageContext;
    @Mock
    private ServletRequest servletRequest;
    @Mock
    private RequestContext reqCtx;
    @Mock
    private ExecutorBeanContainer executorBeanContainer;
    @Mock
    private JspWriter writer;


    @InjectMocks
    private FreemarkerTemplateParameterTag freemarkerTemplate;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(pageContext.getRequest()).thenReturn(this.servletRequest);
        when(servletRequest.getAttribute(RequestContext.REQCTX)).thenReturn(this.reqCtx);
        when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXECUTOR_BEAN_CONTAINER)).thenReturn(executorBeanContainer);
        Mockito.lenient().when(pageContext.getOut()).thenReturn(this.writer);
        this.freemarkerTemplate.release();
    }
    
    @Test
    void shouldOkWithNullParallel() throws Throwable {
        int result = this.freemarkerTemplate.doStartTag();
        Assertions.assertEquals(EVAL_BODY_INCLUDE, result);
        Mockito.verify(pageContext, Mockito.times(0)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void shouldOkWithNotNullParallel() throws Throwable {
        Map<String,String> envsOrig = System.getenv().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String,String> envs = (HashMap<String, String>) envsOrig.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        envs.put("PARALLEL_WIDGET_RENDER","true");
        UnitTestUtils.setEnv(envs);

        int result = this.freemarkerTemplate.doStartTag();
        Assertions.assertEquals(EVAL_BODY_INCLUDE, result);
        Mockito.verify(pageContext, Mockito.times(0)).getOut();
        Mockito.verify(pageContext, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.anyString());

        UnitTestUtils.setEnv(envsOrig);
    }

}
