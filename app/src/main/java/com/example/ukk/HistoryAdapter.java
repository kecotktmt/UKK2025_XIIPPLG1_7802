package com.example.ukk;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> historyList;
    private Context context;
    private static final String COMPLETE_TASK_URL = "http://172.16.0.93/ukk/undoCompleteTask.php";

    public HistoryAdapter(Context context, List<HistoryModel> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryModel history = historyList.get(position);

        holder.txtCategory.setText(history.getCategory());
        holder.txtTask.setText(history.getTask());
        holder.txtStatus.setText(history.getStatus());

        // Tampilkan ikon checklist hanya jika tugas belum selesai
        if ("complete".equals(history.getStatus())) {
            holder.imgComplete.setImageResource(R.drawable.ic_checked); // Ganti dengan ikon "completed"
            holder.imgComplete.setEnabled(false); // Nonaktifkan klik
        } else {
            holder.imgComplete.setImageResource(R.drawable.ic_check); // Ganti dengan ikon "not completed"
            holder.imgComplete.setEnabled(true);
        }

        // Klik ikon checklist untuk menandai tugas selesai
        holder.imgComplete.setOnClickListener(v -> markTaskComplete(history, holder));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategory, txtTask, txtStatus;
        ImageView imgComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.taskCategory);
            txtTask = itemView.findViewById(R.id.taskTitle);
            txtStatus = itemView.findViewById(R.id.taskStatus);
            imgComplete = itemView.findViewById(R.id.imgComplete);
        }
    }

    // Function untuk menandai tugas sebagai complete
    private void markTaskComplete(HistoryModel history, HistoryAdapter.ViewHolder holder) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                COMPLETE_TASK_URL,
                response -> {
                    Log.d("TaskAdapter", "Response: " + response);
                    if (response.equals("Task marked as 'not complete'")) {
                        history.setStatus("not complete");
                        holder.imgComplete.setImageResource(R.drawable.ic_check);
                        holder.imgComplete.setEnabled(false);
                    }
                },
                error -> Log.e("TaskAdapter", "Volley Error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", history.getId());
                return params;
            }
        };

        requestQueue.add(postRequest);
    }
}
