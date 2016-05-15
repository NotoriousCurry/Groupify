package com.tssssa.sgaheer.groupify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private static Context mContext;
    public final static String EXTRA_ID = "com.tssssa.groupify.ID";

    public static class GEventsHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView name;
        TextView description;
        TextView location;
        TextView eId;
        TextView eDate;

        public GEventsHolder(View vv) {
            super(vv);
            name = (TextView) vv.findViewById(R.id.ecvText1);
            description = (TextView) vv.findViewById(R.id.ecvText2);
            location = (TextView) vv.findViewById(R.id.ecvText3);
            eId = (TextView) vv.findViewById(R.id.ecvText4);
            eDate = (TextView) vv.findViewById(R.id.ecvText5);
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

    public MyRecyclerViewAdapter(ArrayList<GEvents> myDataset, Context mContext) {
        this.mDataSet = myDataset;
        this.mContext = mContext;
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
        holder.itemView.setTag(position);
        holder.name.setText(mDataSet.get(position).getName());
        holder.description.setText("Description: " + mDataSet.get(position).getDescription());
        holder.location.setText("Location: " + mDataSet.get(position).getLocation());
        holder.eId.setText(mDataSet.get(position).getId());
        holder.eDate.setText(mDataSet.get(position).getDate() + " - " + mDataSet.get(position).getTime());
        final String eventId = holder.eId.getText().toString();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewEvent.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(EXTRA_ID, eventId);
                mContext.startActivity(intent);
            }
        });
    }

    public void addItem(GEvents eve) {
        mDataSet.add(eve);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public boolean checkExist(GEvents ev) {
       return mDataSet.contains(ev);
    }

    public String getEid(int index) {
        return mDataSet.get(index).getId();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
