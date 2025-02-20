package com.example.ukk;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageView prf;

    private Button kat;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_kategori) {
                    startActivity(new Intent(MainActivity.this, Kategori.class));
                    return true;
                } else if (itemId == R.id.nav_task) {
                    startActivity(new Intent(MainActivity.this, Task.class));
                    return true;
                } else if (itemId == R.id.nav_profil) {
                    startActivity(new Intent(MainActivity.this, Profil.class));
                    return true;
                }

                return false;
            }
        });
    }
}