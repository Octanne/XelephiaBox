package eu.octanne.xelephia.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class WorldCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		
		if (sender.hasPermission("xelephia.worldmanager")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("====== WORLDS ======");
					for (String world : XelephiaPlugin.getWorldManager().getWorlds("status")) {
						sender.sendMessage(world);
					}
					sender.sendMessage("===================");
					return true;
				} else if (args[0].equalsIgnoreCase("info") && sender instanceof Player) {
					sender.sendMessage(
							"§eVous êtes dans le monde: §a" + Bukkit.getPlayer(sender.getName()).getWorld().getName());
					return true;
				} else if (args[0].equalsIgnoreCase("create")) {
					if (args.length > 3) {
						File file = new File(args[1] + "/level.dat");
						if (!file.exists()) {
							if ((args[2].equalsIgnoreCase("normal") || args[2].equalsIgnoreCase("flat")
									|| args[2].equalsIgnoreCase("void"))
									&& (args[3].equalsIgnoreCase("normal") || args[3].equalsIgnoreCase("end")
											|| args[3].equalsIgnoreCase("nether"))) {
								sender.sendMessage("§eCréation du monde en cours...");
								if (args.length > 4 && args[4].equalsIgnoreCase("structure")) {
									//XelephiaPlugin.getWorldManager().createWorld(args[1], args[2], args[3], true);
								} else {
									//XelephiaPlugin.getWorldManager().createWorld(args[1], args[2], args[3], true);
								}
							} else {
								sender.sendMessage("§4Type de monde ou d'Environement inconnu.");
								return false;
							}
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("createWorld")
									.replace("{WORLD}", args[1]).replace("{TYPE}", args[2]).replace("{ENV}", args[3]));
							return true;
						} else {
							sender.sendMessage("§4Erreur ce monde existe déjà !");
							return false;
						}
					} else {
						sender.sendMessage("§4Invalid usage: /world create <worldName> <typeWorld> <environmentWorld>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("tp")) {
					if (args.length > 2) {
						if (Bukkit.getWorld(args[1]) != null) {
							if (Bukkit.getPlayer(args[2]) != null) {
								Bukkit.getPlayer(args[2]).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
								sender.sendMessage(
										XelephiaPlugin.getMessageConfig().getConfig().getString("teleportPlayerInWorld")
												.replace("{PLAYER}", args[2]).replace("{WORLD}", args[1]));
								return true;
							} else {
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("incorrectPlayer").replace("{PLAYER}", args[2]));
								return false;
							}
						} else {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("inexistantOrNoLoadWorld").replace("{WORLD}", args[1]));
							return false;
						}
					} else {
						sender.sendMessage("§4Invalid usage: /world tp <world> <player>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("unload")) {
					if (args.length > 1) {
						if (Bukkit.getWorld(args[1]) != null) {
							sender.sendMessage("§eDéchargement du monde en cours...");
							//XelephiaPlugin.getWorldManager().unloadWorld(args[1]);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("unloadWorld")
									.replace("{WORLD}", args[1]));
							return true;
						} else {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("inexistantOrNoLoadWorld").replace("{WORLD}", args[1]));
							return false;
						}
					} else {
						sender.sendMessage("§4Invalid usage: /world unload <worldName>");
						return false;
					}

				} else if (args[0].equalsIgnoreCase("load")) {
					if (args.length > 1) {
						File file = new File(args[1] + "/level.dat");
						if (Bukkit.getWorld(args[1]) == null && file.exists()) {
							sender.sendMessage("§eChargement du monde en cours...");
							//XelephiaPlugin.getWorldManager().loadWorld(args[1]);
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("loadWorld")
									.replace("{WORLD}", args[1]));
							return true;
						} else {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("inexistantOrAlwaysLoadWorld").replace("{WORLD}", args[1]));
							return false;
						}
					} else {
						sender.sendMessage("§4Invalid usage: /world load <worldName>");
						return false;
					}
				} else {
					sender.sendMessage("§4Invalid usage: /world <tp, unload, create, list, info or load>");
					return false;
				}
			} else {
				sender.sendMessage("§4Invalid usage: /world <tp, unload, create, list, info or load>");
				return false;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}
}