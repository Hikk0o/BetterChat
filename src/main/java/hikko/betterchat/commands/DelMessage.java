package hikko.betterchat.commands;

import hikko.betterchat.playerhistory.ChatController;
import hikko.betterchat.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelMessage extends AbstractCommand {

    public DelMessage() {
        super("dmsg");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender.hasPermission("betterchat.detelemessage")) {
            if (args.length == 0) {
                sender.sendMessage("/dmsg [id]");
            }
            if (args.length > 0) {
                try {
                    int id = Integer.parseInt(args[0]);
                    if (id != -1) {
                        if (ChatUtils.deletedIdMessages.contains(id)) {
                            Component component = Component.text("Это сообщение уже удалено", NamedTextColor.YELLOW);
                            sender.sendMessage(component);
                            ChatController.getPlayer((Player) sender).appendMessage(-1, null, null, component);
                            return;
                        }
                        ChatUtils.deletedIdMessages.add(id);
                        ChatController.deleteMessage(id);
                        if (ChatUtils.reportedIdMessages.contains(id)) {
                            ChatController.deleteMessage((id * -1));
                            ChatUtils.deletedIdMessages.add((id * -1));
                        }
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("/dmsg [id]");
                }
                sender.sendMessage("/dmsg [id]");
            }
        }
    }
}
