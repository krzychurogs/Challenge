package com.example.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
public class ChoiceRoadAdapter extends RecyclerView.Adapter<ChoiceRoadAdapter.ExampleViewHolder> {
    private ArrayList<ChoiceRoadItem> mExampleList;
    private OnItemClickListener mListener;
    private Context mContext;

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
        public TextView mTextView1;
        public TextView mTextView2;
        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
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
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ChoiceRoadItem currentItem = mExampleList.get(position);

        holder.mTextView1.setText(currentItem.getText1());
        Glide.with(mContext).load(mExampleList.get(position).getImageResource()).into(holder.mImageView);





    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}