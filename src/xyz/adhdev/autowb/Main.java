package xyz.adhdev.autowb;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	createConfig();
    	autoWBStart();
    	Logs("Started");
    }

    @Override
    public void onDisable() {
    	Logs("Stopped");
    }
    
    public void Logs(String Message) {
    	Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"[AutoWB] "+ChatColor.WHITE+Message);
    }
    
    public void createConfig() {
        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
            	Logs("Config.yml Not Found, Creating");
            	config.options().header("Minimum Player Before WorldBorder Get Stopped");
            	config.addDefault("Minimum Player", 1);
                config.options().copyDefaults(true);
                saveConfig();
                this.getConfig();
            } else {
                Logs("Config.yml Found, Loading");
                this.getConfig();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void autoWBStart() {
    	int pcount = Bukkit.getServer().getOnlinePlayers().size();
    	int minp = config.getInt("Minimum Player")-1;
    	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    	if(pcount == config.getInt("Minimum Player")) {
    		Logs("There's "+config.getInt("Minimum Player")+" Players In Server, Stopping World Border");
    		Bukkit.dispatchCommand(console, "wb fill pause");
    	}else if(pcount == minp) {
    		Logs("There Just "+minp+" Players On Server, Starting World Border");
    		if(!start) {
    			Bukkit.dispatchCommand(console, "wb world fill");
    			Bukkit.dispatchCommand(console, "wb fill confirm");
    			start = true;
    		}else {
    			Bukkit.dispatchCommand(console, "wb fill pause");
    		}
    	}
    }
    
    

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
    	if (command.getName().equalsIgnoreCase("startautowb")) {
            if(start) {
            	Logs("AutoWB Already Started");
            }else {
            	autoWBStart();
            }
            return true;
        }
        return false;
    }
    
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
    	autoWBStart();
    }
    
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
    	new BukkitRunnable() {
			@Override
			public void run() {
				autoWBStart();
			}
        }.runTaskLater(this, 20);
    }
    
    FileConfiguration config = getConfig();
    
    public boolean start = false;
    
}