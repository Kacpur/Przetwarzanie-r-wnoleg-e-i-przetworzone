package tak.bo.irc.ircclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import tak.bo.irc.ircclient.service.MsgHandler;

public class Server extends Service {
    public Server() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        String nick = intent.getStringExtra(LoginActivity.NICK);
        tak.bo.irc.ircclient.service.Server server = null;
        try {
            server = new tak.bo.irc.ircclient.service.Server(getIp(this), 8090, nick, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new MsgHandler(server);
        new Thread(server).start();
        return START_STICKY;
    }

    private String getIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            ipAddressString = null;
        }

        return ipAddressString;
    }

}
