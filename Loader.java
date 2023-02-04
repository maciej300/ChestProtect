package Plugin;

import org.bukkit.plugin.java.JavaPlugin;

import Plugin.Commands.Lock;
import Plugin.Commands.Locks;
import Plugin.Listeners.ChestListener;
import Plugin.Listeners.MenuListeners;
import Plugin.Listeners.PlayerAcceptListener;

public class Loader extends JavaPlugin{
	
	@Override
	public void onEnable() {
		
		/*
		 * 
		 *  TO-DO:
		 *  	1. Add Database Support (config.yml isnt good option xddd)
		 *  	2. Block grabbing items by hopers etc.
		 *  	3. Create utils because many functions are the same and repeat in different classes
		 *  	4. Delete PlayerAcceptEvent. Useless.
		 *  	5. Add key to lock chests.
		 *  	6. Add paginated menu to MenuUtils.java
		 *  	7. Do Clean Code.
		 *  	8. Add Command Manager
		 * 
		 */
		
		getConfig().options().copyDefaults();
		saveDefaultConfig();
		
		getCommand("lock").setExecutor(new Lock(this));
		getCommand("locks").setExecutor(new Locks());
		
		getServer().getPluginManager().registerEvents(new ChestListener(this), this);
		getServer().getPluginManager().registerEvents(new MenuListeners(this), this);
		getServer().getPluginManager().registerEvents(new PlayerAcceptListener(this), this);
		
	}
	
}
