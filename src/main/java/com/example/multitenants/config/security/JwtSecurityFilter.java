package com.example.multitenants.config.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.multitenants.entity.common.Role;
import com.example.multitenants.model.RoleModel;
import com.example.multitenants.model.UserModel;
import com.example.multitenants.util.Constants;
import com.example.multitenants.util.CurrentTenant;
import com.example.multitenants.util.ExistedTenants;
import com.example.multitenants.util.IEnum;
import com.example.multitenants.util.Constants.JWT_CLAIM_KEY;
import com.example.multitenants.util.Constants.USER_ROLE;
import com.example.multitenants.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ExistedTenants existedTenants;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring("Bearer ".length());
                if (jwtUtil.verifyToken(authToken)) {
                    DecodedJWT decodedToken = jwtUtil.getDecodedJWT(authToken);
                    String username = jwtUtil.getUserNameFromToken(decodedToken);
                    String tenant = jwtUtil.getClaimValue(Constants.JWT_CLAIM_KEY.CLAIM_KEY_TENANT.getValue(),
                            decodedToken,
                            String.class);
                    String[] authorities = jwtUtil.getArrayClaimValue(JWT_CLAIM_KEY.CLAIM_KEY_ROLES.getValue(),
                            decodedToken,
                            String.class);
                    checkAndSetTenant(tenant);
                    Set<RoleModel> roleModelSet = Arrays.stream(authorities)
                            .map(authority -> (USER_ROLE) IEnum.getEnumValueFromValue(authority, USER_ROLE.values())
                                    .orElseGet(null))
                            .filter(role -> role != null).map(userRole -> RoleModel.builder().role(userRole).build())
                            .collect(Collectors.toSet());
                    var authenticatedUser = UserModel.builder().username(username).tenant(tenant).roles(roleModelSet)
                            .build();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            authenticatedUser,
                            null, getAuthoritiesFromRoles(authorities));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

        }

    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromRoles(String[] roles) {
        return Arrays.stream(roles).map(role -> new SimpleGrantedAuthority(role)).toList();
    }

    public void checkAndSetTenant(String tenant) throws Exception {
        var tenantOpt = existedTenants.findTenant(tenant);
        if (tenantOpt.isPresent()) {
            CurrentTenant.set(tenant);
            return;
        }
        throw new Exception("INVALID TENANT");
    }
}
