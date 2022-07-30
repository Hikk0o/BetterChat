package hikko.betterchat.commands;

import hikko.betterchat.BetterChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class PortalCommand extends AbstractCommand {

    public PortalCommand() {
        super("allowportal");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("betterchat.*")) {
            if (args.length == 0) {
                sender.sendMessage("/allowportal [isAllowEnd] [isSilent]");
            }
            if (args.length == 2) {
                try {
                    boolean isAllowEnd = Boolean.parseBoolean(args[0]);
                    boolean isSilent = Boolean.parseBoolean(args[1]);
                    BetterChat.getInstance().getConfig().set("allow-end", isAllowEnd);
                    BetterChat.getInstance().saveConfig();

                    if (!isSilent) {
                        Component message = Component.empty();
                        if (isAllowEnd) {
                            message = message
                                    .append(Component.text("Администратор открыл измерение края!", NamedTextColor.GREEN));
                        }
                        else {
                            message = message
                                    .append(Component.text("Администратор закрыл измерение края.", NamedTextColor.RED));
                        }

                        for (Player player : BetterChat.getInstance().getServer().getOnlinePlayers()) {
                            player.sendMessage(message);
                        }
                    }
                    sender.sendMessage(isAllowEnd ? Component.text("The End opened", NamedTextColor.GREEN) : Component.text("The End closed", NamedTextColor.RED));
                    return;
                } catch (NumberFormatException e) {
                    sender.sendMessage("/allowportal [isAllowEnd] [isSilent]");
                }
                sender.sendMessage("/allowportal [isAllowEnd] [isSilent]");
            }
        }
    }
}
