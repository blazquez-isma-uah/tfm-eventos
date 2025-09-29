package com.tfm.bandas.eventos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var conv = new JwtAuthenticationConverter();
    // Extraemos los roles del claim "realm_access" de Keycloak
    // y los convertimos a GrantedAuthority con prefijo "ROLE_"
    // para que Spring Security los reconozca como roles
    conv.setJwtGrantedAuthoritiesConverter(SecurityConfig::extractRealmRoles);

    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsCfg()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    // Lectura: ADMIN o MUSICIAN
                    .requestMatchers(HttpMethod.GET, "/api/events/**").hasAnyRole("ADMIN","MUSICIAN")

                    // Escritura: solo ADMIN
                    .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,  "/api/events/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/events/**").hasRole("ADMIN")

                    // Endpoint público de calendario (lo añadimos en D)
                    .requestMatchers(HttpMethod.GET, "/api/public/events/calendar").permitAll()

                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)));

    return http.build();
  }

  /**
   * Extrae los roles del realm del token JWT de Keycloak (realm_access.roles) y los convierte
   * en una colección de GrantedAuthority con el prefijo "ROLE_". Esto permite que Spring Security
   * reconozca y utilice estos roles para la autorización basada en roles.
   * @param jwt
   * @return
   */
  private static Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
    var out = new HashSet<SimpleGrantedAuthority>();
    var realm = jwt.getClaimAsMap("realm_access");
    if (realm != null && realm.get("roles") instanceof List<?> roles) {
      for (Object r : roles) out.add(new SimpleGrantedAuthority("ROLE_" + r.toString()));
    }
    return new HashSet<GrantedAuthority>(out);
  }

  @Bean
  CorsConfigurationSource corsCfg() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://localhost:5173"));
    cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
    cfg.setAllowCredentials(true);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}

