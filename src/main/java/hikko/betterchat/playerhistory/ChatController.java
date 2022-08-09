package hikko.betterchat.playerhistory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

public final class ChatController {
    private static final ArrayList<ChatPlayer> ChatPlayers = new ArrayList<>();

    public static boolean chatIsCooldown(Player player) {
        final boolean[] result = {false};
        ChatPlayers.forEach(chatPlayer -> {
            if (Objects.equals(chatPlayer.getNickname(), player.getName())) {
                if (chatPlayer.isCooldown()) {
                    Component cooldownMessage = Component.text("Немного подождите, прежде чем снова отправить сообщение", NamedTextColor.YELLOW);
                    player.sendMessage(cooldownMessage);
                    ChatController.getPlayer(player).appendMessage(-1, null, null, cooldownMessage, null);
                    result[0] = true;
                } else {
                    chatPlayer.setCooldown();
                }
            }
        });
        return result[0];
    }

    public static void removePlayer(Player player) {
        ChatPlayers.removeIf(chatPlayer -> chatPlayer.getNickname().equals(player.getName()));
    }

    public static void addPlayer(Player player) {
        ChatPlayers.add(new ChatPlayer(player));
    }

    public static ChatPlayer getPlayer(Player player) {
        ChatPlayer[] result = {null};
        ChatPlayers.forEach(chatPlayer -> {
            if (chatPlayer.get().getName().equals(player.getName())) result[0] = chatPlayer;
        });
        return result[0];
    }

    public static void deleteMessage(int id) {
        ChatPlayers.forEach(chatPlayer -> chatPlayer.deleteMessage(id));
    }
    public static ChatMessage getMessage(int id) {
        for (ChatPlayer chatPlayer : ChatPlayers) {
            ChatMessage chatMessage = chatPlayer.getMessage(id);
            if (chatMessage != null) return chatMessage;
        }
        return null;
    }
}
