package com.company.data;

import com.company.model.Member;
import com.company.model.Room;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vnagpurkar on 11/28/2015.
 */
public class Data  {

    private static Map<String,Member> members;
    private static Map<String,Room> rooms;

    // This is the function to initialise mock data.
    // This function will not be needed in production.
    public static void init() {
        members = new HashMap<String,Member>();
        rooms = new HashMap<String,Room>();

        Member member = new Member("Vrunda","1234567");
        Room room = new Room("chat");
        members.put(member.getLoginName(), member);
        rooms.put(room.getRoomName(), room);

        member = new Member("gc","23456");
        room = new Room("hottub");
        members.put(member.getLoginName(), member);
        rooms.put(room.getRoomName(), room);

    }

    public static void setMembers(Member member) {
        members.put(member.getLoginName(), member);
    }

    public static void setRooms(Room room) {
        rooms.put(room.getRoomName(), room);
    }

    public static Map<String, Member> getMembers() {
        return members;
    }

    public static Map<String, Room> getRooms() {
        return rooms;
    }
}
