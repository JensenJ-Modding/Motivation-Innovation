package net.motivationinnovation.mixin;

import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.Villager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.motivationinnovation.WhipItem;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "registerBrainGoals", at = @At("TAIL"))
    private void motivationinnovation$refreshVillager(Brain<Villager> brain, CallbackInfo ci) {
        Villager villager = (Villager) (Object) (this);
        WhipItem.markDirty(villager);
    }
}
