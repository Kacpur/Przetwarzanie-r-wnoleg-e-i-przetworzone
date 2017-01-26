
package tak.bo.irc.ircclient.service;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.widget.TextView;

import java.util.Date;

/**
 * A channel or server message
 *
 * @author Michał Wypych
 */
public class Message
{
    public static final int COLOR_GREEN   = 0xFF4caf50;
    public static final int COLOR_RED     = 0xFFf44336;
    public static final int COLOR_BLUE    = 0xFF3f51b5;
    public static final int COLOR_YELLOW  = 0xFFffc107;
    public static final int COLOR_GREY    = 0xFF607d8b;
    public static final int COLOR_DEFAULT = 0xFF212121;

    public static final int TYPE_MESSAGE = 0;

    public static final int TYPE_MISC    = 1;
    public static final int NO_ICON  = -1;
    public static final int NO_TYPE  = -1;
    public static final int NO_COLOR = -1;

    private final String text;
    private final String sender;
    private SpannableString canvas;
    private long timestamp;

    private int color = NO_COLOR;
    private int type  = NO_ICON;
    private int icon  = NO_TYPE;

    public Message(String text)
    {
        this(text, null, TYPE_MESSAGE);
    }


    public Message(String text, int type)
    {
        this(text, null, type);
    }

    public Message(String text, String sender)
    {
        this(text, sender, TYPE_MESSAGE);
    }

    public Message(String text, String sender, int type)
    {
        this.text = text;
        this.sender = sender;
        this.timestamp = new Date().getTime();
        this.type = type;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public int getIcon()
    {
        return icon;
    }

    public String getText()
    {
        return text;
    }

    public int getType()
    {
        return type;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    private boolean hasSender()
    {
        return sender != null;
    }

}
