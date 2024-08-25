package com.example.terrestrial_tutor.security;

public class SecurityConstants {
    public static String SIGN_UP_URLS = "/api/auth/**";
    public static String FILES_URL = "/api/tasks/files/**";

    public static final String SECRET = "SecretKeyGenJWT";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

}
