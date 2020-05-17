package com.example.yourdesires;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

public class DesiresAdapter extends RecyclerView.Adapter<DesiresAdapter.DesiresViewHolder> {
    ArrayList<Desires> arrayList;
    public DesiresAdapter (ArrayList<Desires> arrayList){
        this.arrayList = arrayList;
    }

    public class DesiresViewHolder extends RecyclerView.ViewHolder {
        TextView name,tag1,tag2,data;
        ImageView statusIMG,menu;
        LinearLayout lin;
        public DesiresViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_desires);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            statusIMG = itemView.findViewById(R.id.status);
            data = itemView.findViewById(R.id.data);
            menu = itemView.findViewById(R.id.menu);
            lin = itemView.findViewById(R.id.lin);
        }
    }

    @NonNull
    @Override
    public DesiresViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maket_recycler_view,parent,false);
        final DesiresViewHolder desiresViewHolder = new DesiresViewHolder(view);
        return desiresViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final DesiresViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent in = new Intent(context, DesiresActivity.class);
                in.putExtra("name", holder.name.getText());
                in.putExtra("status", arrayList.get(position).getStatus());
                in.putExtra("position", position);
                in.putExtra("data", arrayList.get(position).getData());
                in.putExtra("op", arrayList.get(position).getOp());
                in.putExtra("tag1", arrayList.get(position).getTag1());
                in.putExtra("tag2", arrayList.get(position).getTag2());
                in.putExtra("search",arrayList.get(position).getSearch());
                in.putExtra("positions",arrayList.get(position).getPosition());
                if (arrayList.get(position).getLight()) {
                    in.putExtra("color", "light");
                } else{
                    in.putExtra("color", "dark");
            }
                context.startActivity(in);
            }
        });
        holder.name.setText(arrayList.get(position).getName());
        holder.tag1.setText(arrayList.get(position).getTag1());
        holder.data.setText(arrayList.get(position).getData());

        if(arrayList.get(position).getTag2().equals("no")){
            holder.tag2.setVisibility(View.INVISIBLE);
        }else{
            holder.tag2.setText(arrayList.get(position).getTag2());
        }

        if(arrayList.get(position).getTag1().equals("no")){
            holder.tag1.setText(arrayList.get(position).getTag2());
            holder.tag2.setVisibility(View.INVISIBLE);
        }else{
            holder.tag1.setText(arrayList.get(position).getTag1());
        }

        switch (arrayList.get(position).getStatus()){
            case 1:
                holder.statusIMG.setImageResource(R.color.yellow);
                break;
            case 2:
                holder.statusIMG.setImageResource(R.color.green);
                break;
            case 3:
                holder.statusIMG.setImageResource(R.color.orange);
                break;
            case 4:
                holder.statusIMG.setImageResource(R.color.red);
                break;
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Context con = v.getContext();
                PopupMenu popupMenu = new PopupMenu(con,v);
                popupMenu.inflate(R.menu.status_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()) {
                            case R.id.menu_work:
                                arrayList.get(position).setStatus(1);
                                holder.statusIMG.setImageResource(R.color.yellow);
                                SQLite.update(Lost.class)
                                        .set(Lost_Table.status.is(1))
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                bool = true;
                                break;
                            case R.id.menu_great:
                                arrayList.get(position).setStatus(2);
                                holder.statusIMG.setImageResource(R.color.green);
                                SQLite.update(Lost.class)
                                        .set(Lost_Table.status.is(2))
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                bool = true;
                                break;
                            case R.id.menu_time:
                                arrayList.get(position).setStatus(3);
                                holder.statusIMG.setImageResource(R.color.orange);
                                SQLite.update(Lost.class)
                                        .set(Lost_Table.status.is(3))
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                bool = true;
                                break;
                            case R.id.menu_sleep:
                                holder.statusIMG.setImageResource(R.color.red);
                                arrayList.get(position).setStatus(4);
                                SQLite.update(Lost.class)
                                        .set(Lost_Table.status.is(4))
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                bool = true;
                                break;
                            case R.id.menu_delete:
                                SQLite.delete(Lost.class)
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                arrayList.remove(position);
                                notifyDataSetChanged();
                                bool = true;
                                break;
                        }
                        return bool;
                    }
                });
                popupMenu.show();
            }
        });
        if(arrayList.get(position).getLight()){
            holder.lin.setBackgroundResource(R.drawable.maket_back_recycler_view);
            holder.menu.setImageResource(R.drawable.open_menu);
            holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.dark));
            holder.tag1.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.grey));
            holder.tag2.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.grey));
            holder.data.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.dark));
        }else{
            holder.lin.setBackgroundResource(R.drawable.maket_back_recycler_view_dark);
            holder.menu.setImageResource(R.drawable.open_menu_light);
            holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tag1.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white_text));
            holder.tag2.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white_text));
            holder.data.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
