package es.albertqc.albertoquismondo_documenta_tfg.ui.autonomos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AutonomosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AutonomosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}