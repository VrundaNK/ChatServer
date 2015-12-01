package com.company;

/**
 * Created by vnagpurkar on 11/30/2015.
 */
public class Constants {

    public static final String DOWNSTREAM = "<= ";
    public static final String UPSTREAM = "=> ";
    public static final String COMMANDPREFIX = "=> /";
    public static final String STARPREFIX = " * ";

    public static final String CURRENTUSERMEG = " (** this is you)";
    public static final String ENDOFLISTMSG = "end of list.";
    public static final String WELCOMEMSG = "Welcome to the XYZ chat server";
    public static final String NEWUSERMSG = "New User? (Y/N)";
    public static final String PINGERRORMEG = "ping using a valid username";
    public static final String CHATROOMERRORMSG = "You have not joined any chat room";
    public static final String PRIVATEMSGERRORMSG = "You are not in private conversation";
    public static final String PROTOCOLERRORMSG = "Please enter valid text ";
    public static final String LOGINFAILEDMSG = "login failed. Please try again.";
    public static final String NOCONVERSATIONMSG = "No conversation is in progress!";
    public static final String LEAVECHATROOMMSG = "user has left the chat:";
    public static final String USEROFFLINEMSG = " is offline";
    public static final String ENTERINGCHATROOMMSG = "entering room: ";
    public static final String NEWUSERINCHATMSG = "new user joined the chat:";
    public static final String ALREADYINCHATMSG = "Already in chat room ";
    public static final String CHATROOMINPROGRSSMSG = "Please leave this chat room before joining the other room.";
    public static final String LOGINNAMEMSG = "Login Name?";
    public static final String PASSWORDMSG = "Password?";
    public static final String FIRSTNAMEMSG = "First Name: ";
    public static final String LASTNAMEMSG = "Last Name: ";
    public static final String EMAILIDMSG = "Email Id ";
    public static final String BYEMSG = "BYE";
    public static final String WRONGPASSWORDMSG = "Password does not match!";
    public static final String WRONGLOGINNAMEMSG = "This loginName does not exist.";
    public static final String WELCOMEUSERMSG = "Welcome ";
    public static final String NAMEUNAVAILABLEMSG = "Sorry, name taken.";
    public static final String DIFFERENTNAMEMSG = "Please enter different name";
    public static final String NEWROOMMSG = "New room (%s) is added.";
    public static final String VALIDROOMNAMEMSG = "Please enter room name";

    // usage constants
    public static final String USAGEMSG = "Displays use of various commands";
    public static final String HELPMSG = "/help - displays the usage";
    public static final String ROOMSMSG = "/rooms - displays list of rooms";
    public static final String JOINMSG = "/join <chatRoomName> - joins the room";
    public static final String ADDROOMMSG = "addRoom <roomName> - adds new room";
    public static final String LEAVEMSG = "/leave - leaves current chat room";
    public static final String QUITMSG = "/quit - quits the chatserver";
    public static final String PINGMSG = "/ping <userName> - pings user";
    public static final String EXITMSG = "/exit - exits the current conversation";
}
