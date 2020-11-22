package com.gps.ludke.ui.produto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProdutoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProdutoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}