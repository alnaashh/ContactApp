package com.example.tugasfirebase.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasfirebase.R;
import com.example.tugasfirebase.DetailContactActivity;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private final Context context;
    private List<ContactWithId> contactList;
    private final ContactClickListener listener;

    public interface ContactClickListener {
        void onUpdateClick(ContactWithId contact);
        void onDeleteClick(String contactId);
    }

    public ContactAdapter(Context context, List<ContactWithId> contactList, ContactClickListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    public void updateData(List<ContactWithId> newList) {
        this.contactList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        ContactWithId contactWithId = contactList.get(position);
        Contact contact = contactWithId.getContact();

        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());
        holder.tvEmail.setText(contact.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailContactActivity.class);
            intent.putExtra("name", contact.getName());
            intent.putExtra("phone", contact.getPhone());
            intent.putExtra("email", contact.getEmail());
            context.startActivity(intent);
        });

        holder.ivMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.contact_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    listener.onUpdateClick(contactWithId);
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    listener.onDeleteClick(contactWithId.getId());
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvEmail;
        ImageView ivMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvPhone = itemView.findViewById(R.id.tv_contact_phone);
            tvEmail = itemView.findViewById(R.id.tv_contact_email);
            ivMore = itemView.findViewById(R.id.iv_more_options);
        }
    }
}