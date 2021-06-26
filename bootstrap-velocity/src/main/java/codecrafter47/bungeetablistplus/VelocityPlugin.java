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

package codecrafter47.bungeetablistplus;


import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

@Plugin(id = "BungeeTabListPlus_Velocity")
public class VelocityPlugin {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        if (Float.parseFloat(System.getProperty("java.class.version")) < 52.0) {
            logger.error("§cBungeeTabListPlus requires Java 8 or above. Please download and install it!");
            logger.error("Disabling plugin!");
            return;
        }
        if (!server.getAllPlayers().isEmpty()) {
            for (Player proxiedPlayer : server.getAllPlayers()) {
                proxiedPlayer.disconnect(Component.text("Cannot reload BungeeTabListPlus while players are online."));
            }
        }

        if (!server.getAllPlayers().isEmpty()) {
            for (Player proxiedPlayer : server.getAllPlayers()) {
                proxiedPlayer.disconnect(Component.text("Cannot reload BungeeTabListPlus while players are online."));
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }
}
