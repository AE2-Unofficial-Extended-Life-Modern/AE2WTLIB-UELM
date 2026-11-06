package de.mari_023.ae2wtlib.wft;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.terminal.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.wut.TerminalSelectionButton;

import appeng.client.gui.me.filterterminal.FilterTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.menu.SlotSemantics;

public class WFTScreen extends FilterTerminalScreen<WFTMenu> implements IUniversalTerminalCapable {
    public WFTScreen(WFTMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(new TerminalSelectionButton(getMenu().getHost(), this::storeState));

        widgets.add("scrollingUpgrades", new ScrollingUpgradesPanel(getMenu().getSlots(SlotSemantics.UPGRADE),
                getMenu().getHost(), widgets));
        if (getMenu().getToolbox().isPresent())
            widgets.add("toolbox", new ToolboxPanel(style, getMenu().getToolbox().getName()));
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    @Override
    public void storeState() {
    }
}
