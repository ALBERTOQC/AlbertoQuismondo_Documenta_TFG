package es.albertqc.albertoquismondo_documenta_tfg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SociedadMainActivity extends AppCompatActivity implements DialogoDocumentosSociedades.OnCerrarDialogo {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvBienvenida;
    private ImageButton btnVerUsuarios;
    private Button btnVerAgenda;
    private LinearLayout layoutDocumentos;
    private ImageView iconExpandir;
    private boolean documentosVisible = false;

    private final HashMap<String, String> documentosMap = new HashMap<>();

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvBienvenida = findViewById(R.id.tvBienvenida);
        btnVerUsuarios = findViewById(R.id.btnVerUsuarios);
        btnVerAgenda = findViewById(R.id.btnVerAgendaSociedad);
        layoutDocumentos = findViewById(R.id.layoutDocumentos);
        iconExpandir = findViewById(R.id.iconExpandir);

        cargarNombreUsuario();
        configurarVerUsuarios();
        configurarAgenda();
        configurarGastosIngresos();
        configurarGenerarFactura();
        configurarExpansorDocumentos();
        cargarDocumentosFirestore();

        Button btnInfoDocumentos = findViewById(R.id.btnVerInfoDocumentosSociedad);
        btnInfoDocumentos.setOnClickListener(v -> {
            DialogoDocumentosSociedades dialog = new DialogoDocumentosSociedades();
            dialog.show(getSupportFragmentManager(), "DialogoDocumentosSociedades");
        });

        findViewById(R.id.btnCerrar).setOnClickListener(v -> {
            Intent intent = new Intent(SociedadMainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void cargarNombreUsuario() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(docSnapshot -> {
                    if (docSnapshot.exists()) {
                        String nombre = docSnapshot.getString("nombre");
                        tvBienvenida.setText("Bienvenido, " + nombre);
                    }
                })
                .addOnFailureListener(e -> tvBienvenida.setText("Bienvenido, usuario"));
    }

    private void configurarVerUsuarios() {
        btnVerUsuarios.setOnClickListener(v -> {
            DialogoUsuarios dialogo = new DialogoUsuarios();
            dialogo.show(getSupportFragmentManager(), "DialogoUsuarios");
        });
    }

    private void configurarAgenda() {
        btnVerAgenda.setOnClickListener(v -> {
            AgendaSociedadDialog dialogo = new AgendaSociedadDialog();
            dialogo.show(getSupportFragmentManager(), "AgendaSociedadDialog");
        });
    }

    private void configurarGastosIngresos() {
        findViewById(R.id.btnAbrirGastosIngresosSociedad).setOnClickListener(v -> {
            DialogoGastosIngresosSociedades dialog = new DialogoGastosIngresosSociedades();
            dialog.show(getSupportFragmentManager(), "DialogoGastosIngresosSociedades");
        });
    }

    private void configurarGenerarFactura() {
        findViewById(R.id.btnGenerarFacturaSociedad).setOnClickListener(v -> {
            DialogoGenerarFacturaSociedades dialog = new DialogoGenerarFacturaSociedades();
            dialog.show(getSupportFragmentManager(), "DialogoGenerarFacturaSociedades");
        });
    }

    private void configurarExpansorDocumentos() {
        LinearLayout headerDocumentos = findViewById(R.id.headerDocumentos);
        headerDocumentos.setOnClickListener(v -> {
            documentosVisible = !documentosVisible;
            layoutDocumentos.setVisibility(documentosVisible ? View.VISIBLE : View.GONE);
            iconExpandir.setImageResource(documentosVisible ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
        });
    }

    private void cargarDocumentosFirestore() {
        db.collection("documentos").document("estatuto").get()
                .addOnSuccessListener(docSnapshot -> {
                    if (docSnapshot.exists()) {
                        String[] keys = {"itemModelo600", "itemModelo036", "itemModeloTA6", "itemContrato", "itemBaja", "itemVacaciones"};
                        for (String key : keys) {
                            String url = docSnapshot.contains(key) ? docSnapshot.getString(key) : "";
                            documentosMap.put(key, url != null ? url : "");
                        }
                    }
                    configurarClicksDocumentos();
                })
                .addOnFailureListener(e -> {
                    documentosMap.put("itemModelo600", "https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/GC12/600/mod600e.pdf");
                    documentosMap.put("itemModelo036", "https://www.hacienda.gob.es/.../modelo036.pdf");
                    documentosMap.put("itemModeloTA6", "https://www.seg-social.es/.../TA-6.pdf");
                    documentosMap.put("itemContrato", "https://www.inmujeres.gob.es/.../contrato.pdf");
                    documentosMap.put("itemBaja", "https://eal.economistas.es/.../baja.pdf");
                    documentosMap.put("itemVacaciones", "https://www.sesametime.com/.../vacaciones.pdf");
                    configurarClicksDocumentos();
                });
    }

    private void configurarClicksDocumentos() {
        setClickDocumento(R.id.itemModelo600, documentosMap.get("itemModelo600"), "Modelo 600");
        setClickDocumento(R.id.itemModelo036, documentosMap.get("itemModelo036"), "Modelo 036");
        setClickDocumento(R.id.itemModeloTA6, documentosMap.get("itemModeloTA6"), "TA6");
        setClickDocumento(R.id.itemContrato, documentosMap.get("itemContrato"), "Contrato");
        setClickDocumento(R.id.itemBaja, documentosMap.get("itemBaja"), "Baja Voluntaria");
        setClickDocumento(R.id.itemVacaciones, documentosMap.get("itemVacaciones"), "Solicitud de Vacaciones");
    }

    private void setClickDocumento(int idLayout, String url, String nombre) {
        View layout = findViewById(idLayout);
        if (layout != null) {
            TextView tvNombre = layout.findViewById(R.id.tvNombreDocumento);
            if (tvNombre != null) tvNombre.setText(nombre);

            layout.setOnClickListener(v -> {
                if (url != null && !url.isEmpty()) {
                    abrirWeb(url);
                } else {
                    Toast.makeText(this, "URL no disponible", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void abrirWeb(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCerrar() {
        Toast.makeText(this, "Cerrado", Toast.LENGTH_SHORT).show();
    }
}

