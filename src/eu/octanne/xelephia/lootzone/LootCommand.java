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

	private String COMMAND_TAG = "§eLoot §8|§r ";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission("lootzone.admin")) {
				if(args.length >= 1) {
					if(args[0].equalsIgnoreCase("list")) {
						p.sendMessage(COMMAND_TAG+"§aListe des zones :");
						for(LootZone zone : XelephiaPlugin.getLootZoneManager().getLootZones()) {
							p.sendMessage("     §e=> §9"+zone.getName());
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("tags")) {
						p.sendMessage(COMMAND_TAG+"§aListe des tags :");
						p.sendMessage("     §e=> §9Permanent");
						p.sendMessage("     §e=> §9Lootable");
						p.sendMessage("     §e=> §9Kit");
						return true;
					}
					if(args[0].equalsIgnoreCase("edit")) {
						if(args.length >= 2) {
							if(!XelephiaPlugin.getLootZoneManager().editLootZone(p, args[1])) {
								p.sendMessage(COMMAND_TAG+"§cLa zone §9" + args[1] + " §cn'existe pas.");
								return false;
							}else return true;
						}else {
							p.sendMessage(COMMAND_TAG+"§cUsage : /loot edit <zone>");
							return false;
						}
					}
					if(args[0].equalsIgnoreCase("remove")) {
						if(args.length >= 2) {
							if(XelephiaPlugin.getLootZoneManager().removeZone(args[1])) {
								p.sendMessage(COMMAND_TAG+"§aSupression de la zone §9" + args[1] + " §aconfirmé.");
								return true;
							}else {
								p.sendMessage(COMMAND_TAG+"§cErreur : La zone §9" + args[1] + " §cn'existe pas.");
								return false;
							}
						}else {
							p.sendMessage(COMMAND_TAG+"§cUsage : /loot remove <zone>");
							return false;
						}
					}
					if(args[0].equalsIgnoreCase("tag")) {
						if(args.length >= 2) {
							addTag(args[1], p);
							return true;
						}else {
							p.sendMessage(COMMAND_TAG+"§cUsage : /loot tag <tag>");
							return false;
						}
					}
					if(args[0].equalsIgnoreCase("create")) {
						if(args.length >= 3) {
							int timeZone = 30;
							try {
								timeZone = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								p.sendMessage(COMMAND_TAG+"§cLa valeur §9" + args[2] + " §cn'est pas un nombre.");
								return false;
							}
							// Create Zone
							if (XelephiaPlugin.getLootZoneManager().createZone(args[1], timeZone, p.getLocation())) {
								p.sendMessage(COMMAND_TAG+"§aCréation de la zone §9" + args[1] + " §a(ControlTime : §9"
										+ timeZone + "§asec).");
								return true;
							} else {
								p.sendMessage(COMMAND_TAG+"§cErreur: Création de la zone §9" + args[1] + " §cimpossible.");
								return false;
							}
						}else {
							p.sendMessage(COMMAND_TAG+"§cUsage : /loot create <zone> <controlTime>");
							return false;
						}
					}else {
						p.sendMessage(COMMAND_TAG+"§cUsage : /loot <tag|tags|create|remove|list|edit> [args]");
						return false;
					}
				}else {
					p.sendMessage(COMMAND_TAG+"§cUsage : /loot <tag|tags|create|remove|list|edit> [args]");
					return false;
				}
			}else {
				p.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("noPermission"));
				return false;
			}
		}else {
			sender.sendMessage(XelephiaPlugin.getMessageConfig().getConfig().getString("playerOnly"));
			return false;
		}
	}
	
	private void addTag(String tag, Player p) {
		if(!p.getItemInHand().getType().equals(Material.AIR)) {
			ItemStack stack = p.getItemInHand();
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
				p.sendMessage(COMMAND_TAG+"§cLe tag : §9"+tag+" §cn'existe pas.");
				return;
			}
			p.sendMessage(COMMAND_TAG+"§aLe tag : §9"+tag+" §aa vient d'être ajouté.");
			return;
		}else p.sendMessage(COMMAND_TAG+"§cVous ne pouvez pas appliquer un tag sur l'air.");
	}
}