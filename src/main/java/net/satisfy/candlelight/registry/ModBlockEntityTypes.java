package net.satisfy.candlelight.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.satisfy.candlelight.block.entity.CookingPanEntity;
import net.satisfy.candlelight.block.entity.SideBoardBlockEntity;
import net.satisfy.candlelight.block.entity.WineStationBlockEntity;
import net.satisfy.candlelight.util.CandlelightIdentifier;

import java.util.HashMap;
import java.util.Map;

public class ModBlockEntityTypes {

    private static final Map<Identifier, BlockEntityType<?>> BLOCK_ENTITY_TYPES = new HashMap<>();
    public static final BlockEntityType<SideBoardBlockEntity> SIDEBOARD = create("sideboard", FabricBlockEntityTypeBuilder.create(SideBoardBlockEntity::new, ObjectRegistry.SIDEBOARD).build());
    public static final BlockEntityType<CookingPanEntity> COOKING_PAN_BLOCK_ENTITY = create("cooking_pan", FabricBlockEntityTypeBuilder.create(CookingPanEntity::new, ObjectRegistry.COOKING_PAN).build());
    public static final BlockEntityType<WineStationBlockEntity> WINE_STATION_BLOCK_ENTITY = create("wine_station", FabricBlockEntityTypeBuilder.create(WineStationBlockEntity::new, ObjectRegistry.WINE_STATION).build());



    private static <T extends BlockEntityType<?>> T create(final String path, final T type) {
        BLOCK_ENTITY_TYPES.put(new CandlelightIdentifier(path), type);
        return type;
    }

    public static void init() {
        for (Map.Entry<Identifier, BlockEntityType<?>> entry : BLOCK_ENTITY_TYPES.entrySet()) {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, entry.getKey(), entry.getValue());
        }
    }
}