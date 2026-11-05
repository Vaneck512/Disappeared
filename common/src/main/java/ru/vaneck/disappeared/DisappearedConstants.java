package ru.vaneck.disappeared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vaneck.disappeared.platform.Services;

import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class DisappearedConstants {

    public static final String MOD_ID = "disappeared";
    public static final String MOD_NAME = "Disappeared";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final Pattern IDENTIFIER_PATTERN = Pattern.compile("([a-z0-9_.-]+:[a-z0-9_/.-]+)");
    // .minecraft/config/disappeared/
    public static final Supplier<Path> MAIN_WORK_DIRECTORY = () -> Services.PLATFORM.getGameDirectory().resolve("config").resolve(MOD_ID);
}