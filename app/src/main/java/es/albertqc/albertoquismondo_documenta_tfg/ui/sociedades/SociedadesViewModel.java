package es.albertqc.albertoquismondo_documenta_tfg.ui.sociedades;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SociedadesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SociedadesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}