package ru.vaneck.disappeared;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import ru.vaneck.disappeared.animation.GhostEffect;

@Mod(DisappearedConstants.MOD_ID)
public class DisappearedNeoforge {

    public DisappearedNeoforge(IEventBus eventBus) {

        DisappearedConstants.LOG.info("Hello NeoForge world!");
        DisappearedCommon.init();


    }

    @EventBusSubscriber(modid = DisappearedConstants.MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

//        @SubscribeEvent
//        public static void onAddClientResourceReloadListener(AddClientReloadListenersEvent event) {
//            event.addListener(Constants.RELOAD_LISTENER, new ResourceReloadListener());
//        }

//        @SubscribeEvent
//        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
//            DisappearedKeys.KEY_MAPPINGS.forEach(event::register);
//        }

//        @SubscribeEvent
//        public static void registerCommands(RegisterClientCommandsEvent event) {
//            DisappearedClientCommands.register(event.getDispatcher());
//        }

//        @SubscribeEvent
//        public static void onClientTick(ClientTickEvent.Post event) {
//            DisappearedKeys.handleKeys();
//        }

        @SubscribeEvent
        public static void onEntityLoad(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide() && event.getLevel().isClientSide()) {
                DisappearedCommon.appear(entity);
            }
        }

        @SubscribeEvent
        public static void onEntityUnload(EntityLeaveLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide() && event.getLevel().isClientSide()) {
                DisappearedCommon.disappear(entity);
            }
        }

        @SubscribeEvent
        public static void onClientChangeLevel(ClientPlayerNetworkEvent.LoggingIn event) {
            DisappearedCommon.updateDistances(null);
            GhostEffect.clearAll();
        }

//        @SubscribeEvent
//        public static void onPlayerLeaveGame(ClientStoppingEvent event) {
//            Constants.CONFIG.save();
//        }
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