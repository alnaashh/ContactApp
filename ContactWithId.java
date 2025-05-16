package com.example.tugasfirebase.models;

public class ContactWithId {
    private String id;
    private Contact contact;

    public ContactWithId(String id, Contact contact) {
        this.id = id;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}