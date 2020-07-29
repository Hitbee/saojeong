package com.example.saojeong.model;

import com.example.saojeong.R;

import java.util.ArrayList;

public class ContactFullview {

    private int mFvImage;
    private int mFvLeft;
    private int mFvRight;

    public ContactFullview(int mFvLeft, int mFvRight, int mFvImage) {
        this.mFvLeft = mFvLeft;
        this.mFvRight = mFvRight;
        this.mFvImage = mFvImage;
    }

    public int getmFvLeft() {
        return mFvLeft;
    }

    public int getmFvRight() {
        return mFvRight;
    }

    public int getmFvImage() {
        return mFvImage;
    }

    public static ArrayList<ContactFullview> createContactsList(int numContacts) {
        ArrayList<ContactFullview> contacts = new ArrayList<ContactFullview>();


        for(int i = 1; i <= numContacts; i++) {
            contacts.add(new ContactFullview(android.R.drawable.ic_media_previous, android.R.drawable.ic_media_next, R.drawable.fullview));
        }

        return contacts;
    }
}