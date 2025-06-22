package net.motivationinnovation.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.motivationinnovation.MotivationInnovation;
import org.jetbrains.annotations.NotNull;

public record VillagerSyncPacket(boolean home, BlockPos homePos, boolean job, BlockPos jobPos)
        implements CustomPacketPayload {
    public static final Type<VillagerSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MotivationInnovation.MOD_ID, "villager_sync"));

    public static final StreamCodec<FriendlyByteBuf, VillagerSyncPacket> STREAM_CODEC = StreamCodec.composite(
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
