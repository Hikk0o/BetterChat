package hikko.betterchat.playerhistory;

import hikko.betterchat.BetterChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ChatPlayer {
    private final BukkitScheduler scheduler = BetterChat.getInstance().getServer().getScheduler();
    private final Player player;
    private boolean cooldown = false;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final Logger loggerChatPlayer = Logger.getLogger("Debug ChatPlayer");


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

    public void appendMessage(int id, Component tag, Component sender, Component content) {
        if (messages.size() >= 100) {
            messages.remove(0);
        }
        messages.add(new ChatMessage(id, tag, sender, content));
//        messages.forEach(chatMessage -> loggerChatPlayer.log(Level.INFO, chatMessage.id + " (" + messages.size() + "):: " + PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent())));
    }
    public void deleteMessage(int id) {
        IntStream.range(0, 100).forEach(n -> player.sendMessage(""));
        messages.forEach(chatMessage -> {
            if (chatMessage.id == id) {
                chatMessage.setContent(Component.text()
                        .append(Component.text("* Сообщение удалено администратором *", NamedTextColor.DARK_GRAY))
                        .build());
            }
            player.sendMessage(chatMessage.getFullComponent());
        });
    }

    public Player get() {
        return player;
    }
}
