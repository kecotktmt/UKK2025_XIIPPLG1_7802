package com.example.ukk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class Profil extends AppCompatActivity {

    private Button Log;
    private TextView name, email;
    private String userId;
    private ImageView bck;
    private static final String API_URL = "http://172.16.0.93/ukk/profil.php"; // Ganti dengan URL API Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        Log = findViewById(R.id.log);
        name = findViewById(R.id.txNama);
        email = findViewById(R.id.txEmail);
        bck = findViewById(R.id.Back);

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profil.this, MainActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = loginPrefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        if (userId != null) {
            fetchUserData(userId);// Ambil data pengguna berdasarkan ID
        }

        Log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(); // Panggil metode logout
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Profil.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_kategori) {
                startActivity(new Intent(Profil.this, Kategori.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_task) {
                startActivity(new Intent(Profil.this, Task.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchUserData(String id) {
        String url = API_URL + "?user_id=" + id;// Buat URL untuk request API

        // Menggunakan Volley untuk request data
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil data dari response JSON
                            String Name = response.getString("name");
                            String Email = response.getString("email");

                            // Set data ke TextView
                            name.setText(Name);
                            email.setText(Email);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Profil.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Menambahkan request ke queue
        queue.add(request);
    }

    // Metode untuk logout
    private void logout() {
        // Hapus data di SharedPreferences
        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);

        SharedPreferences.Editor loginEditor = loginPrefs.edit();
        SharedPreferences.Editor regisEditor = regisPrefs.edit();

        loginEditor.clear(); // Hapus data di LoginPrefs
        regisEditor.clear(); // Hapus data di RegisPrefs

        loginEditor.apply();
        regisEditor.apply();

        // Arahkan pengguna kembali ke halaman login
        Intent intent = new Intent(Profil.this, Login.class); // Ganti Login.class sesuai dengan activity login Anda
        startActivity(intent);
        finish(); // Menutup Profil activity agar tidak bisa kembali ke halaman profil
    }
}