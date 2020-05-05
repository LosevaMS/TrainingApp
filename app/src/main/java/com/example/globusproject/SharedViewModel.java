package com.example.globusproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<ArrayList<InlineExercises>> text = new MutableLiveData<>();

    public void setText(ArrayList<InlineExercises> input) {
        text.setValue(input);
    }

    public LiveData<ArrayList<InlineExercises>> getText() {
        return text;
    }
}
