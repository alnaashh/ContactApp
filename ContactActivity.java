package com.example.tugasfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasfirebase.models.Contact;
import com.example.tugasfirebase.models.ContactWithId;
import com.example.tugasfirebase.models.ContactAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, ContactAdapter.ContactClickListener {

    private TextView tvEmail, tvUid;
    private MaterialButton btnKeluar, btnSubmit;
    private TextInputEditText etName, etPhone, etEmail;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ContactAdapter contactAdapter;
    private List<ContactWithId> contactList;

    private String contactToUpdateId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("contacts");

        tvEmail = findViewById(R.id.tv_email);
        tvUid = findViewById(R.id.tv_uid);
        btnKeluar = findViewById(R.id.btn_keluar);
        btnSubmit = findViewById(R.id.btn_submit);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_contact_email);
        recyclerView = findViewById(R.id.recycler_view);

        btnKeluar.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList, this);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            tvUid.setText(currentUser.getUid());
            loadContacts();
        }
    }

    private void loadContacts() {
        databaseReference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    String id = data.getKey();
                    if (contact != null) {
                        contactList.add(new ContactWithId(id, contact));
                    }
                }
                contactAdapter.updateData(contactList);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ContactActivity.this, "Gagal memuat kontak", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Nama wajib diisi");
            result = false;
        }

        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Nomor telepon wajib diisi");
            result = false;
        }

        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Email wajib diisi");
            result = false;
        }

        return result;
    }

    private void submitContact() {
        if (!validateForm()) return;

        Contact newContact = new Contact(
                etName.getText().toString(),
                etPhone.getText().toString(),
                etEmail.getText().toString()
        );

        if (contactToUpdateId != null) {
            // Update mode
            databaseReference.child(mAuth.getUid()).child(contactToUpdateId).setValue(newContact)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Kontak diperbarui", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal memperbarui kontak", Toast.LENGTH_SHORT).show());
            contactToUpdateId = null;
        } else {
            databaseReference.child(mAuth.getUid()).push()
                    .setValue(newContact)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(ContactActivity.this, "Kontak berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ContactActivity.this, "Gagal menambahkan kontak", Toast.LENGTH_SHORT).show());
        }
    }

    private void clearForm() {
        etName.setText("");
        etPhone.setText("");
        etEmail.setText("");
    }

    private void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(ContactActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_keluar) {
            logOut();
        } else if (v.getId() == R.id.btn_submit) {
            submitContact();
        }
    }

    @Override
    public void onUpdateClick(ContactWithId contact) {
        etName.setText(contact.getContact().getName());
        etPhone.setText(contact.getContact().getPhone());
        etEmail.setText(contact.getContact().getEmail());
        contactToUpdateId = contact.getId();
    }

    @Override
    public void onDeleteClick(String contactId) {
        databaseReference.child(mAuth.getUid()).child(contactId).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Kontak dihapus", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus kontak", Toast.LENGTH_SHORT).show());
    }
}