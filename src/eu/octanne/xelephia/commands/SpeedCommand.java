package eu.octanne.xelephia.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.octanne.xelephia.XelephiaPlugin;

public class SpeedCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (sender.hasPermission("xelephia.commands.speed")) {
			if (args.length > 0) {
				if (args.length > 1) {
					if (Bukkit.getPlayer(args[1]) != null) {
						Player p = (Player) sender;
						Player pt = Bukkit.getPlayer(args[1]);
						try {
							Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectNumber").replace("{NUMBER}", args[0]));
							return false;
						}
						int speedValue = Integer.parseInt(args[0]);
						if (speedValue >= 0 && speedValue < 11) {
							if (pt.getPlayer().isFlying()) {
								if (sender != pt)
									p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeYourSpeed").replace("{SPEED}", "" + speedValue)
											.replace("{PLAYER}", pt.getName()));
								pt.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeMeSpeed")
										.replace("{SPEED}", "" + speedValue));
								pt.setFlySpeed(speedValue / 10f);
								return true;
							} else {
								if (sender != pt)
									p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
											.getString("changeYourSpeed").replace("{SPEED}", "" + speedValue)
											.replace("{PLAYER}", pt.getName()));
								pt.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("changeMeSpeed")
										.replace("{SPEED}", "" + speedValue));
								pt.setWalkSpeed(speedValue / 10f);
								return true;
							}
						} else {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectNumber").replace("{NUMBER}", args[0]));
							return false;
						}
					} else {
						sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("incorrectPlayer")
								.replace("{PLAYER}", args[1]));
						return false;
					}
				} else {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						String strSpeed = args[0];
						try {
							Integer.parseInt(args[0]);
						} catch (NumberFormatException e) {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectNumber").replace("{NUMBER}", strSpeed));
							return false;
						}
						int speedValue = Integer.parseInt(args[0]);
						if (speedValue >= 0 && speedValue < 11) {
							if (p.getPlayer().isFlying()) {
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeSpeed").replace("{SPEED}", "" + speedValue));
								p.setFlySpeed(speedValue / 10f);
								return true;
							} else {
								sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
										.getString("changeMeSpeed").replace("{SPEED}", "" + speedValue));
								p.setWalkSpeed(speedValue / 10f);
								return true;
							}
						} else {
							sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig()
									.getString("incorrectNumber").replace("{NUMBER}", args[0]));
							return false;
						}
					} else {
						sender.sendMessage("§4Invalid Usage: /speed <0 to 10> <player>");
						return false;
					}
				}
			} else {
				sender.sendMessage("§4Invalid Usage: /speed <0 to 10> [<player>]");
				return false;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
			return false;
		}
	}

}
