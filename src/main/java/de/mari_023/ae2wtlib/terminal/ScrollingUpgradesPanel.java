package de.mari_023.ae2wtlib.terminal;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.Upgrades;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.Rects;
import appeng.client.gui.Tooltip;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.Scrollbar;
import appeng.menu.slot.AppEngSlot;

public class ScrollingUpgradesPanel implements ICompositeWidget {
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 7;
    private static final int SCROLLBAR_WIDTH = 10;

    private final List<Slot> slots;
    private final IUpgradeableObject upgradeableObject;
    private final Scrollbar scrollbar;

    private Point screenOrigin = Point.ZERO;

    private int x;
    private int y;
    private int maxHeight;

    private int maxRows = 1;

    public ScrollingUpgradesPanel(List<Slot> slots, IUpgradeableObject upgradeableObject, WidgetContainer widgets) {
        this.slots = slots;
        this.upgradeableObject = upgradeableObject;

        scrollbar = widgets.addScrollBar("upgradeScrollbar", Scrollbar.SMALL);
        scrollbar.setCaptureMouseWheel(false);
    }

    @Override
    public void setPosition(Point position) {
        x = position.getX();
        y = position.getY();
        scrollbar.setPosition(new Point(x + PADDING + SLOT_SIZE + 2, y + PADDING + 1));
    }

    @Override
    public void setSize(int width, int height) {
        maxHeight = height;
    }

    @Override
    public Rect2i getBounds() {
        int height = 2 * PADDING + getVisibleSlotCount() * SLOT_SIZE;
        int width = 2 * PADDING + SLOT_SIZE + (scrolling() ? SCROLLBAR_WIDTH : 0);
        return new Rect2i(x, y, width, height);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        screenOrigin = Point.fromTopLeft(bounds);
        maxRows = Math.max(1, (bounds.getHeight() - y - 2 * PADDING) / SLOT_SIZE);

        int available = bounds.getHeight() - y;
        if (maxHeight > 0) {
            available = Math.min(available, maxHeight);
        }
        maxRows = Math.max(1, (available - 2 * PADDING) / SLOT_SIZE);
    }

    @Override
    public void updateBeforeRender() {
        setScrollbarRange();
        int firstVisibleSlot = scrollbar.getCurrentScroll();

        int i = 0;
        for (Slot s : slots) {
            if (!(s instanceof AppEngSlot slot) || !slot.isSlotEnabled())
                continue;

            boolean visible = firstVisibleSlot <= i && firstVisibleSlot + maxRows > i;
            slot.setActive(visible);
            if (visible) {
                slot.x = x + PADDING + 1;
                slot.y = y + PADDING + 1 + (i - firstVisibleSlot) * SLOT_SIZE;
            }
            i++;
        }
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        int slotCount = getVisibleSlotCount();
        if (slotCount <= 0) {
            return;
        }

        int originX = screenOrigin.getX() + x;
        int slotOriginY = screenOrigin.getY() + y + PADDING;
        UpgradeBackground bg = UpgradeBackground.get(scrolling());

        bg.top().getBlitter().dest(originX, slotOriginY - PADDING).blit(guiGraphics);
        for (int i = 1; i < slotCount - 1; i++) {
            bg.middle().getBlitter().dest(originX, slotOriginY + i * SLOT_SIZE).blit(guiGraphics);
        }
        bg.bottom().getBlitter().dest(originX, slotOriginY + (slotCount - 1) * SLOT_SIZE).blit(guiGraphics);
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i screenBounds) {
        exclusionZones.add(Rects.expand(Rects.move(getBounds(), screenBounds.getX(), screenBounds.getY()), 2));
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        if (getUpgradeSlotCount() == 0) {
            return null;
        }

        var tooltip = Upgrades.getTooltipLinesForMachine(upgradeableObject.getUpgrades().getUpgradableItem());
        if (tooltip.isEmpty()) {
            return null;
        }

        return new Tooltip(tooltip);
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        return scrollbar.onMouseWheel(mousePos, delta);
    }

    private int getUpgradeSlotCount() {
        int count = 0;
        for (Slot slot : slots) {
            if (slot instanceof AppEngSlot appEngSlot && appEngSlot.isSlotEnabled()) {
                count++;
            }
        }
        return count;
    }

    private void setScrollbarRange() {
        scrollbar.setRange(0, getUpgradeSlotCount() - getVisibleSlotCount(), 1);
        scrollbar.setHeight(getVisibleSlotCount() * SLOT_SIZE - 2);
        scrollbar.setVisible(scrolling());
    }

    private int getVisibleSlotCount() {
        return Math.min(maxRows, getUpgradeSlotCount());
    }

    private boolean scrolling() {
        return getUpgradeSlotCount() > maxRows;
    }
}
