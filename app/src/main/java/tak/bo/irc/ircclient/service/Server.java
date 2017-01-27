package tak.bo.irc.ircclient.service;

/**
 * Created by napior on 04.12.16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import tak.bo.irc.ircclient.ChatActivity;
import tak.bo.irc.ircclient.service.message.ServMessage;
import tak.bo.irc.ircclient.service.server.Channel;
import tak.bo.irc.ircclient.service.server.Client;
import tak.bo.irc.ircclient.service.server.ConnectionState;
import tak.bo.irc.ircclient.service.utils.CaseInsensitiveMap;
import tak.bo.irc.ircclient.service.utils.VarMap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


public class Server extends VarMap implements Runnable {
    private Selector selector;
    private Map<SocketChannel, List> dataMapper;
    private InetSocketAddress listenAddress;
    private InetAddress host;
    private List<String> motd;
    private String nick;
    private static Context context;


    public static final String VERSION = "0.0.1";

    private Map<String, Channel> channels;
    private List<Client> clients;

    private ClientReceiver clientReceiver;

//    public static void main(String[] args) throws Exception {
//        Server server = new Server("localhost", 8090);
//        new MsgHandler(server);
//        new Thread(server).start();
//
//    }

    public Server(String address, int port, String nick, Context context) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        this.nick = nick;
        this.context = context;
        dataMapper = new HashMap<SocketChannel, List>();
        channels = Collections.synchronizedMap(new CaseInsensitiveMap<String, Channel>());
        host = InetAddress.getLocalHost();
        clients = Collections.synchronizedList(new ArrayList<Client>());
        //TODO Zmienic
        motd = new ArrayList<>();
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");
        motd.add("TEST");

        putString("sName", "michal-napior");
        putInteger("sMaxConns", 1028);
        putInteger("cMaxConns", 10);
        putInteger("cPingTime", 600);
        putInteger("cIdentTime", 300);

        initReceiver();
        Client c = new Client(nick);
        c.setState(ConnectionState.CONNECTED_AS_CLIENT);
        getClients().add(c);
    }

    // create server channel
    private void startServer() throws IOException {
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // retrieve server socket and bind to port
        serverChannel.socket().bind(this.listenAddress);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started...");
        Channel channel1 = getChannel(ChatActivity.KANAL);

        if (channel1 == null) {
            channel1 = new Channel(ChatActivity.KANAL, "", "");
            getChannels().put(ChatActivity.KANAL, channel1);
        }
        channel1.clientJoin(clients.get(0));

        while (true) {
            // wait for events
            this.selector.select();

            //work on selected keys
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                // this is necessary to prevent the same key from coming up
                // again the next time around.
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    this.accept(key);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        String msg = MsgHandler.getInstance().read(channel);
                        MsgHandler.getInstance().acceptNewClient(channel);
                        MsgHandler.getInstance().handdleMessage(msg, channel);
                        MsgHandler.getInstance().acceptNewClient(channel);
                    } catch (Exception e){
                        key.cancel();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //accept a connection made to this channel's socket
    private SocketChannel accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        // register channel with selector for further IO
        dataMapper.put(channel, new ArrayList());
        channel.register(this.selector, SelectionKey.OP_READ);
        return channel;
    }

    @Override
    public void run() {
        try {
            this.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<SocketChannel, List> getDataMapper() {
        return dataMapper;
    }

    public Selector getSelector() {
        return selector;
    }

    public InetAddress getHost() {
        return host;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public synchronized Channel getChannel(String key) {
        return channels.get(key);
    }

    public synchronized Client getClient(String key) {
        for (Client client : clients){
            if(client.getNick().equals(key)){
                return client;
            }
        }
        return null;
    }

    public synchronized List<Client> getClients() {
        return clients;
    }

    public List<String> getMotd() {
        return motd;
    }

    public void setMotd(List<String> motd) {
        this.motd = motd;
    }

    public synchronized Client getClientByChannel(SocketChannel channel) {
        for (Client client : clients){
            if(!client.isSever() && client.getChannel().equals(channel)){
                return client;
            } else if(channel == null && client.isSever()){
                return client;
            }
        }
        return null;
    }

    public String getNick() {
        return nick;
    }

    public static void sendBroadcast(Client client, ServMessage message) {
        Intent intent = new Intent();
        intent.setAction(ChatActivity.ServerReceiver.ACTION_NEW_MSG);
        intent.putExtra("COMMAND", message.getCommand());
//        intent.putExtra("WHO", message);
        intent.putExtra("PREFIX", message.getPrefix());
        intent.putExtra("PARAMS", (ArrayList<String>)message.getParameters());
        context.sendBroadcast(intent);
    }

    private void initReceiver() {
        clientReceiver = new Server.ClientReceiver();
        IntentFilter filter = new IntentFilter(ClientReceiver.ACTION_NEW_MSG);
        context.registerReceiver(clientReceiver, filter);
    }

    public class ClientReceiver extends BroadcastReceiver {

        public static final String ACTION_NEW_MSG = "tak.bo.irc.ircclient.Client";

        public ClientReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_NEW_MSG)) {
//                I/System.out: _______________________PRIVMSG Napior2!Manjarouser-c@Smaug-linux #lol, :lol,
//                PRIVMSG #lol :lol
                String command = intent.getStringExtra("COMMAND");
                String privmsg = intent.getStringExtra("PRIVMSG");
                System.out.println("SEND: " + command + " " + ChatActivity.KANAL + " :" + privmsg);
                MsgHandler.getInstance().handdleMessage(command + " " + ChatActivity.KANAL + " :" + privmsg, null);
            }
        }
    }
}