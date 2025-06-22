package net.motivationinnovation.neoforge;

import net.motivationinnovation.MotivationInnovation;
import net.motivationinnovation.MotivationInnovationClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@Mod(value = MotivationInnovation.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = MotivationInnovation.MOD_ID)
public class MotivationInnovationClientNeoForge {

    public MotivationInnovationClientNeoForge(ModContainer container, IEventBus bus) {
        MotivationInnovationClient.init();
    }

    @SubscribeEvent
    public static void handleWorldRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            MotivationInnovationClient.renderVillager();
        }
    }
}
