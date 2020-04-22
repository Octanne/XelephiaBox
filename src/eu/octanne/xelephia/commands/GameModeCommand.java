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
					sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("incorrectPlayer")
							.replace("{PLAYER}", args[1]));
					return false;
				}
				if(mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
					p.setGameMode(GameMode.SURVIVAL);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().get()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().get()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
					}
					return true;
				}else if(mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("creative")) {
					p.setGameMode(GameMode.CREATIVE);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().get()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().get()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
					}
					return true;
				}else if(mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("adventure")) {
					p.setGameMode(GameMode.ADVENTURE);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().get()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().get()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
					}
					return true;
				}else if(mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spec") || mode.equalsIgnoreCase("spectator")) {
					p.setGameMode(GameMode.SPECTATOR);
					if(sender instanceof Player && p.equals(sender))	p.sendMessage(XelephiaPlugin.getMessageConfig().get()
							.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
					else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("changeOtherPlayerGameMode")
								.replace("{GAMEMODE}", "Survie").replace("{PLAYER}", p.getName()));
						p.sendMessage(XelephiaPlugin.getMessageConfig().get()
								.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
					}
					return true;
				}else {
					sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("incorrectGameMode")
							.replace("{GAMEMODE}", args[0]));
					return false;
				}
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
			return false;
		}
	}
}