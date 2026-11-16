package ru.vaneck.disappeared;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import ru.vaneck.disappeared.animation.GhostEffect;
import ru.vaneck.disappeared.config.DisappearedConfiguration;
import ru.vaneck.disappeared.files.SoundInstanceRecord;
import ru.vaneck.disappeared.files.SoundResourcesCache;
import ru.vaneck.disappeared.files.Utils;
import ru.vaneck.disappeared.platform.Services;
import ru.vaneck.disappeared.sound.SoundManager;
import ru.vaneck.sap.lib.SAP;
import ru.vaneck.sap.lib.sound.AudioType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DisappearedCommon {

    private static final DisappearedConfiguration configuration = DisappearedConfiguration.CONFIGURATION;

    public static void init() {
        if (Services.PLATFORM.isModLoaded("disappeared")) {
            DisappearedConstants.LOG.info("Disappeared is loaded.");
        }
        SoundResourcesCache.init();

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            Utils.runOnResourceManagerLoaded(() -> {
                SoundInstanceRecord s = SoundResourcesCache.getActiveSounds().get(ThreadLocalRandom.current().nextInt(SoundResourcesCache.getActiveSounds().size()));
                DisappearedConstants.LOG.info("Playing " + s.fileName);
                SAP.play(s.path, DisappearedConstants.IDENTIFIER_PATTERN.matcher(s.path).find() ? AudioType.LOCATION : AudioType.FILE, 0.25f);
                DisappearedConstants.LOG.info(SoundResourcesCache.ALL_SOUNDS.toString());
                DisappearedConstants.LOG.info(SoundResourcesCache.ALL_SOUNDS.stream().toList().getFirst().toString());
            });
        }
    }

    public static final List<Double> DISTANCES = new ArrayList<>();

    public static void updateDistances(Entity entity) {
        if (entity == null) {
            DISTANCES.clear();
            return;
        }
        int maxSize = 25;
        Player clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) return;
        double distance = (double) Math.round(clientPlayer.position().distanceTo(entity.position()));
        distance = distance >= 30 ? distance : 30;
        if (entity instanceof Player) {
            if (DISTANCES.size() > maxSize + 5)
                DISTANCES.clear();
            if (DISTANCES.size() >= maxSize)
                DISTANCES.removeFirst();
            DISTANCES.addLast(distance);
        }
    }

    public static double getWorkRadius() {
        return DISTANCES.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(60D) - 0.5;
    }


    public static void appear(Entity justEntity) {
        if (!configuration.ENABLE_MOD.getValue()) return;

        updateDistances(justEntity);
        if (!(justEntity instanceof LivingEntity l) || l instanceof ArmorStand) return;
        Player clientPlayer = Minecraft.getInstance().player;

        if (!configuration.ONLY_PLAYERS.getValue() && !(justEntity instanceof Mob) && !(justEntity instanceof Player))
            return;

        if (
                clientPlayer == null ||
                        (configuration.ONLY_PLAYERS.getValue() && !(l instanceof Player))
                        || clientPlayer.position().distanceTo(l.position()) > getWorkRadius()
                        || l.getUUID().equals(clientPlayer.getUUID())
                        || !l.level().equals(clientPlayer.level())
                        || l.isInvisible()

        ) return;

        boolean isMusicMob = false;
        long lifeTime = configuration.APPEAR_ANIMATION_DURATION.getValue();

        if (configuration.SHOW_GHOSTS.getValue())
            GhostEffect.submitReversedGhost(l, lifeTime, isMusicMob);
    }

    public static void disappear(Entity justEntity) {
        if (!configuration.ENABLE_MOD.getValue()) return;
        updateDistances(justEntity);
        if (!configuration.ONLY_PLAYERS.getValue() && !(justEntity instanceof Mob) && !(justEntity instanceof Player))
            return;

        if (!(justEntity instanceof LivingEntity l) || l instanceof ArmorStand) return;

        if (justEntity instanceof Creeper && !justEntity.isAlive()) return; // Disable for creepers (animation and sound was played after the explosion)

        Vec3 entityPos = l.position();
        Player clientPlayer = Minecraft.getInstance().player;
        if (
                clientPlayer == null ||
                        (configuration.ONLY_PLAYERS.getValue() && !(l instanceof Player))
                        || clientPlayer.position().distanceTo(entityPos) > getWorkRadius()
                        || justEntity.getRemovalReason() == null
                        || justEntity.getRemovalReason() == Entity.RemovalReason.UNLOADED_WITH_PLAYER
                        || justEntity.getRemovalReason() == Entity.RemovalReason.UNLOADED_TO_CHUNK
                        || l.isInvisibleTo(clientPlayer)
                        || l.isInvisible()
                        || l.getUUID().equals(clientPlayer.getUUID())
                        || !l.level().equals(clientPlayer.level())
                        || (l.getHealth() <= 0.0F && !configuration.PLAY_ANIMATION_AFTER_DEATH.getValue())
        ) return;
        if (configuration.SHOW_GHOSTS.getValue())
            GhostEffect.submitGhost(l, configuration.DISAPPEAR_ANIMATION_DURATION.getValue());

        if (l.getHealth() <= 0.0F && configuration.PLAY_ANIMATION_AFTER_DEATH.getValue())
            return; // death animation without sound
        if (configuration.ENABLE_SOUNDS.getValue())
            playRandomSound(l.position());

    }

    private static void playRandomSound(Vec3 position) {
        List<SoundInstanceRecord> activeSounds = SoundResourcesCache.getActiveSounds();
        if (activeSounds.isEmpty()) return;
        SoundInstanceRecord randomSound = activeSounds.get(ThreadLocalRandom.current().nextInt(activeSounds.size()));
        SoundManager.playSound(randomSound.path, settings -> {
            settings.setMaxDistance(70);
            settings.setReferenceDistance(25);
            settings.setVolume(randomSound.volume * configuration.MASTER_VOLUME.getValue()/100);
            settings.setSoundChannel(SoundSource.MASTER);
            settings.setPosition(position);
        }, true);
    }

}