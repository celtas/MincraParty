package mincra.party.version;

import org.bukkit.entity.Player;

public interface VersionListener{
	public void sendJsonMessage(String message);
	public void sendJsonMessage(Player player,String message);
}
