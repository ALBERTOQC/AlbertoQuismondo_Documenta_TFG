package es.albertqc.albertoquismondo_documenta_tfg.ui.acerca;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.albertqc.albertoquismondo_documenta_tfg.R;

public class AcercaDeFragment extends Fragment {

    public AcercaDeFragment() {

    }

    public static AcercaDeFragment newInstance() {
        return new AcercaDeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acerca_de, container, false);
    }
}