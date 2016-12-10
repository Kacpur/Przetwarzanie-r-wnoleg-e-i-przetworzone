package IRC.bo.tak.server;



import IRC.bo.tak.MsgHandler;
import IRC.bo.tak.message.ServMessage;
import IRC.bo.tak.utils.CaseInsensitiveMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Channel
{

    private String name;
    private String key;
    private String topic;
    private ChannelState state;
    private Map<String, Client> clients;

    public Channel( String name, String key, String topic)
    {
        this.name = name;
        this.key = key;
        this.topic = "";
        state = ChannelState.PUBLIC;
        clients = Collections.synchronizedMap(new CaseInsensitiveMap<>());
    }

    public void sendMsg(ServMessage message)
    {
        Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Client> e = i.next();
            Client c = (Client) e.getValue();
            MsgHandler.getInstance().write(c.getChannel(),message);
        }
    }

    public void sendMsgAndFlush(ServMessage message)
    {
        Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Client> e = i.next();
            Client c = (Client) e.getValue();
            MsgHandler.getInstance().write(c.getChannel(), message);
        }
    }

    public void sendMsg(Client client, ServMessage message)
    {
        Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Client> e = i.next();
            Client c = (Client) e.getValue();

            if (c.getNick().equals(client.getNick()))
                continue;

            MsgHandler.getInstance().write(c.getChannel(), message);
        }
    }

    public void sendMsgAndFlush(Client client, ServMessage message)
    {
        Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Client> e = i.next();
            Client c = (Client) e.getValue();

            if (c.getNick().equals(client.getNick()))
                continue;

            MsgHandler.getInstance().write(c.getChannel(), message);
        }
    }

    public void clientJoin(Client client)
    {

        if (client.getChannel(name) == null)
        {
            client.addChannel(this);
            clients.put(client.getNick(), client);
            sendMsgAndFlush(new ServMessage(client, "JOIN", name));

            if (!topic.isEmpty())
            {
                client.sendMsgAndFlush(new ServMessage(MsgHandler.getInstance().getServer(), Commands.RPL_TOPIC, client.getNick(), name, topic));
            }
            else
            {
                client.sendMsgAndFlush(new ServMessage(MsgHandler.getInstance().getServer(), Commands.RPL_NOTOPIC, client.getNick(), name, "No topic is set."));
            }

            client.sendMsgAndFlush(new ServMessage(MsgHandler.getInstance().getServer(), Commands.RPL_NAMREPLY, client.getNick(), "@", name, getNames()));
            client.sendMsgAndFlush(new ServMessage(MsgHandler.getInstance().getServer(), Commands.RPL_ENDOFNAMES, client.getNick(), name, "End of /NAMES list."));
        }
        else
        {
            clientPart(client, "Rejoining this channel...");

            if (client.getChannel(name) == null)
            {
                clientJoin(client);
            }
        }
    }

    public void clientPart(Client client, String reason)
    {

        if (client.getChannel(name) != null)
        {
            sendMsgAndFlush(new ServMessage(client, "PART", name, reason));
            client.removeChannel(this);
            clients.remove(client.getNick());
        }
        else
        {
            client.sendMsgAndFlush(new ServMessage(client, Commands.ERR_USERNOTINCHANNEL, client.getNick(), name, "You are not on that channel."));
        }

        if (clients.isEmpty())
        {
            state = ChannelState.EMPTY;
        }
    }

    public void clientQuit(Client client, String reason)
    {

        if (client.getChannel(name) != null)
        {
            sendMsgAndFlush(new ServMessage(client, "QUIT", reason));
            client.removeChannel(this);
            clients.remove(client.getNick());
        }
        else
        {
            client.sendMsgAndFlush(new ServMessage(client, Commands.ERR_USERNOTINCHANNEL, client.getNick(), name, "Is not on the channel."));
        }
    }

    public void setTopic(Client client, String topic)
    {
        this.topic = topic;

        if (!this.topic.trim().isEmpty())
        {
            Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

            while (i.hasNext())
            {
                Entry<String, Client> e = i.next();
                Client c = (Client) e.getValue();
                c.sendMsgAndFlush(new ServMessage(MsgHandler.getInstance().getServer(), Commands.RPL_TOPIC, c.getNick(), name, this.topic));
            }
        }
        else
        {
            this.topic = "";
        }
    }

    public void setState(ChannelState state)
    {
        this.state = state;
    }

    public String getName()
    {
        return name;
    }

    public String getKey()
    {
        return key;
    }

    public String getTopic()
    {
        return topic;
    }

    public ChannelState getState()
    {
        return state;
    }

    public String getNames()
    {
        String names = "";

        Iterator<Entry<String, Client>> i = clients.entrySet().iterator();

        while (i.hasNext())
        {
            Entry<String, Client> e = i.next();
            Client c = (Client) e.getValue();
            names += c.getNick() + " ";
        }

        return names;
    }

    public Map<String, Client> getClients()
    {
        return clients;
    }

    public Client getClient(String key)
    {
        return clients.get(key);
    }

}
