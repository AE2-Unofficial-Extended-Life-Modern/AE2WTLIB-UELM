package de.mari_023.ae2wtlib.wet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.terminal.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.wut.TerminalSelectionButton;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.menu.SlotSemantics;

public class WETScreen extends PatternEncodingTermScreen<WETMenu> implements IUniversalTerminalCapable {
    public WETScreen(WETMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(new TerminalSelectionButton((WTMenuHost) getMenu().getHost(), this::storeState));
        widgets.add("scrollingUpgrades", new ScrollingUpgradesPanel(getMenu().getSlots(SlotSemantics.UPGRADE),
                getMenu().getHost(), widgets));
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }
}
