package com.example.oneday.onecode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String DATA = "feng";

    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etContent = (EditText) findViewById(R.id.edit_something);
        String content = read();
        etContent.setText(content);
        etContent.setSelection(content.length());
    }

    @Override
    protected void onDestroy() {
        String inputText = etContent.getText().toString();
        save(inputText);
        super.onDestroy();
    }

    private void save(String inputText) {
        FileOutputStream fos = null;
        BufferedWriter writer = null;
        try {
            fos = openFileOutput(DATA, MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(inputText);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.e(TAG, "save: " + e.getMessage(), e);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e(TAG, "save: " + e.getMessage(), e);
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "save: " + e.getMessage(), e);
            }
        }
    }

    private String read(){
        FileInputStream fis = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fis = openFileInput(DATA);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.e(TAG, "read: " + e.getMessage(), e);
        } catch (IOException e) {
            //e.printStackTrace();
            Log.e(TAG, "read: " + e.getMessage(), e);
        } finally {
            try {
                if(reader != null) reader.close();
                if(fis != null) fis.close();
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "read: " + e.getMessage(), e);
            }
        }
        return stringBuilder.toString();
    }
}
