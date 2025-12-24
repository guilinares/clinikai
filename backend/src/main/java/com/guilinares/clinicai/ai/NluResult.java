package com.guilinares.clinicai.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class NluResult {

    private String intent;
    private Map<String, Object> entities;

    public <T> T getEntity(String key, Class<T> type) {
        if (entities == null) return null;
        Object value = entities.get(key);
        if (value == null) return null;
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public <T> java.util.List<T> getListEntity(String key, Class<T> itemType) {
        if (entities == null) return java.util.List.of();
        Object value = entities.get(key);
        if (!(value instanceof java.util.List<?> rawList)) return java.util.List.of();

        var out = new java.util.ArrayList<T>();
        for (Object item : rawList) {
            if (itemType.isInstance(item)) out.add(itemType.cast(item));
        }
        return out;
    }

}
