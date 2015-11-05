package mincra.party.version.v_1_8_R3;

import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import mincra.party.version.VersionListener;

public class SendJsonMessage implements VersionListener {
	public SendJsonMessage(){
	}
	
	@Override
	public void sendJsonMessage(String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(message));
		for (Player p: Bukkit.getOnlinePlayers())
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	@Override
	public void sendJsonMessage(Player player,String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(message));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
	
}
