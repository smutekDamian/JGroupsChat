package com.smutek.chat;

/**
 * Created by damian on 26.03.17.
 */
public class User {
    private String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
