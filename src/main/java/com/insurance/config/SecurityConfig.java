
package com.insurance.config;

import com.insurance.security.JwtAuthenticationFilter;
import com.insurance.service.AgentUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AgentUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AgentUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/", "/*.html", "/index.html", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
                // Swagger UI and OpenAPI endpoints
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                // Authentication endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Policyholder authentication endpoints
                .requestMatchers("/api/policyholder-auth/**").permitAll()
                // Policyholder profile endpoints - accessible with valid JWT token
                .requestMatchers("/api/policyholder/**").hasRole("POLICYHOLDER")
                // Dashboard endpoints
                .requestMatchers("/api/dashboard/**").hasAnyRole("AGENT", "ADMIN")
                // Policyholder endpoints
                .requestMatchers(HttpMethod.GET, "/api/policyholders/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/policyholders/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/policyholders/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/policyholders/**").hasRole("ADMIN")
                // Policy endpoints
                .requestMatchers(HttpMethod.GET, "/api/policies/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/policies/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/policies/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/policies/**").hasRole("ADMIN")
                // Report endpoints
                .requestMatchers(HttpMethod.GET, "/api/reports/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/reports/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/reports/**").hasAnyRole("AGENT", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/reports/**").hasRole("ADMIN")
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(userDetailsService);
        prov.setPasswordEncoder(passwordEncoder());
        return prov;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}
