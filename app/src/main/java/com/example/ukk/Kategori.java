package com.example.ukk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kategori extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private KategoriAdapter adapter;
    private List<KategoriModel> kategoriList;
    private Button btnTambahPelanggan;
    private ImageView bck;

    private String userId;
    private static final String URL_VIEW = "http://172.16.0.93//ukk/kategori.php";
    private static final String URL_ADD = "http://172.16.0.93/ukk/tambahKat.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = loginPrefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPelanggan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi Adapter
        kategoriList = new ArrayList<>();
        adapter = new KategoriAdapter(this, kategoriList);
        recyclerView.setAdapter(adapter);

        // Tombol Tambah Pelanggan,l
        btnTambahPelanggan = findViewById(R.id.btnTambahKategori);
        btnTambahPelanggan.setOnClickListener(v -> showAddKategoriDialog());

        bck = findViewById(R.id.Back);
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Kategori.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loadData();

    }

    private void showAddKategoriDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_kategori, null);
        builder.setView(view);

        EditText editNama = view.findViewById(R.id.addNama);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSimpan.setOnClickListener(v -> {
            String nama = editNama.getText().toString().trim();

            if (nama.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                tambahKategori(nama); // Ganti 1 dengan user_id yang sesuai
                dialog.dismiss();
            }
        });
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        String url = URL_VIEW + "?user_id=" + userId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    kategoriList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            kategoriList.add(new KategoriModel(obj.getString("id"), obj.getString("category")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void tambahKategori(String namaKategori) {

        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userId = loginPrefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        if (userId == null) {
            Toast.makeText(this, "Gagal mendapatkan User ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalUserId = userId;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    Toast.makeText(this, "Kategori berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    loadData();
                },
                error -> {
                    Toast.makeText(this, "Gagal menambahkan kategori: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category", namaKategori);
                params.put("user_id", finalUserId);
                return params;
            }
        };

        // Tambahkan request ke antrian Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}