package com.tfm.bandas.eventos.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Stream;

public class RolesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
  private final String resourceClient; // opcional, p.ej. "events"

  public RolesExtractor(String resourceClient) {
    this.resourceClient = resourceClient;
  }

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Set<String> roles = new HashSet<>();

    // 1) roles / authorities plano
    roles.addAll(getStringList(jwt.getClaim("roles")));
    roles.addAll(getStringList(jwt.getClaim("authorities")));

    // 2) Keycloak: realm_access.roles
    Map<String,Object> realm = jwt.getClaim("realm_access");
    if (realm != null) roles.addAll(getStringList(realm.get("roles")));

    // 3) Keycloak: resource_access.{client}.roles
    Map<String,Object> ra = jwt.getClaim("resource_access");
    if (ra != null && resourceClient != null) {
      Object client = ra.get(resourceClient);
      if (client instanceof Map<?,?> rc) {
        roles.addAll(getStringList(rc.get("roles")));
      }
    }

    // Prefijo ROLE_
    return roles.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
        .distinct()
        .map(s -> (GrantedAuthority) new SimpleGrantedAuthority(s))
        .toList();
  }

  @SuppressWarnings("unchecked")
  private static List<String> getStringList(Object claim) {
    if (claim instanceof Collection<?> c) {
      return c.stream().filter(Objects::nonNull).map(Object::toString).toList();
    }
    return List.of();
  }
}
