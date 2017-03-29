package com.smutek.chat;

import com.smutek.chat.connection.ChatChannel;
import com.smutek.chat.management.ChatManagement;
import com.smutek.chat.protos.ChatOperationProtos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Map<String,List<String>> chatState = new HashMap<>();
    private static String nickname;
    private static ChatManagement chatManagement;
    private static List<ChatChannel> myChannels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Write your nickname: ");
        nickname = bufferedReader.readLine();
        User user = new User(nickname);
        chatManagement = new ChatManagement();
        while (true){
                String command = bufferedReader.readLine();
                switch (command){
                    case "create":
                        System.out.println("Write name of the channel: ");
                        System.out.println(">>>");
                        String channelName = bufferedReader.readLine();
                        joinOrCreateChannel(channelName);
                        break;
                    case "join":
                        System.out.println("Write name of the channel in which you want to join : ");
                        System.out.println(">>>");
                        String joiningChannelName = bufferedReader.readLine();
                        joinOrCreateChannel(joiningChannelName);
                        break;
                    case "show":
                        chatManagement.showState();
                        break;
                    case "help":
                        System.out.println("create --> create the new channel ");
                        System.out.println("join --> join to channel ");
                        System.out.println("show --> show available channels and users");
                        System.out.println("exit --> exit the application ");
                        System.out.println("leave --> leave channel ");
                        break;
                    case "leave":
                        System.out.println("Write channel you want to leave");
                        String leavingChannel = bufferedReader.readLine();
                        ChatManagement.sendLeavingMessage(nickname, leavingChannel);
                        removeChannelToSend(leavingChannel);
                        break;
                    case "exit":
                        for (ChatChannel channel: myChannels){
                            ChatManagement.sendLeavingMessage(nickname,channel.getChannelName());
                        }
                        System.exit(0);
                        break;
                    default:
                        for (ChatChannel channel: myChannels){
                            channel.sendMessage(createMessage(command));
                        }
                        break;
                }
        }
    }

    private static void joinOrCreateChannel(String channelName) throws UnknownHostException {
        ChatChannel newChannel = new ChatChannel(channelName, nickname);
        ChatManagement.sendJoiningMessage(nickname, channelName);
        myChannels.add(newChannel);
    }
    private static byte[] createMessage(String message){
        ChatOperationProtos.ChatMessage chatMessage = ChatOperationProtos.ChatMessage.newBuilder()
                .setMessage(message)
                .build();
        return chatMessage.toByteArray();
    }
    private static void removeChannelToSend(String channelName){
        ChatChannel tmpChannel = null;
        for (ChatChannel channel: myChannels){
            if (channel.getChannelName().equals(channelName)){
                tmpChannel = channel;
                break;
            }
        }
        myChannels.remove(tmpChannel);
    }
}
