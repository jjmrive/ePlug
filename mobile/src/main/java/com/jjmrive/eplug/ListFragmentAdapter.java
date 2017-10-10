package com.jjmrive.eplug;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>{

    private ArrayList<Plug> sortedPlugList;
    private double[] distances;

    public class ViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout plugRow;
        public ImageView img;
        public TextView title;
        public TextView distance;
        public TextView isFree;

        public ViewHolder(View view){
            super(view);
            img = (ImageView) view.findViewById(R.id.img_plug);
            title = (TextView) view.findViewById(R.id.title);
            distance = (TextView) view.findViewById(R.id.distance);
            isFree = (TextView) view.findViewById(R.id.isFree);
            plugRow = (RelativeLayout) view.findViewById(R.id.plug_row);
        }
    }

    public ListFragmentAdapter(ArrayList<Plug> sortedPlugList, double[] distances){
        this.sortedPlugList = sortedPlugList;
        this.distances = distances;
    }

    @Override
    public ListFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plug_row, parent, false);

        return new ListFragmentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (!Objects.equals(sortedPlugList.get(position).getUrlPhoto(), "")) {
            Picasso.with(holder.img.getContext()).load(sortedPlugList.get(position).getUrlPhoto()).transform(new CircleTransform()).into(holder.img);
        } else {
            holder.img.setImageDrawable(holder.img.getContext().getDrawable(R.drawable.ic_no_photo_vector));
        }
        holder.title.setText(sortedPlugList.get(position).getName());
        holder.distance.setText(String.format("%.3f", distances[position]) + " " + holder.distance.getResources().getString(R.string.distance_end));
        if (sortedPlugList.get(position).isFree()){
            holder.isFree.setText(holder.isFree.getResources().getString(R.string.dialog_add_editIsFree));
            holder.isFree.setTextColor(ContextCompat.getColor(holder.isFree.getContext(), R.color.colorGreen500));
        } else {
            holder.isFree.setText(holder.isFree.getResources().getString(R.string.non_free_plug));
            holder.isFree.setTextColor(ContextCompat.getColor(holder.isFree.getContext(), R.color.colorRed700));
        }

        holder.plugRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                SharedPreferences mapStatePrefs = holder.plugRow.getContext().getSharedPreferences("mapCameraState", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = mapStatePrefs.edit();
                editor.putFloat("lat", (float) sortedPlugList.get(position).getLatitude());
                editor.putFloat("long", (float) sortedPlugList.get(position).getLongitude());
                editor.putFloat("zoom", 13);
                editor.apply();

                ((Activity)holder.plugRow.getContext()).onBackPressed();
            }
        });

    }

    @Override
    public int getItemCount() {
        return sortedPlugList.size();
    }
}
