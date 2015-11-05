package mincra.party.listener;

import mincra.party.data.PlayerData;
import mincra.party.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerChat implements Listener {
	static JavaPlugin plugin;
	@SuppressWarnings("static-access")
	public PlayerChat(JavaPlugin plugin) {
	    this.plugin = plugin;
	}
	@EventHandler
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		if(e.getMessage().charAt(0)=='"'||e.getMessage().charAt(0)=='”'){
			if(player.hasMetadata("partychat")){
				if(player.getMetadata("partychat").get(0).asBoolean()){
					return;
				}
			}
			if(PlayerData.isJoin(player)&&player.hasMetadata("partyname")){
				e.setCancelled(true);
				String message = "[PT専用チャット]<"+player.getName()+"> "+e.getMessage();
				for(Player p:PlayerData.partylist.get(player.getMetadata("partyname").get(0).asString())){
					p.sendMessage(message);
				}
			}else{
				e.setCancelled(true);
				player.sendMessage(ChatColor.GRAY+"パーティーチャット機能はパーティーに入っている場合のみ使用できます。");
			}
		}else{
			if(player.hasMetadata("partychat")&&player.hasMetadata("partyname")){
				if(player.getMetadata("partychat").get(0).asBoolean()){
					e.setCancelled(true);
					String message = "[PT専用チャット]<"+player.getName()+"> "+e.getMessage();
					Util.debug2(message);
					for(Player p:PlayerData.partylist.get(player.getMetadata("partyname").get(0).asString())){
						p.sendMessage(message);
					}
				}
			}
		}
	}
}
