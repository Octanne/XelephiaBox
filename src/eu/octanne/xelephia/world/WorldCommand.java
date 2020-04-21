package eu.octanne.xelephia.world;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class WorldCommand implements CommandExecutor {

	private String COMMAND_TAG = "§9World §8|§r ";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("xelephia.worldmanager")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("list")) {
					sender.sendMessage(COMMAND_TAG+"§aListe des mondes :");
					for(XWorld world : XelephiaPlugin.getWorldManager().getWorlds()) {
						sender.sendMessage("      §e=> "+(world.isLoad() ? "§a": "§c")+world.getName());
					}
					return true;
				}else if(args[0].equalsIgnoreCase("info")) {
					XWorld world;
					if(args.length > 1 || !(sender instanceof Player)) {
						world = XelephiaPlugin.getWorldManager().getWorld(args[1]);
						if(world == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe monde §9"+args[1]+" §cn'existe pas !");
							return false;
						}
					}else if(!(sender instanceof Player) && args.length < 1) {
						sender.sendMessage(COMMAND_TAG+"§cUsage : /world info <monde>");
						return false;
					}else {
						sender.sendMessage(COMMAND_TAG+"§aInformations du monde :");
						sender.sendMessage("  §e=> Nom : §9"+world.getName());
						sender.sendMessage("  §e=> Type : §9"+world.getType().getName());
						sender.sendMessage("  §e=> Env. : §9"+world.getWorld().getEnvironment().name());
						sender.sendMessage("  §e=> DefaultLoad : §9"+world.defaultLoad());
						return true;
					}
				}else if(args[0].equalsIgnoreCase("load")) {
					if(args.length > 1) {
						XWorld world = XelephiaPlugin.getWorldManager().getWorld(args[1]);
						if(world == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe monde §9"+args[1]+" §cn'existe pas !");
							return false;
						}else {
							sender.sendMessage(COMMAND_TAG+"§aChargement du monde §9"+args[1]+" §a en cours...");
							if(world.load()) {
								sender.sendMessage(COMMAND_TAG+"§aChargement du monde §9"+args[1]+" §a terminé !");
								return true;
							}else {
								sender.sendMessage(COMMAND_TAG+"§cChargement du monde §9"+args[1]+" §c impossible !");
								return false;
							}
						}
					}else {
						sender.sendMessage(COMMAND_TAG+"§cUsage : /world load <monde>");
						return false;
					}
				}else if(args[0].equalsIgnoreCase("unload")) {
					if(args.length > 1) {
						XWorld world = XelephiaPlugin.getWorldManager().getWorld(args[1]);
						if(world == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe monde §9"+args[1]+" §cn'existe pas !");
							return false;
						}else {
							sender.sendMessage(COMMAND_TAG+"§aDéchargement du monde §9"+args[1]+" §a en cours...");
							if(world.unload()) {
								sender.sendMessage(COMMAND_TAG+"§aDéchargement du monde §9"+args[1]+" §a terminé !");
								return true;
							}else {
								sender.sendMessage(COMMAND_TAG+"§cDéchargement du monde §9"+args[1]+" §c impossible !");
								return false;
							}
						}
					}else {
						sender.sendMessage(COMMAND_TAG+"§cUsage : /world unload <monde>");
						return false;
					}
				}else if(args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport") ) {
					
				}else if(args[0].equalsIgnoreCase("spawn")) {
					
				}else if(args[0].equalsIgnoreCase("create")) {
					
				}else if(args[0].equalsIgnoreCase("import")) {
					
				}
			}else {
				sender.sendMessage(COMMAND_TAG+"§cUsage : /world <list, info, spawn, load, unload, tp, create or import>");
				return false;
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}


		if (sender.hasPermission("xelephia.worldmanager")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create")) {
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
							XelephiaPlugin.getWorldManager().loadWorld(args[1]);
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