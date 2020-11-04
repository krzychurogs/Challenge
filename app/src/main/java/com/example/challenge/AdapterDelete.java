package com.example.challenge;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapterDelete extends BaseAdapter implements Filterable {

    Context c;
    customButtonListener customListner;
    ArrayList<SingleRow> originalArray,tempArray;
    CustomFilter cs;

    public interface customButtonListener {
        public void onButtonClickListner(int position);
    }
    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }


    public  AdapterDelete(Context c, ArrayList<SingleRow>originalArray)
    {
        this.c=c;
        this.originalArray=originalArray;
        this.tempArray=originalArray;
    }


    @Override
    public Object getItem(int i) {
        return originalArray.get(i);
    }

    @Override
    public int getCount() {
        return originalArray.size();
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row= inflater.inflate(R.layout.delete_listview,null);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        viewHolder.text = (TextView) row.findViewById(R.id.textViewName);
        viewHolder.button = (Button) row.findViewById(R.id.list_item_btn);


        viewHolder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(i);
                }

            }
        });



        TextView tname=(TextView) row.findViewById(R.id.textViewName);
        TextView tlvl=(TextView) row.findViewById(R.id.textViewLvl);
        Button btn=(Button) row.findViewById(R.id.list_item_btn);
        tname.setText(originalArray.get(i).getName());
        tlvl.setText(originalArray.get(i).getLvl());
        btn.setText("Usuń");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customListner != null) {
                    customListner.onButtonClickListner(i);

                }
            }
        });



        return row;
    }


    @Override
    public Filter getFilter() {
        if(cs==null)
        {
            cs=new CustomFilter();
        }
        return cs;
    }

    class CustomFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            if(constraint!=null && constraint.length()>0) {
                constraint = constraint.toString().toUpperCase();

                ArrayList<SingleRow> filters = new ArrayList<>();
                for (int i = 0; i < tempArray.size(); i++) {
                    if (tempArray.get(i).getName().toUpperCase().contains(constraint)) {
                        SingleRow singleRow = new SingleRow(tempArray.get(i).getName(), tempArray.get(i).getLvl());
                        filters.add(singleRow);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }
            else
            {
                results.count=tempArray.size();
                results.values=tempArray;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            originalArray=(ArrayList<SingleRow>)filterResults.values;
            notifyDataSetChanged();
        }
    }
    public class ViewHolder {
        TextView text;
        Button button;
    }

}
