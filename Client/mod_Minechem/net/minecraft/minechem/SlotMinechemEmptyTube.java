package net.minecraft.minechem;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.mod_Minechem;

public class SlotMinechemEmptyTube extends Slot {

	private EntityPlayer entityplayer;
	final Container container;
	
	public SlotMinechemEmptyTube(Container container, EntityPlayer player, IInventory iinventory,
			int i, int j, int k) {
		super(iinventory, i, j, k);
		this.entityplayer = player;
		this.container = container;
	}
	
	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return (itemstack.itemID == mod_Minechem.itemTesttubeEmpty.shiftedIndex);
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
