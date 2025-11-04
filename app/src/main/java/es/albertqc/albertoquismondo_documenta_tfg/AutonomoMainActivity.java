package es.albertqc.albertoquismondo_documenta_tfg;

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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;


public class AutonomoMainActivity extends AppCompatActivity implements DialogoDocumentosAutonomos.OnCerrarDialogo{


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvBienvenida;
    private ImageButton btnVerUsuarios;

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
        // Inicializamos Firebase y vistas
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);

        // Botones de descarga de modelos
        ImageButton btnDescargar036 = findViewById(R.id.btnDescargar036);
        ImageButton btnDescargar037 = findViewById(R.id.btnDescargar037);
        ImageButton btnDescargarTA0521 = findViewById(R.id.btnDescargarTA0521);

        // Botón para ver info general de documentos
        Button btnVerInfoDocumentos = findViewById(R.id.btnVerInfoDocumentos);

        // Cargar nombre del usuario actual desde Firebase
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

        // Mostrar el diálogo de usuarios
        btnVerUsuarios.setOnClickListener(v -> {
            DialogoUsuarios dialogo = new DialogoUsuarios();
            dialogo.show(getSupportFragmentManager(), "DialogoUsuarios");
        });

        // Mostrar el diálogo informativo sobre los documentos
        btnVerInfoDocumentos.setOnClickListener(v -> {
            DialogoDocumentosAutonomos dialogo = new DialogoDocumentosAutonomos();
            dialogo.show(getSupportFragmentManager(), "DialogoDocumentos");
        });

        // Abrir URL del Modelo 036 en navegador
        btnDescargar036.setOnClickListener(v -> {
            String url036 = "https://www.hacienda.gob.es/SGT/NormativaDoctrina/main/main_2017/anexo%20ii%20-%20modelo%20036.pdf";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url036));
            startActivity(intent);
        });

        //  Abrir URL del Modelo 037 en navegador
        btnDescargar037.setOnClickListener(v -> {
            String url037 = "https://www.hacienda.gob.es/SGT/NormativaDoctrina/main/main_2017/anexo%20iii%20-%20modelo%20037.pdf";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url037));
            startActivity(intent);
        });

        btnDescargarTA0521.setOnClickListener(v -> {
            String urlTA0521 = "https://www.fremap.es/SiteCollectionDocuments/Formularios/cobertura_contingencias_prof_y_cese_actividad.pdf";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTA0521));
            startActivity(intent);
        });
    }

    @Override
    public void onCerrar() {
        Toast.makeText(this, "Diálogo cerrado", Toast.LENGTH_SHORT).show();
    }
}