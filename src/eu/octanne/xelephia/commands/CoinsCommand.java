package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.octanne.xelephia.XelephiaPlugin;
import eu.octanne.xelephia.xplayer.XPlayer;

public class CoinsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("xelephia.commands.coins")) {
			if (args.length >= 3) {
				XPlayer xP = XelephiaPlugin.getXPlayer(args[1]);
				if (xP != null) {
					int amount;
					try {
						amount = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage("§6Coins §8| §cLa valeur §e" + args[2] + " §cn'est pas un nombre.");
						return false;
					}
					if (args[0].equalsIgnoreCase("take")) {
						xP.takeCoins(amount);
						sender.sendMessage("§6Coins §8| §9(§c-" + amount + " §9coins) §e" + xP.getName()
								+ " §9possède désormais §e" + xP.getCoins() + "§9 coins.");
						xP.saveIntoDB();
						return true;
					}
					if (args[0].equalsIgnoreCase("give")) {
						xP.giveCoins(amount);
						sender.sendMessage("§6Coins §8| §9(§a+" + amount + " §9coins) §e" + xP.getName()
								+ " §9possède désormais §e" + xP.getCoins() + "§9 coins.");
						xP.saveIntoDB();
						return true;
					}
					if (args[0].equalsIgnoreCase("set")) {
						xP.setCoins(amount);
						sender.sendMessage("§6Coins §8| §e" + xP.getName() + " §9possède désormais §e" + xP.getCoins()
								+ "§9 coins.");
						xP.saveIntoDB();
						return true;
					} else {
						sender.sendMessage("§6Coins §8| §c/coins <take|give|set> <pseudo> <amount>");
						return false;
					}
				} else {
					sender.sendMessage("§6Coins §8| §cLe joueur §e" + args[1] + " §cn'a pas été trouvé.");
					return false;
				}
			} else {
				sender.sendMessage("§6Coins §8| §c/coins <take|give|set> <pseudo> <amount>");
				return false;
			}
		} else {
			sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command",
					"Commande inconnue. Faites \"/aide\" pour l'aide."));
			return false;
		}
	}

}
