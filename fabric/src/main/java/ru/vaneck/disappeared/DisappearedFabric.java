package ru.vaneck.disappeared;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import ru.vaneck.disappeared.animation.GhostEffect;

public class DisappearedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DisappearedCommon.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(
                    ClientCommands.literal("disappeared").then(
                            ClientCommands.argument("lifeTime", LongArgumentType.longArg())
                                    .executes(context -> {
                                        GhostEffect.submitGhost(Minecraft.getInstance().player, LongArgumentType.getLong(context, "lifeTime"));
                                        return 1;
                                    })
                    )
            );
            dispatcher.register(
                    ClientCommands.literal("disapClear")
                            .executes(context -> {
                                GhostEffect.clearAll();
                                return 1;
                            })
            );
        });
    }
}
