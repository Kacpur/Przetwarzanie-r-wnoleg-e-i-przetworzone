package IRC.bo.tak.server;

import java.util.List;

public class User
{

    private String username;
    private String hostname;
    private String servername;
    private String realname;

    public User(String username, String hostname, String servername, String realname)
    {
        this.username = username;
        this.hostname = hostname;
        this.servername = servername;
        this.realname = realname;
    }

    public User(List<String> parameters)
    {
        if (parameters.size() >= 1)
        {
            username = parameters.get(0);
        }
        else
        {
            username = "";
        }

        if (parameters.size() >= 2)
        {
            hostname = parameters.get(1);
        }
        else
        {
            hostname = "";
        }

        if (parameters.size() >= 3)
        {
            servername = parameters.get(2);
        }
        else
        {
            servername = "";
        }

        if (parameters.size() >= 4)
        {
            realname = parameters.get(3);
        }
        else
        {
            realname = "";
        }
    }

    @Override
    public String toString()
    {
        return username + "@" + hostname + " " + servername + " :" + realname;
    }

    public void setUserName(String username)
    {
        this.username = username;
    }

    public void setHostName(String hostname)
    {
        this.hostname = hostname;
    }

    public void setServerName(String servername)
    {
        this.servername = servername;
    }

    public void setRealName(String realname)
    {
        this.realname = realname;
    }

    public String getUserName()
    {
        return username;
    }

    public String getHostName()
    {
        return hostname;
    }

    public String getServerName()
    {
        return servername;
    }

    public String getRealName()
    {
        return realname;
    }

}
