package com.company.model;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by vnagpurkar on 11/28/2015.
 */
public class Room {

    private String roomName;

    public Room(String roomName){

        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return !(roomName != null ? !roomName.equals(room.roomName) : room.roomName != null);

    }

    @Override
    public int hashCode() {
        return roomName != null ? roomName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomName='" + roomName + '\'' +
                '}';
    }
}
