package com.example.tugasfirebase;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailContactActivity extends AppCompatActivity {

    TextView tvDetailName, tvDetailPhone, tvDetailEmail;
    ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        imgAvatar = findViewById(R.id.imgAvatar);

        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");

        tvDetailName.setText(name);
        tvDetailPhone.setText(phone);
        tvDetailEmail.setText(email);
    }
}