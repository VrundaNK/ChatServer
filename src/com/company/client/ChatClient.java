package com.company.client;

import com.company.Constants;
import com.company.data.Data;
import com.company.model.Commands;
import com.company.model.Member;
import com.company.model.Room;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatClient extends Thread {

    private Socket user;
    private PrintStream output;
    private DataInputStream input;
    private Member currentMember; // this is the current signed in member
    private Room currentRoom; // this is the current chatroom for public messages
    private String group; // this is group of two clients (users) for private messaging

    private static Map<String, ChatClient> chatClients = new HashMap<String, ChatClient>();
    private static Map<String, Set<String>> clientsInGroup = new HashMap<String, Set<String>>();

    public ChatClient (Socket user) {

        this.user = user;
        try {
            input = new DataInputStream(new BufferedInputStream(this.user.getInputStream()));
            output = new PrintStream(this.user.getOutputStream());
        } catch (IOException e){

        }
    }

    @Override
    public void run() {

        try {
            displayMessage(Constants.WELCOMEMSG);
            usage();
            String line;
            displayMessage(Constants.NEWUSERMSG);

            while (this.user.isConnected() && !this.user.isClosed()) {
                //login() functionality the user (signup for new user, signin for existing user).
                login();
                line = input.readLine();
                if (line != null) {
                    String message = "";
                    if (line.startsWith(Constants.UPSTREAM)) {
                        // currentRoom != null indicates conversation in public chat room
                        if (currentRoom != null) {
                            message = line.replace(Constants.UPSTREAM, "");
                            if (currentRoom != null && getCurrentMember() != null) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(getCurrentMember().getLoginName());
                                sb.append(":");
                                sb.append(message);
                                notifyAllMembers(sb.toString(), currentRoom.getRoomName(), null, false);
                            }
                        } else if( group != null) { // group != null indicates private message
                            message = line.replace(Constants.UPSTREAM, "");
                            if (getCurrentMember() != null) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(Constants.DOWNSTREAM);
                                sb.append(message);
                                sendPrivateMessage(message);
                            }
                        } else { // this indicates no private conversation or public chatroom is in progress
                            if (line.startsWith(Constants.COMMANDPREFIX + Commands.quit.toString())) {
                                quit();
                            } else if (line.startsWith(Constants.COMMANDPREFIX + Commands.rooms.toString())) {
                                printRoomList();
                            } else if (line.startsWith(Constants.COMMANDPREFIX + Commands.join.toString())) {
                                String roomName = line.replace(Constants.COMMANDPREFIX + Commands.join.toString(), "").trim();
                                joinChatRoom(roomName);
                            } else if (line.startsWith(Constants.COMMANDPREFIX + Commands.ping.toString())) {
                                String recieverLoginName = line.replace(Constants.COMMANDPREFIX + Commands.ping.toString(), "").trim();
                                if (recieverLoginName != null && !recieverLoginName.isEmpty()) {
                                    pingUser(recieverLoginName);
                                } else {
                                    displayMessage(Constants.PINGERRORMEG);
                                }
                            } else if (line.startsWith(Constants.COMMANDPREFIX + Commands.addRoom.toString())) {
                                String roomName = line.replace(Constants.COMMANDPREFIX + Commands.addRoom.toString(), "").trim();
                                if(roomName != null || !roomName.isEmpty()) {
                                    addRoom(roomName);
                                    displayMessage(String.format(Constants.NEWROOMMSG, roomName));
                                } else {
                                    displayMessage(Constants.VALIDROOMNAMEMSG);
                                }
                            }
                        }
                        if (line.startsWith(Constants.COMMANDPREFIX + Commands.leave.toString())) {
                            if (currentRoom != null && getCurrentMember() != null) {
                                leaveChatRoom();
                            } else {
                                displayMessage(Constants.CHATROOMERRORMSG);
                            }
                        }
                        if (line.startsWith(Constants.COMMANDPREFIX + Commands.exit.toString())) {
                            if (group != null && getCurrentMember() != null) {
                                exitPrivateConverstation();
                            } else {
                                displayMessage(Constants.PRIVATEMSGERRORMSG);
                            }
                        }
                        if (line.startsWith(Constants.COMMANDPREFIX + Commands.help.toString())) {
                            usage();
                        }
                    }else {
                        displayMessage(Constants.PROTOCOLERRORMSG);
                    }
                }
            }
        } catch(IOException e){

        }
    }

    private void usage() {
        displayMessage(Constants.USAGEMSG);
        displayMessage(Constants.HELPMSG);
        displayMessage(Constants.ROOMSMSG);
        displayMessage(Constants.JOINMSG);
        displayMessage(Constants.ADDROOMMSG);
        displayMessage(Constants.LEAVEMSG);
        displayMessage(Constants.QUITMSG);
        displayMessage(Constants.PINGMSG);
        displayMessage(Constants.EXITMSG);
    }

    private boolean login() {
        try {
            while (this.getCurrentMember() == null) {
                String line = input.readLine();
                if(line != null){
                    if (line.toLowerCase().equals(Constants.UPSTREAM + "y")) {
                        signUp();
                    } else if (line.toLowerCase().equals(Constants.UPSTREAM + "n")) {
                        boolean signIn;
                        do {
                            signIn = signInExistingUser();
                            if(!signIn) {
                                displayMessage(Constants.LOGINFAILEDMSG);
                            }
                        } while(!signIn);
                    }
                }
            }
            if(!chatClients.containsKey(getCurrentMember().getLoginName())){
                chatClients.put(getCurrentMember().getLoginName(), this);
            }
        }
        catch (IOException e){
            return false;
        }
        return true;
    }

    private static ChatClient getChatClient (String loginName) {
        synchronized (chatClients) {
            return chatClients.get(loginName);
        }
    }

    private boolean pingUser(String loginName){
        synchronized (chatClients) {
            ChatClient reciever = getChatClient(loginName);
            if(reciever != null){
                if (getCurrentMember() != null) {
                    this.group = String.valueOf(getCurrentMember().getLoginName().hashCode() + loginName.hashCode());
                    reciever.group = this.group;
                    Set<String> clientLoginNames;
                    if (!clientsInGroup.containsKey(group)) {
                        clientLoginNames = new HashSet<String>();
                        clientLoginNames.add(getCurrentMember().getLoginName());
                        clientLoginNames.add(loginName);
                        clientsInGroup.put(group, clientLoginNames);
                    }
                    sendPrivateMessage("Hello "+loginName);
                }
            } else {
                StringBuffer sb = new StringBuffer();
                sb.append(loginName);
                sb.append(Constants.USEROFFLINEMSG);
                displayMessage(sb.toString());
            }
        }
        return true;
    }

    private void sendPrivateMessage(String message) {
        if(this.group != null){
            StringBuffer sb;
            synchronized (chatClients) {
                if (clientsInGroup.containsKey(group)) {
                    Set<String> clientLoginNames = clientsInGroup.get(group);
                    try {
                        for (String s : clientLoginNames) {
                            if(!s.equals(getCurrentMember().getLoginName())){
                                ChatClient c = getChatClient(s);

                                if (c != null) {
                                    PrintStream outputStream = new PrintStream(c.getUser().getOutputStream());
                                    sb = new StringBuffer();
                                    sb.append(Constants.DOWNSTREAM);
                                    sb.append(message);
                                    outputStream.println(sb.toString());
                                } else {
                                    sb = new StringBuffer();
                                    sb.append(s);
                                    sb.append(Constants.USEROFFLINEMSG);
                                    displayMessage(sb.toString());
                                }
                            }
                        }
                    } catch (IOException e){

                    }
                }
            }
        }else {
            displayMessage(Constants.NOCONVERSATIONMSG);
        }
    }

    private void exitPrivateConverstation() {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.STARPREFIX);
        sb.append(Constants.LEAVECHATROOMMSG);
        sb.append(currentMember.getLoginName());
        sendPrivateMessage(sb.toString());
        Set<String> memberNames = clientsInGroup.get(group);
        for(String s: memberNames){
            ChatClient chatClient = getChatClient(s);
            if(chatClient != null){
                chatClient.group = null;
            }
        }
        clientsInGroup.remove(group);
    }

    private static void notifyAllMembers(String message, String roomName, String currentMemberLoginName, boolean displayCurrentUserMsg) {
        synchronized (clientsInGroup){
            Set<String> chatClientLoginNames = clientsInGroup.get(roomName);
            for(String s : chatClientLoginNames){
                try {
                    ChatClient chatClient = getChatClient(s);
                    PrintStream outputStream = new PrintStream(chatClient.getUser().getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    sb.append(Constants.DOWNSTREAM);
                    sb.append(message);
                    if(displayCurrentUserMsg) {
                        if (currentMemberLoginName.matches(s)) {
                            sb.append(Constants.CURRENTUSERMEG);
                        }
                    }
                    outputStream.println(sb.toString());
                } catch(IOException e){

                }
            }
        }
    }

    private void leaveChatRoom() {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.STARPREFIX);
        sb.append(Constants.LEAVECHATROOMMSG);
        sb.append(currentMember.getLoginName());
        notifyAllMembers(sb.toString(), currentRoom.getRoomName(), currentMember.getLoginName(), true);
        Set<String> members = clientsInGroup.get(currentRoom.getRoomName());
        members.remove(currentMember.getLoginName());
        clientsInGroup.put(currentRoom.getRoomName(), members);
        this.currentRoom = null;
    }

    private void joinChatRoom(String roomName){
        StringBuffer sb = new StringBuffer();
        Map<String, Room> rooms = Data.getRooms();
        Set<String> chatClientLoginNames;
        if(this.currentRoom == null){
            if(rooms.keySet().contains(roomName)) {
                currentRoom = rooms.get(roomName);
                if (clientsInGroup.containsKey(currentRoom.getRoomName())) {
                    chatClientLoginNames = clientsInGroup.get(currentRoom.getRoomName());
                } else {
                    chatClientLoginNames = new HashSet<String>();
                }
                chatClientLoginNames.add(this.getCurrentMember().getLoginName());
                clientsInGroup.put(currentRoom.getRoomName(), chatClientLoginNames);
                displayMessage(Constants.ENTERINGCHATROOMMSG);
                printMemberList();
                sb = new StringBuffer();
                sb.append(Constants.STARPREFIX);
                sb.append(Constants.NEWUSERINCHATMSG);
                sb.append(getCurrentMember().getLoginName());
                notifyAllMembers(sb.toString(), currentRoom.getRoomName(), currentMember.getLoginName(), true);
            }
        } else {
            sb.append(Constants.DOWNSTREAM);
            sb.append(Constants.ALREADYINCHATMSG);
            sb.append(this.currentRoom.getRoomName());
            sb.append("\n");
            sb.append(Constants.CHATROOMINPROGRSSMSG);
            output.println(sb.toString());
        }
    }

    private void printMemberList(){
        Set<String> memberLoginNames= clientsInGroup.get(currentRoom.getRoomName());
        for(String s: memberLoginNames){
            StringBuffer sb = new StringBuffer();
            sb.append(Constants.DOWNSTREAM);
            sb.append(Constants.STARPREFIX);
            sb.append(s);
            if(s.equals(getCurrentMember().getLoginName())) {
                sb.append(Constants.CURRENTUSERMEG);
            }
            output.println(sb.toString());
        }
        displayMessage(Constants.ENDOFLISTMSG);
    }

    private void printRoomList(){
        StringBuffer sb;
        Map<String, Room> rooms= Data.getRooms();
        for(Map.Entry<String, Room> entry: rooms.entrySet()){
            sb = new StringBuffer();
            sb.append(Constants.DOWNSTREAM);
            sb.append(Constants.STARPREFIX);
            sb.append(entry.getKey());
            sb.append(" (");
            Set<String> memberNames = clientsInGroup.get(entry.getKey());
            if(memberNames != null){
                sb.append(memberNames.size());
            } else {
                sb.append(0);
            }
            sb.append(") ");
            output.println(sb.toString());
        }
        displayMessage(Constants.ENDOFLISTMSG);
    }

    private boolean signInExistingUser() {
        try {
            displayMessage(Constants.LOGINNAMEMSG);
            String line = input.readLine();
            if(line != null) {
                Member currentMember = getExistingMember(line.replace(Constants.UPSTREAM, ""));
                if (currentMember != null) {
                    this.currentMember = currentMember;
                    displayMessage(Constants.PASSWORDMSG);
                    line = input.readLine();
                    if(line != null) {
                        if (currentMember.getPassword().equals(line.replace(Constants.UPSTREAM, ""))) {
                            displayWelcomeMessage();
                            return true;
                        } else {
                            displayMessage(Constants.WRONGPASSWORDMSG);
                        }
                    }
                } else {
                    displayMessage(Constants.WRONGLOGINNAMEMSG);
                }
            }
        } catch(IOException e){

        }
        return false;
    }

    private void displayWelcomeMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.DOWNSTREAM);
        sb.append(Constants.WELCOMEUSERMSG);
        sb.append(getCurrentMember().getLoginName());
        sb.append("!");
        output.println(sb.toString());
    }

    private Member getExistingMember(String loginName){
        Map<String, Member> members = Data.getMembers();
        return members.get(loginName);
    }

    private boolean signUp() {
        try {
            boolean uniqueLoginName;
            String line;
            displayMessage(Constants.LOGINNAMEMSG);
            do {
                uniqueLoginName = true;
                line = input.readLine();
                if(line != null && getExistingMember(line.replace(Constants.UPSTREAM, "")) != null){
                    StringBuffer sb = new StringBuffer();
                    sb.append(Constants.DOWNSTREAM);
                    sb.append(Constants.NAMEUNAVAILABLEMSG);
                    sb.append("\n");
                    sb.append(Constants.DOWNSTREAM);
                    sb.append(Constants.DIFFERENTNAMEMSG);
                    output.println(sb.toString());
                    uniqueLoginName = false;
                }
            } while(!uniqueLoginName);
            String loginName = line.replace(Constants.UPSTREAM,"");
            displayMessage(Constants.PASSWORDMSG);
            line = input.readLine();
            if(line != null) {
                String pasword = line.replace(Constants.UPSTREAM, "");
                Member member = new Member(loginName, pasword);
                displayMessage(Constants.FIRSTNAMEMSG);
                line = input.readLine();
                if (line != null) {
                    member.setFirstName(line);
                }
                displayMessage(Constants.LASTNAMEMSG);
                line = input.readLine();
                if(line != null) {
                    member.setLastName(line);
                }
                displayMessage(Constants.EMAILIDMSG);
                line = input.readLine();
                if(line != null) {
                    member.setEmailId(line);
                }
                Data.setMembers(member);
                this.currentMember = member;
                displayWelcomeMessage();
            } else {
                displayMessage(Constants.PROTOCOLERRORMSG);
            }

        } catch(IOException e) {

        }
        return true;
    }

    private void quit() {
        displayMessage(Constants.BYEMSG);
        try {
            this.user.close();
        } catch (IOException e) {

        }
    }

    private void displayMessage(String message) {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.DOWNSTREAM);
        sb.append(message);
        output.println(sb.toString());
    }

    private void addRoom(String roomName) {
        Data.setRooms(new Room(roomName));
    }

    public Socket getUser() {
        return user;
    }

    public Member getCurrentMember() {
        return currentMember;
    }
}
