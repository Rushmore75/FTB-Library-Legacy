package com.feed_the_beast.ftbl.net;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgePlayerSP;
import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;
import com.feed_the_beast.ftbl.api.net.MessageToClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageLMPlayerLoggedOut extends MessageToClient<MessageLMPlayerLoggedOut>
{
    public UUID playerID;
    
    public MessageLMPlayerLoggedOut() { }
    
    //FIXME: Unused Message
    public MessageLMPlayerLoggedOut(ForgePlayerMP p)
    {
        playerID = p.getProfile().getId();
    }
    
    @Override
    public LMNetworkWrapper getWrapper()
    { return FTBLibNetHandler.NET; }
    
    @Override
    public void fromBytes(ByteBuf io)
    {
        playerID = readUUID(io);
    }
    
    @Override
    public void toBytes(ByteBuf io)
    {
        writeUUID(io, playerID);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void onMessage(MessageLMPlayerLoggedOut m, Minecraft mc)
    {
        ForgePlayerSP p = ForgeWorldSP.inst.getPlayer(m.playerID);
        p.onLoggedOut();
        p.isOnline = false;
    }
}