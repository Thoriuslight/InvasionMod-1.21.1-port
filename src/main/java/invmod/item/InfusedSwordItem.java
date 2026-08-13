package invmod.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class InfusedSwordItem extends SwordItem {
    public InfusedSwordItem(Properties props) {
        super(ModToolTiers.INFUSED_GOLD, props.attributes(SwordItem.createAttributes(ModToolTiers.INFUSED_GOLD, 0, -2.4F)));
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.isDamaged()) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (stack.isDamaged()) {
            return InteractionResultHolder.fail(stack);
        }
        // if player isSneaking then refill hunger else refill health
        if (player.isCrouching()) {
            player.getFoodData().eat(6, 0.5f);
            level.playSound(player, player.blockPosition(), SoundEvents.PLAYER_BURP, player.getSoundSource(),
                    0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        } else {
            player.heal(6.0F);

            // spawn heart particles around the player
            level.addParticle(ParticleTypes.HEART, player.getX() + 1.5D, player.getEyeY(), player.getZ(), 1.D, 0.D, 0.D);
            level.addParticle(ParticleTypes.HEART, player.getX() - 1.5D, player.getEyeY(), player.getZ(), 1, 0, 0);
            level.addParticle(ParticleTypes.HEART, player.getX(), player.getEyeY(), player.getZ() + 1.5D, 1, 0, 0);
            level.addParticle(ParticleTypes.HEART, player.getX(), player.getEyeY(), player.getZ() - 1.5D, 1, 0, 0);

        }
        stack.setDamageValue(this.getMaxDamage(stack));
        return InteractionResultHolder.success(stack);
    }
}
