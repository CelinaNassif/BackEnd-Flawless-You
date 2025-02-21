package com.flawlessyou.backend.Security;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;

import com.flawlessyou.backend.Security.Jwt.AuthEntryPointJwt;
import com.flawlessyou.backend.Security.Jwt.AuthTokenFilter;
import com.flawlessyou.backend.Security.Services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
  
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    private static final String[] WHITE_LIST_URL = {
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/swagger-resources/**",
      "/swagger-ui.html",
      "/webjars/**",
      "/swagger",
      "/error",
      "/api/auth/**",
      "/authenticate"
  };

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:57191",  
            "https://accounts.google.com",
            "https://www.googleapis.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "*",
            "Authorization",
            "Content-Type",
            "Cross-Origin-Opener-Policy",
            "Cross-Origin-Embedder-Policy"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Cross-Origin-Opener-Policy",
            "Cross-Origin-Embedder-Policy"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Change from "/" to "/**" to match all paths
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .headers(headers -> headers
        .httpStrictTransportSecurity().disable() 
            .frameOptions().disable() 
            .crossOriginOpenerPolicy(policy -> 
                policy.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN_ALLOW_POPUPS))
            .crossOriginEmbedderPolicy(embedder -> 
                embedder.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP))
        )
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exception -> 
            exception.authenticationEntryPoint(unauthorizedHandler))
        .authorizeHttpRequests(auth -> 
            auth
            .requestMatchers(WHITE_LIST_URL).permitAll()
            .requestMatchers("/oauth2/**", "/login/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()  
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/product/product").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/product/{productId}").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/cards/{id}/reply").hasRole("SKIN_EXPERT")
            .requestMatchers(HttpMethod.PUT, "/product/product").hasRole("ADMIN")
                                      .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/oauth2/callback/**").permitAll()
                .anyRequest().authenticated()
              

        )
        .formLogin(AbstractHttpConfigurer::disable) // تعطيل form login
        .httpBasic(AbstractHttpConfigurer::disable)
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(authorization -> 
                authorization.baseUri("/oauth2/authorize"))
            .redirectionEndpoint(redirection -> 
                redirection.baseUri("/oauth2/callback/*"))
            .defaultSuccessUrl("/api/auth/success", true)
            .failureUrl("/api/auth/failure")
        )
        .formLogin(Customizer.withDefaults());

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), 
                        UsernamePasswordAuthenticationFilter.class);

    return http.build();
}}