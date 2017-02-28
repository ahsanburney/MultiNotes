package com.ahsanburney.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;



import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Christopher on 1/30/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView mdateTime;
    public TextView mtitle;
    public TextView mcontent;

    public MyViewHolder(View view) {
        super(view);
        mdateTime = (TextView) view.findViewById(R.id.card_note_date);
        mtitle = (TextView) view.findViewById(R.id.card_note_title);
        mcontent = (TextView) view.findViewById(R.id.card_note_content);
    }

}
