package com.agiletec.aps.system.services.url;

import com.agiletec.aps.system.RequestContext;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageURLTest {

    @Mock
    private IURLManager urlManager;
    @Mock
    private RequestContext reqCtx;

    @InjectMocks
    private PageURL pageURL;

    @Test
    void shouldSetParamRepeat() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(reqCtx.getRequest()).thenReturn(request);
        Mockito.when(request.getParameterMap()).thenReturn(
                Map.of("param1", new String[]{}, "param2", new String[]{})
        );
        Mockito.when(request.getParameter("param2")).thenReturn("value2");

        pageURL.setParamRepeat(List.of("param1"));

        Assertions.assertEquals(1, pageURL.getParams().size());
        Assertions.assertEquals("value2", pageURL.getParams().get("param2"));
    }
}
