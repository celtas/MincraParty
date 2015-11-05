package mincra.party.main;

import java.io.File;

import mincra.party.cmd.MincraCommands;
import mincra.party.listener.PlayerChat;
import mincra.party.listener.PlayerQuit;
import mincra.party.util.Util;
import mincra.party.version.Version;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MincraParty extends JavaPlugin{
	private File configFile;
	private FileConfiguration config;
	public static int maxplayers;
	public static JavaPlugin plugin;

	public void onEnable(){
		plugin=this;
		this.saveDefaultConfig();
		configFile= new File(getDataFolder(),"config.yml");
		config =this.getConfig();
		try {
			config.load(this.configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadconfig();
		this.getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerChat(this), this);
		getCommand("party").setExecutor(new MincraCommands(this));

		//Json関係
		try {
			new Version(this);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onDisable(){
	}

	private void loadconfig(){
		maxplayers = config.getInt("MincraParty-Config.players");
		if(maxplayers>=1&&maxplayers<=10){
			Util.debug("最大パーティー人数:"+maxplayers+"人");
		}else{
			Util.debug("Error:"+"maxplayers:"+maxplayers);
			maxplayers=8;
		}
	}

}
