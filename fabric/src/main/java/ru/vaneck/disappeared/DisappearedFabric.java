package ru.vaneck.disappeared;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
            dispatcher.register(
                    ClientCommands.literal("disappearedReversed").then(
                            ClientCommands.argument("lifeTime", LongArgumentType.longArg())
                                    .then(ClientCommands.argument("musicMob", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        builder.suggest("true");
                                        builder.suggest("false");
                                        return builder.buildFuture();
                                    }).executes(context -> {
                                                        GhostEffect.submitReversedGhost(Minecraft.getInstance().player, LongArgumentType.getLong(context, "lifeTime"), Boolean.parseBoolean(StringArgumentType.getString(context, "musicMob")));
                                                        return 1;
                                                    })
                                    )

                    )
            );
        });
    }
}
