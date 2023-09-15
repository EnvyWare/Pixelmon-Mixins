package com.envyful.mixins;

import net.minecraftforge.fml.common.Mod;

@Mod(PixelmonFixies.MOD_ID)
public class PixelmonFixies {

    public static final String MOD_ID = "pixelmonfixes";

    private static PixelmonFixies instance;

    public PixelmonFixies() {
        instance = this;
    }

    public static PixelmonFixies getInstance() {
        return instance;
    }
}
