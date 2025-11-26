package es.albertqc.albertoquismondo_documenta_tfg.ui.sociedades;

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

import es.albertqc.albertoquismondo_documenta_tfg.RegistroLogin;
import es.albertqc.albertoquismondo_documenta_tfg.R;
import es.albertqc.albertoquismondo_documenta_tfg.databinding.FragmentSociedadesBinding;

public class SociedadesFragment extends Fragment {

    private FragmentSociedadesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new ViewModelProvider(this).get(SociedadesViewModel.class);
        binding = FragmentSociedadesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configuración del VideoView
        VideoView videoView = binding.videoSociedades;
        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.videoempresas;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

        // 🔑 Botón para acceder al área de clientes
        Button btnAreaClientes = binding.btnAreaClientesSociedad;
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