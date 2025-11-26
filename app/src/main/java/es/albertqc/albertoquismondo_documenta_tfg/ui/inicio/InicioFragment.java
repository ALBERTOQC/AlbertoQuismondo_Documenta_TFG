package es.albertqc.albertoquismondo_documenta_tfg.ui.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import es.albertqc.albertoquismondo_documenta_tfg.R;

import es.albertqc.albertoquismondo_documenta_tfg.databinding.FragmentInicioBinding;

public class InicioFragment extends Fragment {

    private FragmentInicioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.ibAutonomo.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_autonomos));

        binding.ibSociedad.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_sociedades));

        binding.ibDocumentacion.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_documentacion));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

