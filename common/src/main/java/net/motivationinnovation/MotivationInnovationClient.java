package net.motivationinnovation;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

import dev.architectury.networking.NetworkManager;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.motivationinnovation.network.VillagerSyncPacket;

public class MotivationInnovationClient {

    public static BlockPos jobSitePos;
    public static BlockPos bedPos;

    public static void init() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                VillagerSyncPacket.TYPE,
                VillagerSyncPacket.STREAM_CODEC,
                ((value, context) -> {
                    if (value.home()) {
                        bedPos = value.homePos();
                    } else {
                        bedPos = null;
                    }
                    if (value.job()) {
                        jobSitePos = value.jobPos();
                    } else {
                        jobSitePos = null;
                    }
                }));
    }

    public static void renderVillagerOutline(Villager villager) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stackMain = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack stackOff = player.getItemInHand(InteractionHand.MAIN_HAND);

        // Prioritize the whip in the main hand for rendering
        if (stackMain.getItem() instanceof WhipItem whip) {
            internalRenderVillager(whip, villager);
        } else if (stackOff.getItem() instanceof WhipItem whip) {
            internalRenderVillager(whip, villager);
        }
    }

    public static void internalRenderVillager(WhipItem whip, Villager villager) {
        Villager targetVillager = whip.getTargetedVillager();
        if (targetVillager == null) {
            return;
        }

        if (targetVillager.isDeadOrDying()) {
            return;
        }

        if (targetVillager.touchingUnloadedChunk()) {
            whip.setTargetedVillager(null);
        }

        if (targetVillager.equals(villager)) {
            Outliner.getInstance()
                    .showAABB("motivationinnovation:villager", villager.getBoundingBox())
                    .colored(Color.RED)
                    .disableCull();
        }
    }

    public static void renderVillagerPOIs() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stackMain = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack stackOff = player.getItemInHand(InteractionHand.MAIN_HAND);

        // Prioritize the whip in the main hand for rendering
        if (stackMain.getItem() instanceof WhipItem whip) {
            internalRenderVillagerPOIs(whip.getTargetedVillager());
        } else if (stackOff.getItem() instanceof WhipItem whip) {
            internalRenderVillagerPOIs(whip.getTargetedVillager());
        }
    }

    public static void internalRenderVillagerPOIs(Villager villager) {
        if (villager == null) {
            return;
        }

        if (villager.isDeadOrDying()) {
            return;
        }

        Outliner.getInstance()
                .showAABB("motivationinnovation:villager", villager.getBoundingBox())
                .colored(Color.RED)
                .disableCull();

        if (jobSitePos != null) {
            BlockState state = villager.level().getBlockState(jobSitePos);
            boolean foundSite = false;
            for (PoiType poiType : BuiltInRegistries.POINT_OF_INTEREST_TYPE) {
                if (poiType.is(state)) {
                    Outliner.getInstance()
                            .showAABB("motivationinnovation:jobSite", new AABB(jobSitePos))
                            .colored(Color.RED)
                            .disableCull();
                    Outliner.getInstance()
                            .showLine(
                                    "motivationinnovation:jobSiteLink",
                                    jobSitePos.getCenter(),
                                    villager.getEyePosition())
                            .colored(Color.RED)
                            .disableCull();
                    foundSite = true;
                    break;
                }
            }
            if (!foundSite) {
                jobSitePos = null;
            }
        }

        if (bedPos != null) {
            BlockState state = villager.level().getBlockState(bedPos);
            if (state.getBlock() instanceof BedBlock) {
                Direction direction = state.getValue(FACING).getOpposite();
                Outliner.getInstance()
                        .showAABB(
                                "motivationinnovation:bed",
                                AABB.encapsulatingFullBlocks(bedPos, bedPos.relative(direction)))
                        .colored(Color.RED)
                        .disableCull();
                Outliner.getInstance()
                        .showLine("motivationinnovation:bedLink", bedPos.getCenter(), villager.getEyePosition())
                        .colored(Color.RED)
                        .disableCull();
            } else {
                bedPos = null;
            }
        }
    }
}
