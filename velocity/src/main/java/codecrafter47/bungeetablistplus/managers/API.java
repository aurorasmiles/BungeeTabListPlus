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

package codecrafter47.bungeetablistplus.managers;

import codecrafter47.bungeetablistplus.BungeeTabListPlus;
import codecrafter47.bungeetablistplus.api.velocity.*;
import codecrafter47.bungeetablistplus.data.BTLPBungeeDataKeys;
import codecrafter47.bungeetablistplus.placeholder.PlayerPlaceholderResolver;
import codecrafter47.bungeetablistplus.placeholder.ServerPlaceholderResolver;
import codecrafter47.bungeetablistplus.player.VelocityPlayer;
import codecrafter47.bungeetablistplus.player.FakePlayerManagerImpl;
import codecrafter47.bungeetablistplus.tablist.DefaultCustomTablist;
import codecrafter47.bungeetablistplus.util.IconUtil;
import com.google.common.base.Preconditions;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.icon.IconManager;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;


public class API extends BungeeTabListPlusAPI {

    private final TabViewManager tabViewManager;
    private final IconManager iconManager;
    private final PlayerPlaceholderResolver playerPlaceholderResolver;
    private final ServerPlaceholderResolver serverPlaceholderResolver;
    private final Logger logger;
    private final BungeeTabListPlus btlp;

    private final Map<String, Variable> variablesByName = new HashMap<>();
    private final Map<String, ServerVariable> serverVariablesByName = new HashMap<>();

    public API(TabViewManager tabViewManager, IconManager iconManager, PlayerPlaceholderResolver playerPlaceholderResolver, ServerPlaceholderResolver serverPlaceholderResolver, Logger logger, BungeeTabListPlus btlp) {
        this.tabViewManager = tabViewManager;
        this.iconManager = iconManager;
        this.playerPlaceholderResolver = playerPlaceholderResolver;
        this.serverPlaceholderResolver = serverPlaceholderResolver;
        this.logger = logger;
        this.btlp = btlp;
    }

    @Override
    protected void setCustomTabList0(Player player, CustomTablist customTablist) {
        TabView tabView = tabViewManager.getTabView(player);
        if (tabView == null) {
            throw new IllegalStateException("unknown player");
        }
        if (customTablist instanceof DefaultCustomTablist) {
            tabView.getTabOverlayProviders().removeProviders(DefaultCustomTablist.TabOverlayProviderImpl.class);
            ((DefaultCustomTablist) customTablist).addToPlayer(tabView);
        } else {
            throw new IllegalArgumentException("customTablist not created by createCustomTablist()");
        }
    }

    @Override
    protected void removeCustomTabList0(Player player) {
        TabView tabView = tabViewManager.getTabView(player);
        if (tabView == null) {
            throw new IllegalStateException("unknown player");
        }
        tabView.getTabOverlayProviders().removeProviders(DefaultCustomTablist.TabOverlayProviderImpl.class);
    }

    @Nonnull
    @Override
    protected Icon getIconFromPlayer0(Player player) {
        return IconUtil.convert(IconUtil.getIconFromPlayer(player));
    }

    @Override
    protected void createIcon0(BufferedImage image, Consumer<Icon> callback) {
        CompletableFuture<de.codecrafter47.taboverlay.Icon> future = iconManager.createIcon(image);
        future.thenAccept(icon -> callback.accept(IconUtil.convert(icon)));
    }

    @Override
    protected void registerVariable0(Plugin plugin, Variable variable) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(variable, "variable");
        String id = variable.getName().toLowerCase();
        Preconditions.checkArgument(!variablesByName.containsKey(id), "Variable name already registered.");
        DataKey<String> dataKey = BTLPBungeeDataKeys.createBungeeThirdPartyVariableDataKey(id);
        playerPlaceholderResolver.addCustomPlaceholderDataKey(id, dataKey);
        btlp.scheduleSoftReload();
        variablesByName.put(id, variable);
    }

    String resolveCustomPlaceholder(String id, Player player) {
        Variable variable = variablesByName.get(id);
        if (variable != null) {
            try {
                return variable.getReplacement(player);
            } catch (Throwable th) {
                logger.log(Level.SEVERE, "Failed to query custom placeholder replacement " + id, th);
            }
        }
        return "";
    }

    @Override
    protected void registerVariable0(Plugin plugin, ServerVariable variable) {
        Preconditions.checkNotNull(plugin, "plugin");
        Preconditions.checkNotNull(variable, "variable");
        String id = variable.getName().toLowerCase();
        Preconditions.checkArgument(!serverVariablesByName.containsKey(id), "Variable name already registered.");
        DataKey<String> dataKey = BTLPBungeeDataKeys.createBungeeThirdPartyServerVariableDataKey(id);
        serverPlaceholderResolver.addCustomPlaceholderServerDataKey(id, dataKey);
        btlp.scheduleSoftReload();
        serverVariablesByName.put(id, variable);
    }

    String resolveCustomPlaceholderServer(String id, String serverName) {
        ServerVariable variable = serverVariablesByName.get(id);
        if (variable != null) {
            try {
                return variable.getReplacement(serverName);
            } catch (Throwable th) {
                logger.log(Level.SEVERE, "Failed to query custom placeholder replacement " + id, th);
            }
        }
        return "";
    }

    @Override
    protected CustomTablist createCustomTablist0() {
        return new DefaultCustomTablist();
    }

    @Override
    protected FakePlayerManager getFakePlayerManager0() {
        FakePlayerManagerImpl fakePlayerManager = btlp.getFakePlayerManagerImpl();
        if (fakePlayerManager == null) {
            throw new IllegalStateException("Cannot call getFakePlayerManager() before onEnable()");
        }
        return fakePlayerManager;
    }

    @Override
    protected boolean isHidden0(Player player) {
        VelocityPlayer velocityPlayer = BungeeTabListPlus.getInstance().getVelocityPlayerProvider().getPlayerIfPresent(player);
        return velocityPlayer != null && velocityPlayer.get(BTLPBungeeDataKeys.DATA_KEY_IS_HIDDEN);
    }
}
