package com.tfm.bandas.events.utils;

public class Constants {
    public static final String[] PATTERNS_PERMITED = {"/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};
    public static final String[] PATTERNS_AUTHENTICATED = {"/api/events/**"};
    public static final String[] PATTERNS_PUBLIC = {"/api/events/public/calendar"};

}
