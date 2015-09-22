/*
 * BungeeTabListPlus - a BungeeCord plugin to customize the tablist
 *
 * Copyright (C) 2014 - 2015 Florian Stober
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package codecrafter47.bungeetablistplus.packet.v1_6_4;

import codecrafter47.bungeetablistplus.packet.LegacyPacketAccess;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.protocol.packet.PacketC9PlayerListItem;

/**
 * @author Florian Stober
 */
public class PlayerListPacketAccess16 implements LegacyPacketAccess.PlayerListPacketAccess {

    @Override
    public void createOrUpdatePlayer(Connection.Unsafe connection, String player, int ping) {
        connection.sendPacket(new PacketC9PlayerListItem(player, true, (short) ping));
    }

    @Override
    public void removePlayer(Connection.Unsafe connection, String player) {
        connection.sendPacket(new PacketC9PlayerListItem(player, false, (short) 9999));
    }

}
