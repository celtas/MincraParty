package mincra.party.listener;

import mincra.party.data.PlayerData;
import mincra.party.main.MincraParty;
import mincra.party.scorebord.ScoreBord;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
	static JavaPlugin plugin;
	@SuppressWarnings("static-access")
	public PlayerQuit(JavaPlugin plugin) {
	    this.plugin = plugin;
	}
	@EventHandler
	public void PlayerQuitEvent(PlayerQuitEvent e){
		Player player = e.getPlayer();
		if(PlayerData.isJoin(player)){
			if(player.getMetadata("partyleader").get(0).asBoolean()==true){
				String key=player.getMetadata("partyname").get(0).asString();
				player.sendMessage(ChatColor.GOLD+key+"パーティを解散しました。");
				for(Player leaveplayer:PlayerData.partylist.get(key)){
					PlayerData.playerlist.remove(leaveplayer);
					
					//プレイヤーのメタデータを削除
				    PlayerData.removeMetadata(leaveplayer);
				    
					leaveplayer.sendMessage(ChatColor.GOLD+key+"パーティが解散されました。");

				}
				ScoreBord scorebord = PlayerData.scoreboardlist.get(key);
				if(scorebord != null) scorebord.cancel();
				PlayerData.scoreboardlist.remove(key);
				PlayerData.leaderlist.remove(player);
				PlayerData.partylist.remove(key);
			}else {
				final String key=player.getMetadata("partyname").get(0).asString();
				ScoreBord scorebord = PlayerData.scoreboardlist.get(key);
				scorebord.cancel();
				PlayerData.partylist.get(key).remove(player);
				PlayerData.playerlist.remove(player);
				
				//プレイヤーのメタデータを削除
			    PlayerData.removeMetadata(player);
			    
				player.sendMessage(ChatColor.GOLD+key+"パーティから脱退しました。");
				for(Player pl:PlayerData.partylist.get(key)){
					if(!pl.equals(player)){
						pl.sendMessage(ChatColor.GRAY+player.getName()+"がパーティから抜けました。");
					}
				}
				PlayerData.scoreboardlist.remove(key);
				new BukkitRunnable(){
					@Override
					public void run() {
						PlayerData.scoreboardlist.put(key, new ScoreBord(key,PlayerData.partylist.get(key)));
					}
				}.runTaskLater(plugin, 0);
			}
	    }
	}
}
