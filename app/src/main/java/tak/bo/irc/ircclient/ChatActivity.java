package tak.bo.irc.ircclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String nick;
    private String addres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nick = (String) getIntent().getExtras().get(LoginActivity.NICK);
        addres = (String) getIntent().getExtras().get(LoginActivity.ADRESS);

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            userList(null);
        } else if (id == R.id.nav_channals) {
            channelList(null);
        } else if (id == R.id.nav_add) {
            createChannel();
        } else {
            if(item.getIcon().getConstantState().equals(
                    getResources().getDrawable(R.drawable.ic_user).getConstantState())){
                sendPrivateMsg(item.getTitle().toString());
            } else if(item.getIcon().getConstantState().equals(
                    getResources().getDrawable(R.drawable.ic_channel).getConstantState())) {
                dodaj("System", item.getTitle().toString());
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userList(View v) {
        startActivity(new Intent(this, UserListActivity.class));
    }

    public void channelList(View v) {
        startActivity(new Intent(this, ChanneListActivity.class));
    }

    public void wyslij(View v) {
        EditText editText = (EditText) findViewById(R.id.editText2);
        dodaj(this.nick, editText.getText().toString());
        addToNavigation(editText.getText().toString(), true);
    }

    public void dodaj(String nick, String msg) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_chat);
        linearLayout.addView(new MsgView(this, nick, msg).getForm());
    }

    public void addToNavigation(String title, boolean user){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem newItem = navigationView.getMenu().add(title);
        if(user) {
            newItem.setIcon(R.drawable.ic_user);
        } else {
            newItem.setIcon(R.drawable.ic_channel);
        }
    }

    private void createChannel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.addChanell);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                addToNavigation(m_Text,false);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void sendPrivateMsg(final String who){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder title = new StringBuilder()
                .append(getResources().getString(R.string.sendPrivateMsg))
                .append(" ")
                .append(who);
        builder.setTitle(title.toString());

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                dodaj(who,m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
