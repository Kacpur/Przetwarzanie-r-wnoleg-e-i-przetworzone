package tak.bo.irc.ircclient.service.message;

import tak.bo.irc.ircclient.service.MsgHandler;
import tak.bo.irc.ircclient.service.Server;
import tak.bo.irc.ircclient.service.server.Client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServMessage
{

    private String       prefix;
    private String       command;
    private List<String> parameters;

    public ServMessage(String prefix, String command, String... parameters)
    {
        this.prefix = prefix;
        this.command = command;
        this.parameters = new ArrayList<String>(Arrays.asList(parameters));
    }

    public ServMessage(Server server, String command, String... parameters)
    {
        this.prefix = server.getHost().getHostName();
        this.command = command;
        this.parameters = new ArrayList<String>(Arrays.asList(parameters));
    }
    public ServMessage(Client client, String command, String... parameters)
    {
        if(client.isSever()) {
            this.prefix = client.getNick() + "!" + client.getUser().getUserName();
        } else {
            this.prefix = client.getNick() + "!" + client.getUser().getUserName() + "@" + client.getHost().getHostName();
        }
        this.command = command;
        this.parameters = new ArrayList<String>(Arrays.asList(parameters));
    }


    @Override
    public String toString()
    {
        return ((prefix.isEmpty() ? "" : ":") + prefix + " " + command + " " + getParametersAsString()).trim() + "\r\n";
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getCommand()
    {
        return command;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public String getParametersAsString()
    {
        String parameters = "";

        for (String p : this.parameters)
        {
            parameters += (p.contains(" ") && !p.startsWith(":") ? ":" : "") + p + " ";
        }

        return parameters.trim();
    }

}
