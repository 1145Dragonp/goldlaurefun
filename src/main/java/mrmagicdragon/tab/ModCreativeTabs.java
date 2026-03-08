package mrmagicdragon.tab;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import mrmagicdragon.item.ModItems;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "mmjmod");

    // 示例创造模式标签页
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("mmj",
            () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("creativetab.mmj"))
                    .icon(() -> ModItems.MMJ_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MMJ_ITEM.get());
                        output.accept(ModItems.RECORD_BXYF.get());
                        output.accept(ModItems.INSTRUCTOR_ROCKET_LAUNCHER.get());
                    }).build());
}
