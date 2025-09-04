package carpet.patches;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class NetHandlerPlayServerFake extends ServerGamePacketListenerImpl
{
    public static final Connection DUMMY_CONNECTION;

    public NetHandlerPlayServerFake(final MinecraftServer minecraftServer, final ServerPlayer serverPlayer, final CommonListenerCookie i)
    {
        super(minecraftServer, DUMMY_CONNECTION, serverPlayer, i);
    }

    @Override
    public void send(final Packet<?> packetIn)
    {
    }

    public void send(Packet<?> packet, @Nullable PacketSendListener sendListener)
    {
    }

    @Override
    public void disconnect(Component message)
    {
        if (message.getContents() instanceof TranslatableContents text && (text.getKey().equals("multiplayer.disconnect.idling") || text.getKey().equals("multiplayer.disconnect.duplicate_login")))
        {
            ((EntityPlayerMPFake) player).kill(message);
        }
    }

    @Override
    public void teleport(double d, double e, double f, float g, float h, Set<RelativeMovement> set)
    {
        super.teleport(d, e, f, g, h, set);
        if (player.serverLevel().getPlayerByUUID(player.getUUID()) != null) {
            resetPosition();
            player.serverLevel().getChunkSource().move(player);
        }
    }

    static {
        DUMMY_CONNECTION = new FakeClientConnection(PacketFlow.SERVERBOUND);
    }
}



