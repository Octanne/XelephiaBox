package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class MessageCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (label.equalsIgnoreCase("message") || label.equalsIgnoreCase("msg") || label.equalsIgnoreCase("m")) {
				if (args.length >= 2) {
					if (Bukkit.getPlayer(args[0]) != null) {
						String message = "";
						for (int nbr = 1; nbr <= args.length - 1; nbr++) {
							message += " " + args[nbr];
						}
						Player p = Bukkit.getPlayer(args[0]);
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Envoyé à " + ChatColor.RESET + p.getName() + " §l»"
								+ ChatColor.RESET + message);
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Reçu de " + ChatColor.RESET + sender.getName() + " §l»"
								+ ChatColor.AQUA + message);
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 100f);
						XelephiaPlugin.getXPlayer(p.getUniqueId())
								.setLastMessenger(XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()));
						return true;
					} else {
						sender.sendMessage("§4Merci de préciser un joueur correct");
						return false;
					}
				} else {
					sender.sendMessage("§4Merci de respecter la syntaxe :\"/message <Player> <Message...>\"");
					return false;
				}
			} else if (label.equalsIgnoreCase("respond") || label.equalsIgnoreCase("r")) {
				if (args.length > 0) {
					if (XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()).getLastMessenger() != null
							&& XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()).getLastMessenger()
									.getBukkitPlayer() != null) {
						Player pt = XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()).getLastMessenger()
								.getBukkitPlayer();
						String message = "";
						for (int nbr = 0; nbr <= args.length - 1; nbr++) {
							message += " " + args[nbr];
						}
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Envoyé à " + ChatColor.RESET + pt.getName()
								+ " §l»" + ChatColor.RESET + message);
						pt.sendMessage(ChatColor.LIGHT_PURPLE + "Reçu de " + ChatColor.RESET + sender.getName() + " §l»"
								+ ChatColor.AQUA + message);
						XelephiaPlugin.getXPlayer(pt.getName())
								.setLastMessenger(XelephiaPlugin.getXPlayer(((Player) sender).getUniqueId()));

						pt.playSound(pt.getLocation(), Sound.CHICKEN_EGG_POP, 0.9f, 100f);
						return true;
					} else {
						sender.sendMessage("§4Vous n'avez personne à qui répondre ou le joueur est déconnecté.");
					}
				} else {
					sender.sendMessage("§4Merci de préciser un message.");
					return false;
				}
			} else {
				sender.sendMessage(XelephiaPlugin.getMessageConfig().get().getString("noPermission"));
				return false;
			}
			return false;
		} else {
			sender.sendMessage("§4Commande reservee aux joueurs !");
			return false;
		}
	}

}
