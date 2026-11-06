package ru.vaneck.disappeared;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.Minecraft;
import ru.vaneck.disappeared.animation.GhostEffect;
import ru.vaneck.disappeared.keys.DisappearedKeys;

public class DisappearedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        DisappearedCommon.init();

//        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
//            DisappearedClientCommands.register(dispatcher);
//        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> DisappearedKeys.handleKeys());

        DisappearedKeys.KEY_MAPPINGS.forEach(KeyMappingHelper::registerKeyMapping);

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, _) -> {
            DisappearedCommon.disappear(entity);
        });

        ClientEntityEvents.ENTITY_LOAD.register(((entity, level) -> {
            DisappearedCommon.appear(entity);
        }));

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> {
            GhostEffect.clearAll();
            DisappearedCommon.updateDistances(null);
        });

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
