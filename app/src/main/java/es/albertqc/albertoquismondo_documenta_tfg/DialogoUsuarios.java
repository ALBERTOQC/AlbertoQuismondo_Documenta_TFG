package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DialogoUsuarios extends DialogFragment {

    private RecyclerView recyclerView;
    private UsuariosAdapter adapter;
    private List<Usuario> listaUsuarios;
    private FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Creamos la vista del diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.usuarios_dialogo, null);

        // Inicializamos elementos
        recyclerView = view.findViewById(R.id.recyclerUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(listaUsuarios);
        recyclerView.setAdapter(adapter);

        // Configuramos Firestore
        db = FirebaseFirestore.getInstance();
        cargarUsuarios();

        builder.setView(view)
                .setTitle("Miembros de la comunidad")
                .setPositiveButton("Cerrar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void cargarUsuarios() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    listaUsuarios.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Usuario usuario = document.toObject(Usuario.class);
                        listaUsuarios.add(usuario);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // si falla, pendiente mostrar un Toast
                });
    }
}
