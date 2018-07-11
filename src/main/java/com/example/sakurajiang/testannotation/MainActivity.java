package com.example.sakurajiang.testannotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AnimalFactory animalFactory = new AnimalFactory();
        final Button button = findViewById(R.id.cat_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Animal animal = animalFactory.create(((EditText)findViewById(R.id.animal_name_et)).getText().toString());
                    button.setText(animal.getName());
                    Toast.makeText(MainActivity.this, animal.getName(), Toast.LENGTH_LONG).show();
                }catch (IllegalArgumentException e){
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
