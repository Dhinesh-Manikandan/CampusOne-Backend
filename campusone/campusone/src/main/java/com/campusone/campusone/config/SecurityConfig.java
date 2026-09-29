package com.campusone.campusone.config;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.campusone.campusone.dto.response.ApiErrorResponse;
import com.campusone.campusone.security.JwtAuthenticationFilter;
import com.campusone.campusone.security.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
	private String allowedOrigins;

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			CustomUserDetailsService customUserDetailsService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		List<String> origins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();
		configuration.setAllowedOriginPatterns(origins);
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, exception) -> {
					Object authError = request.getAttribute("auth_error_message");
					String message = authError instanceof String ? (String) authError : "Unauthorized";
					writeError(response, HttpStatus.UNAUTHORIZED, message, request.getRequestURI());
				})
						.accessDeniedHandler((request, response, exception) -> writeError(response, HttpStatus.FORBIDDEN,
								"Forbidden", request.getRequestURI())))
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login",
						"/api/auth/refresh").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id:[0-9]+}", "/api/events/{id:[0-9]+}/participants/count", "/api/announcements/event/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/app-admin-requests")
						.hasAnyRole("STUDENT", "EVENT_ADMIN").requestMatchers("/error").permitAll()
						.requestMatchers("/api/admin/event-admin-requests/**").hasAnyRole("EVENT_ADMIN", "APP_ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/admin/application-admins/event-admins").hasAnyRole("EVENT_ADMIN", "APP_ADMIN")
						.requestMatchers("/api/admin/**").hasRole("APP_ADMIN").requestMatchers("/api/event-admin/**")
						.hasAnyRole("EVENT_ADMIN", "APP_ADMIN").requestMatchers("/api/student/**")
						.hasAnyRole("STUDENT", "EVENT_ADMIN", "APP_ADMIN")
						.anyRequest().authenticated())
				.authenticationManager(authenticationManager())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authenticationProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private void writeError(jakarta.servlet.http.HttpServletResponse response, HttpStatus status, String message,
			String path) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");
		ApiErrorResponse error = new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(),
				message, path);
		response.getWriter().write("{\"timestamp\":\"" + error.timestamp() + "\",\"status\":"
				+ error.status() + ",\"error\":\"" + escapeJson(error.error()) + "\",\"message\":\""
				+ escapeJson(error.message()) + "\",\"path\":\"" + escapeJson(error.path()) + "\"}");
	}

	private String escapeJson(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
