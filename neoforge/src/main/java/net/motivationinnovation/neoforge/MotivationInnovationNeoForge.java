package net.motivationinnovation.neoforge;

import net.motivationinnovation.MotivationInnovation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MotivationInnovation.MOD_ID)
public class MotivationInnovationNeoForge {
    public MotivationInnovationNeoForge(ModContainer container, IEventBus bus) {
        MotivationInnovation.init();
    }
}
