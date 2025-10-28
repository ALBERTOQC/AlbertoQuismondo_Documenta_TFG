package es.albertqc.albertoquismondo_documenta_tfg.ui.sociedades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import es.albertqc.albertoquismondo_documenta_tfg.databinding.FragmentSociedadesBinding;

public class SociedadesFragment extends Fragment {

    private FragmentSociedadesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SociedadesViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SociedadesViewModel.class);

        binding = FragmentSociedadesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}