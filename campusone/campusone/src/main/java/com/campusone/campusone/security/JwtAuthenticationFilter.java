package com.campusone.campusone.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.JwtException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path != null && (path.startsWith("/api/auth/") || path.equals("/error"));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = resolveToken(request);
		if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				if (jwtService.isAccessToken(token)) {
					String subject = jwtService.extractSubject(token);
					UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
					if (userDetails.isEnabled() && jwtService.isTokenValid(token, userDetails)) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					} else if (!userDetails.isEnabled()) {
						request.setAttribute("auth_error_message", "User account is disabled");
					}
				} else {
					request.setAttribute("auth_error_message", "Use an access token for this endpoint");
				}
			} catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
				request.setAttribute("auth_error_message", "Invalid or expired access token");
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (!StringUtils.hasText(header)) {
			return null;
		}
		String trimmedHeader = header.trim();
		if (trimmedHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
			String token = trimmedHeader.substring(7).trim();
			if (token.startsWith("\"") && token.endsWith("\"") && token.length() > 1) {
				token = token.substring(1, token.length() - 1).trim();
			}
			return token;
		}
		return null;
	}
}
