package mrmagicdragon.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.RecordItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import mrmagicdragon.block.ModBlocks;
import mrmagicdragon.effect.ModEffects;
import mrmagicdragon.ModSounds;
import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "mmjmod");

    // MMJ 物品 - 食物，带有长草效果
    public static final RegistryObject<Item> MMJ_ITEM = ITEMS.register("mmj",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .alwaysEat()
                            .nutrition(2)
                            .saturationMod(0.5f)
                            .effect(() -> new MobEffectInstance(ModEffects.GRASS_GROWING.get(), 600, 1), 1.0f) // 30 秒，等级 1
                            .build())) {
                        @Override
                        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
                            tooltip.add(Component.translatable("item.mmjmod.mmj.desc"));
                        }
                    });

    // 唱片 - 奔向远方
    public static final RegistryObject<Item> RECORD_BXYF = ITEMS.register("record_bxyf",
            () -> new RecordItem(3, ModSounds.RECORD_BXYF.get(),
                    new Item.Properties().stacksTo(1), 226)); // 226 tick = 约 11.3 秒，实际时长由.ogg 文件决定

    // 教官火箭筒 - 右键发射烟花，可秒杀生物
    public static final RegistryObject<Item> INSTRUCTOR_ROCKET_LAUNCHER = ITEMS.register("gjhjt",
            () -> new InstructorRocketLauncherItem());
}
