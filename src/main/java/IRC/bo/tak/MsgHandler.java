package IRC.bo.tak;

import IRC.bo.tak.message.CliMessage;
import IRC.bo.tak.server.*;
import IRC.bo.tak.message.ServMessage;
import IRC.bo.tak.utils.CLILogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by michal on 08.12.16.
 */
public class MsgHandler {

    private Server server;

    private static MsgHandler msgHandler;

    public MsgHandler(Server server) {
        this.server = server;
        if (msgHandler == null){
            msgHandler = this;
        }
    }

    public static MsgHandler getInstance(){
        return msgHandler;
    }

    public String read(SelectionKey key){
        String temp = null;
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int numRead = -1;
            numRead = channel.read(buffer);
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            temp= new String(data);
            System.out.println("Got: " + temp);
            channel.register(server.getSelector(), SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void write(SelectionKey key, String msg) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate (1024);
            prepWriteBuffer(msg,buffer);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.rewind();
            channel.register(server.getSelector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepWriteBuffer(String mesg, ByteBuffer writeBuffer) {
        writeBuffer.clear();
        writeBuffer.put(mesg.getBytes());
        writeBuffer.putChar('\n');
        writeBuffer.flip();
    }

    public void write(SelectionKey key, ServMessage message){
        write(key,message.toString());
    }

    public Server getServer() {
        return server;
    }

    public void handdleMessage(String msg, SelectionKey key){
        /* Read input from client */
        List<String> input_data = new ArrayList<String>();
        Client client = (Client) key.attachment();
        assert client != null;


        Collections.addAll(input_data, msg.split(" "));

        /* Return if input data was empty */
        if (input_data.isEmpty())
        {
            return;
        }

        /* Log the input to console */
//        CLILogger.LOG("Input from %s: %s", client.getNick(), msg);

        /* Parse input and handle it appropriately */
        for (String l : input_data)
        {
            CliMessage message = new CliMessage(l);
            String privmsg = (l.split("\\s+", 3).length >= 3) ? l.split("\\s+", 3)[2] : null;
            String prefix = message.getPrefix();
            String command = message.getCommand();
            List<String> params = message.getParameters();

            switch (command)
            {
                case "GET":
                    client.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", client.getNick(), "*** Detected that the connection was made using a browser, disconnected."));
                    client.setState(ConnectionState.DISCONNECTED);
                    break;
                case "NICK":
                    if (message.getParameters().size() > 0)
                    {
                        client.setNick(message.getParameter(0));
                    }
                    break;
                case "USER":
                    if (message.getParameters().size() > 0)
                    {
                        client.setUser(new User(message.getParameters()));
                    }
                    break;
                case "SERVER":
                    if (message.getParameters().size() > 0)
                    {
                        CLILogger.LOG("Wywolano metode SERWER");
                        //m_server = new ServerInfo(message.getParameters());
                    }
                    break;
                case "PASS":
                    if (message.getParameters().size() > 0)
                    {
                        client.setPass(message.getParameter(0));
                    }
                    break;

                case "PONG":
                    client.setPingTimer(100);
                    break;
                case "JOIN":
                    if (!params.isEmpty())
                    {
                        for (String p : params)
                        {
                            Channel channel = getServer().getChannel(p);

                            if (channel == null)
                            {
                                channel = new Channel( p, "", "");
                                getServer().getChannels().put(p, channel);
                            }

                            channel.clientJoin(client);
                        }
                    }
                    else
                    {
                        client.sendMsgAndFlush(new ServMessage(getServer(), Commands.ERR_NEEDMOREPARAMS, command, "Not enough parameters."));
                    }
                    break;
                case "PART":
                    if (!params.isEmpty())
                    {
                        for (String p : params)
                        {
                            Channel channel = getServer().getChannel(p);

                            if (channel != null)
                            {
                                channel.clientPart(client, "For an unknown reason.");
                            }
                        }
                    }
                    else
                    {
                        client.sendMsgAndFlush(new ServMessage(getServer(), Commands.ERR_NEEDMOREPARAMS, command, "Not enough parameters."));
                    }
                    break;
                case "PRIVMSG":
                case "NOTICE":
                    if (!params.isEmpty() || privmsg == null)
                    {
                        if (params.get(0).startsWith("#"))
                        {
                            Channel channel = client.getChannel(params.get(0));

                            if (channel != null)
                            {
                                channel.sendMsgAndFlush(client, new ServMessage(getServer(), command, params.get(0), privmsg));
                            }
                        }
                        else
                        {
                            Client c = getServer().getClient(params.get(0));

                            if (c != null)
                            {
                                c.sendMsgAndFlush(new ServMessage(client, command, params.get(0), privmsg));
                            }
                        }
                    }
                    else
                    {
                        client.sendMsgAndFlush(new ServMessage(client, Commands.ERR_NEEDMOREPARAMS, command, "Not enough parameters."));
                    }
                    break;
                case "WHOIS":
                    if (!params.isEmpty())
                    {
                        for (String p : params)
                        {
                            Client c = getServer().getClient(p);

                            if (c != null)
                            {
                                User u = c.getUser();
                                client.sendMsg(new ServMessage(getServer(), Commands.RPL_WHOISUSER, client.getNick(), c.getNick(), u.getUserName(), u.getHostName(), "*", u.getRealName() + " "));
                                client.sendMsgAndFlush(new ServMessage(getServer(), Commands.RPL_ENDOFWHOIS, client.getNick(), c.getNick(), "End of /WHOIS list."));
                            }
                            else
                            {
                                client.sendMsgAndFlush(new ServMessage(getServer(), Commands.ERR_NOSUCHNICK, client.getNick(), p, "No such nick."));
                            }
                        }
                    }
                    break;
                case "TOPIC":
                    if (!params.isEmpty())
                    {
                        Channel channel = getServer().getChannel(params.get(0));
                        String topic = privmsg;

                        if (channel != null && !topic.isEmpty())
                        {
                            channel.setTopic(client, topic);
                        }
                    }
                    break;
                case "QUIT":
                    client.quitChannels(message.getParameter(0));
                    client.setState(ConnectionState.DISCONNECTED);
                    break;
            }
        }

        if (!client.getNick().equals("*"))
        {
            /* Nick length must be in-between 1 and 9 characters */
            if (client.getNick().length() < 1 || client.getNick().length() > 9)
            {
                client.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", client.getNick(), "*** NICK length must be in-between 1 and 9 characters. Disconnecting."));
                client.setState(ConnectionState.DISCONNECTED);
                return;
            }

            /* Nick must not exist on the server */
            if (getServer().getClients().containsKey(client.getNick()))
            {
                client.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", client.getNick(), "*** NICK already exists on this server. Disconnecting."));
                client.setState(ConnectionState.DISCONNECTED);
                return;
            }

            /* Accept the connection as identified client */
            client.setState(ConnectionState.IDENTIFIED_AS_CLIENT);
        }
    }

    public void acceptNewClient(SelectionKey key){

        Client c = (Client) key.attachment();
        if(c == null) {
            c = new Client(key);
            key.attach(c);
        }

        if (c.getState() == ConnectionState.UNIDENTIFIED)
        {
            if (c.getIdentTime() == -1)
                c.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", c.getNick(), "*** Checking ident..."));

            String msg = read(key);
            handdleMessage(msg,key);

                    /* Wait for x seconds before disconnecting the connection */
            if (c.getIdentTime() > getServer().getInteger("cIdentTime"))
            {
                c.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", c.getNick(), "*** Failed to identify the connection, disconnected."));
                c.setState(ConnectionState.DISCONNECTED);
            }

            c.setIdentTime(c.getIdentTime() + 1);
        }

                /* Handle identified client connections */
        if (c.getState() == ConnectionState.IDENTIFIED_AS_CLIENT)
        {
                    /* Add the connection as a client and inform them for the success */
            c.sendMsgAndFlush(new ServMessage(getServer(), "NOTICE", c.getNick(), "*** Found your ident, identified as a client."));
            c.setState(ConnectionState.CONNECTED_AS_CLIENT);
            c.getUser().setHostName(c.getHost().getHostName());

                    /* Send some info about the server */
            c.sendMsg(new ServMessage(getServer(), "001", c.getNick(), "Welcome to the " + getServer().getString("sName") + " IRC network, " + c.getNick()));
            c.sendMsg(new ServMessage(getServer(), "002", c.getNick(), "Your host is " + c.getHost().getHostName() + ", running version mirage-ircd-" + Server.VERSION));
            c.sendMsg(new ServMessage(getServer(), "003", c.getNick(), "This server was created on " + getServer().getString("sCreationDate")));
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_LUSERCLIENT, c.getNick(), "There are " + server.getClients().size() + " users and 0 invisible on 1 server."));
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_LUSEROP, c.getNick(), "0", "IRC Operators online."));
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_LUSERUNKNOWN, c.getNick(), "0", "Unknown connections."));
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_LUSERCHANNELS, c.getNick(), Integer.toString(getServer().getChannels().size()), "Channels formed."));
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_LUSERME, c.getNick(), "I have " + server.getClients().size() + " clients and " + 1 + " servers."));

                    /* Send MOTD to the client */
            c.sendMsg(new ServMessage(getServer(), Commands.RPL_MOTDSTART, c.getNick(), "- Message of the day -"));

            for (String s : getServer().getMotd())
            {
                c.sendMsg(new ServMessage(getServer(), Commands.RPL_MOTD, c.getNick(), "- " + s));
            }

            c.sendMsgAndFlush(new ServMessage(getServer(), Commands.RPL_ENDOFMOTD, c.getNick(), "End of /MOTD command."));
        }
                /* Handle connected client connections */
        if (c.getState() == ConnectionState.CONNECTED_AS_CLIENT)
        {

                    /* Send a PING request between intervals */
            int pingtime = getServer().getInteger("cPingTime");
            if (c.getPingTimer() >= pingtime && c.getPingTimer() % (pingtime / 10) == 0)
            {
                c.sendMsgAndFlush(new ServMessage("", "PING", c.getNick()));
            }

            String msg =read(key);
            handdleMessage(msg,key);

                    /* Disconnect if it didn't respond to the PING request given enough time */
            if (c.getPingTimer() > (int) (pingtime * 1.5))
            {
                c.setState(ConnectionState.DISCONNECTED);
            }

            c.setPingTimer(c.getPingTimer() + 1);
        }

        if (c.getState() == ConnectionState.DISCONNECTED)
        {

                    /* Is it a client? */
            if (c != null)
            {
                c.quitChannels("Connection reset by peer...");
            }


            CLILogger.LOG(c + " Has disconnected.");
        }
    }
}
