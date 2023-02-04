package Plugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Plugin.Loader;
import Plugin.Events.PlayerAcceptEvent;
import Plugin.Menu.MenuUtils;

public class PlayerAcceptListener implements Listener{
	
	MenuUtils utils = new MenuUtils();
	
	private Loader plugin;
	
	public PlayerAcceptListener(Loader plugin) {
		
		this.plugin = plugin;
		
	}

	@EventHandler
	public void onPlayerAccept(PlayerAcceptEvent e) {
		
		e.getBlock().breakNaturally();
		
		e.getPlayer().closeInventory();
		
		deleteLock(e.getBlock().getLocation(), e.getPlayer());
		
		
	}
	
	public void deleteLock(Location location, Player player) {
		
		double requiredX = location.getX();
		double requiredY = location.getY();
		double requiredZ = location.getZ();
		
		for(String crateName : plugin.getConfig().getConfigurationSection(player.getName() + ".locks").getKeys(false)) {
			
			double x = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".x");
			double y = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".y");
			double z = plugin.getConfig().getDouble(player.getName() + ".locks." + crateName + ".z");
			
			if(requiredX == x && requiredY == y && requiredZ == z) {
				
				plugin.getConfig().set(player.getName() + ".locks." + crateName, null);
				plugin.saveConfig();
				
				player.sendMessage(ChatColor.RED + "Pomyślnie zniszczyłeś zablokowaną skrzynię!");
				
				return;
			}
		}
	}
	
	public void initializeInventory(Player player) {
		
		Inventory inv = utils.createMenu(player, ChatColor.RED + "Lock Menu!", 9, false);
		
		ItemStack chest = new ItemStack(Material.GREEN_CONCRETE, 1);
		ItemMeta chestMeta = chest.getItemMeta();
		chestMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Potwierdz");
		chest.setItemMeta(chestMeta);
		
		ItemStack lock = new ItemStack(Material.RED_CONCRETE, 1);
		ItemMeta lockMeta = lock.getItemMeta();
		lockMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Anuluj");
		lock.setItemMeta(lockMeta);
		
		
		inv.setItem(3, chest);
		inv.setItem(5, lock);
		
		player.openInventory(inv);
		
	}
	
}
