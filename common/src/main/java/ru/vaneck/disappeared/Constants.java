package ru.vaneck.disappeared;

import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationsAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vaneck.disappeared.platform.Services;

import java.nio.file.Path;

public class Constants {

    public static final String MOD_ID = "disappeared";
    public static final String MOD_NAME = "Disappeared";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    // .minecraft/config/disappeared/
    public static final Path MAIN_WORK_DIRECTORY = Services.PLATFORM.getGameDirectory().resolve("config").resolve(MOD_ID);
}