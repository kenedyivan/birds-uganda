package com.hamsoftug.birduganda;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hamsoftug.birduganda.adapters.ChatsAdapter;
import com.hamsoftug.birduganda.models.Others;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Chat extends AppCompatActivity {

    private EditText new_msg;
    private ImageView send_msg;
    private ProgressBar sending_msg;
    private HTTPclass httpRequest = null;
    private String server_message, server_status, user_number="", user_pw="", user_email="";
    private sendMsgSyc sendMsgSyc = null;
    private SQLDatabaseHelper sqlDatabaseHelper = null;
    private getMsgSyc getMsgSyc = null;
    private ArrayList<String> chat_user = new ArrayList<>();
    private ArrayList<String> chat_msg = new ArrayList<>();
    private ArrayList<String> chat_seen = new ArrayList<>();
    private ArrayList<String> chat_time = new ArrayList<>();
    private Others others = null;
    private boolean netconn = false;

    private ListView chats_list = null;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sqlDatabaseHelper = new SQLDatabaseHelper(Chat.this);
        others = new Others();

        if(sqlDatabaseHelper.getProfile().size()>0){

            user_number = sqlDatabaseHelper.getProfile().get(0).split("--")[0];
            user_email = sqlDatabaseHelper.getProfile().get(0).split("--")[1];
            user_pw = sqlDatabaseHelper.getProfile().get(0).split("--")[2];

            others.setUser_num(user_number);

        } else {
            startActivity(new Intent(Chat.this, LoginActivity.class));
            finish();
        }

        httpRequest = new HTTPclass();

        send_msg = (ImageView) findViewById(R.id.send_msg);
        sending_msg = (ProgressBar) findViewById(R.id.sending_progressBar);
        new_msg = (EditText) findViewById(R.id.new_msg);
        chats_list = (ListView) findViewById(R.id.chats_list);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempSend();
            }
        });

        attemptGetMsg();

        final Handler h = new Handler();
        final int delay = 3000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                attemptGetMsg();
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    void attempSend(){
        if(sendMsgSyc != null){
            return;
        }

        String msg = new_msg.getText().toString();

        if(TextUtils.isEmpty(msg)){
            new_msg.setError("Write message");
            new_msg.setFocusable(true);
            return;
        }

        sending_msg.setVisibility(View.VISIBLE);

        sendMsgSyc = new sendMsgSyc(msg);
        sendMsgSyc.execute((Void) null);
    }

    void attemptGetMsg(){
        if(getMsgSyc != null){
            return;
        }

        getMsgSyc = new getMsgSyc();
        getMsgSyc.execute((Void) null);

    }

    class getMsgSyc extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            return doGetMsg();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            getMsgSyc = null;
            if(aBoolean){
                ChatsAdapter chatsAdapter = new ChatsAdapter(Chat.this,chat_msg,chat_user,chat_seen,chat_time);
                chats_list.setAdapter(chatsAdapter);
                if(count<chat_msg.size()){
                    count = chat_msg.size();
                    chats_list.smoothScrollToPosition(chatsAdapter.getCount()-1);
                }
            }

            if(netconn){
                netconn = false;
                Snackbar.make(chats_list,"Network error",Snackbar.LENGTH_LONG).show();
            }

        }
    }

    boolean doGetMsg(){

        HashMap<String,String> params = new HashMap<>();
        params.put("u_pw", user_pw);
        params.put("u_email", user_email);
        params.put("u_num", user_number);

        JSONObject jsonObj = httpRequest.PostRequest(httpRequest.base_url()+"mob/?bd=do_get_msg",params);
        try {
            JSONArray resultsArray = jsonObj.getJSONArray("result");
            int length = resultsArray.length();

            chat_seen.clear();
            chat_user.clear();
            chat_msg.clear();
            chat_time.clear();

            for(int i=0; i<length; i++){
                JSONObject result = resultsArray.getJSONObject(i);
                    /*server_message = result.getString("message");
                    server_status = result.getString("status");*/
                chat_msg.add(result.getString("chat_msg"));
                chat_user.add(result.getString("chat_u_num"));
                chat_time.add(result.getString("chat_date_time"));
                chat_seen.add(result.getString("chat_status"));

            }
            return resultsArray.length()>0;

        } catch (JSONException e) {
            netconn = true;
            e.printStackTrace();

        } catch (Exception e){

            netconn = true;

        }
        return false;
    }

    class sendMsgSyc extends AsyncTask<Void, Void, Boolean>{

        String msg;

        sendMsgSyc(String msg){

            this.msg = msg;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return doSend(msg);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            sendMsgSyc = null;
            sending_msg.setVisibility(View.GONE);

            if(aBoolean){

                if(server_status.trim().equals("1")){
                    new_msg.setText(null);
                    Snackbar.make(new_msg,"Sent", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(new_msg,server_message, Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }

            } else {
                Snackbar.make(new_msg,"Network error", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    boolean doSend(String user_msg){

        HashMap<String,String> params = new HashMap<>();
        params.put("u_name", user_msg);
        params.put("u_num", user_number);

        JSONObject jsonObj = httpRequest.PostRequest(httpRequest.base_url()+"mob/?bd=do_send_msg",params);
        try {
            JSONArray resultsArray = jsonObj.getJSONArray("result");
            for(int i=0; i<resultsArray.length(); i++){
                JSONObject result = resultsArray.getJSONObject(i);
                server_message = result.getString("message");
                server_status = result.getString("status");
            }
            return resultsArray.length()>0;

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e){

        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, FavoriteListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
