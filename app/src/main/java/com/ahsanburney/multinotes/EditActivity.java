package com.ahsanburney.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etNote;
    private TextView dateTime;
    private Note note1, note2;
    private final String TAG = "EditActivity";
    int sizeT;
    private String titleChanges;
    private String hasChanges;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etTitle = (EditText) findViewById(R.id.edit_et_title);
        etNote = (EditText) findViewById(R.id.edit_et_content);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent i = getIntent();
        if (i.hasExtra(Note.class.getName())) {
            note1 = (Note) i.getSerializableExtra(Note.class.getName());
        }

    }

    @Override
    protected void onResume() {
        super.onStart();
        if (note1 != null) {

            hasChanges = note1.getMcontent();
            etTitle.setText(note1.getMtitle());
            etTitle.setSelection(note1.getMtitle().length());
            etNote.setText(note1.getMcontent());
            titleChanges=note1.getMcontent();

        }
    }

    @Override
    protected void onPause() {
        note1.setMtitle(etTitle.getText().toString());
        note1.setMcontent(etNote.getText().toString());
        if (note1.getMtitle().equals("")) {
            Toast.makeText(getApplicationContext(), "Untitled Activity was not saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!hasChanges.equals(etNote.getText().toString())) {

                saveMultinotes(note1, this);
            }
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_note_save:

                    if(!(titleChanges.equals(etTitle.getText().toString())||hasChanges.equals(etNote.getText().toString())))
                {
                    //Toast.makeText(getApplicationContext(), "Untitled Activity was not saved", Toast.LENGTH_SHORT).show();
                    note1.setMtitle(etTitle.getText().toString());
                    note1.setMcontent(etNote.getText().toString());
                    finish();
                    return true;

                }else
                if (!hasChanges.equals(etNote.getText().toString())){

                    saveMultinotes(note1, this);
                    finish();
                }

                note1.setMtitle(etTitle.getText().toString());
                note1.setMcontent(etNote.getText().toString());
                if (note1.getMtitle().equals("")) {
                    Toast.makeText(getApplicationContext(), "Untitled Activity was not saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {


                    saveMultinotes(note1, this);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Note is not saved!"+"\n"+"Do you want to save this note?" )
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditActivity.this.onSuperBackPressed();
                        if (hasChanges.equals(etNote.getText().toString())) {
                            finish();
                        }

                        note1.setMtitle(etTitle.getText().toString());
                        note1.setMcontent(etNote.getText().toString());
                        if (note1.getMtitle().equals("")) {
                            Toast.makeText(getApplicationContext(), "Untitled Activity was not saved", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {

                            saveMultinotes(note1, EditActivity.this);
                            finish();
                        }

                       // note1.setMtitle(etTitle.getText().toString());
                       // note1.setMcontent(etNote.getText().toString());
                        //saveMultinotes(note1, EditActivity.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onSuperBackPressed(){
        super.onBackPressed();
    }


    //------------------------------------------------------Functioning-----------------------------------//

    void saveMultinotes(Note note1, EditActivity ma) {





            Log.d(TAG, "saveProduct: Saving JSON File ");
            try {
                FileOutputStream fos = ma.getApplicationContext().openFileOutput("temporaryFile.json", Context.MODE_PRIVATE);

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
                writer.setIndent("  ");
                writer.beginObject();
                DateFormat df = new SimpleDateFormat("EEE MMM d, h:mm a");
                String date = df.format(Calendar.getInstance().getTime());
                writer.name("ID").value(note1.getID());
                writer.name("Time").value(date);
                writer.name("Title").value(note1.getMtitle());
                writer.name("Content").value(note1.getMcontent());
                writer.endObject();

                writer.close();

                StringWriter sw = new StringWriter();
                writer = new JsonWriter(sw);
                writer.setIndent("  ");
                writer.beginObject();
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    sizeT = extras.getInt("size");
                }
                writer.name("ID").value(sizeT);
                writer.name("Title").value(note1.getMtitle());
                writer.name("Time").value(date);
                writer.name("Content").value(note1.getMcontent());
                writer.endObject();
                writer.close();
                Log.d(TAG, "saveMultiNotes: JSON:\n" + sw.toString());
            } catch (Exception e) {
                e.getStackTrace();
            }

        }


    Note loadFile(EditActivity ma) {
        Note note1;
        Log.d(TAG, "loadFile: Loading JSON File");
        note1 = new Note(999);
        try {
            InputStream is = ma.getApplicationContext().openFileInput("temporaryFile.json");
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
            Toast.makeText(ma, "No Notes Found", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note1;
    }
}
