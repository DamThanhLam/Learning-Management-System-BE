package fit.iuh.edu.com.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/course/search").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .anonymous(anonymousConfigurer -> anonymousConfigurer.principal("guest").authorities("ROLE_ANONYMOUS"));
        return http.build();
    }
//    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("cognito:groups");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_TEACHER > ROLE_STUDENT");
    }
}
//how to login sso aws in the postman