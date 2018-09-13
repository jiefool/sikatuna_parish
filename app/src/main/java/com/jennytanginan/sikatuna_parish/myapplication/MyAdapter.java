package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public Context context;
    ApiUtils apiUtils;

    // data is passed into the constructor
    MyAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.apiUtils = new ApiUtils(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final JSONObject event = mData.get(position);

        try {
            holder.eventName.setText(event.getString("name"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeStart = formatter.parse(event.getString("time_start"));
            Date timeEnd = formatter.parse(event.getString("time_start"));

            formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa");
            String timeStartz = formatter.format(timeStart);
            String timeEndz = formatter.format(timeEnd);

            holder.timeStart.setText(timeStartz);
            holder.timeEnd.setText(timeEndz);

            JSONObject priestDate = event.getJSONObject("user");
            holder.priest.setText(priestDate.getString("name"));

            holder.details.setText(event.getString("details"));

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String eventId = event.getString("id");
                        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                              Intent intent = new Intent(context, EventActivity.class);
                              context.startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                EventActivity eventActivity = new EventActivity();
                                eventActivity.setErrorText("Unable to delete event.");
                            }
                        };


                        apiUtils.deleteEvent(eventId, jhrh);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyAdapter.this.context, EditEventActivity.class);
                    intent.putExtra("event", event.toString());
                    ((Activity) context).finish();
                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
        TextView eventName;
        TextView timeStart;
        TextView timeEnd;
        TextView priest;
        TextView details;
        Button editBtn;
        Button deleteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            timeStart = itemView.findViewById(R.id.time_start);
            timeEnd = itemView.findViewById(R.id.time_end);
            priest = itemView.findViewById(R.id.priest);
            details = itemView.findViewById(R.id.details);

            editBtn = itemView.findViewById(R.id.edit_event_btn);
            deleteBtn = itemView.findViewById(R.id.delete_event_btn);


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
