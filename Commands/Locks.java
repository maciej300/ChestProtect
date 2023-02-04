package Plugin.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Plugin.Menu.MenuUtils;

public class Locks implements CommandExecutor{

	private MenuUtils utils;
	
	public Locks() {
		
		this.utils = new MenuUtils();
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

		if(sender instanceof Player) {
			
			Player p = (Player) sender;
		
			initializeInventory(p);
			
		}
		
		return true;
	}
	
	private void initializeInventory(Player player) {
		
		Inventory inventory = utils.createMenu(player, ChatColor.GOLD + "Zarządzanie Skrzyniami!", 9, false);
		
		ItemStack lockItem = new ItemStack(Material.TRIPWIRE_HOOK, 1);
		ItemMeta lockMeta = lockItem.getItemMeta();
		lockMeta.setDisplayName(ChatColor.RED + "Twoje Skrzynie");
		lockItem.setItemMeta(lockMeta);
		
		inventory.setItem(0, lockItem);
		
		player.openInventory(inventory);
		
	}
}
