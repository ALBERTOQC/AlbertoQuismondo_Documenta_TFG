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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InicioViewModel homeViewModel =
                new ViewModelProvider(this).get(InicioViewModel.class);

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton botonEnlaceAutonomos = binding.ibAutonomo;
        ImageButton botonEnlaceSociedades = binding.ibSociedad;
        ImageButton botonEnlaceAdmin = binding.ibAdmin;


        // Navegar a AutonomosFragment
        botonEnlaceAutonomos.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.nav_autonomos));

        // Navegar a SociedadesFragment
        botonEnlaceSociedades.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.nav_sociedades));

        // Navegar a AdminFragment
        botonEnlaceAdmin.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.nav_admin));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}