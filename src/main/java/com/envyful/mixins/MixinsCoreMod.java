package com.envyful.mixins;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.DependsOn("pixelmon")
public class MixinsCoreMod implements IFMLLoadingPlugin {

    private static final String[] ACCEPTED_TYPES = new String[] { "jar", "zip" };
    private static final File MODS_FOLDER = new File(System.getProperty("user.dir"), "mods");

    public MixinsCoreMod() {
        this.findAndLoadMods();
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.pixelmon.json");
        /*Mixins.addConfiguration("mixins.pixelhunt.json");*/
    }

    public void findAndLoadMods() {
        if (!MODS_FOLDER.exists()) {
            return;
        }

        this.findAndLoadJarFiles(
                "com/pixelmonmod/pixelmon/Pixelmon.class",
                "ca/landonjw/gooeylibs2/api/button/GooeyButton.class",
                "com/xpgaming/pixelhunt/PixelHuntForge.class"
        );
    }

    private void findAndLoadJarFiles(String... classNames) {
        Collection<File> jars = FileUtils.listFiles(MODS_FOLDER, ACCEPTED_TYPES, false);
        Set<String> classNameSet = Sets.newHashSet(classNames);
        List<File> toLoad = Lists.newArrayList();

        for (File jar : jars) {
            if (this.searchForClasses(jar, classNameSet)) {
                toLoad.add(jar);
            }
        }

        for (File jar : toLoad) {
            if (jar == null) {
                continue;
            }

            this.attemptLoadMod(jar);
        }
    }

    private boolean searchForClasses(File file, Set<String> classNames) {
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                zip.closeEntry();

                if (classNames.contains(entry.getName())) {
                    return true;
                }
            }

            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void attemptLoadMod(File jar) {
        if (CoreModManager.getReparseableCoremods().contains(jar.getName())) {
            return;
        }

        try {
            ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(jar.toURI().toURL());
            CoreModManager.getReparseableCoremods().add(jar.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
