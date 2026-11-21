package de.aetherkit.registry;

import de.aetherkit.AetherKit;
import de.aetherkit.block.GraveTileEntity;
import de.aetherkit.block.GrowthTotemTileEntity;
import de.aetherkit.block.WardingLanternTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, AetherKit.MODID);

    public static final RegistryObject<TileEntityType<GraveTileEntity>> GRAVE = TILE_ENTITIES.register("grave",
            () -> TileEntityType.Builder.of(GraveTileEntity::new, ModBlocks.GRAVE.get()).build(null));
    public static final RegistryObject<TileEntityType<WardingLanternTileEntity>> WARDING_LANTERN = TILE_ENTITIES.register("warding_lantern",
            () -> TileEntityType.Builder.of(WardingLanternTileEntity::new, ModBlocks.WARDING_LANTERN.get()).build(null));
    public static final RegistryObject<TileEntityType<GrowthTotemTileEntity>> GROWTH_TOTEM = TILE_ENTITIES.register("growth_totem",
            () -> TileEntityType.Builder.of(GrowthTotemTileEntity::new, ModBlocks.GROWTH_TOTEM.get()).build(null));

    private ModTileEntities() {}
}
