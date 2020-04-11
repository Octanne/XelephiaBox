package eu.octanne.xelephia.kit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class KitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && sender.hasPermission("kit.admin")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length >= 3 && !((Player) sender).getItemInHand().getType().equals(Material.AIR)) {
						int price;
						try {
							price = Integer.parseInt(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage("§cKit §8| §cLa valeur §e" + args[2] + "§cn'est pas un nombre.");
							return false;
						}
						for (Kit kit : XelephiaPlugin.getKitSystem().getKits()) {
							if (kit.getUnName().equalsIgnoreCase(args[1])) {
								sender.sendMessage("§cKit §8| §cLe kit §e" + args[1] + " §cest déjà existant.");
								return false;
							}
						}
						XelephiaPlugin.getKitSystem().createKit(args[1], price, (Player) sender,
								((Player) sender).getItemInHand().getType());
						return true;
					} else {
						sender.sendMessage("§cKit §8| §c/kit create <name> <cost> | Item dans la main");
						return false;
					}
				}
				if (args[0].equalsIgnoreCase("menu")) {
					XelephiaPlugin.getKitSystem().openMenu((Player) sender);
					return true;
				}
				if (args[0].equalsIgnoreCase("remove")) {
					if (args.length >= 2) {
						for (Kit kit : XelephiaPlugin.getKitSystem().getKits()) {
							if (kit.getUnName().equalsIgnoreCase(args[1])) {
								if (XelephiaPlugin.getKitSystem().removeKit(args[1])) {
									sender.sendMessage("§cKit §8| §aLe kit §e" + args[1] + " §aa été supprimé.");
									return true;
								} else {
									sender.sendMessage(
											"§cKit §8| §cUne erreur est survenue lors de la supression du kit §e"
													+ args[1] + "§c.");
									return true;
								}
							}
						}
						sender.sendMessage("§cKit §8| §cLe kit §e" + args[1] + " §cn'existe pas.");
						return false;
					} else {
						sender.sendMessage("§cKit §8| §c/kit remove <name>");
						return false;
					}
				}if (args[0].equalsIgnoreCase("edit")) {
					if (args.length >= 2) {
						for (Kit kit : XelephiaPlugin.getKitSystem().getKits()) {
							if (kit.getUnName().equalsIgnoreCase(args[1])) {
								if (XelephiaPlugin.getKitSystem().editKit(args[1], (Player)sender)) {
									return true;
								} else {
									sender.sendMessage(
											"§cKit §8| §cUne erreur est survenue lors de la supression du kit §e"
													+ args[1] + "§c.");
									return true;
								}
							}
						}
						sender.sendMessage("§cKit §8| §cLe kit §e" + args[1] + " §cn'existe pas.");
						return false;
					} else {
						sender.sendMessage("§cKit §8| §c/kit edit <name>");
						return false;
					}
				}
				sender.sendMessage("§cKit §8| §c/kit <menu|edit|create|remove>");
				return false;
			} else {
				sender.sendMessage("§cKit §8| §c/kit <menu|edit|create|remove>");
				return false;
			}
		} else {
			sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command",
					"Commande inconnue. Faites \"/aide\" pour l'aide."));
			return false;
		}
	}

}
