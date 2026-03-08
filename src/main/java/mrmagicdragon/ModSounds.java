package mrmagicdragon;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "mmjmod");

    // 唱片 - 奔向远方
    public static final RegistryObject<SoundEvent> RECORD_BXYF = SOUND_EVENTS.register("record_bxyf",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("mmjmod", "record.bxyf")));
}
