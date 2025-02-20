package com.example.todolist.config;

import com.example.todolist.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final UserService userService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserService userService){
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    // method untuk konfigurasi keamanan spring
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        // http.csrf disable csrf (cross site request forgery)
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(coreCustomizer -> coreCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true); // mengizinkan kredensial
                        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // menizinkan apa saja yang bisa akses resource ini
                        corsConfiguration.addAllowedHeader("*"); // mengizinkan semu header
                        corsConfiguration.addAllowedMethod("*"); // mengizinkan semua method (post, put, get, delete, dll)
                        corsConfiguration.setMaxAge(3600L); // durasi dalam detik
                        return corsConfiguration;
                    }
                }))
                // pengaturan otorisasi (siapa aja yang bisa akses endpoint)
                .authorizeHttpRequests(session -> session
                        // user
                        .requestMatchers(HttpMethod.GET,"/api/user/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/user/id/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/user/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/user/login2").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/user/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/user/delete").hasRole("ADMIN")

                        // todolist
                        .requestMatchers(HttpMethod.GET,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/api/todolist/**").permitAll()

                        // category
                        .requestMatchers(HttpMethod.GET,"/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/category/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // ngatur session untuk tidak menyimpan informasi user di dalam session tapi pake jwt
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // method bua otentikasi user
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // ngambil data user dari database
    @Bean
    public UserDetailsService userDetailService(){
        return userService::loadUserByUsername;
    }

    // buat nge encode passwors
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }

}
