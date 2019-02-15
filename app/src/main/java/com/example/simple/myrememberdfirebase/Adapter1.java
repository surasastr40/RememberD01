package com.example.simple.myrememberdfirebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter1 extends RecyclerView.Adapter<Adapter1.ViewHolder> {
    private int lastPosition = -1;
    private ArrayList<String> data;
    private final OnItemClickListener listener;
    private Context context;

    Adapter1(Context context, ArrayList<String> data, OnItemClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(String data, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView cardView;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.head);
            cardView = itemView.findViewById(R.id.card);
            img = itemView.findViewById(R.id.img);
        }

        void bind(final String data, final OnItemClickListener listener, int position) {
            textView.setText(data);

            switch (position) {
                case 0:
                    img.setImageResource(R.drawable.ic_one);
                    break;
                case 1:
                    img.setImageResource(R.drawable.ic_two);
                    break;
                case 2:
                    img.setImageResource(R.drawable.ic_three);
                    break;
                case 3:
                    img.setImageResource(R.drawable.ic_four);
                    break;
                case 4:
                    img.setImageResource(R.drawable.ic_five);
                    break;
                case 5:
                    img.setImageResource(R.drawable.ic_six);
                    break;
                case 6:
                    img.setImageResource(R.drawable.ic_seven);
                    break;
                case 7:
                    img.setImageResource(R.drawable.ic_eight);
                    break;
                case 8:
                    img.setImageResource(R.drawable.ic_nine);
                    break;
                case 9:
                    img.setImageResource(R.drawable.ic_ten);
                    break;
                case 10:
                    img.setImageResource(R.drawable.ic_eleven);
                    break;
                case 11:
                    img.setImageResource(R.drawable.ic_twelve);
                    break;
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(data, getAdapterPosition());
                }
            });
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.right_to_left);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

        holder.bind((data.get(position)), listener, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}




