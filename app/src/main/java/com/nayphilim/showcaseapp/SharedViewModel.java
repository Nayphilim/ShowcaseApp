package com.nayphilim.showcaseapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> FirstName = new MutableLiveData<String>();
    private MutableLiveData<String> LastName = new MutableLiveData<String>();
    private MutableLiveData<String> Email = new MutableLiveData<String>();

    public void setLastName(String lastName) {
        LastName.setValue(lastName);
    }

    public void setFirstName(String firstName) {
        FirstName.setValue(firstName);
    }

    public void setEmail(String email) {
        Email.setValue(email);
    }

    public LiveData<String> getFirstName() {
        return FirstName;
    }

    public LiveData<String> getLastName() {
        return LastName;
    }

    public LiveData<String> getEmail() {
        return Email;
    }
}
