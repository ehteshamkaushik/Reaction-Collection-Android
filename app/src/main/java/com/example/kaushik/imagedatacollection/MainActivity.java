package com.example.kaushik.imagedatacollection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText nameEntry = findViewById(R.id.nameEntryId);
        Button nameSubmit = findViewById(R.id.nameSubmit);

        final String userName = nameEntry.getText().toString();

        nameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter a Valid Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    
                }
            }
        });
    }
}
