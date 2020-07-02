package com.example.yourdesires;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Vibrator;
import android.view.ContextThemeWrapper;
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
import com.example.yourdesires.model.MediaLost;
import com.example.yourdesires.model.MediaLost_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesiresAdapter extends RecyclerView.Adapter<DesiresAdapter.DesiresViewHolder> {
    MainActivity mainActivity = new MainActivity();
    ArrayList<Desires> arrayList;
    Context wrapper;
    private DesiresViewHolder holder;
    private RecyclerView recyclerView;
    public DesiresAdapter (ArrayList<Desires> arrayList){
        this.arrayList = arrayList;
    }

    public class DesiresViewHolder extends RecyclerView.ViewHolder {
        TextView name,tag1,tag2,data;
        ImageView statusIMG,menu,cup_img,cup_audio,cup_bell,cup_video;
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
            cup_img = itemView.findViewById(R.id.cup_img);
            cup_audio = itemView.findViewById(R.id.cup_audio);
            cup_bell = itemView.findViewById(R.id.cup_bell);
            cup_video = itemView.findViewById(R.id.cup_video);
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
        this.holder = holder;
        wrapper = holder.itemView.getContext();             //Берём контекст
        wrapper = getWrapperStyle(wrapper,arrayList.get(position).getLight());      //Модернизируем этот же контекст
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
                mainActivity.addLogBD("open",mainActivity.createNewIdLog(),mainActivity.getNumEdit(holder.name.getText().toString()),holder.name.getText().toString());
                context.startActivity(in);
                ((Activity)context).finish();
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
                PopupMenu popupMenu = new PopupMenu(wrapper,v);
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
                                Lost Lost = SQLite.select()
                                        .from(Lost.class)
                                        .where(Lost_Table.desires.is(holder.name.getText().toString()))
                                        .querySingle();
                                int id = Lost.getId();          //Определяем id желания и дальше удаляем
                                SQLite.delete(Lost.class)           //Удаление в основной таблице
                                        .where(Lost_Table.desires.is(String.valueOf(arrayList.get(position).getName())))
                                        .execute();
                                arrayList.remove(position);
                                if(arrayList.size() == 0)
                                    recyclerView.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                                    SQLite.delete(MediaLost.class)   //Удаление в медиа таблице
                                            .where(MediaLost_Table.id_desires.is(id))
                                            .execute();
                                File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+holder.name.getText().toString()+"/" );
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mainActivity.addLogBD("delete",mainActivity.createNewIdLog(),mainActivity.getNumEdit(holder.name.getText().toString()),holder.name.getText().toString());
                                bool = false;
                                break;
                        }
                        if(bool)
                            mainActivity.addLogBD("update status",mainActivity.createNewIdLog(),mainActivity.getNumEdit(holder.name.getText().toString())+1,holder.name.getText().toString());
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
            holder.cup_img.setImageResource(R.drawable.photo);
            holder.cup_audio.setImageResource(R.drawable.music);
            holder.cup_video.setImageResource(R.drawable.video);
            holder.cup_bell.setImageResource(R.drawable.bell);


        }else{
            holder.lin.setBackgroundResource(R.drawable.maket_back_recycler_view_dark);
            holder.menu.setImageResource(R.drawable.open_menu_light);
            holder.name.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tag1.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white_text));
            holder.tag2.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white_text));
            holder.data.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.cup_img.setImageResource(R.drawable.photo_light);
            holder.cup_audio.setImageResource(R.drawable.music_light);
            holder.cup_video.setImageResource(R.drawable.video_light);
            holder.cup_bell.setImageResource(R.drawable.bell_light);
        }
        visibleCups(holder.cup_img,holder.cup_audio,holder.cup_bell,holder.cup_video);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private Context getWrapperStyle (Context wrapper,boolean light){
        if(light){
            wrapper = new ContextThemeWrapper(wrapper,R.style.Pop_menu_light);
        }else{
            wrapper = new ContextThemeWrapper(wrapper,R.style.Pop_menu_dark);
        }
        return wrapper;
    }
    private void visibleCups (ImageView img,ImageView audio,ImageView bell,ImageView video){
        if(getCupInfo("img"))
            img.setVisibility(View.VISIBLE);
        else
            img.setVisibility(View.GONE);
        if(getCupInfo("audio"))
            audio.setVisibility(View.VISIBLE);
        else
            audio.setVisibility(View.GONE);
        if(getCupInfo("video"))
            video.setVisibility(View.VISIBLE);
        else
            video.setVisibility(View.GONE);
        if(getCupInfo("bell"))
            bell.setVisibility(View.VISIBLE);
        else
            bell.setVisibility(View.GONE);
    }
    private boolean getCupInfo (String cup){
        List<MediaLost> mediaLosts = SQLite.select()
                .from(MediaLost.class)
                .queryList();
        //Вычесляем id желания
        Lost lost = SQLite.select()
                .from(Lost.class)
                .where(Lost_Table.desires.is(holder.name.getText().toString()))
                .querySingle();
        int id = lost.getId();      //id желания
        switch (cup){
            case "img":
                List<MediaLost> mediaLostImg = SQLite.select()
                        .from(MediaLost.class)
                        .where(MediaLost_Table.id_desires.is(id))
                        .and(MediaLost_Table.type.is("img"))
                        .queryList();
                if(mediaLostImg.size() != 0 && getNumThisMediaFolder("img") != 0)
                    return true;
                else
                    return false;
            case "audio":
                List<MediaLost> mediaLostAudio = SQLite.select()
                        .from(MediaLost.class)
                        .where(MediaLost_Table.id_desires.is(id))
                        .and(MediaLost_Table.type.is("audio"))
                        .queryList();
                if(mediaLostAudio.size() != 0 && getNumThisMediaFolder("audio") != 0)
                    return true;
                else
                    return false;
            case "video":
                List<MediaLost> mediaLostVideo = SQLite.select()
                        .from(MediaLost.class)
                        .where(MediaLost_Table.id_desires.is(id))
                        .and(MediaLost_Table.type.is("video"))
                        .queryList();
                if(mediaLostVideo.size() != 0 && getNumThisMediaFolder("video") != 0)
                    return true;
                else
                    return false;

        }
        return false;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private int getNumThisMediaFolder (String mode){
        int num = 0;
        File fileDir = new File(Environment.getExternalStorageDirectory()+ "/Desires/"+holder.name.getText().toString()); //Папка желания

        if(fileDir.listFiles().length == 0 && fileDir.listFiles() == null)     //Если папка пуста возращяем 0
            return 0;
        File[] listFile = fileDir.listFiles();
        switch (mode){
            case "img":
                for (int i = 0;i<listFile.length;i++){
                    String nameFile = listFile[i].getName();        //Берём 1 имя файла и
                    if(nameFile.contains(".jpg")){                   //Находим в нём .jpg в
                        num++;
                    }
                }
                break;
            case "audio":
                for (int i = 0;i<listFile.length;i++){
                    String nameFile = listFile[i].getName();
                    if(nameFile.contains(".mp3")){
                        num++;
                    }
                }
                break;
            case "video":
                for (int i = 0;i<listFile.length;i++){
                    String nameFile = listFile[i].getName();
                    if(nameFile.contains(".mp4")){
                        num++;
                    }
                }
                break;

        }
        return num;
    }

}
