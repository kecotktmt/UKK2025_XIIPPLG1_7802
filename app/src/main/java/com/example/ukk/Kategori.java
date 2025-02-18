package com.example.ukk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Kategori extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private KategoriAdapter adapter;
    private List<KategoriModel> kategoriList;
    private Button btnTambahPelanggan;

    private String userId;
    private static final String URL_VIEW = "http://172.16.0.93//ukk/kategori.php";
    private static final String URL_ADD = "http://172.16.0.93/ukk/tambahKat.php";
    private static final String URL_EDIT = "http://172.16.0.93/ukk/editKat.php";
    private static final String URL_DELETE = "http://172.16.0.93/ukk/hapusKat.php";



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

        // Tombol Tambah Pelanggan
        btnTambahPelanggan = findViewById(R.id.btnTambahKategori);
        btnTambahPelanggan.setOnClickListener(v -> showAddKategoriDialog());

        loadData();

    }

    private void showAddKategoriDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_kategori, null);
        builder.setView(view);

        EditText editNama = view.findViewById(R.id.editNama);
        Button btnSimpan = view.findViewById(R.id.btnSimpan);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSimpan.setOnClickListener(v -> {
            String nama = editNama.getText().toString().trim();

            if (nama.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
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


}