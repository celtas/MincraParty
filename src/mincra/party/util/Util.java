package mincra.party.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Util {
	public static void debug(ChatColor ChatColor, String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor+"[MincraParty] "+msg);
	}
	public static void debug(String msg) {
		Bukkit.getConsoleSender().sendMessage("[MincraParty] "+msg);
	}
	public static void debug2(String msg) {
		Bukkit.getConsoleSender().sendMessage(msg);
	}
}
