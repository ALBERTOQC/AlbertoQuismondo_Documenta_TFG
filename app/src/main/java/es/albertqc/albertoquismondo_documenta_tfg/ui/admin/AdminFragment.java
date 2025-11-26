package es.albertqc.albertoquismondo_documenta_tfg.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import es.albertqc.albertoquismondo_documenta_tfg.AdministradorMainActivity;
import es.albertqc.albertoquismondo_documenta_tfg.R;
public class AdminFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button btnEntrar = view.findViewById(R.id.btnEntrarAdministrador);

        btnEntrar.setOnClickListener(v -> mostrarDialogoLoginAdmin());

        return view;
    }

    private void mostrarDialogoLoginAdmin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_admin, null);

        EditText etCorreo = dialogView.findViewById(R.id.etCorreoAdmin);
        EditText etPass = dialogView.findViewById(R.id.etPassAdmin);

        builder.setView(dialogView)
                .setTitle("Acceso Administrador")
                .setPositiveButton("Entrar", (dialog, which) -> {
                    String correo = etCorreo.getText().toString().trim();
                    String pass = etPass.getText().toString().trim();

                    if (correo.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(getActivity(), "Rellena los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loginAdministrador(correo, pass);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loginAdministrador(String correo, String pass) {

        mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        String rol = doc.getString("rol");

                                        if ("administrador".equalsIgnoreCase(rol)) {
                                            // ACCESO CORRECTO
                                            Intent intent = new Intent(getActivity(), AdministradorMainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "No tienes permisos de administrador", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

