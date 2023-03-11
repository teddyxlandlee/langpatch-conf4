package xland.mcmod.enchlevellangpatch.ext.conf4;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class LangPatchConfig {
    private final Path configurationFile;
    @Nullable
    private String enchantmentCfg = "none", potionCfg = "none";   // default: "null"
    private static final Pattern ID_PATTERN = Pattern.compile("^([a-z0-9\\u002d_]+:)?[a-z0-9\\u002d\\u002f_]+$");
    private static final Marker MARKER = MarkerManager.getMarker("LangPatch/Conf4");
    private static final Supplier<LangPatchConfig> INSTANCE = Suppliers.memoize(() ->
            new LangPatchConfig(PlatformUtil.getConfigDir().resolve("enchlevel-langpatch-conf4.txt")));

    LangPatchConfig(Path configurationFile) {
        this.configurationFile = configurationFile;
    }

    public static LangPatchConfig getInstance() {
        return INSTANCE.get();
    }

    private static @Nullable String emptyToNull(@Nullable String s) {
        if (s == null || s.isEmpty() || "null".equalsIgnoreCase(s) || "none".equalsIgnoreCase(s))
            return null;
        if (!ID_PATTERN.matcher(s).matches()) {
            PlatformUtil.LOGGER.error(MARKER, "{} is not a valid resource location", s);
            return null;
        }
        return s;
    }

    private Properties asProperties() {
        Properties p = new Properties();
        if (enchantmentCfg != null)
            p.setProperty("enchantment", enchantmentCfg);
        if (potionCfg != null)
            p.setProperty("potion", potionCfg);
        return p;
    }

    private void fromProperties(Properties p) {
        enchantmentCfg = emptyToNull(p.getProperty("enchantment"));
        potionCfg = emptyToNull(p.getProperty("potion"));
    }

    static void init() {
        try {
            getInstance().read();
        } catch (IOException e) {
            PlatformUtil.LOGGER.error(MARKER, "Failed to read config", e);
        }
    }

    public void read() throws IOException {
        if (!Files.exists(configurationFile)) {
            this.dump();
            return;
        }

        Properties p = new Properties();
        try (BufferedReader r = Files.newBufferedReader(configurationFile)) {
            p.load(r);
        }
        fromProperties(p);
    }

    public void dump() throws IOException {
        final Properties p = asProperties();
        try (BufferedWriter w = Files.newBufferedWriter(configurationFile)) {
            p.store(w, "NOTE: enchlevel-langpatch:default and enchlevel-langpatch:roman are equal.\n" +
                    "Default/Roman things are controlled with language keys:\n" +
                    " - langpatch.conf.enchantment.default.type\n- langpatch.conf.potion.default.type");
        }
    }

    @Nullable
    public String getEnchantmentCfg() {
        return emptyToNull(enchantmentCfg);
    }

    @Nullable
    public String getPotionCfg() {
        return emptyToNull(potionCfg);
    }
}
