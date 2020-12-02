package com.example.challenge;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.Table;

import java.util.ArrayList;
public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ExampleViewHolder> {
    private ArrayList<TableItem> mExampleList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView=  itemView.findViewById(R.id.imagemedal);
            mTextView1 = itemView.findViewById(R.id.txtRank);
            mTextView3 = itemView.findViewById(R.id.txtAvg);
            mTextView2 = itemView.findViewById(R.id.txtLvl);

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
    public TableAdapter(ArrayList<TableItem> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        TableItem currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem.getmText1());
        holder.mImageView.setImageResource(R.drawable.medal7);

        if(position==0)
        {
            holder.mTextView1.setBackgroundResource(R.drawable.table_content_cell_gol);
            holder.mTextView2.setBackgroundResource(R.drawable.table_content_cell_gol);
            holder.mTextView3.setBackgroundResource(R.drawable.table_content_cell_gol);
            holder.mImageView.setImageResource(R.drawable.gol);
        }
        if(position==1)
        {
            holder.mTextView1.setBackgroundResource(R.drawable.table_content_cell_silver);
            holder.mTextView2.setBackgroundResource(R.drawable.table_content_cell_silver);
            holder.mTextView3.setBackgroundResource(R.drawable.table_content_cell_silver);
            holder.mImageView.setImageResource(R.drawable.sil);
        }
        if(position==2)
        {

            holder.mTextView1.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.mTextView2.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.mTextView3.setBackgroundResource(R.drawable.table_content_cell_bg);
            holder.mImageView.setImageResource(R.drawable.medalsilver);

        }

        holder.mTextView3.setText(currentItem.getmText3());
        holder.mTextView2.setText(currentItem.getmText2());


    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}