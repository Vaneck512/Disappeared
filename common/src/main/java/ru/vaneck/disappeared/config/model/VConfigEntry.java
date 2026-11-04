package ru.vaneck.disappeared.config.model;

import java.util.ArrayList;
import java.util.List;

public class VConfigEntry<T extends Comparable<T>> {
    private final List<String> description = new ArrayList<>();
    private final String key;
    private T value;
    private final T minValue;
    private final T maxValue;
    private final T defaultValue;

    private VConfig config = null;

    public VConfigEntry(String key, T value) {
        this.key = key;
        this.value = value;
        this.minValue = null;
        this.maxValue = null;
        this.defaultValue = value;
    }

    public VConfigEntry(String key, T value, T minValue, T maxValue) {
        this.key = key;
        this.value = value;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.defaultValue = value;
    }

    public void setConfig(VConfig vConfig) {
        this.config = vConfig;
    }

    public VConfigEntry<T> addDescLine(String line) {
        description.add(line);
        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    public List<String> getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (this.maxValue != null && value.compareTo(this.maxValue) > 0)
            this.value = this.maxValue;
        else if (this.minValue != null && value.compareTo(this.minValue) < 0)
            this.value = this.minValue;
        else
            this.value = value;
        if (this.config != null)
            this.config.save();
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    @SuppressWarnings("unchecked")
    public void setValueFromString(String stringValue) {
        if (stringValue == null) return;

        Object parsedValue = null;
        Class<?> type = value.getClass();

        try {
            if (type == Integer.class) {
                parsedValue = Integer.valueOf(stringValue);
            } else if (type == Double.class) {
                parsedValue = Double.valueOf(stringValue);
            } else if (type == Float.class) {
                parsedValue = Float.valueOf(stringValue);
            } else if (type == Long.class) {
                parsedValue = Long.valueOf(stringValue);
            } else if (type == Boolean.class) {
                parsedValue = Boolean.valueOf(stringValue);
            } else if (type == String.class) {
                parsedValue = stringValue;
            } else if (type.isEnum()) {
                parsedValue = Enum.valueOf((Class<Enum>) type, stringValue.toUpperCase());
            }
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return;
        }

        if (parsedValue != null) {
            setValue((T) parsedValue);
        }
    }
}
