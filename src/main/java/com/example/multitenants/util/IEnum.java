package com.example.multitenants.util;

import java.util.Optional;

public interface IEnum<T> {
    T getValue();

    public static <V> Optional<IEnum<V>> getEnumValueFromValue(V value, IEnum<V>[] enums) {
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getValue().equals(value)) {
                return Optional.of(enums[i]);
            }
        }
        return Optional.empty();
    }
}