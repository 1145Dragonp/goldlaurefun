package mrmagicdragon.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = 
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "mmjmod");

    public static final RegistryObject<MobEffect> GRASS_GROWING = EFFECTS.register("grass_growing",
            () -> new GrassGrowingEffect());
}
