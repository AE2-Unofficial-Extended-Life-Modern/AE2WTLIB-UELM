package de.mari_023.ae2wtlib.networking.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.networking.AE2wtlibPacket;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.wut.WUTHandler;

import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocator;

public class SelectTerminalPacket extends AE2wtlibPacket {

    public static final String NAME = "select_terminal";

    public SelectTerminalPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    public SelectTerminalPacket(String terminal) {
        super(createBuffer());
        buf.writeUtf(terminal);
    }

    @Override
    public void processPacketData(Player player) {
        if (!(player.containerMenu instanceof AEBaseMenu aeMenu))
            return;

        final MenuLocator locator = aeMenu.getLocator();
        WTMenuHost host = locator.locate(player, WTMenuHost.class);
        if (host == null)
            return;
        ItemStack item = host.getItemStack();
        String terminal = buf.readUtf();

        if (!(item.getItem() instanceof ItemWUT) || !WUTHandler.hasTerminal(item, terminal))
            return;

        WUTHandler.setCurrentTerminal(player, locator, item, terminal);
        WUTHandler.open(player, locator, true);
    }

    @Override
    public String getPacketName() {
        return NAME;
    }
}
