package com.practice.twiliotest.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.practice.twiliotest.R;
import com.practice.twiliotest.dataclasses.RoomData;
import com.practice.twiliotest.viewholders.RoomViewHolder;

import java.util.ArrayList;

public class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomViewHolder> {
    private ArrayList<RoomData> list = new ArrayList<>();
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_room,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder vh, int position) {
        vh.getTv().setText(list.get(position).getUniqueName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<RoomData> getList() {
        return list;
    }
}
