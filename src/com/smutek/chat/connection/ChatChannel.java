package com.smutek.chat.connection;

import com.smutek.chat.receiver.ChatReceiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by damian on 26.03.17.
 */
public class ChatChannel {
    private JChannel channel;
    private String channelName;
    private String nickname;

    public ChatChannel(String channelName, String nickname) throws UnknownHostException {
        this.nickname = nickname;
        this.channelName = channelName;
        System.setProperty("java.net.preferIPv4Stack", "true");
        channel = new JChannel(false);
        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(channelName)))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());
        try {

            stack.init();
            channel.setName(nickname);
            channel.setReceiver(new ChatReceiver(nickname, this.channelName));
            channel.connect(channelName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getChannelName(){
        return channelName;
    }

    public JChannel getChannel() {
        return channel;
    }

    public void sendMessage(byte[] message){
        try {
            channel.send(new Message(null, null, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
