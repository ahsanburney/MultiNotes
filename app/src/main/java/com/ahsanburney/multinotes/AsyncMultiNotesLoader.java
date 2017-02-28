package com.ahsanburney.multinotes;


import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AsyncMultiNotesLoader extends AsyncTask<String, Integer, String>{

    private MainActivity mainActivity;
    private int count;

    private final String dataURL = "https://restcountries.eu/rest/v1/all";
    private static final String TAG = "AsyncMultiNotesLoader";

    public AsyncMultiNotesLoader(MainActivity mainact) {
        mainActivity = mainact;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Loading MultiNotes .......", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Note> note = new ArrayList<>();
        mainActivity.updateData(note);
    }

    @Override
    protected String doInBackground(String... params) {

        String json = null;
        List<Note> qnote;
        try {
            qnote=getMultiNotes(mainActivity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;

    }

    public List<Note> getMultiNotes(MainActivity mainAct) throws IOException {
        InputStream is = mainAct.getApplicationContext().openFileInput("save.json");
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        try {
            return getMsgArray(reader);
        } finally {
            reader.close();
        }
    }
    public List<Note> getMsgArray(JsonReader reader) throws IOException {
        List<Note> msg = new ArrayList<Note>();

        reader.beginArray();
        while (reader.hasNext()) {
            msg.add(getMsg(reader));
        }
        reader.endArray();
        return msg;
    }

    public Note getMsg(JsonReader reader) throws IOException {
        Note note1 = new Note(999);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("ID")) {
                note1.setID(Integer.parseInt(reader.nextString()));
            } else if (name.equals("Title")) {
                note1.setMtitle(reader.nextString());
            }else if (name.equals("Time")) {
                note1.setMdateTime(reader.nextString());
            } else if (name.equals("Content")) {
                note1.setMcontent(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        if(note1.getID()==999)
        {
            return null;
        }
        return note1;
    }

    public Note loadFile(MainActivity mainActivity) {
        Note note1;
        Log.d(TAG, "loadFile: Loading JSON File");
        note1 = new Note();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput("temporaryFile.json");
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("ID")) {
                    note1.setID(Integer.parseInt(reader.nextString()));
                } else if (name.equals("Title")) {
                    note1.setMtitle(reader.nextString());
                }else if (name.equals("Time")) {
                    note1.setMdateTime(reader.nextString());
                } else if (name.equals("Content")) {
                    note1.setMcontent(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            Toast.makeText(mainActivity, "No Notes Found", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note1;
    }
}
