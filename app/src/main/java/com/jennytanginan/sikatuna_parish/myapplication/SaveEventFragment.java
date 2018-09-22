package com.jennytanginan.sikatuna_parish.myapplication;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SaveEventFragment extends DialogFragment {
    String name;
    String time_start;
    String time_end;
    String priest_name;
    String details;
    static EventActivity thisEventActivity;
    String fileNameStr;
    View view;
    DialogFragment frag;
    public static SaveEventFragment newInstance(JSONObject event, EventActivity eventActivity) throws JSONException {
        Bundle bundle = new Bundle();
        bundle.putString("name", event.getString("name"));
        bundle.putString("time_start", event.getString("time_start"));
        bundle.putString("time_end", event.getString("time_end"));
        bundle.putString("priest_name",event.getJSONObject("user").getString("name"));
        bundle.putString("details", event.getString("details"));

        thisEventActivity = eventActivity;

        SaveEventFragment fragment = new SaveEventFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            name = bundle.getString("name");
            time_start = bundle.getString("time_start");
            time_end = bundle.getString("time_end");
            priest_name = bundle.getString("priest_name");
            details = bundle.getString("details");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_filename, container, false);
        frag = this;

        readBundle(getArguments());

        Button button = view.findViewById(R.id.save_event_to_docs);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date c = Calendar.getInstance().getTime();
                fileNameStr = "untitled_"+c+".txt";
                EditText inputText = view.findViewById(R.id.filename);
                String inputStr = inputText.getText().toString();

                if (!inputStr.isEmpty()){
                    fileNameStr = inputStr+".txt";
                }

                Boolean isWritable = thisEventActivity.isExternalStorageWritable();


                if (isWritable) {
                    String data = "";

                    data += name;
                    data += "\n";
                    data += time_start;
                    data += "\n";
                    data += time_end;
                    data += "\n";
                    data += priest_name;
                    data += "\n";
                    data += details;

                    data += "\n";
                    data += "======================================";
                    data += "\n";


                    System.out.println(data);

                    File dir = thisEventActivity.getPublicDocumentStorageDir("/SikatunaParishEvents");
                    File file = new File(dir, fileNameStr);

                    //Write to file
                    try (FileWriter fileWriter = new FileWriter(file, true)) {
                        fileWriter.write(data);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        //Handle exception
                    }

                    thisEventActivity.scanMedia(file.getPath(), thisEventActivity);

                    Context context = thisEventActivity.getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, "Data saved to "+file.toString(), duration);
                    toast.show();

                }



                frag.dismiss();
            }
        });

        return view;
    }
}
