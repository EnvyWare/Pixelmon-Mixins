package com.envyful.mixins;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinsCoreMod implements IFMLLoadingPlugin {

    private static final String[] ALLOWED_TYPES = {
            "jar",
            "zip"
    };

    private final List<Tuple<String, String>> coremods = Lists.newArrayList();
    private final List<Tuple<String, String>> loadedCoremods = Lists.newArrayList();

    private void addToCoremodList(String mod, String mixin) {
        if (mod == null || mod.trim().isEmpty() || mixin == null || mixin.trim().isEmpty()) {
            this.log("Mixins", "Loaded a null mod or mixins (mod=" + mod + ", mixin=" + mixin + ") this is not valid and will be dumped!");
        } else {
            this.log("Mixins", "Added Optional Preloader for \"" + mod + "\" using \"" + mixin + "\"");
            this.coremods.add(new Tuple<>(mod, mixin));
        }
    }

    private boolean loadCoremod(File coremod, Tuple<String, String> target) {
        try {
            if (!CoreModManager.getReparseableCoremods().contains(coremod.getName())) {
                ((LaunchClassLoader) getClass().getClassLoader()).addURL(coremod.toURI().toURL());
                CoreModManager.getReparseableCoremods().add(coremod.getName());
                log("Mixins", "Preloaded mod \"" + coremod.getName() + "\" containing \"" + target.getFirst() + "\"");
                return true;
            } else {
                log("Mixins", "Skipped Preloading already loaded coremod \"" + coremod.getName() + "\" with \"" + target.getSecond() + "\"");
                return false;
            }
        } catch (Throwable t) {
            log("Mixins", "Failed to  load a coremod! Caught " + t.getClass().getSimpleName() + "!" + " caused by " + (target == null ? "target was null" : target.getFirst() + " - " + target.getSecond()), t);
            return false;
        }

    }

    private void loadCoremodList() {
        loadFolder("mods");
        initializeMixins();
    }

    private void initializeMixins() {
        loadedCoremods.add(new Tuple<>("internal", "mixins.minecraft.json"));

        log("Mixins", "Loading Sponge mixins");
        Mixins.addConfiguration("mixins.sponge.json");

        try {
            MixinBootstrap.init();
            for (final Tuple<String, String> tuple : new ArrayList<>(loadedCoremods)) {
                try {
                    log("Mixins", "Loading Coremod mixins \"" + tuple.getSecond() + "\"");
                    Mixins.addConfiguration(tuple.getSecond());
                } catch (Throwable t) {
                    log("Mixins", "Caught Exception trying to preload mod configurations for \"" + (tuple != null ? tuple.getSecond() : "null entry") + "\"", t);
                }
            }
        } catch (final Throwable t) {
            log("Mixins", "Caught Exception trying to preload mod configurations", t);
        }
    }

    private void loadFolder(String path) {
        try {
            final File modsFolder = new File(System.getProperty("user.dir"), path);
            if (!modsFolder.exists()) {
                log("Mixins", "The \"" + path + "\" folder couldn't be found skipping this loader! Folder: " + modsFolder.toString());
                return;
            }

            Collection<File> jars = FileUtils.listFiles(modsFolder, ALLOWED_TYPES, false);

            for (final File jar : jars) {
                final ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    zip.closeEntry();
                    String name = entry.getName();
                    for (Tuple<String, String> tuple : new ArrayList<>(coremods)) {
                        if (name.equals(tuple.getFirst())) {
                            loadCoremod(jar, tuple);
                            loadedCoremods.add(tuple);
                            coremods.remove(tuple);
                        }
                    }
                }
                zip.close();
            }

        } catch (Throwable t) {
            log("Mixins", "Caught Exception trying to load \"" + path + "\" for coremods! This will likely be fatal", t);
        }
    }

    public MixinsCoreMod() {
        addToCoremodList("com/pixelmonmod/pixelmon/api/pokemon/Pokemon.class", "mixins.pixelmon.json");
        addToCoremodList("uk/co/proxying/tabmanager/TabManager.class", "mixins.tabmanager.json");
        addToCoremodList("com/pixelextras/PixelExtras.class", "mixins.pixelextras.json");
        addToCoremodList("com/xpgaming/pixelhunt/PixelHuntForge.class", "mixins.pixelhunt.json");
        addToCoremodList("com/mcsimonflash/sponge/activetime/ActiveTime.class", "mixins.activetime.json");

        loadCoremodList();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private void log(final String prefix, final String message) {
        this.log(prefix, message, null);
    }

    private void log(final String prefix, final String message, final Throwable e) {
        FMLLog.log.info("[" + "INFO" + "] [" + prefix + "] " + "> " + message);

        if (e != null) {
            for (final String s : getException(e)) {
                FMLLog.log.info("[" + "INFO" + "] [" + prefix + "] " + "> " + s);
            }
        }
    }

    private static List<String> getException(final Throwable e) {
        List<String> exception = Lists.newArrayList(e.getClass().getSimpleName() + ": " + e.getMessage());

        for (final StackTraceElement element : e.getStackTrace()) {
            exception.add(element.toString());
        }

        return exception;
    }

    public static class Tuple<X, Y> {
        private final X x;
        private final Y y;

        public final X getFirst() {
            return x;
        }

        public final Y getSecond() {
            return y;
        }

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}
