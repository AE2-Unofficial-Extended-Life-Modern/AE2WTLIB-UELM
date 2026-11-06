package de.mari_023.ae2wtlib.wut;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.networking.ClientNetworkManager;
import de.mari_023.ae2wtlib.networking.c2s.SelectTerminalPacket;
import de.mari_023.ae2wtlib.terminal.Icon;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

import appeng.client.gui.style.BackgroundGenerator;
import appeng.client.gui.widgets.ITooltip;

/**
 * Toolbar button for the wut that opens a panel to the left listing all installed terminals
 */
public class TerminalSelectionButton extends Button implements ITooltip {
    private static final int SIZE = 16;
    private static final int MAX_ROWS = 3;
    private static final int GAP = 2;
    private static final int PANEL_GAP = 4;
    private static final int PANEL_PADDING = 4;

    private final WTMenuHost host;
    private final Runnable storeState;
    private final List<String> terminals = new ArrayList<>();
    private final List<ItemStack> icons = new ArrayList<>();
    private boolean menuOpen;
    private List<Component> tooltip = List.of();

    public TerminalSelectionButton(WTMenuHost host, Runnable storeState) {
        super(0, 0, SIZE, SIZE, Component.empty(), btn -> {
        }, Button.DEFAULT_NARRATION);
        this.host = host;
        this.storeState = storeState;
        for (var terminal : WUTHandler.terminalNames) {
            if (!WUTHandler.hasTerminal(host.getItemStack(), terminal))
                continue;
            terminals.add(terminal);
            icons.add(new ItemStack((ItemLike) WUTHandler.wirelessTerminals.get(terminal).item()));
        }
    }

    @Override
    public void onPress() {
        if (!terminals.isEmpty())
            menuOpen = !menuOpen;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (!visible)
            return;

        tooltip = List.of();
        Icon.BUTTON_BACKGROUND.getBlitter().dest(getX(), getY()).blit(guiGraphics);
        int current = terminals.indexOf(WUTHandler.getCurrentTerminal(host.getItemStack()));
        if (current >= 0)
            renderIcon(guiGraphics, icons.get(current), getX(), getY());
        if (isFocused())
            guiGraphics.renderOutline(getX() - 1, getY() - 1, width + 2, height + 2, 0xFFFFFFFF);

        if (isHovered() && !menuOpen)
            tooltip = current < 0 ? List.of(TextConstants.TERMINAL_EMPTY)
                    : List.of(TextConstants.currentTerminal(terminalName(current)));

        if (menuOpen)
            renderMenu(guiGraphics, mouseX, mouseY, current);
    }

    private void renderMenu(GuiGraphics guiGraphics, int mouseX, int mouseY, int current) {
        BackgroundGenerator.draw(panelWidth(), panelHeight(), guiGraphics, panelX(), panelY());

        int hovered = terminalAt(mouseX, mouseY);
        for (int i = 0; i < terminals.size(); i++) {
            int x = buttonX(i);
            int y = buttonY(i);
            Icon.BUTTON_BACKGROUND.getBlitter().dest(x, y).blit(guiGraphics);
            renderIcon(guiGraphics, icons.get(i), x, y);
            if (i == current)
                guiGraphics.renderOutline(x - 1, y - 1, SIZE + 2, SIZE + 2, 0xFFFFFFFF);
            if (i == hovered) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x, y, 0);
                tooltip = List.of(terminalName(i));
            }
        }
    }

    private static void renderIcon(GuiGraphics guiGraphics, ItemStack icon, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 1, y + 1, 0);
        guiGraphics.pose().scale(14 / 16f, 14 / 16f, 1);
        guiGraphics.renderItem(icon, 0, 0);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menuOpen && button == 0) {
            int i = terminalAt(mouseX, mouseY);
            if (i >= 0) {
                menuOpen = false;
                storeState.run();
                ClientNetworkManager.sendToServer(new SelectTerminalPacket(terminals.get(i)));
                return true;
            }
        }

        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        menuOpen = false;
        return false;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return tooltip;
    }

    @Override
    public Rect2i getTooltipArea() {
        if (!menuOpen)
            return new Rect2i(getX(), getY(), width, height);
        return new Rect2i(panelX(), panelY(), getX() + width - panelX(), Math.max(height, panelHeight()));
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    private Component terminalName(int index) {
        return WUTHandler.wirelessTerminals.get(terminals.get(index)).terminalName();
    }

    private int terminalAt(double mouseX, double mouseY) {
        for (int i = 0; i < terminals.size(); i++) {
            int x = buttonX(i);
            int y = buttonY(i);
            if (mouseX >= x && mouseY >= y && mouseX < x + SIZE && mouseY < y + SIZE)
                return i;
        }
        return -1;
    }

    private int panelX() {
        return getX() - PANEL_GAP - panelWidth();
    }

    private int panelY() {
        return getY() - PANEL_PADDING;
    }

    private int panelWidth() {
        return PANEL_PADDING * 2 + columns() * SIZE + (columns() - 1) * GAP;
    }

    private int panelHeight() {
        return PANEL_PADDING * 2 + rows() * SIZE + (rows() - 1) * GAP;
    }

    private int buttonX(int index) {
        return panelX() + PANEL_PADDING + (index / rows()) * (SIZE + GAP);
    }

    private int buttonY(int index) {
        return panelY() + PANEL_PADDING + (index % rows()) * (SIZE + GAP) + lastColumnYOffset(index);
    }

    private int columns() {
        return Math.max(1, (terminals.size() + rows() - 1) / rows());
    }

    private int rows() {
        return Math.max(1, Math.min(MAX_ROWS, terminals.size()));
    }

    private int lastColumnYOffset(int index) {
        int entries = terminals.size() % rows();
        if (entries == 0 || index / rows() != columns() - 1)
            return 0;
        return (rows() - entries) * (SIZE + GAP) / 2;
    }
}
