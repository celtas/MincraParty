package mincra.party.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mincra.party.main.MincraParty;
import mincra.party.scorebord.ScoreBord;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerData {
	//public static Map<String,Integer> partylevel=new HashMap<String,Integer>();
	//public static Map<String,Integer> partyexp=new HashMap<String,Integer>();
	public static Map<String,ArrayList<Player>> partylist=new HashMap<String,ArrayList<Player>>();
	public static Map<String,ScoreBord> scoreboardlist =new HashMap<String,ScoreBord>();
	public static ArrayList<Player> playerlist = new ArrayList<Player>();
	public static ArrayList<Player> leaderlist = new ArrayList<Player>();
	public static HashMap<Player,String> invitelist = new HashMap<Player,String>();

	public static Boolean isJoin(Player player){
		if(playerlist.contains(player)){
			return true;
		}
		return false;
	}

	public static Boolean existsInviteList(Player player){
		if(invitelist.isEmpty()){
			return false;
		}
		if(invitelist.containsKey(player)){
			return true;
		}
		return false;
	}

	public static int getPartySize(String name){
		if(!partylist.containsKey(name)){
			return -1;
		}else{
			return partylist.get(name).size();
		}
	}

	public static String getPartyName(Player player){
		for(Entry<String, ArrayList<Player>> e:partylist.entrySet()){
			for(Player p:e.getValue()){
				if(p.equals(player)){
					return e.getKey();
				}
			}
		}
		return null;
	}
	public static void addMetadata(Player player,String partyName,boolean isLeader,short partySize){
		player.setMetadata("partyname", new FixedMetadataValue(MincraParty.plugin, partyName));
	    player.setMetadata("partyleader", new FixedMetadataValue(MincraParty.plugin, isLeader));
	    player.setMetadata("partychat", new FixedMetadataValue(MincraParty.plugin, true));
	    player.setMetadata("partysize", new FixedMetadataValue(MincraParty.plugin, partySize));
	}

	public static void removeMetadata(Player player) {
		player.removeMetadata("partyname", MincraParty.plugin);
		player.removeMetadata("partyleader", MincraParty.plugin);
		player.removeMetadata("partychat", MincraParty.plugin);
		player.removeMetadata("partysize", MincraParty.plugin);
	}
}
