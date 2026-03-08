package mrmagicdragon.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrassGrowingEffect extends MobEffect {
    
    // 存储玩家的原始头盔数据
    private static final Map<UUID, CompoundTag> HELMET_STORAGE = new HashMap<>();
    
    protected GrassGrowingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x55AA55);
    }

    public ResourceLocation getIconTexture() {
        return ResourceLocation.parse("mmjmod:item/oak_sapling");
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        boolean isClientSide = entity.level().isClientSide();
        
        // 服务端逻辑：生成植物
        if (!isClientSide) {
            Level level = entity.level();
            BlockPos pos = entity.blockPosition();
            Random random = new Random();
            
            UUID playerUUID = entity.getUUID();
            ItemStack currentHelmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            
            // 首次应用时保存并替换头盔
            if (!HELMET_STORAGE.containsKey(playerUUID) && !currentHelmet.is(Blocks.GRASS_BLOCK.asItem())) {
                // 保存原始头盔
                if (!currentHelmet.isEmpty()) {
                    CompoundTag helmetNBT = new CompoundTag();
                    currentHelmet.save(helmetNBT);
                    HELMET_STORAGE.put(playerUUID, helmetNBT);
                } else {
                    // 如果没有头盔，保存空标记
                    HELMET_STORAGE.put(playerUUID, new CompoundTag());
                }
                
                // 替换为草方块头盔，并添加绑定诅咒
                ItemStack grassHelmet = new ItemStack(Blocks.GRASS_BLOCK);
                CompoundTag displayTag = grassHelmet.getOrCreateTag();
                CompoundTag enchantmentsTag = new CompoundTag();
                enchantmentsTag.putString("id", "minecraft:binding_curse");
                enchantmentsTag.putInt("lvl", 1);
                displayTag.put("Enchantments", enchantmentsTag); // 使用 Enchantments 而不是 enchantments
                grassHelmet.setTag(displayTag);
                entity.setItemSlot(EquipmentSlot.HEAD, grassHelmet);
            }
            
            // 检测效果是否即将结束（剩余时间少于 1 秒）
            // 如果快结束了，提前恢复头盔
            if (entity.hasEffect(this) && entity.getEffect(this).getDuration() <= 20) {
                if (HELMET_STORAGE.containsKey(playerUUID)) {
                    CompoundTag savedHelmetNBT = HELMET_STORAGE.remove(playerUUID);
                    ItemStack recoveredHelmet = ItemStack.of(savedHelmetNBT);
                    
                    if (!recoveredHelmet.isEmpty()) {
                        entity.setItemSlot(EquipmentSlot.HEAD, recoveredHelmet);
                    } else {
                        entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }
            }
            
            // 在玩家脚下生成草方块
            BlockPos groundPos = pos.below();
            BlockState groundBlock = level.getBlockState(groundPos);
            
            // 如果地面是泥土或砂砾等，转换为草方块
            if (groundBlock.is(Blocks.DIRT) || groundBlock.is(Blocks.COARSE_DIRT)) {
                level.setBlockAndUpdate(groundPos, Blocks.GRASS_BLOCK.defaultBlockState());
            }
            
            // 骨粉效果：对玩家正下方的方块使用骨粉
            BlockPos targetPos = pos.below();
            BlockState targetBlock = level.getBlockState(targetPos);
            
            // 如果是可催熟的植物（小麦、胡萝卜等）
            if (targetBlock.getBlock() instanceof BonemealableBlock bonemealable) {
                if (bonemealable.isValidBonemealTarget(level, targetPos, targetBlock, false)) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    bonemealable.performBonemeal(serverLevel, serverLevel.getRandom(), targetPos, targetBlock);
                }
            }
            // 如果在草地上，随机生成花或高草丛 - 加快速度
            else if (targetBlock.is(Blocks.GRASS_BLOCK)) {
                // 大幅增加生成数量和范围
                int spawnCount = (amplifier + 1) * 5; // 等级越高，生成越多
                for (int i = 0; i < spawnCount; i++) {
                    int offsetX = random.nextInt(9) - 4; // 扩大范围到 9x9
                    int offsetZ = random.nextInt(9) - 4;
                    BlockPos grassPos = targetPos.offset(offsetX, 1, offsetZ);
                    
                    if (level.isEmptyBlock(grassPos)) {
                        int rand = random.nextInt(10);
                        if (rand < 6) {
                            // 60% 生成高草丛
                            level.setBlockAndUpdate(grassPos, Blocks.TALL_GRASS.defaultBlockState());
                        } else if (rand < 9) {
                            // 30% 生成蒲公英
                            level.setBlockAndUpdate(grassPos, Blocks.DANDELION.defaultBlockState());
                        } else {
                            // 10% 生成虞美人
                            level.setBlockAndUpdate(grassPos, Blocks.POPPY.defaultBlockState());
                        }
                    }
                }
            }
        }
        
        // 客户端逻辑：生成绿色加号粒子效果 - 持续生成
        if (isClientSide) {
            Level level = entity.level();
            BlockPos pos = entity.blockPosition();
            Random random = new Random();
            
            // 在玩家周围和脚下位置持续生成大量粒子
            BlockPos targetPos = pos.below();
            
            // 每次生成 30 个粒子，形成持续的粒子云效果
            for (int i = 0; i < 30; i++) {
                double particleX = targetPos.getX() + 0.5 + random.nextDouble() * 3.0 - 1.5;
                double particleY = targetPos.getY() + 0.3 + random.nextDouble() * 0.8;
                double particleZ = targetPos.getZ() + 0.5 + random.nextDouble() * 3.0 - 1.5;
                
                // 添加向上的速度，让粒子飘起来
                double velocityY = 0.08 + random.nextDouble() * 0.15;
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, particleX, particleY, particleZ, 0, velocityY, 0);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每 10 tick (0.5 秒) 生效一次，更频繁
        int interval = 15;
        return duration % interval == 0;
    }
}
