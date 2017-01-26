package tak.bo.irc.ircclient;

import android.content.Context;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * Created by napior on 25.01.17.
 */

public class MsgView{
    private TextView msg;
    private GridLayout form;

    public MsgView(Context context, String nickName, String msg){
        this.msg = new TextView(context);
        this.form = new GridLayout(context);
        this.msg.setText(nickName + ": " + msg);
        this.form.addView(this.msg);
    }

    public GridLayout getForm() {
        return form;
    }
}
