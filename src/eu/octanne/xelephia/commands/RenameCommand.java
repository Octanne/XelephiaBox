package eu.octanne.xelephia.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && sender.hasPermission("xelephia.commands.rename")) {
			if (args.length >= 1) {
				Player p = (Player) sender;

				// RECUP DU NOM
				int NbrString = args.length - 1;
				String Nom;
				String NomFinal = "";
				String NomFinal2 = "";
				for (int Nbr = 0; Nbr <= NbrString; Nbr++) {
					Nom = args[Nbr];
					NomFinal = NomFinal + Nom + " ";
					NomFinal2 = ChatColor.translateAlternateColorCodes('&', NomFinal);
				}
				ItemMeta itemMeta = p.getItemInHand().getItemMeta();
				itemMeta.setDisplayName(NomFinal2);
				p.getItemInHand().setItemMeta(itemMeta);
				return true;
			} else {
				sender.sendMessage("Vous devez préciser un nom.");
				return false;
			}
		} else {
			sender.sendMessage("Vous n'avez pas la permission d'exécuter cette commande.");
			return false;
		}
	}

}
