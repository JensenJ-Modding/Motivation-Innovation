package net.motivationinnovation.mixin;

import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.motivationinnovation.WhipItem;

@Mixin(AcquirePoi.class)
public class AcquirePoiMixin {

    @Inject(method = "method_46880", at = @At("TAIL"))
    private static void motivationinnovation$refreshVillagerPOI(
            PoiManager poiManager,
            Predicate predicate,
            BlockPos blockPos,
            MemoryAccessor memoryAccessor,
            ServerLevel serverLevel,
            Optional optional,
            PathfinderMob pathfinderMob,
            Long2ObjectMap long2ObjectMap,
            Holder holder,
            CallbackInfo ci) {
        if (pathfinderMob instanceof Villager villager) {
            WhipItem.markDirty(villager);
        }
    }
}
