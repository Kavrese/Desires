package com.example.yourdesires;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class DesiresAdapter extends RecyclerView.Adapter<DesiresAdapter.DesiresViewHolder> {
    ArrayList<Desires> arrayList;

    public DesiresAdapter (ArrayList<Desires> arrayList){
        this.arrayList = arrayList;
    }

    public class DesiresViewHolder extends RecyclerView.ViewHolder {
        TextView name,tag1,tag2,data;
        ImageView statusIMG,menu;
        public DesiresViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_desires);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            statusIMG = itemView.findViewById(R.id.status);
            data = itemView.findViewById(R.id.data);
            menu = itemView.findViewById(R.id.menu);
        }
    }

    @NonNull
    @Override
    public DesiresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maket_recycler_view,parent,false);
        return new DesiresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DesiresViewHolder holder, final int position) {
        holder.name.setText(arrayList.get(position).getName());
        holder.tag1.setText(arrayList.get(position).getTag1());
        holder.data.setText(arrayList.get(position).getData());
        if(arrayList.get(position).getTag2().equals("no")){
            holder.tag2.setVisibility(View.INVISIBLE);
        }else{
            holder.tag2.setText(arrayList.get(position).getTag2());
        }
        switch (arrayList.get(position).getStatus()){
            case 1:
                holder.statusIMG.setImageResource(R.color.yellow);
                break;
            case 2:
                holder.statusIMG.setImageResource(R.color.green);
                break;
            case 3:
                holder.statusIMG.setImageResource(R.color.red);
                break;
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context con = v.getContext();
                PopupMenu popupMenu = new PopupMenu(con,v);
                popupMenu.inflate(R.menu.status_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()) {
                            case R.id.menu_work:
                                holder.statusIMG.setImageResource(R.color.yellow);
                                bool = true;
                                break;
                            case R.id.menu_great:
                                holder.statusIMG.setImageResource(R.color.green);
                                bool = true;
                                break;
                            case R.id.menu_sleep:
                                holder.statusIMG.setImageResource(R.color.red);
                                bool = true;
                                break;
                            case R.id.menu_delete:
                                arrayList.remove(position);
                                bool = true;
                                break;
                        }
                        return bool;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
