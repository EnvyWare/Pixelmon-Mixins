package com.envyful.mixins;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.launch.MixinBootstrap;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.DependsOn("pixelmon")
public class MixinsCoreMod implements IFMLLoadingPlugin {

    private static final String[] ACCEPTED_TYPES = new String[] { "jar", "zip" };
    private static final File MODS_FOLDER = new File(System.getProperty("user.dir"), "mods");

    public MixinsCoreMod() {
        this.findAndLoadPixelmon();
        MixinBootstrap.init();
        //TODO: mixin configs here
    }

    public void findAndLoadPixelmon() {
        if (!MODS_FOLDER.exists()) {
            return;
        }

        File pixelmon = this.findPixelmonFile();

        if (pixelmon == null) {
            return;
        }

        this.attemptLoadPixelmon(pixelmon);
    }

    private File findPixelmonFile() {
        Collection<File> jars = FileUtils.listFiles(MODS_FOLDER, ACCEPTED_TYPES, false);

        for (File jar : jars) {
            if (this.searchForPixelmonPackage(jar)) {
                return jar;
            }
        }

        return null;
    }

    private boolean searchForPixelmonPackage(File file) {
        try {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                zip.closeEntry();
                if (entry.getName().equals("com/pixelmonmod/pixelmon/Pixelmon.class")) {
                    return true;
                }
            }

            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void attemptLoadPixelmon(File pixelmon) {
        if (CoreModManager.getReparseableCoremods().contains(pixelmon.getName())) {
            return;
        }

        try {
            ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(pixelmon.toURI().toURL());
            CoreModManager.getReparseableCoremods().add(pixelmon.getName());
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
