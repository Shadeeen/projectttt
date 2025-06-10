package com.example.project;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin;

    TextView name;
     TextView email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.etEmail);
        txtPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:80/school_backend/login.php";

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        JSONObject jsonBody = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            String role=response.getString("role");

                            Intent intent=null;
                            if(role.equals("student")){
                                 intent= new Intent(this, MainActivityStudent.class);
                            }
                           else if(role.equals("teacher")){
                               // intent= new Intent(this, MainActivityTeacher.class);
                            }
                           else {
                                intent= new Intent(this, MainActivity.class);
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("user_id", Integer.parseInt(response.getString("user_id")));
                            editor.putString("role", response.getString("role"));
                            editor.putString("name",response.getString("name"));
                            editor.putString("email",response.getString("email"));
                            editor.apply();

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("karmel",error.toString());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);

}



}
