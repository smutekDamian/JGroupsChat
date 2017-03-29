package com.smutek.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.smutek.chat.protos.ChatOperationProtos;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

/**
 * Created by damian on 27.03.17.
 */
public class ChatReceiver extends ReceiverAdapter {
    private String nickname;
    private String channelName;

    public ChatReceiver(String nickname, String channelName) {
        this.nickname = nickname;
        this.channelName = channelName;
    }

    @Override
    public void receive(Message msg) {
        if (msg == null || nickname.equals(msg.getSrc().toString())) return;
        try {
            System.out.println("## "+channelName+"##" + msg.getSrc() + "--->" + ChatOperationProtos.
                    ChatMessage.parseFrom(msg.getBuffer()).getMessage());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
