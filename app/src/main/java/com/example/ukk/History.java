package com.example.ukk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTasks;
    private HistoryAdapter historyAdapter;
    private List<HistoryModel> historyList;
    private WeakReference<RequestQueue> requestQueueRef;
    private ImageView bck;

    private String userId;
    private static final String API_URL = "http://172.16.0.93/ukk/history.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this, historyList);
        rvTasks.setAdapter(historyAdapter);
        fetchTasks();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::fetchTasks);

        // Inisialisasi RequestQueue dengan WeakReference
        requestQueueRef = new WeakReference<>(Volley.newRequestQueue(this));

        // Mengambil userId dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = prefs.getString("idL", null);

        if (userId == null) {
            SharedPreferences regisPrefs = getSharedPreferences("RegisPrefs", MODE_PRIVATE);
            userId = regisPrefs.getString("idR", null);
        }

        // Ambil data tugas
        fetchTasks();

        bck = findViewById(R.id.Back);

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, Task.class);
                startActivity(intent);
            }
        });
    }

    private void fetchTasks() {
        if (userId == null) {
            Log.e("Task", "User ID tidak ditemukan.");
            return;
        }

        // Tampilkan loading hanya dengan SwipeRefreshLayout
        swipeRefreshLayout.setRefreshing(true);

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                response -> {
                    historyList.clear();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        // Periksa apakah status sukses
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray tasksArray = jsonResponse.getJSONArray("tasks"); // Ambil array dari "tasks"

                            for (int i = 0; i < tasksArray.length(); i++) {
                                JSONObject taskObj = tasksArray.getJSONObject(i);
                                String id = taskObj.getString("id");
                                String category = taskObj.getString("category");
                                String taskName = taskObj.getString("task");
                                String status = taskObj.getString("status");

                                historyList.add(new HistoryModel(id, category, taskName, status));
                            }
                            historyAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("Task", "Gagal mengambil data");
                        }
                    } catch (JSONException e) {
                        Log.e("Task", "JSON Parsing Error: " + e.getMessage());
                    }
                    // Matikan SwipeRefresh setelah selesai
                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    Log.e("Volley", "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    swipeRefreshLayout.setRefreshing(false);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                return params;
            }
        };

        RequestQueue queue = requestQueueRef.get();
        if (queue != null) {
            queue.add(postRequest);
        }
    }


    @Override
    protected void onDestroy() {
        RequestQueue queue = requestQueueRef.get();
        if (queue != null) {
            queue.cancelAll(request -> true);
        }
        super.onDestroy();
    }
}