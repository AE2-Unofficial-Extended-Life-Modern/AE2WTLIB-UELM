package de.mari_023.ae2wtlib.wct;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.Icon;
import de.mari_023.ae2wtlib.terminal.IconButton;
import de.mari_023.ae2wtlib.terminal.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.wut.TerminalSelectionButton;

import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import appeng.menu.SlotSemantics;

public class WCTScreen extends CraftingTermScreen<WCTMenu> implements IUniversalTerminalCapable {

    private final IconButton magnetCardMenuButton;

    public WCTScreen(WCTMenu container, Inventory playerInventory, Component title, ScreenStyle style) {
        super(container, playerInventory, title, style);
        if (getMenu().isWUT())
            addToLeftToolbar(new TerminalSelectionButton((WTMenuHost) getMenu().getHost(), this::storeState));

        IconButton wirelessTerminalSettingsButton = new IconButton(
                btn -> switchToScreen(new WirelessTerminalSettingsScreen(this)), Icon.MAGNET);
        widgets.add("wirelessTerminalSettingsButton", wirelessTerminalSettingsButton);
        wirelessTerminalSettingsButton.setMessage(TextConstants.TERMINAL_SETTINGS);

        magnetCardMenuButton = new IconButton(btn -> getMenu().openMagnetMenu(), Icon.MAGNET_FILTER);
        widgets.add("magnetCardMenuButton", magnetCardMenuButton);
        magnetCardMenuButton.setMessage(TextConstants.MAGNET_FILTER);

        IconButton trashButton = new IconButton(btn -> getMenu().openTrashMenu(), Icon.TRASH);
        widgets.add("trashButton", trashButton);
        trashButton.setMessage(TextConstants.TRASH);

        widgets.add("player", new PlayerEntityWidget(Objects.requireNonNull(Minecraft.getInstance().player)));
        widgets.add("scrollingUpgrades", new ScrollingUpgradesPanel(getMenu().getSlots(SlotSemantics.UPGRADE),
                getMenu().getHost(), widgets));
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    private void setMagnetModeText() {
        magnetCardMenuButton.setVisibility(
                switch (getMenu().getMagnetMode()) {
                    case INVALID, NO_CARD -> false;
                    case OFF, PICKUP_ME, PICKUP_INVENTORY, PICKUP_ME_NO_MAGNET -> true;
                });
    }

    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setMagnetModeText();
    }
}
