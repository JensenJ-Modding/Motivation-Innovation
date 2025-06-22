package net.motivationinnovation;

import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
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

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import net.motivationinnovation.network.VillagerSyncPacket;

public class WhipItem extends Item {

    private Villager targetedVillager = null;
    private ServerPlayer owner = null;
    public static Map<Villager, WhipItem> boundWhips = new HashMap<>();

    public WhipItem(Properties properties) {
        super(properties);
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
        whipItem.owner = (ServerPlayer) player;
        refreshPacket(villager, whipItem.owner);

        return EventResult.pass();
    }

    public static void refreshPacket(Villager villager, ServerPlayer owner) {
        Brain<Villager> brain = villager.getBrain();
        Optional<GlobalPos> optHomePos = brain.getMemory(MemoryModuleType.HOME);
        Optional<GlobalPos> optJobPos = brain.getMemory(MemoryModuleType.JOB_SITE);

        BlockPos homePos = BlockPos.ZERO;
        BlockPos jobPos = BlockPos.ZERO;

        if (optHomePos.isPresent()) {
            homePos = optHomePos.get().pos();
        }

        if (optJobPos.isPresent()) {
            jobPos = optJobPos.get().pos();
        }

        NetworkManager.sendToPlayer(
                owner, new VillagerSyncPacket(optHomePos.isPresent(), homePos, optJobPos.isPresent(), jobPos));
    }

    public static void markDirty(Villager villager) {
        WhipItem whip = boundWhips.get(villager);
        if (whip == null) {
            return;
        }
        refreshPacket(villager, whip.owner);
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
                        (player.level().getRandom().nextFloat() * 1.1F + 0.8F));

        return EventResult.pass();
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.motivationinnovation.whip").withStyle(ChatFormatting.DARK_PURPLE));
    }

    public void setTargetedVillager(Villager villager) {
        boundWhips.put(villager, this);
        this.targetedVillager = villager;
    }

    public Villager getTargetedVillager() {
        return this.targetedVillager;
    }
}
