package com.example.maintenance.shared.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import java.util.*;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder decoder, org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter converter, CustomAuthenticationEntryPoint entry, CustomAccessDeniedHandler denied) throws Exception {
  return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .exceptionHandling(e->e.authenticationEntryPoint(entry).accessDeniedHandler(denied))
   .authorizeHttpRequests(a->a.requestMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll().requestMatchers(HttpMethod.GET,"/api/v1/categories").permitAll().requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/actuator/health").permitAll().requestMatchers("/api/v1/**").authenticated().anyRequest().permitAll())
   .oauth2ResourceServer(o->o.jwt(j->j.decoder(decoder).jwtAuthenticationConverter(converter)).authenticationEntryPoint(entry)).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") String origins) {
  CorsConfiguration config=new CorsConfiguration(); config.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList()); config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); config.setAllowedHeaders(List.of("Authorization","Content-Type")); config.setAllowCredentials(false); UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",config); return source;
 }
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
