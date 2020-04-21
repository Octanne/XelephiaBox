package eu.octanne.xelephia.world;

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
					XWorld world = null;
					if(sender instanceof Player) world = XelephiaPlugin.getWorldManager().getWorld(((Player) sender).getWorld().getName());
					if(args.length > 1) {
						world = XelephiaPlugin.getWorldManager().getWorld(args[1]);
						if(world == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe monde §9"+args[1]+" §cn'existe pas !");
							return false;
						}
					}
					if(!(sender instanceof Player) && args.length < 1) {
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
					if(args.length > 1) {
						Player p = null;
						if(sender instanceof Player) p = (Player) sender;
						XWorld world = XelephiaPlugin.getWorldManager().getWorld(args[1]);
						if(world == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe monde §9"+args[1]+" §cn'existe pas !");
							return false;
						}
						if(args.length > 2) {
							p = Bukkit.getPlayer(args[2]);
							if(p == null) {
								sender.sendMessage(COMMAND_TAG+"§cLe joueur §9"+args[2]+" §cn'est pas connecté !");
								return false;
							}
						}
						if(!(sender instanceof Player) && args.length < 1) {
							sender.sendMessage(COMMAND_TAG+"§cUsage : /world tp <monde> <joueur>");
							return false;
						}else {
							p.teleport(world.getWorld().getSpawnLocation());
							if(p.equals(sender)) p.sendMessage(COMMAND_TAG+"§aVous avez été téléporté dans le monde §9"+world.getName()+"§a.");
							else {
								p.sendMessage(COMMAND_TAG+"§aVous avez été téléporté dans le monde §9"+world.getName()+"§a.");
								sender.sendMessage(COMMAND_TAG+"§9"+p.getName()+" §aa été téléporté dans le monde §9"+world.getName()+"§a.");
							}
							return true;
						}
					}else{
						sender.sendMessage(COMMAND_TAG+"§cUsage : /world tp <monde> "+(sender instanceof Player ? "[joueur]" : "<joueur>"));
						return false;
					}
				}else if(args[0].equalsIgnoreCase("spawn")) {
					Player p = null;
					if(sender instanceof Player) p = (Player) sender;
					if(args.length > 1) {
						p = Bukkit.getPlayer(args[1]);
						if(p == null) {
							sender.sendMessage(COMMAND_TAG+"§cLe joueur §9"+args[1]+" §cn'est pas connecté !");
							return false;
						}
					}
					if(!(sender instanceof Player) && args.length < 1) {
						sender.sendMessage(COMMAND_TAG+"§cUsage : /world spawn [joueur]");
						return false;
					}else {
						p.teleport(p.getWorld().getSpawnLocation());
						if(p.equals(sender)) p.sendMessage(COMMAND_TAG+"§aVous avez été téléporté dans le monde §9"+p.getWorld().getName()+"§a.");
						else {
							p.sendMessage(COMMAND_TAG+"§aVous avez été téléporté dans le monde §9"+p.getWorld().getName()+"§a.");
							sender.sendMessage(COMMAND_TAG+"§9"+p.getName()+" §aa été téléporté dans le monde §9"+p.getWorld().getName()+"§a.");
						}
						return true;
					}
				}else if(args[0].equalsIgnoreCase("create")) {
					return false;
				}else if(args[0].equalsIgnoreCase("import")) {
					return false;
				}else {
					sender.sendMessage(COMMAND_TAG+"§cUsage : /world <list, info, spawn, setspawn, load, unload, tp, create or import>");
					return false;
				}
			}else {
				sender.sendMessage(COMMAND_TAG+"§cUsage : /world <list, info, spawn, setspawn, load, unload, tp, create or import>");
				return false;
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}
}