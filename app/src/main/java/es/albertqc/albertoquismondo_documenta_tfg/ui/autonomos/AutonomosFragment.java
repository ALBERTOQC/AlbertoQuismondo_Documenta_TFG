package es.albertqc.albertoquismondo_documenta_tfg.ui.autonomos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.albertqc.albertoquismondo_documenta_tfg.R;
import es.albertqc.albertoquismondo_documenta_tfg.RegistroLogin;
import es.albertqc.albertoquismondo_documenta_tfg.databinding.FragmentAutonomosBinding;

public class AutonomosFragment extends Fragment {

    private FragmentAutonomosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new ViewModelProvider(this).get(AutonomosViewModel.class);
        binding = FragmentAutonomosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // 🎥 Configuración del VideoView
        VideoView videoView = binding.videoAutonomos; // con A mayúscula
        String videoPath = "android.resource://" + requireContext().getPackageName() + "/" + R.raw.videoautonomos;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();
        // se reproduce automáticamente

        // 🔑 Botón para acceder al área de clientes
        Button btnAreaClientes = binding.btnAreaClientes;
        btnAreaClientes.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegistroLogin.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}