package ru.vaneck.disappeared;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import ru.vaneck.disappeared.animation.GhostEffect;

@Mod(DisappearedConstants.MOD_ID)
public class DisappearedNeoforge {

    public DisappearedNeoforge(IEventBus eventBus) {

        DisappearedConstants.LOG.info("Hello NeoForge world!");
        DisappearedCommon.init();


    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = DisappearedConstants.MOD_ID)
    public static class Events {
        @SubscribeEvent
        public static void onCommandRegistrationCallback(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

            dispatcher.register(
                    Commands.literal("disappeared").then(
                            Commands.argument("lifeTime", LongArgumentType.longArg())
                                    .executes(context -> {
                                        GhostEffect.submitGhost(Minecraft.getInstance().player, LongArgumentType.getLong(context, "lifeTime"));
                                        return 1;
                                    })
                    )
            );
            dispatcher.register(
                    Commands.literal("disapClear")
                            .executes(context -> {
                                GhostEffect.clearAll();
                                return 1;
                            })
            );
            dispatcher.register(
                    Commands.literal("disappearedReversed").then(
                            Commands.argument("lifeTime", LongArgumentType.longArg())
                                    .then(Commands.argument("musicMob", StringArgumentType.word())
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
        }
    }
}