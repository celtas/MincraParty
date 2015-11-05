package mincra.party.version;


import mincra.party.main.MincraParty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Version{
	public static VersionListener version;

	public Version(MincraParty plugin) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		switch(Bukkit.getServer().getClass().getPackage().getName()){
			case "org.bukkit.craftbukkit.v1_8_R3":
				version = (VersionListener) Class.forName("mincra.party.version.v_1_8_R3.SendJsonMessage").newInstance();
				break;
			default:
				String name = Bukkit.getServer().getClass().getPackage().getName();
				Bukkit.getLogger().info(ChatColor.YELLOW+"バージョン"+name.substring(name.lastIndexOf('.') + 2)+"は非対応です。");
				Bukkit.getLogger().info(ChatColor.YELLOW+"プラグインを停止させました。");
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
				break;
		}
	}

	public static void sendJsonMessage(String message) {
		version.sendJsonMessage(message);
	}

	public static void sendJsonMessage(Player player,String message) {
		version.sendJsonMessage(player,message);
	}

}
