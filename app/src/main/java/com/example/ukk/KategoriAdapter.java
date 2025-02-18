package com.example.ukk;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private List<KategoriModel> kategoriList;
    private Context context;
    private String userId;

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
                .setPositiveButton("Yakin", (dialog, id) -> dialog.dismiss())
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

        builder.setTitle("Edit Pelanggan")
                .setPositiveButton("Simpan", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
