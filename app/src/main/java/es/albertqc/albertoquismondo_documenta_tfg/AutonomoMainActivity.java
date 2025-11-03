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

public class AutonomoMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvBienvenida;
    private RecyclerView recyclerView;
    private UsuariosAdapter adapter;
    private List<Usuario> listaUsuarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_autonomo_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // se inicializa las vistas
        tvBienvenida = findViewById(R.id.tvBienvenida);
        recyclerView = findViewById(R.id.mirecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // se inciia Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // configuro el adapter
        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(listaUsuarios);
        recyclerView.setAdapter(adapter);

        // cargo nombre dle ususairo actual
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

        // cargo todos los usuarios desde FIrebase
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
                .addOnFailureListener(e ->
                        tvBienvenida.setText("Error al cargar usuarios 😕")
                );
    }
}