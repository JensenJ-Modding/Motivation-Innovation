package net.motivationinnovation;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import dev.architectury.event.EventResult;
import org.jetbrains.annotations.NotNull;

public class WhipItem extends Item {

    private Villager targetedVillager = null;

    public WhipItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        if (targetedVillager == null) {
            return InteractionResult.PASS;
        }

        if (targetedVillager.isDeadOrDying()) {
            return InteractionResult.PASS;
        }

        if (targetedVillager.touchingUnloadedChunk()) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    public static EventResult handleWhipInteraction(Player player, Entity entity, InteractionHand hand) {
        if (!(entity instanceof Villager villager)) {
            return EventResult.pass();
        }

        if (player.level().isClientSide()) {
            return EventResult.pass();
        }

        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof WhipItem whipItem)) {
            return EventResult.pass();
        }

        if (player.getCooldowns().isOnCooldown(whipItem)) {
            return EventResult.pass();
        }

        if (!player.isShiftKeyDown()) {
            return tryRefreshVillagerTrades(player, villager, whipItem);
        }

        whipItem.setTargetedVillager(villager);

        Brain<Villager> brain = villager.getBrain();
        Optional<GlobalPos> jobSite = brain.getMemory(MemoryModuleType.JOB_SITE);
        Optional<GlobalPos> bed = brain.getMemory(MemoryModuleType.HOME);

        MotivationInnovation.LOGGER.info("job: {}", jobSite);
        MotivationInnovation.LOGGER.info("bed: {}", bed);

        return EventResult.pass();
    }

    public static EventResult tryRefreshVillagerTrades(Player player, Villager villager, WhipItem whipItem) {
        VillagerData data = villager.getVillagerData();
        VillagerProfession profession = data.getProfession();
        if (profession == VillagerProfession.NONE || profession == VillagerProfession.NITWIT) {
            return EventResult.pass();
        }

        // If trade is locked in, we shouldn't allow it to be reset
        if (villager.getVillagerXp() > 0 || data.getLevel() != 1) {
            return EventResult.pass();
        }

        // Refresh the trades
        villager.setOffers(null);
        villager.refreshBrain((ServerLevel) player.level());

        player.getCooldowns().addCooldown(whipItem, 20);
        player.awardStat(Stats.ITEM_USED.get(whipItem));
        if (player.level().getRandom().nextInt(8) == 0) {
            player.level()
                    .playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.VILLAGER_CELEBRATE,
                            SoundSource.NEUTRAL,
                            0.5F,
                            player.level().getRandom().nextFloat() / 2);
        } else {
            player.level()
                    .playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.VILLAGER_HURT,
                            SoundSource.NEUTRAL,
                            0.5F,
                            0.4F / (player.level().getRandom().nextFloat() * 0.4F + 0.8F));
        }
        player.level()
                .playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.LIGHTNING_BOLT_IMPACT,
                        SoundSource.NEUTRAL,
                        0.25F,
                        0.4F / (player.level().getRandom().nextFloat() * 0.4F + 0.8F));

        return EventResult.pass();
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.motivationinnovation.whip").withStyle(ChatFormatting.DARK_PURPLE));
    }

    public void setTargetedVillager(Villager villager) {
        this.targetedVillager = villager;
    }

    public Villager getTargetedVillager() {
        return this.targetedVillager;
    }
}
