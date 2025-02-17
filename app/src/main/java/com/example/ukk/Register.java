package com.example.ukk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText etUsr, etPss, etKops, etEmail, etName;
    private Button regis;
    private TextView login;

    final String REGISTER_URL = "http://172.16.0.93/ukk/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsr = findViewById(R.id.username);
        etPss = findViewById(R.id.password);
        etKops = findViewById(R.id.konpass);
        etEmail = findViewById(R.id.email);
        etName = findViewById(R.id.nama);
        regis = findViewById(R.id.regis);
        login = findViewById(R.id.login);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    private void registerUser() {
        String username = etUsr.getText().toString().trim();
        String password = etPss.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String confirmPassword = etKops.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mendaftarkan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Gagal mendaftar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                params.put("name", name);
                params.put("confirmation", confirmPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}