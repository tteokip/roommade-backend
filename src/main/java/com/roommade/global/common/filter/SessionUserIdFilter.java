package com.roommade.global.common.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class SessionUserIdFilter implements Filter {

    private static final String USER_ID_ATTRIBUTE = "userId";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        Object userId = session == null ? null : session.getAttribute(USER_ID_ATTRIBUTE);
        if (userId instanceof Long) {
            chain.doFilter(new SessionUserIdRequestWrapper(httpRequest, userId.toString()), response);
            return;
        }

        chain.doFilter(request, response);
    }

    private static class SessionUserIdRequestWrapper extends HttpServletRequestWrapper {

        private final String userId;

        SessionUserIdRequestWrapper(HttpServletRequest request, String userId) {
            super(request);
            this.userId = userId;
        }

        @Override
        public String getHeader(String name) {
            if (USER_ID_HEADER.equalsIgnoreCase(name)) {
                return userId;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (USER_ID_HEADER.equalsIgnoreCase(name)) {
                return Collections.enumeration(List.of(userId));
            }
            return super.getHeaders(name);
        }
    }
}
