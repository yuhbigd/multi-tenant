package com.example.multitenants.util;

public class Constants {
    public static enum USER_ROLE implements IEnum<String> {
        ADMIN,
        USER;

        @Override
        public String getValue() {
            return this.name();
        }
    }

    public static enum JWT_CLAIM_KEY implements IEnum<String> {
        CLAIM_KEY_ROLES("role"),
        CLAIM_KEY_TENANT("tenant");

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private JWT_CLAIM_KEY(String value) {
            this.value = value;
        }
    }
}