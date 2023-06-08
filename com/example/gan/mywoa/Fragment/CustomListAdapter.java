package com.example.gan.mywoa.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gan.mywoa.R;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList<ListItem> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, ArrayList<ListItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateArray(ArrayList<ListItem> listData) {
        this.listData= listData;
        //this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout_paket, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.reportedtipe = (TextView) convertView.findViewById(R.id.textviewtipe);
            holder.reportedstatus = (TextView) convertView.findViewById(R.id.textviewstatus);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem newsItem = listData.get(position);
        holder.headlineView.setText(newsItem.getHeadline());
        if(newsItem.getIntstatus() != null){
            if(newsItem.getIntstatus().equals("0")){
                //holder.headlineView.setTextColor(Color.RED);
            }
        }

        holder.reportedtipe.setText(newsItem.getTipe());
        holder.reportedstatus.setText(newsItem.getStatus());

        if (holder.imageView != null && newsItem.getUrl() !="") {
            try{
                new ImageDownloaderTask(holder.imageView).execute(newsItem.getUrl());
            }catch (Exception e){
                System.out.print(e);
            }
        }

        return convertView;
    }
    public void removeView(int position) {
        // lv and the adapter must be public-static in their Activity Class
        //SomeActivity.lv.removeViewAt(position);
        //SomeActivity.adapter.notifyDataSetChanged();
    }
    public void notifyDataSetChanged() {
        //mForceRedraw = true;
        super.notifyDataSetChanged();
        //mForceRedraw = false;
    }
    static class ViewHolder {
        TextView headlineView;
        TextView reportedtipe;
        TextView reportedstatus;
        ImageView imageView;
    }
}
