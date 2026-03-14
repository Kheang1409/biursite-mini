package com.biursite.config;

import com.biursite.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /** API chain — stateless JWT for /api/** */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/posts/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()
                )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new ApiAuthenticationEntryPoint())
                .accessDeniedHandler(new ApiAccessDeniedHandler())
            );
        return http.build();
    }

    /** MVC chain — session-based form login for pages */
    @Bean
    @Order(2)
    public SecurityFilterChain mvcFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(auth -> auth
                    // Public (view) routes
                    .requestMatchers("/", "/login", "/register", "/posts", "/posts/*", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/error/**", "/404", "/403", "/500", "/posts/*/json").permitAll()
                    // Protected MVC routes (require authentication)
                    .requestMatchers("/posts/new", "/posts/*/edit", "/posts/*/delete", "/profile", "/profile/**").authenticated()
                    // Admin routes
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // Allow everything else to fall through to DispatcherServlet (so unmapped routes return 404)
                    .anyRequest().permitAll()
                )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/posts", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
