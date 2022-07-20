package hikko.betterchat.playerhistory.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import hikko.betterchat.BetterChat;
import hikko.betterchat.playerhistory.ChatController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static hikko.betterchat.BetterChat.protocolManager;

public class ChatPacketHandler {

	public ChatPacketHandler() {
		protocolManager.addPacketListener(new PacketAdapter(
				BetterChat.getInstance(),
				ListenerPriority.NORMAL,
				PacketType.Play.Server.CHAT) {
			@Override
            public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.CHAT) {
					BetterChat.getInstance().getLogger().log(Level.INFO, "BLOCK_BREAK");
					PacketContainer packet = event.getPacket();
					Player player = event.getPlayer();
					try {
						if (packet.getChatTypes().getValues().isEmpty()) return;
//						if (packet.getChatTypes().getValues().get(0) == ChatType.GAME_INFO) return;
						if (player == null) return;
						if (ChatController.getPlayer(player) == null) return;

						StructureModifier<WrappedChatComponent> chatComponents = packet.getChatComponents();
						try {
							Component component = GsonComponentSerializer.gson().deserialize(chatComponents.getValues().get(0).getJson());
							BetterChat.getInstance().getLogger().log(Level.INFO, player.getName() + " append");
							ChatController.getPlayer(player).appendMessage(-1, null, null, component);
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
					catch (NullPointerException e) {
						e.printStackTrace();
					}
				} else {
					BetterChat.getInstance().getLogger().log(Level.INFO, "!PacketType.Play.Server.CHAT");

				}
			}
		});
	}
}
