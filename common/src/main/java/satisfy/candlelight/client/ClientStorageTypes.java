package satisfy.candlelight.client;

import de.cristelknight.doapi.client.render.block.storage.StorageBlockEntityRenderer;
import de.cristelknight.doapi.client.render.block.storage.StorageTypeRenderer;
import net.minecraft.resources.ResourceLocation;
import satisfy.candlelight.Candlelight;
import satisfy.candlelight.client.render.block.ShelfRenderer;
import satisfy.candlelight.client.render.block.TableSetRenderer;
import satisfy.candlelight.client.render.block.ToolRackRenderer;
import satisfy.candlelight.registry.StorageTypesRegistry;

public class ClientStorageTypes {
    public static void registerStorageType(ResourceLocation location, StorageTypeRenderer renderer){
        StorageBlockEntityRenderer.registerStorageType(location, renderer);
    }

    public static void init(){
        Candlelight.LOGGER.debug("Registering Storage Block Renderers!");
        registerStorageType(StorageTypesRegistry.TABLE_SET, new TableSetRenderer());
        registerStorageType(StorageTypesRegistry.SHELF, new ShelfRenderer());
        registerStorageType(StorageTypesRegistry.TOOL_RACK, new ToolRackRenderer());
    }
}
