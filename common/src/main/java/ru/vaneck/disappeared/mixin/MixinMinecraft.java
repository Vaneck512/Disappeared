package ru.vaneck.disappeared.mixin;

import net.minecraft.client.GameLoadCookie;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.vaneck.disappeared.files.SoundResourcesCache;
import ru.vaneck.disappeared.files.Utils;
import ru.vaneck.disappeared.sound.SoundManager;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    // Clear and stop all executors when the game is closed
    @Inject(method = "close", at=@At("TAIL"))
    public void gameOnClose$disappeared(CallbackInfo ci) {
        Utils.clearAll();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick$disappeared(CallbackInfo ci) {
        Minecraft.getInstance().execute(SoundManager::update);
    }

    @Inject(method = "reloadResourcePacks(ZLnet/minecraft/client/GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;", at = @At("TAIL"))
    private void onResourcePacksLoaded(boolean isRecovery, @Nullable GameLoadCookie loadCookie, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenRun(() -> {
            try {
                SoundResourcesCache.load();
            } catch (IOException _) {}
        });
    }
}