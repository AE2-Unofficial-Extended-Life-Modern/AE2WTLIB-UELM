package de.mari_023.ae2wtlib.terminal;

import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Edit in {@code assets/ae2/textures/wtlib/guis/icons.png}.
 */
public record Icon(int x, int y, int width, int height, Texture texture) {

    public static final Texture TEXTURE = new Texture(AppEng.makeId("textures/wtlib/guis/icons.png"), 128, 128);
    public static final Texture AE2_TEXTURE = new Texture(appeng.client.gui.Icon.TEXTURE,
            appeng.client.gui.Icon.TEXTURE_WIDTH, appeng.client.gui.Icon.TEXTURE_HEIGHT);

    public static final Icon BUTTON_BACKGROUND = new Icon(240, 240, 16, 16, AE2_TEXTURE);

    public static final Icon MAGNET = new Icon(32, 64, 16, 16, AE2_TEXTURE);
    public static final Icon MAGNET_FILTER = new Icon(0, 0);
    public static final Icon TRASH = new Icon(0, 32);

    public static final Icon UPGRADE_BACKGROUND_TOP = new Icon(90, 0, 32, 25);
    public static final Icon UPGRADE_BACKGROUND_MIDDLE = new Icon(90, 25, 32, 18);
    public static final Icon UPGRADE_BACKGROUND_BOTTOM = new Icon(90, 43, 32, 25);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_TOP = new Icon(48, 0, 42, 25);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_MIDDLE = new Icon(48, 25, 42, 18);
    public static final Icon UPGRADE_BACKGROUND_SCROLLING_BOTTOM = new Icon(48, 43, 42, 25);

    private Icon(int x, int y) {
        this(x, y, 16, 16);
    }

    private Icon(int x, int y, int width, int height) {
        this(x, y, width, height, TEXTURE);
    }

    public Blitter getBlitter() {
        return Blitter.texture(texture.location(), texture.width(), texture.height())
                .src(x, y, width, height);
    }

    public record Texture(ResourceLocation location, int width, int height) {
    }
}
