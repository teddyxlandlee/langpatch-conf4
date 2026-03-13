package xland.mcmod.enchlevellangpatch.ext.conf4;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.function.Supplier;

final class PlatformUtil {
    static final Logger LOGGER = LogManager.getLogger();

    private static final Supplier<Path> CONFIG_PATH = Suppliers.memoize(() -> {
        Class<?> c = null;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            MethodHandle mh;
            try {
                c = Class.forName("net.fabricmc.loader.api.FabricLoader");
            } catch (ClassNotFoundException ignore) {
            }

            if (c != null) {    // is Fabric platform
                mh = lookup.findStatic(c, "getInstance", MethodType.methodType(c));
                mh = MethodHandles.collectArguments(
                        lookup.findVirtual(c, "getConfigDir", MethodType.methodType(Path.class)),
                        0, mh
                );
            } else {    // FML-like environment
                try {
                    c = Class.forName("net.neoforged.fml.loading.FMLPaths");
                } catch (ClassNotFoundException ignore) {   // not Neo, must be Forge
                }
                if (c == null) {
                    try {
                        c = Class.forName("net.minecraftforge.fml.loading.FMLPaths");
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Unknown platform, expect Fabric/Quilt/Neo/Forge");
                    }
                }

                mh = lookup.findStaticGetter(c, "CONFIGDIR", c);
                mh = MethodHandles.collectArguments(
                        lookup.findVirtual(c, "get", MethodType.methodType(Path.class)),
                        0, mh
                );
            }
            return (Path) mh.invokeExact();
        } catch (Throwable t) {
            throw new RuntimeException("Can't fetch config path", t);
        }
    });

    public static Path getConfigDir() {
        return CONFIG_PATH.get();
    }
}
