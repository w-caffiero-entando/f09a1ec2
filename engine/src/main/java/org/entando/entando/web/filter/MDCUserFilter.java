package org.entando.entando.web.filter;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.MDC;

public class MDCUserFilter extends HttpFilter {

    private static final String MDC_KEY_USER = "user";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MDC.put(MDC_KEY_USER, getCurrentUser(request));
        chain.doFilter(request, response);
        MDC.remove(MDC_KEY_USER);
    }

    private String getCurrentUser(HttpServletRequest servletRequest) {
        UserDetails userDetails = (UserDetails) servletRequest.getAttribute("user");
        if (userDetails != null) {
            return userDetails.getUsername();
        }
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            userDetails = (UserDetails) session.getAttribute("user");
            if (userDetails != null) {
                return userDetails.getUsername();
            }
        }
        return SystemConstants.GUEST_USER_NAME;
    }
}
