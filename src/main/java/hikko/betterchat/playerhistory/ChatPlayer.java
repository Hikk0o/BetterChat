package hikko.betterchat.playerhistory;

import hikko.betterchat.BetterChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ChatPlayer {
    private final BukkitScheduler scheduler = BetterChat.getInstance().getServer().getScheduler();
    private final Player player;
    private boolean cooldown = false;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final Logger loggerChatPlayer = Logger.getLogger("Debug ChatPlayer");
    private boolean chatLock = false;

    public ChatPlayer(Player player) {
        this.player = player;
    }

    public void setCooldown() {
        cooldown = true;
        scheduler.runTaskLater(BetterChat.getInstance(), () -> cooldown = false, 40);
    }

    public boolean isCooldown() {
        return cooldown;
    }

    public String getNickname() {
        return player.getName();
    }

    public void appendMessage(int id, Component tag, Component senderComponent, Component content, Player sender) {
        if (messages.size() >= 100) {
            messages.remove(0);
        }
        messages.add(new ChatMessage(id, tag, senderComponent, content, sender));
//        messages.forEach(chatMessage -> loggerChatPlayer.log(Level.INFO, chatMessage.id + " (" + messages.size() + "):: " + PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent())));
    }
    public void deleteMessage(int id) {
        if (!containMessage(id)) return;
        chatLock = true;
        IntStream.range(0, 100).forEach(n -> player.sendMessage(""));
        for (ChatMessage chatMessage : messages) {
            if (chatMessage.id == id) {
                chatMessage.setContent(Component.text()
                        .append(Component.text("* удалено администратором *", NamedTextColor.DARK_GRAY))
                        .build());
            }
            player.sendMessage(chatMessage.getFullComponent());
        }
        chatLock = false;
    }
    private boolean containMessage(int id) {
        for (ChatMessage chatMessage : messages) {
            if (chatMessage.id == id) {
                return true;
            }
        }
        return false;
    }

    public ChatMessage getMessage(int id) {
        for (ChatMessage chatMessage : messages) {
            if (chatMessage.id == id) {
                return chatMessage;
            }
        }
        return null;
    }

    public boolean isChatLock() {
        return chatLock;
    }

    public Player get() {
        return player;
    }
}
