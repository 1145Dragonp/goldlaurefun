package mrmagicdragon.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class InstructorRocketLauncherItem extends Item {
    
    public InstructorRocketLauncherItem() {
        super(new Properties().stacksTo(1).durability(100));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            
            // 获取玩家的视线方向
            Vec3 lookVector = player.getLookAngle();
            
            // 计算发射位置（从玩家头顶上方发射）
            double startX = player.getX() + lookVector.x * 0.5;
            double startY = player.getY() + player.getEyeHeight() - 0.2;
            double startZ = player.getZ() + lookVector.z * 0.5;
            
            // 创建烟花实体
            FireworkRocketEntity firework;
            
            // 1/5 概率是普通烟花，4/5 概率是爆炸烟花
            if (player.getRandom().nextFloat() < 0.2) {
                // 20% 概率：普通烟花（不爆炸）
                ItemStack fireworkStack = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET);
                firework = new FireworkRocketEntity(level, player, startX, startY, startZ, fireworkStack);
            } else {
                // 80% 概率：爆炸烟花（有破坏力）
                ItemStack fireworkRocket = createExplosiveFireworkRocket();
                firework = new FireworkRocketEntity(level, fireworkRocket, player, startX, startY, startZ, true);
            }
            
            // 设置飞行方向
            firework.setDeltaMovement(player.getLookAngle().scale(2.0));
            
            // 播放发射声音
            level.playSound(null, player.blockPosition(), 
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            // 生成发射粒子效果
            for (int i = 0; i < 10; i++) {
                serverLevel.sendParticles(ParticleTypes.SMOKE, 
                    startX, startY, startZ, 
                    1, 
                    (player.getRandom().nextDouble() - 0.5) * 0.5,
                    (player.getRandom().nextDouble() - 0.5) * 0.5,
                    (player.getRandom().nextDouble() - 0.5) * 0.5,
                    0.02);
            }
            
            level.addFreshEntity(firework);
            
            // 对瞄准的生物造成伤害（秒杀 20 血生物）
            hitTarget(serverLevel, player, lookVector);
        }
        
        // 消耗耐久度
        itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    /**
     * 创建爆炸烟花的 NBT 数据
     */
    private ItemStack createExplosiveFireworkRocket() {
        ItemStack fireworkRocket = new ItemStack(net.minecraft.world.item.Items.FIREWORK_ROCKET);
        net.minecraft.nbt.CompoundTag tag = fireworkRocket.getOrCreateTagElement("Fireworks");
        
        // 设置飞行高度
        tag.putByte("Flight", (byte) 2);
        
        // 创建 Explosions 列表
        net.minecraft.nbt.ListTag explosions = new net.minecraft.nbt.ListTag();
        
        // 添加多个不同颜色的烟花之星
        int[][] colorsList = {
            {16646144},  // 橙色
            {16711680},  // 红色
            {16711680},  // 红色
            {16580608},  // 金色
            {16711680},  // 红色
            {16712451},  // 粉红色
            {16711680},  // 红色
            {16711680},  // 红色
            {16711680},  // 红色
            {16711680}   // 红色
        };
        
        for (int i = 0; i < colorsList.length; i++) {
            net.minecraft.nbt.CompoundTag explosion = new net.minecraft.nbt.CompoundTag();
            explosion.putByte("Type", (byte) 0); // 小球形
            
            // 设置颜色
            explosion.putIntArray("Colors", colorsList[i]);
            
            // 设置淡出颜色（红色）
            int[] fadeColors = new int[]{16711680};
            explosion.putIntArray("FadeColors", fadeColors);
            
            explosions.add(explosion);
        }
        
        tag.put("Explosions", explosions);
        
        return fireworkRocket;
    }

    /**
     * 对瞄准的生物造成伤害
     */
    private void hitTarget(ServerLevel level, Player player, Vec3 lookVector) {
        // 获取玩家看向的方向和距离
        double range = 50.0; // 射程 50 格
        Vec3 start = player.getEyePosition(1.0f);
        Vec3 end = start.add(lookVector.scale(range));
        
        // 射线检测，查找命中的生物
        var hitResult = level.clip(new ClipContext(
            start, end,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            null
        ));
        
        // 获取命中位置的生物
        var entities = level.getEntitiesOfClass(LivingEntity.class, 
            new AABB(start, end).inflate(2.0));
        
        for (LivingEntity entity : entities) {
            if (entity != player && !entity.isAlliedTo(player)) {
                // 检查生物是否在准星范围内
                double distance = player.distanceToSqr(entity);
                if (distance <= range * range) {
                    Vec3 entityPos = entity.position();
                    Vec3 direction = entityPos.subtract(start).normalize();
                    
                    // 检查是否在看的方向上（小角度内）
                    if (lookVector.dot(direction) > 0.8) {
                        // 造成致命伤害（秒杀 20 血生物）
                        entity.hurt(level.damageSources().explosion(player, player), 9999.0f);
                        
                        // 生成爆炸粒子
                        for (int i = 0; i < 20; i++) {
                            level.sendParticles(ParticleTypes.EXPLOSION,
                                entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                                1, 0.5, 0.5, 0.5, 0.5);
                        }
                        
                        break; // 只攻击第一个目标
                    }
                }
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
}
