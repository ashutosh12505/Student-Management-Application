package com.practice.student_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(
	        HttpSecurity http,
	        JwtService jwtService,
	        UserDetailsService userDetailsService)
	        throws Exception {

	    JwtAuthenticationFilter jwtAuthenticationFilter =
	            new JwtAuthenticationFilter(
	                    jwtService,
	                    userDetailsService
	            );

	    http
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .csrf(csrf -> csrf.disable())

	        .authorizeHttpRequests(auth -> auth

	            .requestMatchers(HttpMethod.OPTIONS, "/**")
	                .permitAll()

	            .requestMatchers("/auth/login")
	                .permitAll()

	            .requestMatchers(HttpMethod.GET, "/students")
	                .hasAnyRole("USER", "ADMIN")

	            .requestMatchers(HttpMethod.GET, "/students/*")
	                .hasAnyRole("USER", "ADMIN")

	            .requestMatchers(HttpMethod.POST, "/students")
	                .hasRole("ADMIN")

	            .requestMatchers(HttpMethod.PUT, "/students/*")
	                .hasRole("ADMIN")

	            .requestMatchers(HttpMethod.DELETE, "/students/*")
	                .hasRole("ADMIN")

	            .anyRequest().authenticated()
	        )
	        
	        .exceptionHandling(exception -> exception

	                .authenticationEntryPoint(
	                    (request, response, authException) ->
	                        response.setStatus(
	                            HttpServletResponse.SC_UNAUTHORIZED
	                        )
	                )

	                .accessDeniedHandler(
	                    (request, response, accessDeniedException) ->
	                        response.setStatus(
	                            HttpServletResponse.SC_FORBIDDEN
	                        )
	                )
	            )

	        .addFilterBefore(
	            jwtAuthenticationFilter,
	            UsernamePasswordAuthenticationFilter.class
	        );

	    return http.build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration configuration) throws Exception {

	    return configuration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:30080"));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("*"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}
}