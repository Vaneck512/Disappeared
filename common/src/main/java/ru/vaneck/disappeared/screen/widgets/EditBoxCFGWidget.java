package ru.vaneck.disappeared.screen.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.vaneck.disappeared.config.model.VConfigEntry;

public class EditBoxCFGWidget extends EditBox {
    private final VConfigEntry<Long> vConfigEntry;
    public EditBoxCFGWidget(Font font, int x, int y, int width, int height, VConfigEntry<Long> configEntry) {
        super(font, x, y, width, height, Component.literal(""));
        vConfigEntry = configEntry;
        setValue(vConfigEntry.getValue()+"");
        setResponder(text -> {
            if (!text.isEmpty() && text.matches("\\d+")) {
                try {
                    configEntry.setValue(Long.parseLong(text));
                } catch (NumberFormatException _) {}
            }
        });
    }

    @Override
    public void insertText(@NonNull String text) {
        if (vConfigEntry.getMaxValue() != null && getValue().length()+1 > (""+vConfigEntry.getMaxValue()).length())
            return;
        if (text.matches("\\d+") && getValue().length() < vConfigEntry.getMaxValue().toString().length())
            super.insertText(text);
    }

    @Override
    public boolean isHovered() {
        if (vConfigEntry.getMinValue() != null)
            setTooltip(Tooltip.create(Component.literal(vConfigEntry.getMinValue() + " - " + vConfigEntry.getMaxValue())));
        return super.isHovered();
    }
}
