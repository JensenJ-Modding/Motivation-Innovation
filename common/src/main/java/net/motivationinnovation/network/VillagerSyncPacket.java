package net.motivationinnovation.network;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.motivationinnovation.MotivationInnovation;
import org.jetbrains.annotations.NotNull;

public record VillagerSyncPacket(UUID villagerUUID, boolean home, BlockPos homePos, boolean job, BlockPos jobPos)
        implements CustomPacketPayload {
    public static final Type<VillagerSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MotivationInnovation.MOD_ID, "villager_sync"));

    public static final StreamCodec<FriendlyByteBuf, VillagerSyncPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            VillagerSyncPacket::villagerUUID,
            ByteBufCodecs.BOOL,
            VillagerSyncPacket::home,
            BlockPos.STREAM_CODEC,
            VillagerSyncPacket::homePos,
            ByteBufCodecs.BOOL,
            VillagerSyncPacket::job,
            BlockPos.STREAM_CODEC,
            VillagerSyncPacket::jobPos,
            VillagerSyncPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
