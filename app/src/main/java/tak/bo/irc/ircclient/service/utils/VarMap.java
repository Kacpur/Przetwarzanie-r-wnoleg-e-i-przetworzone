package tak.bo.irc.ircclient.service.utils;

import java.util.HashMap;
import java.util.Map;

public class VarMap
{

    private Map<String, String> strings;
    private Map<String, Integer> integers;
    private Map<String, Float> floats;

    public VarMap()
    {
        strings = new HashMap<String, String>();
        integers = new HashMap<String, Integer>();
        floats = new HashMap<String, Float>();
    }

    public void putString(String key, String s)
    {
        strings.put(key, s);
    }

    public void putInteger(String key, int i)
    {
        integers.put(key, i);
    }

    public void putFloat(String key, float f)
    {
        floats.put(key, f);
    }

    public String getString(String key)
    {
        String result = strings.get(key);

        if (result == null)
        {
            result = "";
        }

        return result;
    }

    public int getInteger(String key)
    {
        Integer result = integers.get(key);

        if (result == null)
        {
            result = -1;
        }

        return result;
    }

    public float getFloat(String key)
    {
        Float result = floats.get(key);

        if (result == null)
        {
            result = -1.0f;
        }

        return result;
    }

}
