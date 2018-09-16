package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public Context context;
    ApiUtils apiUtils;
    CurrentUser currentUser;

    // data is passed into the constructor
    GroupAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        apiUtils = new ApiUtils(context);
        currentUser = new CurrentUser(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.group_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final JSONObject group = mData.get(position);
        try {
            holder.groupName.setText(group.getString("name"));
            holder.groupLeaderName.setText(group.getString("leader"));
            holder.groupContactNumber.setText(group.getString("contact_number"));
            holder.groupContactNumber.setText(group.getString("contact_number"));

            currentUser = new CurrentUser(context);
            String type = currentUser.getType();
            if (type.equals("priest")){
                holder.deleteGroup.setVisibility(View.GONE);
            }

            holder.deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String groupId = group.getString("id");
                        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Intent intent = new Intent(context, GroupActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                GroupActivity groupActivity = new GroupActivity();
                                groupActivity.setErrorText("Unable to delete event.");
                            }
                        };


                        apiUtils.deleteGroup(groupId, jhrh);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupName;
        TextView groupLeaderName;
        TextView groupContactNumber;
        Button deleteGroup;

        ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            groupLeaderName = itemView.findViewById(R.id.group_leader_name);
            groupContactNumber = itemView.findViewById(R.id.group_contact_number);
            deleteGroup = itemView.findViewById(R.id.delete_group_btn);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    JSONObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
