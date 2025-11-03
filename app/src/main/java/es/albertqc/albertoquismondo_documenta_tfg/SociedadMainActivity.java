package es.albertqc.albertoquismondo_documenta_tfg;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SociedadMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvBienvenida;
    private RecyclerView recyclerUsuarios;
    private UsuariosAdapter adapter;
    private List<Usuario> listaUsuarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sociedad_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        tvBienvenida = findViewById(R.id.tvBienvenida);
        recyclerUsuarios = findViewById(R.id.mirecycler);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(listaUsuarios);
        recyclerUsuarios.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // mostrrar nombre del usuario actual
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        tvBienvenida.setText("Bienvenido, " + nombre + " 👋");
                    }
                })
                .addOnFailureListener(e ->
                        tvBienvenida.setText("Bienvenido, usuario (error al cargar nombre)")
                );

        // cargar todos los usuarios de Firebase
        cargarUsuarios();
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
                    tvBienvenida.setText("Error al cargar usuarios 😕");
                });
    }
}