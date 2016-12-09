package IRC.bo.tak.server;

import IRC.bo.tak.MsgHandler;
import IRC.bo.tak.message.ServMessage;
import IRC.bo.tak.utils.CaseInsensitiveMap;

import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Client
{
    private SelectionKey key;
    private Map<String, Channel> channels;
    private int pingtimer;
    private ConnectionState state;

    private String nick;
    private User user;
    private InetAddress host;
    private String pass;
    private int identTime;

    public Client(SelectionKey key)
    {
        this.key = key;
        channels = Collections.synchronizedMap(new CaseInsensitiveMap<>());
        pingtimer = 100;
        nick = "*";
        user = new User("*", "0", "*", "");
        pass = "";
        state = ConnectionState.UNIDENTIFIED;
        identTime = -1;
    }


    public void sendMsg(ServMessage message)
    {
        MsgHandler.getInstance().write(key, message);
    }

    public void sendMsgAndFlush(ServMessage message)
    {
        MsgHandler.getInstance().write(key, message);
    }

    public void sendMsgToChans(ServMessage message)
    {
        Iterator<Entry<String, Channel>> i = channels.entrySet().iterator();

        while (i.hasNext())
        {
            Channel c = (Channel) i.next();
            c.sendMsg(message);
        }
    }

    public void sendMsgToChansAndFlush(ServMessage message)
    {
        Iterator<Entry<String, Channel>> i = channels.entrySet().iterator();

        while (i.hasNext())
        {
            Channel c = (Channel) i.next();
            c.sendMsgAndFlush(message);
        }
    }

    public void quitChannels(String reason)
    {
        Iterator<Entry<String, Channel>> i = channels.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Channel> e = i.next();
            Channel channel = (Channel) e.getValue();
            channel.clientQuit(this, reason);
        }
    }

    public void quitChannel(Channel channel, String reason)
    {
        if (channels.get(channel.getName()) != null)
        {
            channel.clientQuit(this, reason);
        }
    }

    public void addChannel(Channel channel)
    {
        channels.put(channel.getName(), channel);
    }

    public void removeChannels()
    {
        channels.clear();
    }

    public void removeChannel(Channel channel)
    {
        channels.remove(channel.getName());
    }

    public void setPingTimer(int pingtimer)
    {
        this.pingtimer = pingtimer;
    }

    public Map<String, Channel> getChannels()
    {
        return channels;
    }

    public Channel getChannel(String key)
    {
        return channels.get(key);
    }

    public int getPingTimer()
    {
        return pingtimer;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public ConnectionState getState() {
        return state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getIdentTime() {
        return identTime;
    }

    public void setIdentTime(int identTime) {
        this.identTime = identTime;
    }
}