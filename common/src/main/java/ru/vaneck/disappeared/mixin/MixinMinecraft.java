package ru.vaneck.disappeared.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.vaneck.disappeared.files.Utils;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    // Clear and stop all executors when the game is closed
    @Inject(method = "close", at=@At("TAIL"))
    public void gameOnClose$disappeared(CallbackInfo ci) {
        Utils.clearAll();
    }

}