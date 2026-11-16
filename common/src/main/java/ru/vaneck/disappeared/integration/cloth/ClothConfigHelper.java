package ru.vaneck.disappeared.integration.cloth;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class ClothConfigHelper {

    private static final Component TITLE = Component.literal("Disappeared");
    private static final DisappearedConfiguration CONFIGURATION = DisappearedConfiguration.CONFIGURATION;

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(TITLE);

        ConfigCategory mainCategory = builder.getOrCreateCategory(Component.literal("Main"));

        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.ENABLE_MOD));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.ENABLE_SOUNDS));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.MASTER_VOLUME));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.SHOW_GHOSTS));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.DISAPPEAR_ANIMATION_DURATION));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.APPEAR_ANIMATION_DURATION));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.ONLY_PLAYERS));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.PLAY_ANIMATION_AFTER_DEATH));
        mainCategory.addEntry(castToCloth(builder, CONFIGURATION.MAX_GHOSTS));

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> AbstractConfigListEntry<?> castToCloth(ConfigBuilder builder, VConfigEntry<T> vConfigEntry) {
        AbstractFieldBuilder<?,?,?> entryBuilder = null;
        var defaultValue = vConfigEntry.getDefaultValue();
        StringBuilder tooltipLines = new StringBuilder();
        for (String line : vConfigEntry.getDescription()) {
            if (!line.isEmpty() && !line.contains("===========")) {
                tooltipLines.append(line).append("\n");
            }
        }
        if (!tooltipLines.isEmpty())
            tooltipLines.setLength(tooltipLines.length() - 1);
        Component tooltip = Component.literal(tooltipLines.toString());

        Component component = Component.literal(vConfigEntry.getKey());

        T value = vConfigEntry.getValue();
        if (value instanceof Boolean) {
            entryBuilder = builder.entryBuilder().startBooleanToggle(component, (Boolean) value)
                    .setDefaultValue((Boolean) defaultValue)
                    .setTooltip(tooltip)
                    .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
        } else if (value instanceof Integer) {
            if (vConfigEntry.getKey().contains("volume"))
                entryBuilder = builder.entryBuilder().startIntSlider(component, (Integer)value, (Integer)vConfigEntry.getMinValue(), (Integer)vConfigEntry.getMaxValue())
                        .setDefaultValue((Integer) defaultValue)
                        .setTooltip(tooltip)
                        .setMax((Integer) vConfigEntry.getMaxValue())
                        .setMin((Integer) vConfigEntry.getMinValue())
                        .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
            else
                entryBuilder = builder.entryBuilder().startIntField(component, (Integer) value)
                        .setDefaultValue((Integer) defaultValue)
                        .setTooltip(tooltip)
                        .setMax((Integer) vConfigEntry.getMaxValue())
                        .setMin((Integer) vConfigEntry.getMinValue())
                        .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
        } else if (value instanceof Long) {
            entryBuilder = builder.entryBuilder().startLongField(component, (Long) value)
                    .setDefaultValue((Long) defaultValue)
                    .setTooltip(tooltip)
                    .setMax((Long) vConfigEntry.getMaxValue())
                    .setMin((Long) vConfigEntry.getMinValue())
                    .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
        } else if (value instanceof Double) {
            entryBuilder = builder.entryBuilder().startDoubleField(component, (Double) value)
                    .setDefaultValue((Double) defaultValue)
                    .setTooltip(tooltip)
                    .setMax((Double) vConfigEntry.getMaxValue())
                    .setMin((Double) vConfigEntry.getMinValue())
                    .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
        } else if (value instanceof Float) {
            entryBuilder = builder.entryBuilder().startFloatField(component, (Float) value)
                    .setDefaultValue((Float) defaultValue)
                    .setTooltip(tooltip)
                    .setMax((Float) vConfigEntry.getMaxValue())
                    .setMin((Float) vConfigEntry.getMinValue())
                    .setSaveConsumer(newValue -> setValueAndSave(vConfigEntry, (T) newValue));
        }

        return entryBuilder != null ? entryBuilder.build() : null;
    }

    private static <T extends Comparable<T>> void setValueAndSave(VConfigEntry<T> configEntry, T value) {
        configEntry.setValue(value);
        CONFIGURATION.save();
    }
}