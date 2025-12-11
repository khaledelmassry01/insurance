package com.insurance.security;

import com.insurance.service.AgentUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final AgentUserDetailsService userDetailsService; // For agent authentication
    // We might need a PolicyholderUserDetailsService if complex loading is needed, 
    // but for now, we can construct UserDetails directly from token claims.

    public JwtAuthenticationFilter(
            JwtTokenProvider tokenProvider,
            @Qualifier("agentUserDetailsService") AgentUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (tokenProvider.validateToken(token)) {
                    if (tokenProvider.isPolicyholderToken(token)) {
                        String email = tokenProvider.getEmailFromPolicyholderToken(token);
                        List<String> roles = tokenProvider.getRolesFromToken(token);

                        if (email != null && !roles.isEmpty()) {
                            List<SimpleGrantedAuthority> authorities = roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                            UserDetails userDetails = new User(email, "[N/A]", authorities);
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            logger.warn("Policyholder token did not contain necessary claims (email or roles).");
                        }
                    } else { // Assumed to be an Agent token
                        int agentId = tokenProvider.getAgentIdFromToken(token);
                        AgentUserDetails userDetails = userDetailsService.loadByAgentId(agentId);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Could not set user authentication in security context", e);
            // Ensure SecurityContext is cleared on error to prevent issues with subsequent filters
            SecurityContextHolder.clearContext(); 
        }
        filterChain.doFilter(request, response);
    }
}
