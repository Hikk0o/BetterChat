package hikko.betterchat.playerhistory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class ChatMessage {
    public final int id;
    private final Component tag;
    private final Component senderComponent;
    private Component content;
    private final Player sender;

    ChatMessage(int id, Component tag, Component senderComponent, Component content, Player sender) {
        this.id = id;
        this.tag = tag;
        this.senderComponent = senderComponent;
        this.content = content;
        this.sender = sender;
    }

    public Component getTag() {
        return tag;
    }

    public Component getContent() {
        return content;
    }

    public Component getSenderComponent() {
        return senderComponent;
    }

    public void setContent(Component content) {
        this.content = content;
    }

    public Component getFullComponent() {
        return Component.empty()
                .append(tag == null ? Component.empty() : tag)
                .append(senderComponent == null ? Component.empty() : senderComponent.append(Component.text(": ").color(TextColor.color(0x5D5D5D))))
                .append(content);
    }

    public Player getSender() {
        return sender;
    }
}