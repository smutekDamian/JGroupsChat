package com.smutek.chat.management;

import com.smutek.chat.protos.ChatOperationProtos;
import com.smutek.chat.receiver.ChatManagementReceiver;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by damian on 26.03.17.
 */
public class ChatManagement extends Thread {
    private static JChannel managementChannel;
    private static final Map<String,List<String>> chatState = new HashMap<>();

    public ChatManagement(){
        System.setProperty("java.net.preferIPv4Stack", "true");
        managementChannel = new JChannel(false);
        ProtocolStack stack = new ProtocolStack();
        managementChannel.setProtocolStack(stack);
        stack.addProtocol(new UDP())
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
            managementChannel.setReceiver(new ChatManagementReceiver());
            managementChannel.connect("ChatManagement321321");
            managementChannel.getState(null, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendJoiningMessage(String nickname, String channelName){
        byte[] buffer = createChatActionBuffer(nickname, channelName, ChatOperationProtos.ChatAction.ActionType.JOIN);
        Message msg = new Message(null, null, buffer);
        sendMessage(msg);
    }

    public static void sendLeavingMessage(String nickname, String channelName){
        byte[] buffer = createChatActionBuffer(nickname, channelName, ChatOperationProtos.ChatAction.ActionType.LEAVE);
        Message msg = new Message(null, null, buffer);
        sendMessage(msg);
    }

    private static byte[] createChatActionBuffer(String nickname, String channelName,
                                          ChatOperationProtos.ChatAction.ActionType actionType){
        ChatOperationProtos.ChatAction chatAction = ChatOperationProtos.ChatAction.newBuilder()
                .setAction(actionType)
                .setChannel(channelName)
                .setNickname(nickname)
                .build();
        return chatAction.toByteArray();
    }
    private static void sendMessage(Message message){
        try {
            managementChannel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showState(){
        ChatManagementReceiver.showState();
    }





}
