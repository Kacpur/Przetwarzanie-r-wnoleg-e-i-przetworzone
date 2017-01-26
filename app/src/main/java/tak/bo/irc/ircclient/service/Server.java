package tak.bo.irc.ircclient.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * A server as we know it
 * 
 * @author Michał Wypych
 */
public class Server
{
    private int id;
    private String title;
    private String host;
    private int port;
    private String password;
    private String charset;

    private UserData userData;

    private final LinkedHashMap<String, Room> conversations = new LinkedHashMap<String, Room>();
    private ArrayList<String> autoJoinChannels;
    private ArrayList<String> connectCommands;

    private Status status = Status.DISCONNECTED;
    private String selected = "";
    private boolean isForeground = false;
    private boolean mayReconnect = false;

    /**
     * Create a new server object
     */
    public Server()
    {
    }

    /**
     * Set the userData for this server
     * 
     * @param userData The userData for this server
     */
    public void setUserData(UserData userData)
    {
        this.userData = userData;
    }


    /**
     * Get the userData for this server
     * 
     * @return userData
     */
    public UserData getUserData()
    {
        return userData;
    }

    /**
     * Get unique id of server
     * 
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set unique id of server
     * 
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Set password of the server
     * 
     * @param password The password of the server
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Get the password of the server
     * 
     * @return The password of the server
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Get title of server
     * 
     * @return
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set title of server
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Get hostname of server
     * 
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Set hostname of server
     * 
     * @param host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * Get port of server
     * 
     * @return
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Set port of server
     * 
     * @param port
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Set the charset to be used for all messages sent to the server
     * 
     * @param charset The name of the charset
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    /**
     * Get the charset to be used with this server
     * 
     * @return String charset The name of the charset
     */
    public String getCharset()
    {
        return charset;
    }

    /**
     * Set connection status of server
     * 
     * @status See constants Status.*
     */
    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * Get connection status of server
     * 
     * @return See constants Status.*
     */
    public Status getStatus()
    {
        return status;
    }

    /**
     * Set list of channels to auto join after connect
     * 
     * @param autoJoinChannels List of channel names
     */
    public void setAutoJoinChannels(ArrayList<String> autoJoinChannels)
    {
        this.autoJoinChannels = autoJoinChannels;
    }

    /**
     * Get list of channels to auto join after connect
     * 
     * @return List of channel names
     */
    public ArrayList<String> getAutoJoinChannels()
    {
        return autoJoinChannels;
    }

    /**
     * Set commands to execute after connect
     * 
     * @param connectCommands List of commands
     */
    public void setConnectCommands(ArrayList<String> connectCommands)
    {
        this.connectCommands = connectCommands;
    }

    /**
     * Get commands to execute after connect
     * 
     * @return List of commands
     */
    public ArrayList<String> getConnectCommands()
    {
        return connectCommands;
    }

    /**
     * Is disconnected?
     * 
     * @return true if the user is disconnected, false if the user is connected or currently connecting
     */
    public boolean isDisconnected()
    {
        return status == Status.DISCONNECTED;
    }

    /**
     * Is connected?
     * 
     * @return true if the user is (successfully) connected to this server, false otherwise
     */
    public boolean isConnected()
    {
        return status == Status.CONNECTED;
    }

    /**
     * Get all conversations
     * 
     * @return
     */
    public Collection<Room> getRooms()
    {
        return conversations.values();
    }

    /**
     * Get conversation by name
     */
    public Room getRoom(String name)
    {
        return conversations.get(name.toLowerCase());
    }

    /**
     * Add a new conversation
     * 
     * @param conversation The conversation to add
     */
    public void addRoom(Room conversation)
    {
        conversations.put(conversation.getName().toLowerCase(), conversation);
    }

    /**
     * Removes a conversation by name
     * 
     * @param name
     */
    public void removeRoom(String name)
    {
        conversations.remove(name.toLowerCase());
    }


    /**
     * Set name of currently selected conversation
     * 
     * @param selected The name of the selected conversation
     */
    public void setSelectedRoom(String selected)
    {
        this.selected = selected;
    }

    /**
     * Get name of currently selected conversation
     * 
     * @return The name of the selected conversation
     */
    public String getSelectedRoom()
    {
        return selected;
    }

    /**
     * Get names of the currently joined channels
     * 
     * @return
     */
    public ArrayList<String> getCurrentChannelNames()
    {
        ArrayList<String> channels = new ArrayList<String>();
        Collection<Room> mRooms = conversations.values();

        for (Room conversation : mRooms) {
            if (conversation.getType() == Room.TYPE_CHANNEL) {
                channels.add(conversation.getName());
            }
        }

        return channels;
    }

    /**
     * Get whether a RoomActivity for this server is currently in the foreground.
     */
    public boolean getIsForeground()
    {
        return isForeground;
    }

    /**
     * Set whether a RoomActivity for this server is currently in the foreground.
     */
    public void setIsForeground(boolean isForeground)
    {
        this.isForeground = isForeground;
    }

    /**
     * Get whether a reconnect may be attempted if we're disconnected.
     */
    public boolean mayReconnect()
    {
        return mayReconnect;
    }

    /**
     * Set whether a reconnect may be attempted if we're disconnected.
     */
    public void setMayReconnect(boolean mayReconnect)
    {
        this.mayReconnect = mayReconnect;
    }
}
