package eu.octanne.xelephia.lootzone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.octanne.xelephia.XelephiaPlugin;

public class LootCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission("lootzone.admin")) {
				if (args.length >= 2) {
					if (args[0].equalsIgnoreCase("tag")) {
						if (!p.getItemInHand().getType().equals(Material.AIR)) {
							// Tag System
							addTag(p.getItemInHand(), args[1], p);
							return true;
						} else {
							p.sendMessage("§eLoot §8| §cVous ne pouvez pas appliquer de tag sur l'air.");
							return false;
						}
					} else if (args[0].equalsIgnoreCase("create")) {
						if (args.length >= 3) {
							if (!XelephiaPlugin.getLootZoneManager().hasZone(args[1])) {
								int timeZone = 30;
								try {
									timeZone = Integer.parseInt(args[2]);
								} catch (NumberFormatException e) {
									p.sendMessage("§eLoot §8| §cLa valeur §9" + args[2] + " §cn'est pas un nombre.");
									return false;
								}
								// Create Zone
								if (XelephiaPlugin.getLootZoneManager().createZone(args[1], timeZone, p.getLocation())) {
									p.sendMessage("§eLoot §8| §aCréation de la zone §9" + args[1] + " §a(ControlTime:§9"
											+ timeZone + "§as).");
									return true;
								} else {
									p.sendMessage(
											"§eLoot §8| §cErreur: Création de la zone §9" + args[1] + " §cimpossible.");
									return false;
								}
							} else {
								p.sendMessage("§eLoot §8| §cLa zone §9" + args[1] + " §cexiste déjà.");
								return false;
							}
						} else {
							p.sendMessage("§eLoot §8| §cUsage: §c/loot create <zone> <time>");
							return false;
						}
					} else if (args[0].equalsIgnoreCase("remove")) {
						if (XelephiaPlugin.getLootZoneManager().hasZone(args[1])) {
							// Remove Zone
							if (XelephiaPlugin.getLootZoneManager().removeZone(args[1])) {
								p.sendMessage("§eLoot §8| §aSupression de la zone §9" + args[1] + " confirmé.");
								return true;
							} else {
								p.sendMessage(
										"§eLoot §8| §cErreur: Supression de la zone §9" + args[1] + " §cimpossible.");
								return false;
							}
						} else {
							p.sendMessage("§eLoot §8| §cLa zone §9" + args[1] + " §cn'existe pas.");
							return false;
						}
					} else if (args[0].equalsIgnoreCase("edit")) {
						if (XelephiaPlugin.getLootZoneManager().hasZone(args[1])) {
							// Loot Zone Edit
							XelephiaPlugin.getLootZoneManager().editLootZone(p, args[1]);
							return true;
						} else {
							p.sendMessage("§eLoot §8| §cLa zone §9" + args[1] + " §cn'existe pas.");
							return false;
						}
					} else if (args[0].equalsIgnoreCase("list")) {
						// Loot Zone Listage
						
						return true;
					} else if (args[0].equalsIgnoreCase("taglist")) {
						// Tag Listage
						p.sendMessage("§eLoot §8| §aLes différents tags : §9kit§a, §9lootable§a, §9permanent§a.");
						return true;
					} else {
						p.sendMessage("§eLoot §8| §cUsage: /loot <taglist|tag|create|remove|list|edit> [args]");
						return false;
					}
				} else {
					p.sendMessage("§eLoot §8| §cUsage: /loot <tag|create|remove|list|edit> [args]");
					return false;
				}
			} else {
				sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
				return false;
			}
		} else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("playerOnly"));
			return false;
		}
	}
	
	private void addTag(ItemStack stack, String tag, Player p) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if(meta.hasLore()) {
			lore = meta.getLore();
			for(String str : lore) {
				if(str.equals("§cItem de kit")) lore.remove(str);
				if(str.equals("§cItem lootable")) lore.remove(str);
				if(str.equals("§cItem permanent")) lore.remove(str);
			}
		}
		if(tag.equalsIgnoreCase("kit")) {
			lore.add(" ");
			lore.add("§cItem de kit");
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}else if(tag.equalsIgnoreCase("lootable")){
			lore.add(" ");
			lore.add("§cItem lootable");
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}else if(tag.equalsIgnoreCase("permanent")){
			lore.add(" ");
			lore.add("§cItem permanent");
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}else {
			p.sendMessage("§eLoot §8| §cLe tag : §9"+tag+" §cn'existe pas.");
			return;
		}
		p.sendMessage("§eLoot §8| §aLe tag : §9"+tag+" §aa correctement était ajouté.");
		return;
	}
}