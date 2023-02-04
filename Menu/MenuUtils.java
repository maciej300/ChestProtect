package Plugin.Menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuUtils {

	public Inventory createMenu(Player player, String name, int size, boolean filler) {
		
		Inventory inventory = Bukkit.createInventory(player, size, name);
		
		if(filler) filler(inventory, size);
		
		return inventory;
	}
	
	private void filler(Inventory inventory, int size) {
		
		ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		
		int index = 0;
		for(int i = 0; i < (size / 9); i++) {
			
			for(int j = 0; j < 9; j++) {
				
				if(i == 0 || i == (size / 9) - 1) inventory.setItem(index, filler);
				if(j == 0 || j == 8) inventory.setItem(index, filler);
				
				index++;
			}
			
		}
		
	}
	
}
