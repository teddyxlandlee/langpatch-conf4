package xland.mcmod.enchlevellangpatch.ext.conf4;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class PlatformUtil {
    static final Logger LOGGER = LogManager.getLogger();

    private static final IntSupplier LAZY_FORGE_VERSION = Suppliers.memoize(PlatformUtil::getForgeVersion1)::get;

    private static final Supplier<Path> CONFIG_PATH = Suppliers.memoize(() -> {
        Class<?> c;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            if (getForgeVersion() < 0) {
                c = Class.forName("net.fabricmc.loader.api.FabricLoader");
                MethodHandle mh = lookup.findStatic(c, "getInstance", MethodType.methodType(c));
                MethodHandle mh2 = lookup.findVirtual(c, "getConfigDir", MethodType.methodType(Path.class));

                return (Path) mh2.invoke(mh.invoke());
            } else {
                c = Class.forName("net.minecraftforge.fml.loading.FMLPaths");
                MethodHandle mh = lookup.findStaticGetter(c, "CONFIGDIR", c);
                MethodHandle mh2 = lookup.findVirtual(c, "get", MethodType.methodType(Path.class));

                return (Path) mh2.invoke(mh.invoke());
            }
        } catch (Throwable t) {
            throw new RuntimeException("Can't fetch config path", t);
        }
    });

    public static Path getConfigDir() {
        return CONFIG_PATH.get();
    }

    public static int getForgeVersion() {
        return LAZY_FORGE_VERSION.getAsInt();
    }

    private static int getForgeVersion1() {
        String s = getForgeVersion0();
        if (s != null) {
            s = s.split("\\.")[0];
            // 36 means 1.16.5, 37 means 1.17.1
            return Integer.parseUnsignedInt(s);
        } else {    // Still checking if Fabric, otherwise throw
            try {
                Class.forName("net.fabricmc.api.Environment");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Detected environment is neither FML nor Fabric/Quilt.", e);
            }
            return -1;
        }
    }

    private static @Nullable String getForgeVersion0() {
        try {
            Class<?> clazz = Class.forName("net.minecraftforge.fml.loading.StringSubstitutor");
            MethodHandle mh = MethodHandles.lookup().findStatic(clazz, "replace",
                    MethodType.fromMethodDescriptorString("(Ljava/lang/String;Lnet/minecraftforge/fml/loading/moddiscovery/ModFile;)Ljava/lang/String;",
                            PlatformUtil.class.getClassLoader()));
            return (String) mh.invoke("${global.forgeVersion}", null);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LOGGER.warn(() ->
                    "Can't get forge version: " + e);
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
