package com.example.BOneOnOneChat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class Name extends AppCompatActivity {

    private AppCompatEditText editText;
    protected final String TABLE_NAME="USER";
    protected final String USER_NAME="USER_NAME";
    protected final String USER_ADDRESS="USER_ADDRESS";
    protected   final String PROFILE_BACKGROUND_COLOR="PROFILE_BACKGROUND_COLOR";
    protected final String KEY="key_ID";
    protected SQLiteDatabase db;
    protected String createTableQuery;

    @Override
    public void onBackPressed() {

    }

    @SuppressLint({"MissingPermission", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        editText=findViewById(R.id.editTextPhone);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        Button next = findViewById(R.id.button2);

        next.setOnClickListener(v -> {
            String enterText= Objects.requireNonNull(editText.getText()).toString();
            if(enterText.length()!=0 && enterText.length()<=10) {
                int count=0;
                for(char c:enterText.toCharArray()) {
                    if(Character.isWhitespace(c)) count++;
                }
                if(count!=enterText.length()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    adapter.setName((enterText==null)?"no name" : enterText);
                    editor.putString("oldName", adapter.getName());
                    editor.putString("name", enterText);
                    editor.putBoolean("notificationHide", true);
                    editor.putBoolean("notificationSound", true);

                    int color = ContextCompat.getColor(Name.this, R.color.profilebackground);
                    editor.putInt("profileColor", color);
                    editor.apply();
                    db = openOrCreateDatabase("my_database", Context.MODE_PRIVATE, null);
// Get the current time
                    createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                            KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            USER_NAME + " TEXT, " + USER_ADDRESS + "TEXT" + PROFILE_BACKGROUND_COLOR + "TEXT)";
                    db.execSQL(createTableQuery);

                        Intent i = new Intent(Name.this, MainActivity.class);
                        i.putExtra("username", enterText);
                        Toast.makeText(Name.this, "Welcome " + enterText + "!", Toast.LENGTH_SHORT).show();
                        startActivity(i);

                }
                else Toast.makeText(this, "enter valid name", Toast.LENGTH_SHORT).show();
            }
            else{
                Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vi.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    //noinspection deprecation
                    vi.vibrate(100);
                }

                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 1000);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
                Toast.makeText(Name.this, "please enter your name " + enterText, Toast.LENGTH_SHORT).show();

                Toast.makeText(Name.this, " enter your name between 1 to 10 characters", Toast.LENGTH_SHORT).show();
            }

        });
    }
}