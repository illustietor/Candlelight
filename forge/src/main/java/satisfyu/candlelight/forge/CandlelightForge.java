package satisfyu.candlelight.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import satisfyu.candlelight.Candlelight;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import satisfyu.candlelight.forge.registry.CandlelightPoiTypesForge;

@Mod(Candlelight.MOD_ID)
public class CandlelightForge {
    public CandlelightForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Candlelight.MOD_ID, modEventBus);
        Candlelight.init();
        CandlelightPoiTypesForge.init(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CandlelightPoiTypesForge::registerPOIs);
        Candlelight.commonInit();
    }
}