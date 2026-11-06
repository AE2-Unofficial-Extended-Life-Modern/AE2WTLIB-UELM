package de.mari_023.ae2wtlib.wft;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.wct.WCTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.FilterTerminalMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;

public class WFTMenu extends FilterTerminalMenu {
    public static final String ID = "wireless_filter_terminal";
    public static final MenuType<WFTMenu> TYPE = MenuTypeBuilder.create(WFTMenu::new, WFTMenuHost.class).build(ID);

    private final WFTMenuHost wftMenuHost;
    private final ToolboxMenu toolboxMenu;

    public WFTMenu(int id, final Inventory ip, final WFTMenuHost anchor) {
        super(TYPE, id, ip, anchor);
        wftMenuHost = anchor;
        toolboxMenu = new ToolboxMenu(this);

        IUpgradeInventory upgrades = wftMenuHost.getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            addSlot(slot, SlotSemantics.UPGRADE);
        }
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                wftMenuHost.getSubInventory(WCTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public void broadcastChanges() {
        toolboxMenu.tick();
        super.broadcastChanges();
    }

    public boolean isWUT() {
        return wftMenuHost.getItemStack().getItem() instanceof ItemWUT;
    }

    public WFTMenuHost getHost() {
        return wftMenuHost;
    }

    public ToolboxMenu getToolbox() {
        return toolboxMenu;
    }
}
