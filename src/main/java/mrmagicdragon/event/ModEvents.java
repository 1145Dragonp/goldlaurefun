package mrmagicdragon.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.resources.ResourceLocation;
import mrmagicdragon.effect.ModEffects;

public class ModEvents {
    
    @SubscribeEvent
    public void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effect = event.getEffectInstance();
        if (effect.getEffect() == ModEffects.GRASS_GROWING.get()) {
            // 效果结束时在聊天框输出 hello
            //event.getEntity().sendSystemMessage(Component.literal("hello"));
            
            // 在玩家脚下生成一棵完整的橡树
            Level level = event.getEntity().level();
            BlockPos pos = event.getEntity().blockPosition();
            BlockPos groundPos = pos.below();
            
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                
                // 检查地面是否是草方块或泥土
                BlockState groundBlock = level.getBlockState(groundPos);
                if (groundBlock.is(Blocks.GRASS_BLOCK) || groundBlock.is(Blocks.DIRT) || groundBlock.is(Blocks.COARSE_DIRT)) {
                    // 将地面转换为草方块
                    level.setBlockAndUpdate(groundPos, Blocks.GRASS_BLOCK.defaultBlockState());
                    
                    // 在玩家位置加载并生成 NBT 结构文件（完整的橡树）
                    BlockPos treePos = groundPos.above();
                    
                    // 加载结构模板
                    StructureTemplate template = serverLevel.getStructureManager().getOrCreate(ResourceLocation.parse("mmjmod:mm"));
                    if (template != null) {
                        // 设置放置参数
                        StructurePlaceSettings settings = new StructurePlaceSettings()
                                .setRotation(Rotation.NONE)
                                .setMirror(Mirror.NONE)
                                .setIgnoreEntities(false);
                        
                        // 在指定位置生成结构
                        template.placeInWorld(serverLevel, treePos, treePos, settings, serverLevel.getRandom(), 3);
                    }
                }
            }
        }
    }
}
