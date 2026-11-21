package de.aetherkit.block;

import de.aetherkit.registry.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.UUID;

public class GraveTileEntity extends TileEntity {
    private NonNullList<ItemStack> items = NonNullList.create();
    private UUID owner;
    private String ownerName = "?";

    public GraveTileEntity() {
        super(ModTileEntities.GRAVE.get());
    }

    public void fill(PlayerEntity player, List<ItemStack> stacks) {
        this.owner = player.getUUID();
        this.ownerName = player.getGameProfile().getName();
        this.items = NonNullList.create();
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) items.add(s);
        }
        setChanged();
    }

    public boolean canOpen(PlayerEntity player) {
        return owner == null || owner.equals(player.getUUID()) || player.isCreative() || player.hasPermissions(2);
    }

    public String getOwnerName() {
        return ownerName;
    }

    /** Nimmt alle Items heraus und leert das Grab (verhindert Duplikate). */
    public NonNullList<ItemStack> takeAll() {
        NonNullList<ItemStack> out = items;
        items = NonNullList.create();
        setChanged();
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        int size = nbt.getInt("Size");
        items = NonNullList.withSize(size, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, items);
        if (nbt.hasUUID("Owner")) owner = nbt.getUUID("Owner");
        ownerName = nbt.getString("OwnerName");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("Size", items.size());
        ItemStackHelper.saveAllItems(nbt, items, true);
        if (owner != null) nbt.putUUID("Owner", owner);
        nbt.putString("OwnerName", ownerName);
        return nbt;
    }
}
