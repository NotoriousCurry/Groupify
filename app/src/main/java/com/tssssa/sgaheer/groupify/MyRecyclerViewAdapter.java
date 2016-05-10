package com.tssssa.sgaheer.groupify;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sgaheer on 10/05/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter
        .GEventsHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<GEvents> mDataSet;
    private static MyClickListener myClickListener;

    public static class GEventsHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView name;
        TextView description;
        TextView location;

        public GEventsHolder(View vv) {
            super(vv);
            name = (TextView) vv.findViewById(R.id.ecvText1);
            description = (TextView) vv.findViewById(R.id.ecvText2);
            location = (TextView) vv.findViewById(R.id.ecvText3);
            Log.i(LOG_TAG, "Adding Listener");
            vv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<GEvents> myDataset) {
        this.mDataSet = myDataset;
    }

    @Override
    public GEventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_row,
                parent, false);
        GEventsHolder gEventsHolder = new GEventsHolder(view);
        return gEventsHolder;
    }

    @Override
    public void onBindViewHolder(GEventsHolder holder, int position) {
        holder.name.setText(mDataSet.get(position).getName());
        holder.description.setText("Description: " + mDataSet.get(position).getDescription());
        holder.location.setText("Location: " + mDataSet.get(position).getLocation());
    }

    public void addItem(GEvents eve) {
        mDataSet.add(eve);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
