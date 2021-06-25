/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package codecrafter47.bungeetablistplus.listener;

import codecrafter47.bungeetablistplus.BungeeTabListPlus;
import codecrafter47.bungeetablistplus.player.VelocityPlayer;
import codecrafter47.bungeetablistplus.tablist.ExcludedServersTabOverlayProvider;
import com.sun.security.ntlm.Server;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.platform.EventListener;

public class TabListListener implements Listener {

    private final BungeeTabListPlus btlp;

    public TabListListener(BungeeTabListPlus btlp) {
        this.btlp = btlp;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PostLoginEvent e) {
        try {
            VelocityPlayer player = btlp.getVelocityPlayerProvider().onPlayerConnected(e.getPlayer());
            TabView tabView = btlp.getTabViewManager().onPlayerJoin(e.getPlayer());
            tabView.getTabOverlayProviders().addProvider(new ExcludedServersTabOverlayProvider(player, btlp));
            for (EventListener listener : btlp.getListeners()) {
                listener.onTabViewAdded(tabView, player);
            }
        } catch (Throwable th) {
            BungeeTabListPlus.getInstance().reportError(th);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        try {
            TabView tabView = btlp.getTabViewManager().onPlayerDisconnect(e.getPlayer());
            tabView.deactivate();
            for (EventListener listener : btlp.getListeners()) {
                listener.onTabViewRemoved(tabView);
            }
            btlp.getVelocityPlayerProvider().onPlayerDisconnected(e.getPlayer());

            // hack to revert changes from https://github.com/SpigotMC/BungeeCord/commit/830f18a35725f637d623594eaaad50b566376e59
            Server server = e.getPlayer().getServer();
            if (server != null) {
                server.disconnect("Quitting");
            }
            ((UserConnection) e.getPlayer()).setServer(null);
        } catch (Throwable th) {
            BungeeTabListPlus.getInstance().reportError(th);
        }
    }

    @EventHandler
    public void onReload(ProxyReloadEvent event) {
        btlp.reload();
    }
}
