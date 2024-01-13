package com.example.multitenants.util.interceptor;

import com.example.multitenants.util.CurrentTenant;
import com.example.multitenants.util.ExistedTenants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequiredArgsConstructor
public final class TenantInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantInterceptor.class);

    private static final String X_TENANT = "X-Tenant";

    private final ObjectMapper objectMapper;
    private final ExistedTenants existedTenants;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (request.getRequestURI().startsWith("/login")) {
            final var xTenantName = request.getHeader(X_TENANT);
            if (xTenantName == null) {
                return selectTenantAndContinue("master");
            }
            var tenant = existedTenants.findTenant(xTenantName);
            if (tenant.isEmpty()) {
                return respondUnknownTenant(xTenantName, response);
            }
            return selectTenantAndContinue(tenant.get().getSchema());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {
        CurrentTenant.unset();
        LOGGER.debug("Removed tenant assigned previously before sending response to client");
    }

    private boolean respondUnknownTenant(String xTenantId, HttpServletResponse response) throws IOException {
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Unknown database tenant");
        problemDetail.setDetail("Value of header X-Tenant does not match a known database tenant");
        problemDetail.setProperty("tenantId", xTenantId);

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(objectMapper.writeValueAsString(problemDetail));
        return false;
    }

    private boolean selectTenantAndContinue(String schema) {
        CurrentTenant.set(schema);
        LOGGER.debug("Handling request for tenant {}", CurrentTenant.get());
        return true;
    }
}