package com.mailcursor.auth;

public class AuthUserContext {

    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<AuthUser>();

    private AuthUserContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static AuthUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
