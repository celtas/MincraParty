package mincra.party.scorebord;

import java.util.ArrayList;

import mincra.party.main.MincraParty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreBord extends BukkitRunnable implements Listener{
	private Scoreboard board;
	private ScoreboardManager manager;
	private Objective objective;
	private String[] old_st=new String[14];
	private Score[] score = new Score[13];
	//public int combo=0;
	//private int combotime=5;
	//0 1 2 3 4 5 6 7 8
	//p h p h p h p h p
	//private Score party_level,party_exp,party_combo;
	//private int partylevel=1,partyexp=0;
	private String partyname;
	public ArrayList<Player> playerlist;
	public ScoreBord(String name,ArrayList<Player> pl){
		partyname=name;
		playerlist=pl;
		// メインスコアボードを取得します。
        manager = Bukkit.getScoreboardManager();
        board= manager.getNewScoreboard();
        objective = board.getObjective(ChatColor.GOLD+partyname+"PT");
        if(objective==null){
        	objective = board.registerNewObjective(ChatColor.GOLD+partyname+"PT", "dummy");
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        //objective.score1.setScore(playerlist.get(0).getHealth());
        
        //パーティレベル表示
        /*old_st[11]=ChatColor.YELLOW+"PTlevel  "+String.valueOf(partylevel);
    	party_level= objective.getScore(old_st[11]);
    	party_level.setScore(11);*/
    	
    	
    	//パーティEXP表示
    	/*old_st[10]=ChatColor.YELLOW+"PTexp  "+String.valueOf(partyexp);
    	party_exp= objective.getScore(old_st[10]);
    	party_exp.setScore(10);*/
    	
    	/*//Combo表示
    	old_st[9]=ChatColor.YELLOW+"COMBO  "+String.valueOf(combo)+"/"+String.valueOf(combotime)+"s";
    	party_combo= objective.getScore(old_st[9]);
    	party_combo.setScore(9);*/
    	
        if(playerlist.size()<=6){
	        int scorecnt=0;
	        int cnt2=1;
	        for(Player player:playerlist){
	        	score[scorecnt]= objective.getScore(ChatColor.BOLD+player.getName());
	        	score[scorecnt].setScore(12-scorecnt);
	        	scorecnt+=2;
	        	
	        	//int maxhealth=(int) ((Damageable)player).getMaxHealth();
	        	old_st[12-cnt2]=ChatColor.YELLOW+"HP/"+String.valueOf((int) ((Damageable)player).getHealth())+" "+ChatColor.AQUA+"MP/"+String.valueOf(player.getLevel());
	        	score[cnt2]= objective.getScore(old_st[12-cnt2]);
	        	score[cnt2].setScore(12-cnt2);
	        	cnt2+=2;
	        }
        }else{
        	int cnt=0;
	        for(Player player:playerlist){
	        	score[cnt]= objective.getScore(ChatColor.BOLD+player.getName());
	        	score[cnt].setScore(12-cnt);
	        	cnt++;
	        }
        }
        
        //スコアボードのセット
        for(Player player:playerlist){
        	player.setScoreboard(board);
        }
        
        //自身をスケジュール
        this.runTaskTimer(MincraParty.plugin, 20, 20);
        MincraParty.plugin.getServer().getPluginManager().registerEvents(this, MincraParty.plugin);
	}
	@SuppressWarnings("deprecation")
	public void cancel(){
		//PlayerData.partylevel.put(partyname,PlayerData.partylevel.get(partyname));
		//PlayerData.partyexp.put(partyname,PlayerData.partyexp.get(partyname));
        for (Player player : playerlist) {
        	board.resetScores(player);
        }
		objective.unregister();
		HandlerList.unregisterAll(this);
		super.cancel();
	}
	@Override
	public void run() {
		/*//コンボ処理
		if(combo>0){
			if(combotime>0){
				combotime--;
			}else{
				combotime=5;
				combo=0;
			}
			board.resetScores(old_st[9]);
	    	old_st[9]=ChatColor.YELLOW+"COMBO  "+String.valueOf(combo)+"/"+String.valueOf(combotime)+"s";
	    	party_combo= objective.getScore(old_st[9]);
	    	party_combo.setScore(9);
		}*/
        if(playerlist.size()<=6){
        	int cnt2=1;
	        for(Player player:playerlist){
	        	board.resetScores(old_st[12-cnt2]);
	        	//int maxhealth=(int) ((Damageable)player).getMaxHealth();
	        	old_st[12-cnt2]=ChatColor.YELLOW+"HP/"+String.valueOf((int) ((Damageable)player).getHealth())+" "+ChatColor.AQUA+"MP/"+String.valueOf(player.getLevel());
	        	score[cnt2]= objective.getScore(old_st[12-cnt2]);
	        	score[cnt2].setScore(12-cnt2);
	        	cnt2+=2;
	        }
        }
	}
	/*@EventHandler
	public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getCause().equals(DamageCause.ENTITY_ATTACK)){
			if(event.getDamager() instanceof Player){
				if(event.getEntity() instanceof Monster){
					Player player=(Player) event.getDamager();
					if(playerlist.contains(player)){
						combo++;
						combotime=6;
					}
				}
			}
		}
	}*/
}
