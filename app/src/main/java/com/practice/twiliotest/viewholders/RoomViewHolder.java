package com.practice.twiliotest.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.practice.twiliotest.R;

public class RoomViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    public RoomViewHolder(View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.tv);
    }

    public TextView getTv() {
        return tv;
    }
}
