package es.albertqc.albertoquismondo_documenta_tfg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class SociedadMainActivity extends AppCompatActivity implements DialogoDocumentosSociedades.OnCerrarDialogo  {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvBienvenida;
    private ImageButton btnVerUsuarios;

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

        // Inicializamos Firebase y vistas
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);

        // Cargar nombre del usuario actual
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        tvBienvenida.setText("Bienvenido, " + nombre + " ");
                    }
                })
                .addOnFailureListener(e ->
                        tvBienvenida.setText("Bienvenido, usuario (error al cargar nombre)")
                );

        // Mostrar el diálogo al pulsar el botón
        btnVerUsuarios.setOnClickListener(v -> {
            DialogoUsuarios dialogo = new DialogoUsuarios();
            dialogo.show(getSupportFragmentManager(), "DialogoUsuarios");
        });
        // Botón para abrir el diálogo informativo
        Button btnVerInfoDocumentos = findViewById(R.id.btnVerInfoDocumentosSociedad);
        btnVerInfoDocumentos.setOnClickListener(v -> {
            DialogoDocumentosSociedades dialogo = new DialogoDocumentosSociedades();
            dialogo.show(getSupportFragmentManager(), "DialogoDocumentosSociedades");
        });

        // Descargar documentos
        ImageButton btnDescargar600 = findViewById(R.id.btnDescargar600);
        btnDescargar600.setOnClickListener(v -> abrirWeb("https://www.agenciatributaria.es/static_files/Sede/ITP_AJD/modelo600.pdf"));

        ImageButton btnDescargar036 = findViewById(R.id.btnDescargar036Sociedad);
        btnDescargar036.setOnClickListener(v -> abrirWeb("https://www.hacienda.gob.es/SGT/NormativaDoctrina/main/main_2017/anexo%20ii%20-%20modelo%20036.pdf"));

        ImageButton btnDescargarTA6 = findViewById(R.id.btnDescargarTA6);
        btnDescargarTA6.setOnClickListener(v -> abrirWeb("https://www.seg-social.es/wps/wcm/connect/wss/3eb2f3a1-f501-46ce-928e-d1a00b5441b2/TA6.pdf"));
    }

    private void abrirWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onCerrar() {
        Toast.makeText(this, "Cerrado", Toast.LENGTH_SHORT).show();
    }
}