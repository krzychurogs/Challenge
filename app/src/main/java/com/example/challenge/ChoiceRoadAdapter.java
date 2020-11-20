package com.example.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
public class ChoiceRoadAdapter extends RecyclerView.Adapter<ChoiceRoadAdapter.ExampleViewHolder> {
    private ArrayList<ChoiceRoadItem> mExampleList;
    customButtonListener customListner;
    private OnItemClickListener mListener;
    private Context mContext;

    public interface customButtonListener {
        public void onButtonClickListner(int position);
    }
    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }


    public ChoiceRoadAdapter(ArrayList<ChoiceRoadItem> mExampleList, Context mContext) {
        this.mExampleList = mExampleList;
        this.mContext = mContext;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ImageView mImageViewdist;
        public ImageView mImageViewAvg;
        public ImageView mImageViewStats;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextViewAvg;
        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mImageViewdist= itemView.findViewById(R.id.imageViewDist);
            mImageViewAvg= itemView.findViewById(R.id.imageViewAvg);
            mImageViewStats= itemView.findViewById(R.id.imageViewStats);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textDystans);
            mTextViewAvg= itemView.findViewById(R.id.textBestAvg);








            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
    public ChoiceRoadAdapter(FragmentActivity activity, ArrayList<ChoiceRoadItem> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_road, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, final int position) {
        ChoiceRoadItem currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getmText2());
        holder.mTextViewAvg.setText(currentItem.getmText3());
        holder.mImageViewdist.setImageResource(currentItem.getmImageResourceDist());
        holder.mImageViewAvg.setImageResource(currentItem.getmImageResourceAvg());
        holder.mImageViewStats.setImageResource(currentItem.getmImageResourceStats());
        holder.mImageViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position);

                }
            }
        });


        Glide.with(mContext).load(mExampleList.get(position).getImageResource()).into(holder.mImageView);





    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}