package com.smutek.chat.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.smutek.chat.protos.ChatOperationProtos;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by damian on 27.03.17.
 */
public class ChatManagementReceiver extends ReceiverAdapter {
    private static final Map<String, List<String>> chatState = new HashMap<>();

    public static Map<String, List<String>> getChatState() {
        return chatState;
    }

    public static void showState(){
        for ( String key : chatState.keySet() ) {
            List<String> listOfUsers = chatState.get(key);
            System.out.println(key);
            for (String user: listOfUsers){
                System.out.println("\t " + user);
            }
        }
    }

    @Override
    public synchronized void receive(Message msg) {
        try {
            ChatOperationProtos.ChatAction chatAction = ChatOperationProtos.ChatAction.parseFrom(msg.getBuffer());
            List<String> usersList;
            usersList = chatState.get(chatAction.getChannel());
            if (chatAction.getAction()
                    .equals(ChatOperationProtos.ChatAction.ActionType.JOIN)) {
                addUser(usersList, chatAction);
            } else {
                removeUser(usersList, chatAction);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void addUser(List<String> usersList, ChatOperationProtos.ChatAction chatAction){
        if (usersList == null){
            usersList = new ArrayList<>();
        }
        usersList.add(chatAction.getNickname());
        chatState.put(chatAction.getChannel(), usersList);
    }

    private void removeUser(List<String> usersList, ChatOperationProtos.ChatAction chatAction){
        usersList.remove(chatAction.getNickname());
        if (usersList.isEmpty()){
            chatState.remove(chatAction.getChannel());
        }
        else {
            chatState.put(chatAction.getChannel(), usersList);
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (chatState){
            Util.objectToStream(chatState, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        Map<String,List<String>> newState;
        newState = (Map<String, List<String>>) Util.objectFromStream(new DataInputStream(input));
        synchronized (chatState){
            chatState.clear();
            chatState.putAll(newState);
        }
    }

}
