package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class GameModeCommand implements CommandExecutor {

	private String COMMAND_TAG = "§aMode §8|§r ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("xelephia.commands.gamemode")) {
			if(!(sender instanceof Player) && args.length < 2) {
				sender.sendMessage(COMMAND_TAG+"§cUsage : /gm <mode> <player>");
				return false;
			}else if(sender instanceof Player && args.length < 1){
				sender.sendMessage(COMMAND_TAG+"§cUsage : /gm <mode> [player]");
				return false;
			}else {
				String mode = args[0];
				Player p = args.length > 1 ? Bukkit.getPlayer(args[1]) : (Player)sender;
				if(p == null) {
					sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
							.replace("{PLAYER}", args[1]));
					return false;
				}
				if(mode == "0" || mode == "s" || mode == "survival") {
					p.setGameMode(GameMode.SURVIVAL);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
					}
					return true;
				}else if(mode == "1" || mode == "c" || mode == "creative") {
					p.setGameMode(GameMode.SURVIVAL);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
					}
					return true;
				}else if(mode == "2" || mode == "a" || mode == "adventure") {
					p.setGameMode(GameMode.SURVIVAL);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
					}
					return true;
				}else if(mode == "3" || mode == "spec" || mode == "spectator") {
					p.setGameMode(GameMode.SURVIVAL);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
					}
					return true;
				}else {
					sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectGameMode")
							.replace("{GAMEMODE}", args[0]));
					return false;
				}
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}
}