package com.example.gymerp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private static final String[] SWAGGER = {
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
        "/swagger-resources/**", "/webjars/**", "/configuration/ui", "/configuration/security",
        "/upload/**", "/v1/product/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // 🔹 CSRF 비활성화 (테스트용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 🔹 React CORS 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(SWAGGER).permitAll() // Swagger 허용
                .requestMatchers("/upload/**").permitAll()
                .requestMatchers("/v1/emp/login", "/v1/emp/logout", "/v1/member/**", "/v1/sales/**").permitAll() // 로그인 허용

                .requestMatchers("/v1/pt/**").permitAll()     // Swagger 테스트용 PT API 허용
                .requestMatchers("/v1/schedule/**").permitAll() // 일정 관련 API Swagger 테스트 허용
                .anyRequest().authenticated()
            )
            .formLogin(login -> login.disable()) // 🔹 폼 로그인 비활성화
            .httpBasic(basic -> basic.disable()); // 🔹 기본 로그인 비활성화

        return http.build();
    }

    // BCryptPasswordEncoder 등록
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 메니저 Bean 등록
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder encoder,
                                               UserDetailsService service) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(service)
                   .passwordEncoder(encoder)
                   .and()
                   .build();
    }

    // REACT(React:5173)에서의 요청 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Vite 개발 서버
        // Swagger (Spring 내부)

        config.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:9000")); 

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));


        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}