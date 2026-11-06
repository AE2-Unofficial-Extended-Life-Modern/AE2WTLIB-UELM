package de.mari_023.ae2wtlib.wft;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.ItemWT;

public class ItemWFT extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemStack stack) {
        return WFTMenu.TYPE;
    }
}
