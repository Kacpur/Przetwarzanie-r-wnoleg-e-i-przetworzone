package tak.bo.irc.ircclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public static final String NICK = "NICK";
    public static final String ADRESS = "ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void login(View v) {
        EditText nick = (EditText) findViewById(R.id.editTextNick);
        EditText address = (EditText) findViewById(R.id.editTextAdress);
        Intent chat = new Intent(this, ChatActivity.class);
//        startActivity(new Intent(this, ChatActivity.class));
        chat.putExtra(LoginActivity.NICK, nick.getText().toString());
        chat.putExtra(LoginActivity.ADRESS, address.getText().toString());
        startActivity(chat);
    }

}
