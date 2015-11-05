package mincra.party.cmd;

import java.util.ArrayList;

import mincra.party.data.PlayerData;
import mincra.party.main.MincraParty;
import mincra.party.scorebord.ScoreBord;
import mincra.party.util.Util;
import mincra.party.version.Version;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class MincraCommands extends PlayerData implements CommandExecutor {
	MincraParty plugin;
	public MincraCommands(MincraParty plugin){
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		//if (cmd.getName().equalsIgnoreCase("basic")) {
		/*if(args.length<1){
			sender.sendMessage("パラメタが足りません");
			return false;
		}*/
		if(cmd.getName().equalsIgnoreCase("party")){

			if(sender instanceof Player){

				Player player = (Player) sender;
				if(args.length<1){
					player.sendMessage(ChatColor.GRAY+"パラメタが足りません.");
					player.sendMessage(ChatColor.GRAY+"/party help");
					return true;
				}
				switch(args[0]){
				case "tp":
					if(tp(player)) return true;
					break;
				case "create":
					if(create(player,args)) return true;
					break;
				case "join":
					if(!(args.length==2)){
						player.sendMessage(ChatColor.GRAY+"パーティー名を指定してください.");
						return true;
					}
					if(join(player,args[1])) return true;
					break;
				case "chat":
					if(chat(player,args)) return true;
					break;
				case "invite":
					if(invite(player,args)) return true;
					break;
				case "accept":
					if(accept(player)) return true;
					break;
				case "deny":
					if(deny(player)) return true;
					break;
				case "leave":
					if(leave(player)) return true;
					break;
				case "list":
					if(list(player)) return true;
					break;
				case "info":
					if(info(player)) return true;
					break;
				case "help":
					if(help(player)) return true;
					break;
				case "reload":
					if(reload(player)) return true;
					break;
				default:
					return false;
			}
			return true;
		}else{
			sender.sendMessage("プレイヤーから実行してください。");
		}
	}
	return false;
}
	private boolean tp(final Player player) {
		if (!player.hasPermission("mincra.party")){
			player.sendMessage(ChatColor.GRAY+"権限を持っていない。");
	        return false;
		}
		if(!isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"パーティーに入っていません。");
			return false;
		}
		if(!player.hasMetadata("partyleader")){
			Util.debug(ChatColor.YELLOW+"メタデータを取得できません.");
			return false;
		}
		boolean isLeader = player.getMetadata("partyleader").get(0).asBoolean();

		String partyname = PlayerData.getPartyName(player);
		if(partyname == null){
			Util.debug(ChatColor.YELLOW+"パーティー名を取得できません.");
			return false;
		}

		if(isLeader){
			if(!PlayerData.partylist.containsKey(partyname)){
				Util.debug(ChatColor.YELLOW+"パーティリストにパーティーが存在しません.");
				return false;
			}
			player.sendMessage(ChatColor.GRAY+"強制招集を行いました.");
			for(final Player member:PlayerData.partylist.get(partyname)){
				if(!member.hasMetadata("partyleader")){
					Util.debug(ChatColor.YELLOW+member.getName()+":パーティーリーダーのメタデータが存在しません.");
					return false;
				}
				if(!(member.getMetadata("partyleader").get(0).asBoolean())){
					member.sendMessage(ChatColor.GRAY+"パーティーリーダーから強制招集を受けました.");
					member.sendMessage(ChatColor.GRAY+"15秒後に自動的にテレポートします.");
					new BukkitRunnable(){
						@Override
						public void run() {
							if(checkTeleport(member)||checkNear(member,player)){
								member.teleport(player);
							}else{
								player.sendMessage(ChatColor.GRAY+member.getName()+"はテレポートできませんでした.");
								member.sendMessage(ChatColor.GRAY+"テレポートに失敗しました.");
								member.sendMessage(ChatColor.GRAY+"資源ワールドではリーダーの近くにいる必要があります.");
							}
						}
					}.runTaskLater(plugin, 300);
				}
			}
		}else{
			Player leader = null;
			for(Player member:playerlist){
				if(!member.hasMetadata("partyleader")){
					Util.debug(ChatColor.YELLOW+member.getName()+":パーティーリーダーのメタデータが存在しません.");
					return false;
				}
				if(member.getMetadata("partyleader").get(0).asBoolean()){
					leader = member;
				}
			}
			if(leader == null){
				Util.debug(ChatColor.YELLOW+partyname+"パーティー:パーティーリーダーが見つかりませんでした..");
				return false;
			}

			if(!(checkTeleport(player)||checkNear(leader,player))){
				player.sendMessage(ChatColor.GRAY+"リーダーとの距離が遠すぎます.");
				player.sendMessage(ChatColor.GRAY+"資源ワールドにいるためテレポートできません.");
			}

			final Player leader_final = leader;
			new BukkitRunnable(){
				int count = 15;
				Location loc = player.getLocation();
				@Override
				public void run() {
					if(!loc.equals(player.getLocation())){
						player.sendMessage(ChatColor.GRAY+"動いたためテレポートがキャンセルされました.");
						this.cancel();
					}
					player.sendMessage(ChatColor.GRAY+"残り"+count+"秒でテレポートを開始します.");
					count--;
					if(count<=0){
						player.teleport(leader_final);
					}

				}
			}.runTaskTimer(plugin, 20, 20);

		}
		return true;
	}
	private boolean help(Player player){
		player.sendMessage(ChatColor.GRAY+"/party create <パーティー名>: パーティを作ります.");
		player.sendMessage(ChatColor.GRAY+"/party join <パーティー名>: パーティーに参加します.");
		player.sendMessage(ChatColor.GRAY+"/party chat: 「パーティー専用チャット」と「通常チャット」を切り替えます.");
		player.sendMessage(ChatColor.GRAY+"※チャット時、「\"」,「”」を先頭につけるとチャットの送信先が反転.");
		player.sendMessage(ChatColor.GRAY+"/party invite <プレイヤー>: プレイヤーをパーティーに招待します.");
		player.sendMessage(ChatColor.GRAY+"/party accept: 招待を承諾します.");
		player.sendMessage(ChatColor.GRAY+"/party deny: 招待を拒否します.");
		player.sendMessage(ChatColor.GRAY+"/party leave: パーティーを抜けます.");
		player.sendMessage(ChatColor.GRAY+"/party list: パーティー一覧を表示します.");
		player.sendMessage(ChatColor.GRAY+"/party info: パーティーの情報を見ます.");
		player.sendMessage(ChatColor.GRAY+"------------リーダーのみが使えるコマンド---------");
		player.sendMessage(ChatColor.GRAY+"/party tp: パーティーを全員招集します.");
		player.sendMessage(ChatColor.GRAY+"------------メンバーのみが使えるコマンド---------");
		player.sendMessage(ChatColor.GRAY+"/party tp: パーティーリーダーの元へテレポートできます.");
		player.sendMessage(ChatColor.GRAY+"*リーダーと離れすぎている場合は使えません.");
		return true;
	}
	private Boolean create(Player player,String[] args){
		if (!player.hasPermission("mincra.party")){
			player.sendMessage(ChatColor.GRAY+"権限を持っていない。");
	        return false;
		}
		if(!(args.length==2)){
			player.sendMessage(ChatColor.GRAY+"パラメタが足りない。");
			player.sendMessage(ChatColor.GRAY+"/party create <パーティー名>");
	        return false;
		}
		if(!(args[1].length()<=12)){
			player.sendMessage(ChatColor.GRAY+"13文字以上の名前は設定しないで下さい。");
			return false;
		}
		if(isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"既にパーティーに入っています。");
			return false;
		}
		if(partylist.containsKey(args[1])){
			player.sendMessage(ChatColor.GRAY+args[1]+"は他のパーティーで使用されている名前です。");
			return false;
		}

	    ArrayList<Player> playerl=new ArrayList<Player>();
	    playerl.add(player);
	    partylist.put(args[1],playerl);
	    //partylevel.put(args[1],1);
	    //partyexp.put(args[1],0);
	    playerlist.add(player);
	    leaderlist.add(player);

	    //プレイヤーにメタデータを設定
	    PlayerData.addMetadata(player, args[1], true, (short) 1);

	    scoreboardlist.put(args[1], new ScoreBord(args[1],partylist.get(args[1])));
	    player.sendMessage(ChatColor.GRAY+args[1]+"パーティを作成しました。");
	    Bukkit.getServer().broadcastMessage(ChatColor.GOLD+player.getName()+"によって「"+args[1]+"」パーティーが設立されました.");
	    Version.sendJsonMessage("{\"text\":\""+ChatColor.GOLD+""+ChatColor.UNDERLINE+">「"+args[1]+"」パーティーに参加する!\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party join "+args[1]+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"クリックで参加する!\",\"color\":\"white\"}]}}}");
	    return true;
	}

	private Boolean join(Player player,final String partyname){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(!partylist.containsKey(partyname)){
			player.sendMessage(ChatColor.GRAY+partyname+"は存在しません.");
			return false;
		}
		if(isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"既にパーティーに入っています。");
			return false;
		}
		if(MincraParty.maxplayers<=partylist.get(partyname).size()){
			player.sendMessage(ChatColor.GRAY+"満員です。");
			return false;
		}
		ScoreBord scorebord = scoreboardlist.get(partyname);
		if(scorebord!=null) scorebord.cancel();
		scoreboardlist.remove(partyname);
		partylist.get(partyname).add(player);
		playerlist.add(player);
		int size = partylist.get(partyname).size();
		for(Player pl:partylist.get(partyname)){
			if(!pl.equals(player)){
				pl.setMetadata("partysize", new FixedMetadataValue(MincraParty.plugin, size));
				pl.sendMessage(ChatColor.GOLD+player.getName()+"がパーティに参加しました。");
				pl.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
			}
		}
		player.sendMessage(ChatColor.GOLD+partyname+"パーティに参加しました。");
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

		//プレイヤーにメタデータを設定
	    PlayerData.addMetadata(player, partyname, false, (short) partylist.get(partyname).size());
		new BukkitRunnable(){
			@Override
			public void run() {
				scoreboardlist.put(partyname, new ScoreBord(partyname,partylist.get(partyname)));
			}
		}.runTask(MincraParty.plugin);
		return true;
	}

	private Boolean accept(Player player){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(!invitelist.containsKey(player)){
			player.sendMessage(ChatColor.GRAY+"招待されていません。");
			return false;
		}
		if(isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"既にパーティーに入っています。");
			return false;
		}
		final String partyname=invitelist.get(player);
		if(MincraParty.maxplayers<=partylist.get(partyname).size()){
			player.sendMessage(ChatColor.GRAY+"満員です。");
			return false;
		}
		ScoreBord scorebord = scoreboardlist.get(partyname);
		scorebord.cancel();
		scoreboardlist.remove(partyname);
		partylist.get(partyname).add(player);
		playerlist.add(player);

		int size = partylist.get(partyname).size();
		for(Player pl:partylist.get(partyname)){
			if(!pl.equals(player)){
				pl.setMetadata("partysize", new FixedMetadataValue(MincraParty.plugin, size));
				pl.sendMessage(ChatColor.GOLD+player.getName()+"がパーティに参加しました。");
				pl.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
			}
		}

		//プレイヤーにメタデータを設定
	    PlayerData.addMetadata(player, partyname, false, (short) partylist.get(partyname).size());

		player.sendMessage(ChatColor.GOLD+partyname+"パーティに参加しました。");
		invitelist.remove(player);
		new BukkitRunnable(){
			@Override
			public void run() {
				scoreboardlist.put(partyname, new ScoreBord(partyname,partylist.get(partyname)));
			}
		}.runTask(MincraParty.plugin);
		return true;
	}
	private Boolean deny(Player player){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(invitelist.containsKey(player)){
			invitelist.remove(player);
			player.sendMessage(ChatColor.GRAY+"招待を拒否しました。");
			return true;
		}else {
			player.sendMessage(ChatColor.GRAY+"招待されていない。");
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	private Boolean invite(Player player,String[] args){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }else if(args.length!=2){
        	player.sendMessage("パラメタが足りない。");
            return false;
        }
		if(Bukkit.getPlayer(args[1]) == null){
			player.sendMessage("指定したプレイヤーは存在しません!");
			return false;
		}
		final Player inviteplayer = Bukkit.getPlayer(args[1]);
		if(isJoin(inviteplayer)){
			player.sendMessage(ChatColor.GRAY+args[1]+"は既にパーティーに入っています。");
			return false;
		}
		if(existsInviteList(inviteplayer)){
			player.sendMessage(ChatColor.GRAY+args[1]+"は既に招待されています。");
			return false;
		}
		String partyName = player.getMetadata("partyname").get(0).asString();
		inviteplayer.sendMessage(ChatColor.GRAY+"「"+partyName+"」パーティから招待されました。");
		Version.sendJsonMessage(inviteplayer,"{\"text\":\""+ChatColor.GOLD+">"+ChatColor.UNDERLINE+"「"+partyName+"」"+"パーティーに参加する!\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party accept "+args[1]+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"クリックで参加する!\",\"color\":\"white\"}]}}}");
		inviteplayer.sendMessage(ChatColor.GRAY+"");
		Version.sendJsonMessage(inviteplayer,"{\"text\":\""+ChatColor.GOLD+">"+ChatColor.UNDERLINE+"「"+partyName+"」"+"パーティー招待を拒否する!\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party deny "+args[1]+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"クリックで拒否する!\",\"color\":\"white\"}]}}}");
		invitelist.put(inviteplayer,player.getMetadata("partyname").get(0).asString());
		player.sendMessage(ChatColor.GRAY+args[1]+"をパーティに招待しました。");
		new BukkitRunnable(){
			public void run() {
				if(invitelist.containsKey(inviteplayer)){
					invitelist.remove(inviteplayer);
					inviteplayer.sendMessage(ChatColor.GRAY+"招待を自動拒否しました。");
				}
			}
		}.runTaskLater(MincraParty.plugin, 3600);
		return true;
	}
	private Boolean leave(Player player){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(!isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"既にパーティーから抜けています。");
			return false;
		}
			if(player.getMetadata("partyleader").get(0).asBoolean()==true){
				String key=player.getMetadata("partyname").get(0).asString();
				player.sendMessage(ChatColor.GOLD+key+"パーティを解散しました。");
				ScoreBord scorebord = scoreboardlist.get(key);
				scorebord.cancel();
				for(Player leaveplayer:partylist.get(key)){
					playerlist.remove(leaveplayer);

					//プレイヤーのメタデータを削除
				    PlayerData.removeMetadata(leaveplayer);

					leaveplayer.sendMessage(ChatColor.GOLD+key+"パーティが解散されました。");
				}
				scoreboardlist.remove(key);
				leaderlist.remove(player);
				partylist.remove(key);
				return true;
			}else {
				final String key=player.getMetadata("partyname").get(0).asString();
				ScoreBord scorebord = scoreboardlist.get(key);
				scorebord.cancel();
				partylist.get(key).remove(player);
				playerlist.remove(player);

				//プレイヤーのメタデータを削除
			    PlayerData.removeMetadata(player);

				player.sendMessage(ChatColor.GOLD+key+"パーティから脱退しました。");
				int size = partylist.get(key).size();
				for(Player pl:partylist.get(key)){
					if(!pl.equals(player)){
						pl.setMetadata("partysize", new FixedMetadataValue(MincraParty.plugin, size));
						pl.sendMessage(ChatColor.GRAY+player.getName()+"がパーティから抜けました。");
					}
				}
				scoreboardlist.remove(key);
				new BukkitRunnable(){
					@Override
					public void run() {
						scoreboardlist.put(key, new ScoreBord(key,partylist.get(key)));
					}
				}.runTask(MincraParty.plugin);
				return true;
			}
	}
	private Boolean info(Player player){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"パーティ名:"+player.getMetadata("partyname").get(0).value());
			player.sendMessage(ChatColor.GRAY+"リーダー:"+player.getMetadata("partyleader").get(0).value());
			for(Player pl:partylist.get(player.getMetadata("partyname").get(0).value())){
				player.sendMessage(ChatColor.GRAY+"メンバー:"+pl.getName());
			}
			player.sendMessage(ChatColor.GRAY+"PTチャット:"+player.getMetadata("partychat").get(0).value());
			player.sendMessage(ChatColor.GRAY+"PT人数:"+player.getMetadata("partysize").get(0).value());
			return true;
		}else{
			player.sendMessage(ChatColor.GRAY+"既にパーティーから抜けています。");
			return false;
		}
	}

	private Boolean list(Player player){
		if (!player.hasPermission("mincra.party")) {
            player.sendMessage("mincra.partyの権限を持っていない。");
            return false;
        }
		if(partylist.isEmpty()){
			player.sendMessage(ChatColor.GRAY+"----------------パーティーリスト----------------");
			player.sendMessage(ChatColor.GRAY+"--------------------------------------------------");
			return true;
		}else{
			player.sendMessage(ChatColor.GRAY+"------------------パーティーリスト(クリックで参加)------------------");
			for(String partyname:partylist.keySet()){
				Version.sendJsonMessage(player, "{\"text\":\""+ChatColor.GRAY+partyname+"("+getPartySize(partyname)+"/"+MincraParty.maxplayers+")"+"\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/party join "+partyname+"\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"クリックで参加する!\",\"color\":\"white\"}]}}}");
			}
			player.sendMessage(ChatColor.GRAY+"-----------------------------------------------------------------------");
			return true;
		}
	}
	private Boolean chat(Player player,String[] args){
		if(!isJoin(player)){
			player.sendMessage(ChatColor.GRAY+"パーティーに所属していません。");
			return false;
		}
		if(args.length == 1){
			if(player.getMetadata("partychat").get(0).asBoolean()){
				player.setMetadata("partychat", new FixedMetadataValue(MincraParty.plugin, "false"));
				player.sendMessage(ChatColor.GRAY+"デフォルトの発言先を通常に設定しました。");
				return true;
			}else if(!player.getMetadata("partychat").get(0).asBoolean()){
				player.setMetadata("partychat", new FixedMetadataValue(MincraParty.plugin, "true"));
				player.sendMessage(ChatColor.GRAY+"デフォルトの発言先を"+player.getMetadata("partyname").get(0).asString()+"PTに設定しました。");
				return true;
			}else{
				return false;
			}
		}else{
			if(args[1].equalsIgnoreCase("1")||args[1].equalsIgnoreCase("true")||args[1].equalsIgnoreCase("on")){
				player.setMetadata("partychat", new FixedMetadataValue(MincraParty.plugin, "true"));
				player.sendMessage(ChatColor.GRAY+"デフォルトの発言先を"+player.getMetadata("partyname").get(0).asString()+"PTに設定しました。");
				return true;
			}else if(args[1].equalsIgnoreCase("0")||args[1].equalsIgnoreCase("false")||args[1].equalsIgnoreCase("off")){
				player.setMetadata("partychat", new FixedMetadataValue(MincraParty.plugin, "false"));
				player.sendMessage(ChatColor.GRAY+"デフォルトの発言先を通常に設定しました。");
				return true;
			}else{
				return false;
			}
		}
	}
	private Boolean reload(Player player){
		if (!player.hasPermission("mincra.party.admin")) {
            player.sendMessage("mincra.party.adminの権限を持っていない。");
            return false;
        }
		Bukkit.getServer().getPluginManager().disablePlugin(MincraParty.plugin);
		Bukkit.getServer().getPluginManager().enablePlugin(MincraParty.plugin);
		return true;
	}
	private boolean checkTeleport(Player player) {
		String name = player.getLocation().getWorld().getName();
		if(name.equalsIgnoreCase("world")||name.equalsIgnoreCase("world_nether")||name.equalsIgnoreCase("world_the_end")){
			return false;
		}
		return true;
	}
	private boolean checkNear(Player member,Player leader) {
		if(member.getLocation().distance(leader.getLocation())<256){
			return true;
		}
		return false;
	}
}
