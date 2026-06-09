package com.features;

import java.util.HashSet;
import java.util.Set;

public class FriendManager {
    private static final Set<String> friends = new HashSet<>();

    public static void addFriend(String name) {
        friends.add(name.toLowerCase());
    }

    public static void removeFriend(String name) {
        friends.remove(name.toLowerCase());
    }

    public static boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }
}
