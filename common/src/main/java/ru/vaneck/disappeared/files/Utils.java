package ru.vaneck.disappeared.files;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import ru.vaneck.disappeared.DisappearedConstants;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    @SuppressWarnings("all")
    public static boolean isResourceManagerLoaded() {
        if (Minecraft.getInstance() == null) return false;
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager == null) return false;
        Optional<Resource> resource = resourceManager.getResource(Identifier.parse("disappeared:sounds/disappear.ogg"));
        return resource.isPresent();
    }

    public static void runOnResourceManagerLoaded(Runnable runnable) {
        AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        ScheduledFuture<?> future = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(256);
                if (isResourceManagerLoaded() && null != futureRef.get()) {
                    futureRef.get().cancel(true); // to stop next run
                    runnable.run();
                }
            } catch (Throwable exception) {
                futureRef.get().cancel(false);
                DisappearedConstants.LOG.error("Exception while playing sound: {}", exception.toString());
            }
        }, 993, 512, TimeUnit.MILLISECONDS);

        futureRef.set(future);
    }

    public static void clearAll() {
        SCHEDULED_EXECUTOR.shutdownNow();
    }
}
