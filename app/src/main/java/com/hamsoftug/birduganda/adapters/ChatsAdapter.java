package com.hamsoftug.birduganda.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hamsoftug.birduganda.R;
import com.hamsoftug.birduganda.SQLDatabaseHelper;

import java.util.ArrayList;

/**
 * Created by USER on 10/2/2016.
 */

public class ChatsAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> chat_user;
    private final ArrayList<String> chat_msg;
    private final ArrayList<String> chat_seen;
    private final ArrayList<String> chat_time;
    private SQLDatabaseHelper sqlDatabaseHelper = null;

    private final Activity mcontext;

    public ChatsAdapter(Activity context,ArrayList<String> _msg, ArrayList<String> _user_num, ArrayList<String> _seen, ArrayList<String> _time) {
        super(context, R.layout.chatbubble, _msg);
        mcontext = context;
        this.chat_msg = _msg;
        this.chat_seen = _seen;
        this.chat_time = _time;
        this.chat_user = _user_num;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mcontext.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.chatbubble, null, true);
        View cUser = rowView.findViewById(R.id.layout1);
        View oUser = rowView.findViewById(R.id.layout2);

        sqlDatabaseHelper = new SQLDatabaseHelper(mcontext);

        String user_num = chat_user.get(position);
        String c_user = sqlDatabaseHelper.getProfile().get(0).split("--")[0];


        if(user_num.trim().equals(c_user)){

            cUser.setVisibility(View.VISIBLE);
            oUser.setVisibility(View.GONE);

            TextView _message = (TextView) rowView.findViewById(R.id.current_user_msg);
            _message.setText(chat_msg.get(position)+"\n"+chat_time.get(position));


        } else {

            cUser.setVisibility(View.GONE);
            oUser.setVisibility(View.VISIBLE);
            TextView _message = (TextView) rowView.findViewById(R.id.second_user_msg);
            _message.setText(chat_msg.get(position)+"\n"+chat_time.get(position));

        }



        return rowView;

    }
}
