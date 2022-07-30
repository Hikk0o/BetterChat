package hikko.betterchat;

import hikko.betterchat.commands.DelMessage;
import hikko.betterchat.commands.PortalCommand;
import hikko.betterchat.events.ChatEvents;
import hikko.betterchat.events.PortalEvents;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class BetterChat extends JavaPlugin {

    private static BetterChat instance;
    public static Logger logger;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        logger = this.getLogger();
        pluginManager = Bukkit.getPluginManager();

        // Commands
        new DelMessage();
        new PortalCommand();

        // Events
        pluginManager.registerEvents(new ChatEvents(), this);
        pluginManager.registerEvents(new PortalEvents(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BetterChat getInstance() {
        return instance;
    }

    public IEssentials getAPIEssentials() {
        return (IEssentials) pluginManager.getPlugin("Essentials");
    }

}
