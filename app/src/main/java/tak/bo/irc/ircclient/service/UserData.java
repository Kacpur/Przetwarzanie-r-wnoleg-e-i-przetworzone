package tak.bo.irc.ircclient.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * userdata: nickname, an ident and a real name
 * 
 * @author Michał Wypych
 */
public class UserData
{
    private String nickname;
    private final List<String> aliases = new ArrayList<String>();
    private String ident;
    private String realname;

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }


    public String getNickname()
    {
        return nickname;
    }


    public void setAliases(Collection<String> aliases)
    {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    public List<String> getAliases()
    {
        return Collections.unmodifiableList(aliases);
    }

    public void setIdent(String ident)
    {
        this.ident = ident;
    }

    public String getIdent()
    {
        return ident;
    }

    public void setRealName(String realname)
    {
        this.realname = realname;
    }

    public String getRealName()
    {
        return realname;
    }
}
