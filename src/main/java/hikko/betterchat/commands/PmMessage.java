package hikko.betterchat.commands;

import com.google.common.collect.Lists;
import hikko.betterchat.BetterChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class PmMessage extends AbstractCommand {

    private final Logger pmLogger = Logger.getLogger("PM");

    public PmMessage() {
        super("pm");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("betterchat.pm")) {
            if (args.length >= 2) {
                Player recipient = Bukkit.getPlayer(args[0]);

                if (recipient == null) {
                    sender.sendMessage(Component.text("Игрок не найден.").color(NamedTextColor.RED));
                    return;
                }

                // Build personalMessage
                List<String> listMessage = new ArrayList<>(Arrays.stream(args).toList());
                listMessage.remove(0);
                StringBuilder senderMessageBuilder = new StringBuilder();
                for (String word : listMessage) {
                    senderMessageBuilder.append(word).append(" ");
                }
                Component personalMessage = Component.text(senderMessageBuilder.toString());

                // Tags
                Component senderTag = Component.text("PM для " + recipient.getName() + ":").color(NamedTextColor.YELLOW);
                Component recipientTag = Component.text("PM от " + sender.getName() + ":").color(NamedTextColor.GOLD);
                Component reply = Component.text("⮪")
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/w " + sender.getName() + " "))
                        .hoverEvent(Component.text("Ответить"));

                // Custom messages
                Component senderMessage = Component.empty()
                        .append(senderTag)
                        .append(Component.space())
                        .append(personalMessage);

                Component recipientMessage = Component.empty()
                        .append(reply)
                        .append(Component.space())
                        .append(recipientTag)
                        .append(Component.space())
                        .append(personalMessage);

                recipient.sendMessage(recipientMessage);
                sender.sendMessage(senderMessage);
                pmLogger.info(sender.getName() + " to " + recipient.getName() + ": " + senderMessageBuilder);
            } else {
                Component usageCommand = Component.text("/msg <никнейм> [сообщение]").color(NamedTextColor.YELLOW);
                sender.sendMessage(usageCommand);
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        ArrayList<String> list = Lists.newArrayList();
        if (args.length == 1) {
            for (Player onlinePlayer : BetterChat.getInstance().getServer().getOnlinePlayers()) {
                list.add(onlinePlayer.getName());
            }
        }
        return list;
    }
}
