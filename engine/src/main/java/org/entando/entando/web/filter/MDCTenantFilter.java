package org.entando.entando.web.filter;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

public class MDCTenantFilter extends HttpFilter {

    private static final String MDC_KEY_TENANT = "tenant";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put(MDC_KEY_TENANT, ApsTenantApplicationUtils.getTenant().orElse(""));
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY_TENANT);
        }
    }
}
