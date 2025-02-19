package com.example.ukk;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class   KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

        private List<KategoriModel> kategoriList;
        private Context context;

        private static final String URL_DELETE = "http://172.16.0.93/ukk/hapusKat.php";
        private static final String URL_EDIT = "http://172.16.0.93/ukk/editKat.php";

        public KategoriAdapter(Context context, List<KategoriModel> kategoriList) {
            this.context = context;
            this.kategoriList = kategoriList;
        }

        @NonNull
        @Override
        public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_kategori, parent, false);
            return new KategoriViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
            KategoriModel kategori = kategoriList.get(position);
            holder.textNomor.setText(String.valueOf(position + 1));
            holder.textKategori.setText(kategori.getKategori());

            // DELETE ACTION
            holder.deleteIcon.setOnClickListener(v -> showDeleteConfirmationDialog(kategori, position));

            // EDIT ACTION
            holder.editIcon.setOnClickListener(v -> showEditDialog(kategori, position));
        }

        @Override
        public int getItemCount() {
            return kategoriList.size();
        }

        public static class KategoriViewHolder extends RecyclerView.ViewHolder {
            TextView textNomor, textKategori;
            ImageView deleteIcon, editIcon;

            public KategoriViewHolder(@NonNull View itemView) {
                super(itemView);
                textNomor = itemView.findViewById(R.id.textNomor);
                textKategori = itemView.findViewById(R.id.textKategori);
                deleteIcon = itemView.findViewById(R.id.deleteIcon);
                editIcon = itemView.findViewById(R.id.editIcon);
            }
        }

        private void showDeleteConfirmationDialog(KategoriModel kategori, int position) {
            new AlertDialog.Builder(context)
                    .setMessage("Apakah Anda yakin ingin menghapus Kategori ini?")
                    .setCancelable(false)
                    .setPositiveButton("Yakin", (dialog, id) -> {
                        dialog.dismiss();
                        deleteKategori(kategori, position); // Panggil fungsi hapus di sini
                    })
                    .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss())
                    .create()
                    .show();
        }

        private void showEditDialog(KategoriModel kategori, int position) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_edit_kategori, null);
            builder.setView(view);

            TextInputEditText editNama = view.findViewById(R.id.editNama);

            editNama.setText(kategori.getKategori());

            builder.setTitle("Edit Kategori")
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String newName = editNama.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            editKategori(kategori, newName, position); // Panggil function editKategori
                        } else {
                            Toast.makeText(context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        // Fungsi untuk menghapus kategori
        private void deleteKategori(KategoriModel kategori, int position) {

            StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                    response -> {
                        if (response.trim().equals("Category deleted successfully")) {
                            Toast.makeText(context, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show();
                            kategoriList.remove(position); // Hapus item dari daftar
                            notifyItemRemoved(position); // Perbarui adapter
                        } else {
                            Toast.makeText(context, "Gagal menghapus kategori: " + response, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(context, "Gagal menghapus kategori. Kode: " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("category_id", String.valueOf(kategori.getId()));
                    return params;
                }
            };

            // Tambahkan request ke antrian Volley
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }

        private void editKategori(KategoriModel kategori, String newName, int position) {

            // Validasi input sebelum dikirim ke server
            if (newName.isEmpty()) {
                Toast.makeText(context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                    response -> {
                        // Cek apakah respons mengandung pesan sukses
                        if (response.trim().equalsIgnoreCase("Category updated successfully")) {
                            Toast.makeText(context, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            kategori.setKategori(newName); // Update data di model
                            notifyItemChanged(position); // Refresh item di RecyclerView
                        } else {
                            Toast.makeText(context, "Gagal memperbarui kategori: " + response, Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = "Terjadi kesalahan, coba lagi.";
                        if (error.networkResponse != null) {
                            errorMessage = "Error " + error.networkResponse.statusCode + ": " + new String(error.networkResponse.data);
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("category_id", String.valueOf(kategori.getId())); // Pastikan ID kategori benar
                    params.put("new_name", newName);
                    return params;
                }
            };

            // Menambahkan request ke antrian Volley
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

    }
