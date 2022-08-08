package hikko.betterchat.commands;

import hikko.betterchat.playerhistory.ChatController;
import org.bukkit.command.CommandSender;

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
                    if (id >= 0) {
                        ChatController.deleteMessage(id);
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
