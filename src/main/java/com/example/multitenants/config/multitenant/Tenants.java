package com.example.multitenants.config.multitenant;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tenants {
    private Set<String> names;
}
