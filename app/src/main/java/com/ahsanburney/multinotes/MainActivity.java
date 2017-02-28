package com.ahsanburney.multinotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class    MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{


    public List<Note> multiNotesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter multiNotesAdapter;
    private int multiNotesSize =0;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        multiNotesAdapter = new NoteAdapter(multiNotesList, this);
        recyclerView.setAdapter(multiNotesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new AsyncMultiNotesLoader(this).execute();
        multiNotesList.clear();
        try {
            multiNotesList.addAll(getMultiNotes(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onStart();

        Note temporaryMultiNotes = loadFile(this);
        if(temporaryMultiNotes.getID()!=999) {
            int flag = 0;
            for (int i = 0; i < multiNotesList.size(); i++) {
                if (temporaryMultiNotes.getID() == multiNotesList.get(i).getID()) {
                    multiNotesList.get(i).setMtitle(temporaryMultiNotes.getMtitle());
                   multiNotesList.get(i).setMdateTime(temporaryMultiNotes.getMdateTime());
                    multiNotesList.get(i).setMcontent(temporaryMultiNotes.getMcontent());
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                if(temporaryMultiNotes.getID()!=0){
                    multiNotesList.add(0,temporaryMultiNotes);
                }
                 }
            setMultiNotes(multiNotesList, this);
        }
        recyclerView.setAdapter(multiNotesAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info , menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main_new_note:
                multiNotesSize++;
                Note addSingleMultiNote = new Note(multiNotesSize);
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(Note.class.getName(), addSingleMultiNote);
                i.putExtra("size", multiNotesSize);
                startActivity(i);
                return true;
            case R.id.action_note_info:
                startActivity(new Intent(this,InfoActivity.class) );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int multinotesNumber = recyclerView.getChildLayoutPosition(view);
        Note num = multiNotesList.get(multinotesNumber);
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        i.putExtra(Note.class.getName(), num);
        startActivity(i);
    }

    @Override
    public boolean onLongClick(final View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Delete").setMessage("Delete Note?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int multinotesNumber = recyclerView.getChildLayoutPosition(view);
                        Toast.makeText(view.getContext(), "This Note has been Deleted" , Toast.LENGTH_SHORT).show();
                        multiNotesList.remove(multinotesNumber);
                        setMultiNotes(multiNotesList,MainActivity.this);
                        recyclerView.setAdapter(multiNotesAdapter);
                    }
                })
                .setNegativeButton("NO",null).setCancelable(false);
        dialog.show();
        return true;
    }

    public void updateData(ArrayList<Note> cList) {
       // qnotesList.addAll(cList);
        recyclerView.setAdapter(multiNotesAdapter);

    }

    //------------------------------------------------------------Functioning-----------------------------------//

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

    public void setMultiNotes(List<Note> note1, MainActivity ma) {
        try {
            FileOutputStream fos = ma.getApplicationContext().openFileOutput("save.json", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos,"UTF-8"));
            writer.setIndent("  ");
            setMsgArray(writer, note1);
            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void setMsgArray(JsonWriter writer, List<Note> note1) throws
            IOException {
        writer.beginArray();
        for ( Note note :note1) {
            setMsg(writer, note);
        }
        writer.endArray();
    }

    public void setMsg(JsonWriter writer, Note note) throws IOException {
        writer.beginObject();
        writer.name("ID").value(note.getID());
        writer.name("Title").value(note.getMtitle());
        writer.name("Time").value(note.getMdateTime());
        writer.name("Content").value(note.getMcontent());
        writer.endObject();
    }

}



