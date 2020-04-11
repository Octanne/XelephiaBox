package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class GameModeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.gamemode")) {
			if (sender instanceof Player) {
				if (args.length == 1) {
					try {
						switch (Integer.parseInt(args[0])) {
						case 0:
							((Player) sender).setGameMode(GameMode.SURVIVAL);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
							break;
						case 1:
							((Player) sender).setGameMode(GameMode.CREATIVE);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
							break;
						case 2:
							((Player) sender).setGameMode(GameMode.ADVENTURE);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
							break;
						case 3:
							((Player) sender).setGameMode(GameMode.SPECTATOR);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
							break;
						default:
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectGameMode").replace("{GAMEMODE}", args[0]));
							break;
						}
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectGameMode")
								.replace("{GAMEMODE}", args[0]));
						return false;
					}
				} else if (args.length > 1) {
					if (Bukkit.getPlayer(args[1]) != null) {
						Player pE = Bukkit.getPlayer(args[1]);
						try {
							switch (Integer.parseInt(args[0])) {
							case 0:
								pE.setGameMode(GameMode.SURVIVAL);
								if (sender != pE)
									sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Survie")
											.replace("{PLAYER}", pE.getName()));
								pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
								break;
							case 1:
								pE.setGameMode(GameMode.CREATIVE);
								if (sender != pE)
									sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Créatif")
											.replace("{PLAYER}", pE.getName()));
								pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
								break;
							case 2:
								pE.setGameMode(GameMode.ADVENTURE);
								if (sender != pE)
									pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Aventure")
										.replace("{PLAYER}", pE.getName()));
								break;
							case 3:
								pE.setGameMode(GameMode.SPECTATOR);
								if (sender != pE)
									pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Spectateur")
										.replace("{PLAYER}", pE.getName()));
								break;
							default:
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("incorrectGameMode").replace("{GAMEMODE}", args[0]));
								break;
							}
							return true;
						} catch (NumberFormatException e) {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectGameMode").replace("{GAMEMODE}", args[0]));
							return false;
						}
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
								.replace("{PLAYER}", args[1]));
						return false;
					}
				} else {
					sender.sendMessage("§4Invalid usage: /gm <mode> [player]");
					return false;
				}
			} else {
				if (args.length > 1) {
					if (Bukkit.getPlayer(args[1]) != null) {
						Player pE = Bukkit.getPlayer(args[1]);
						try {
							switch (Integer.parseInt(args[0])) {
							case 0:
								pE.setGameMode(GameMode.SURVIVAL);
								if (sender != pE)
									sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Survie")
											.replace("{PLAYER}", pE.getName()));
								pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeGameMode").replace("{GAMEMODE}", "Survie"));
								break;
							case 1:
								pE.setGameMode(GameMode.CREATIVE);
								if (sender != pE)
									sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Créatif")
											.replace("{PLAYER}", pE.getName()));
								pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeGameMode").replace("{GAMEMODE}", "Créatif"));
								break;
							case 2:
								pE.setGameMode(GameMode.ADVENTURE);
								if (sender != pE)
									pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeMeGameMode").replace("{GAMEMODE}", "Aventure"));
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Aventure")
										.replace("{PLAYER}", pE.getName()));
								break;
							case 3:
								pE.setGameMode(GameMode.SPECTATOR);
								if (sender != pE)
									pE.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeMeGameMode").replace("{GAMEMODE}", "Spectateur"));
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeOtherPlayerGameMode").replace("{GAMEMODE}", "Spectateur")
										.replace("{PLAYER}", pE.getName()));
								break;
							default:
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("incorrectGameMode").replace("{GAMEMODE}", args[0]));
								break;
							}
							return true;
						} catch (NumberFormatException e) {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectGameMode").replace("{GAMEMODE}", args[0]));
							return false;
						}
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
								.replace("{PLAYER}", args[1]));
						return false;
					}
				} else {
					sender.sendMessage("§4Invalid usage: /gm <mode> <player>");
					return false;
				}
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}

}
