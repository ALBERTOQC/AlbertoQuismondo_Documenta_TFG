package es.albertqc.albertoquismondo_documenta_tfg.ui.autonomos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import es.albertqc.albertoquismondo_documenta_tfg.AutonomoMainActivity;
import es.albertqc.albertoquismondo_documenta_tfg.databinding.FragmentAutonomosBinding;

public class AutonomosFragment extends Fragment {


    private FragmentAutonomosBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etNombre, etCorreo, etContrasenia;
    private RadioGroup rgRoles;
    private Button btnLogin, btnRegistrarse;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new ViewModelProvider(this).get(AutonomosViewModel.class);
        binding = FragmentAutonomosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        etNombre = binding.etNombre;
        etCorreo = binding.etCorreo;
        etContrasenia = binding.etContrasenia;
        rgRoles = binding.rgRoles;
        btnLogin = binding.button3;
        btnRegistrarse = binding.btnRegistrarse;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegistrarse.setOnClickListener(v -> registrarUsuario());
        btnLogin.setOnClickListener(v -> iniciarSesion());

        return root;
    }

    // método para registrar nuevo usuario
    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasenia = etContrasenia.getText().toString().trim();

        int idRol = rgRoles.getCheckedRadioButtonId();
        RadioButton rbSeleccionado = getView().findViewById(idRol);
        String rol = (rbSeleccionado != null) ? rbSeleccionado.getText().toString().toLowerCase() : "";

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) ||
                TextUtils.isEmpty(contrasenia) || TextUtils.isEmpty(rol)) {
            Toast.makeText(getContext(), "Rellena todos los campos y selecciona un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> datosUsuario = new HashMap<>();
                            datosUsuario.put("nombre", nombre);
                            datosUsuario.put("correo", correo);
                            datosUsuario.put("rol", rol);

                            db.collection("usuarios").document(userId)
                                    .set(datosUsuario)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Error al guardar en Firestore", Toast.LENGTH_SHORT).show()
                                    );
                        } else {
                            Toast.makeText(getContext(), "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // método para iniciar sesión
    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String contrasenia = etContrasenia.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(getContext(), "Introduce el correo y la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        db.collection("usuarios").document(userId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String rol = documentSnapshot.getString("rol");
                                        if ("Autónomo".equalsIgnoreCase(rol)) {
                                            Intent intent = new Intent(getContext(), AutonomoMainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "No tienes rol de autónomo", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}