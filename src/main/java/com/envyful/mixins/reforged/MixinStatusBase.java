package com.envyful.mixins.reforged;

import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.battles.status.NoStatus;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Mixin(StatusBase.class)
public class MixinStatusBase {

    private static final transient Map<Class<? extends StatusBase>, Constructor<? extends StatusBase>> CONSTRUCTORS_CACHE = Maps.newHashMap();

    /**
     *
     * @reason Optimizes the {@link StatusBase} class' usage of reflection to get new instances of statuses
     * @author danorris709
     */
    @Overwrite(remap = false)
    public static StatusBase getNewInstance(Class<? extends StatusBase> statusClass) {
        try {
            return CONSTRUCTORS_CACHE.computeIfAbsent(statusClass, ___ -> {
                try {
                    return statusClass.getConstructor();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                return null;
            }).newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException var2) {
            return NoStatus.noStatus;
        }
    }
}
