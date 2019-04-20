package com.vedmitryapps.makeittestapp.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vedmitryapps.makeittestapp.R;
import com.vedmitryapps.makeittestapp.api.models.Result;
import com.vedmitryapps.makeittestapp.views.MapsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<Result> results;
    Context context;

    public RecyclerAdapter(List<Result> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null){
            context = viewGroup.getContext();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.placeAddress.setText(results.get(position).getVicinity());
        holder.placeName.setText(results.get(position).getName());
        Picasso.get().load(results.get(position).getIcon()).into(holder.icon);
    }


    @Override
    public int getItemCount() {
        return results.size();
    }

    public void update(List<Result> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.placeName)
        TextView placeName;
        @BindView(R.id.placeAddress)
        TextView placeAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("lat", results.get(getAdapterPosition()).getGeometry().getLocation().getLat());
                    intent.putExtra("lng", results.get(getAdapterPosition()).getGeometry().getLocation().getLng());
                    intent.putExtra("name", results.get(getAdapterPosition()).getName());
                    context.startActivity(intent);

                }
            });

        }


    }
}