package org.entando.entando.web.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.user.UserDetails;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.entando.entando.aps.system.services.actionlog.IActionLogManager;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;
import org.entando.entando.web.common.annotation.ActivityStreamAuditable;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

class ActivityStreamInterceptorTest {


    @Test
    void shouldReadUserFromRequest() throws Exception {
        ActivityStreamInterceptor activityStreamInterceptor = new ActivityStreamInterceptor();
        IActionLogManager actionLogManager = mock(IActionLogManager.class);
        ArgumentCaptor<ActionLogRecord> captor = ArgumentCaptor.forClass(ActionLogRecord.class);
        ReflectionTestUtils.setField(activityStreamInterceptor, "actionLogManager", actionLogManager);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HandlerMethod handler = mock(HandlerMethod.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user1");
        when(request.getAttribute("user")).thenReturn(userDetails);
        when(request.getRequestURI()).thenReturn("/do/Page");
        Method method = mock(Method.class);
        when(method.isAnnotationPresent(RequestMapping.class)).thenReturn(true);
        when(method.isAnnotationPresent(ActivityStreamAuditable.class)).thenReturn(true);
        when(handler.getMethod()).thenReturn(method);
        when(method.getAnnotation(RequestMapping.class)).thenReturn(new DoubleRequestMapping());

        activityStreamInterceptor.postHandle(request, response, handler, null);
        verify(actionLogManager).addActionRecord(captor.capture());
        assertEquals("user1", captor.getValue().getUsername());

    }

    @Test
    void shouldUseGuestUserNameIfUserIsNotPresentInTheRequest() throws Exception {
        ActivityStreamInterceptor activityStreamInterceptor = new ActivityStreamInterceptor();
        IActionLogManager actionLogManager = mock(IActionLogManager.class);
        ArgumentCaptor<ActionLogRecord> captor = ArgumentCaptor.forClass(ActionLogRecord.class);
        ReflectionTestUtils.setField(activityStreamInterceptor, "actionLogManager", actionLogManager);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HandlerMethod handler = mock(HandlerMethod.class);
        when(request.getRequestURI()).thenReturn("/do/Page");
        Method method = mock(Method.class);
        when(method.isAnnotationPresent(RequestMapping.class)).thenReturn(true);
        when(method.isAnnotationPresent(ActivityStreamAuditable.class)).thenReturn(true);
        when(handler.getMethod()).thenReturn(method);
        when(method.getAnnotation(RequestMapping.class)).thenReturn(new DoubleRequestMapping());

        activityStreamInterceptor.postHandle(request, response, handler, null);
        verify(actionLogManager).addActionRecord(captor.capture());
        assertEquals("guest", captor.getValue().getUsername());

    }
}




