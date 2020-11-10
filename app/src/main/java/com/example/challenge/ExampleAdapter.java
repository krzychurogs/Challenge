package com.example.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<ExampleItem> mExampleList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ImageView mImageView1;
        public ImageView mImageView2;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public TextView mTextView5;
        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mImageView1 = itemView.findViewById(R.id.imageViewDist);
            mImageView2 = itemView.findViewById(R.id.imageViewAvg);
            mTextView1 = itemView.findViewById(R.id.textView);

            mTextView3 = itemView.findViewById(R.id.textView3);
            mTextView4 = itemView.findViewById(R.id.textView6);
            mTextView5 = itemView.findViewById(R.id.textViewinImage);
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
    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_train, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ExampleItem currentItem = mExampleList.get(position);
        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mImageView1.setImageResource(currentItem.getmImageResource1());
        holder.mImageView2.setImageResource(currentItem.getmImageResource2());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView3.setText(currentItem.getmText3());
        holder.mTextView4.setText(currentItem.getmText4());
        holder.mTextView5.setText(currentItem.getmText5());


    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}