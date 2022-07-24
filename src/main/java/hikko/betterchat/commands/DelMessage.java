package hikko.betterchat.commands;

import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.logging.Level;

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
                BetterChat.getInstance().getLogger().log(Level.INFO, Arrays.toString(args));
                try {
                    int id = Integer.parseInt(args[0]);
                    ChatController.deleteMessage(id);
                } catch (NumberFormatException e) {
                    sender.sendMessage("/dmsg [id]");
                }
            }
        }
    }
}
