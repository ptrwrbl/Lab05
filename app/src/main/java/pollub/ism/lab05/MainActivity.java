package pollub.ism.lab05;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Button saveButton = null,
                   readButton = null;
    private Spinner availableFilenames = null;
    private EditText newNoteName = null,
                     noteContent = null;

    private ArrayList<String> filenameList = null;
    private ArrayAdapter<String> spinnerAdapter = null;

    private final String PREFERENCES_NAME = "Aplikacja do notatek";
    private final String PREFERENCES_KEY = "Zapisane nazwy plików";

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();

        readButton = (Button) findViewById(R.id.OpenButton);
        availableFilenames = (Spinner) findViewById(R.id.AvailableFilenames);
        saveButton = (Button) findViewById(R.id.SaveButton);
        newNoteName = (EditText) findViewById(R.id.NewNoteName);
        noteContent = (EditText) findViewById(R.id.NoteContent);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNote();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

    }

    @Override
    protected void onPause() {
        savePreferences();
        
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        filenameList = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filenameList);
        availableFilenames.setAdapter(spinnerAdapter);

        readPreferences();
    }

    private void readNote() {
        String filename = availableFilenames.getSelectedItem().toString();

        noteContent.getText().clear();

        if(readFile(filename, noteContent)) {
            Toast.makeText(this, res.getString(R.string.opening_success),Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, res.getString(R.string.opening_error),Toast.LENGTH_SHORT).show();
    }

    private void saveNote() {
        String filename = newNoteName.getText().toString();

        if(saveFile(filename, noteContent)) {
            Toast.makeText(this, res.getString(R.string.saving_success),Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, res.getString(R.string.saving_error),Toast.LENGTH_SHORT).show();
    }

    private boolean readFile(String filename, EditText contentArea) {
        File directory = getApplicationContext().getExternalFilesDir(null);
        File file = new File(directory + File.separator + filename);
        BufferedReader reader = null;

        if(file.exists()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine() + "\n";
                
                while (line != null) {
                    contentArea.getText().append(line);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                return false;
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean saveFile(String filename, EditText editArea) {
        File fileDirectory = getApplicationContext().getExternalFilesDir(null);
        File file = new File(fileDirectory + File.separator + filename);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(editArea.getText().toString());
        } catch (Exception e) {
            return false;
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                return false;
            }
        }

        if(!filenameList.contains(filename)) {
            filenameList.add(filename);
            spinnerAdapter.notifyDataSetChanged();
        }

        return true;
    }

    private void readPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        Set<String> savesFilenames = preferences.getStringSet(PREFERENCES_KEY, null);

        if (savesFilenames != null) {
            filenameList.clear();
            for (String filename : savesFilenames) {
                filenameList.add(filename);
            }
            spinnerAdapter.notifyDataSetChanged();
        }
    }

    private void savePreferences() {

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putStringSet(PREFERENCES_KEY, new HashSet<String>(filenameList));

        editor.apply();
    }

}