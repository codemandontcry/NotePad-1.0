package com.zc.memo.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.zc.memo.item.Note;
import com.zc.memo.R;

import java.util.List;

public class NoteAdapter extends MyBaseAdapter<Note> {

    public NoteAdapter(Context context, List<Note> data) {
        super(context, data);
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_main_list, viewGroup, false);
            viewHolder.tv_lv_month = (TextView) view.findViewById(R.id.tv_lv_month);
            viewHolder.tv_lv_day= (TextView) view.findViewById(R.id.tv_lv_day);
            viewHolder.tv_lv_title = (TextView) view.findViewById(R.id.tv_lv_title);
            viewHolder.tv_lv_content = (TextView) view.findViewById(R.id.tv_lv_content);
            viewHolder.layout = (LinearLayout) view.findViewById(R.id.Linelayout);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_lv_month.setText(data.get(i).getUpdate_date().split("-")[1]);
        viewHolder.tv_lv_day.setText(data.get(i).getUpdate_date().split("-")[2]);
        viewHolder.tv_lv_title.setText(data.get(i).getTitle());
        viewHolder.tv_lv_content.setText(data.get(i).getText());

        if(data.get(i).getBackground()!=null){
            viewHolder.layout.setBackgroundColor(Color.parseColor(data.get(i).getBackground()));
        }
        return view;
    }

    class ViewHolder{
        TextView tv_lv_month;
        TextView tv_lv_day;
        TextView tv_lv_title;
        TextView tv_lv_content;
        LinearLayout layout;
    }
}

