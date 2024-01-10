package com.example.multitenants.util;

public class CurrentTenant {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String get() {
        return CURRENT_TENANT.get();
    }

    public static void set(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static void unset() {
        CURRENT_TENANT.remove();
    }
}
