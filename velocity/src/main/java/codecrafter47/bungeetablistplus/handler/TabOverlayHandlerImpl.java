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

package codecrafter47.bungeetablistplus.handler;

import codecrafter47.bungeetablistplus.BungeeTabListPlus;
import com.velocitypowered.api.proxy.Player;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class TabOverlayHandlerImpl extends AbstractTabOverlayHandler {

    private final Player player;

    public TabOverlayHandlerImpl(Logger logger, Executor eventLoopExecutor, UUID viewerUuid, Player player, boolean is18, boolean is13OrLater) {
        super(logger, eventLoopExecutor, viewerUuid, is18, is13OrLater);
        this.player = player;
    }

    @Override
    protected void sendPacket(DefinedPacket packet) {
        player.unsafe().sendPacket(packet);
    }

    @Override
    protected boolean isExperimentalTabCompleteSmileys() {
        return BungeeTabListPlus.getInstance().getConfig().experimentalTabCompleteSmileys;
    }

    @Override
    protected boolean isExperimentalTabCompleteFixForTabSize80() {
        return BungeeTabListPlus.getInstance().getConfig().experimentalTabCompleteFixForTabSize80;
    }

    @Override
    protected boolean isUsingAltRespawn() {
        return WaterfallCompat.isDisableEntityMetadataRewrite()
                || (player.getPendingConnection().getVersion() >= 735
                && ProtocolConstants.SUPPORTED_VERSION_IDS.contains(736));
    }
}
