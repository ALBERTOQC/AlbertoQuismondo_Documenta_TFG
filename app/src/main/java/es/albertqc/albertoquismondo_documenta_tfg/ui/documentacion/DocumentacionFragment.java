package es.albertqc.albertoquismondo_documenta_tfg.ui.documentacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import es.albertqc.albertoquismondo_documenta_tfg.R;

public class DocumentacionFragment extends Fragment {

    private Spinner spinnerCCAA;
    private Button btnEstatuto, btnVerConvenio;
    private Button btnUGT, btnCCOO, btnUSO;
    private Button btnGuiaDerechos, btnSimuladores, btnContactos;

    private FirebaseFirestore db;

    // Mapa dinámico de convenios
    private final HashMap<String, String> conveniosMap = new HashMap<>();
    private final HashMap<String, String> otrosDocumentosMap = new HashMap<>();

    public DocumentacionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_documentacion, container, false);

        db = FirebaseFirestore.getInstance();

        // --- Referencias a vistas ---
        spinnerCCAA = view.findViewById(R.id.spinnerCCAA);
        btnEstatuto = view.findViewById(R.id.btnEstatuto);
        btnVerConvenio = view.findViewById(R.id.btnVerConvenio);
        btnUGT = view.findViewById(R.id.btnUGT);
        btnCCOO = view.findViewById(R.id.btnCCOO);
        btnUSO = view.findViewById(R.id.btnUSO);
        btnGuiaDerechos = view.findViewById(R.id.btnGuiaDerechos);
        btnSimuladores = view.findViewById(R.id.btnSimuladores);
        btnContactos = view.findViewById(R.id.btnContactos);

        cargarDocumentosFirestore();

        // --- Botón Estatuto de los Trabajadores ---
        btnEstatuto.setOnClickListener(v -> abrirUrl(
                otrosDocumentosMap.getOrDefault("Estatuto de los Trabajadores",
                        "https://www.boe.es/biblioteca_juridica/abrir_pdf.php?id=PUB-DT-2025-139")));

        // --- Botón Ver Convenio según CCAA seleccionada ---
        btnVerConvenio.setOnClickListener(v -> {
            String comunidad = spinnerCCAA.getSelectedItem().toString();
            String url = conveniosMap.get(comunidad);

            if (url != null) {
                abrirUrl(url);
            } else {
                Toast.makeText(getContext(), "No hay convenio disponible para " + comunidad, Toast.LENGTH_SHORT).show();
            }
        });

        // --- Botones Sindicatos ---
        btnUGT.setOnClickListener(v -> abrirUrl(otrosDocumentosMap.getOrDefault("UGT", "https://www.ugt.es/")));
        btnCCOO.setOnClickListener(v -> abrirUrl(otrosDocumentosMap.getOrDefault("CCOO", "https://www.ccoo.es/")));
        btnUSO.setOnClickListener(v -> abrirUrl(otrosDocumentosMap.getOrDefault("USO", "https://www.uso.es/")));

        // --- Botón Guía de Derechos ---
        btnGuiaDerechos.setOnClickListener(v -> abrirUrl(
                otrosDocumentosMap.getOrDefault("Guía de Derechos",
                        "https://antoniosilva.es/derechos-laborales-en-espana-guia-completa-para-trabajadores/")));

        // --- Botón Simuladores ---
        btnSimuladores.setOnClickListener(v -> abrirUrl(
                otrosDocumentosMap.getOrDefault("Simuladores Laborales",
                        "https://www.asinom.com/documentacion/caracteristicas_asinom.pdf")));

        // --- Botón Contactos Útiles ---
        btnContactos.setOnClickListener(v -> abrirUrl(
                otrosDocumentosMap.getOrDefault("Contactos Útiles",
                        "https://www.mites.gob.es/es/informacion/infgral/directorio/index.htm")));

        return view;
    }

    /**
     * Carga los convenios y documentos desde Firestore y los guarda en mapas dinámicos.
     */
    private void cargarDocumentosFirestore() {
        db.collection("documentos").document("estatuto").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Cargar convenios por CCAA
                        String[] comunidades = {"Andalucía","Cataluña","Madrid","Valencia","Galicia",
                                "País Vasco","Aragón","Castilla y León","Murcia","Extremadura","Castilla-La Mancha"};
                        for (String ccaa : comunidades) {
                            if (documentSnapshot.contains("convenio" + ccaa.replace(" ", ""))) {
                                conveniosMap.put(ccaa, documentSnapshot.getString("convenio" + ccaa.replace(" ", "")));
                            }
                        }

                        // Cargar otros documentos
                        String[] otrosKeys = {"url", "guiaDerechos", "simuladores", "contactos", "ccoo", "ugt", "uso"};
                        for (String key : otrosKeys) {
                            String mapKey = "";
                            if (key.equalsIgnoreCase("url")) mapKey = "Estatuto de los Trabajadores";
                            else if (key.equalsIgnoreCase("guiaDerechos")) mapKey = "Guía de Derechos";
                            else if (key.equalsIgnoreCase("simuladores")) mapKey = "Simuladores Laborales";
                            else if (key.equalsIgnoreCase("contactos")) mapKey = "Contactos Útiles";
                            else if (key.equalsIgnoreCase("ccoo")) mapKey = "CCOO";
                            else if (key.equalsIgnoreCase("ugt")) mapKey = "UGT";
                            else if (key.equalsIgnoreCase("uso")) mapKey = "USO";

                            if (documentSnapshot.contains(key)) {
                                // Solo añade si la clave se pudo mapear
                                if (!mapKey.isEmpty()) {
                                    otrosDocumentosMap.put(mapKey, documentSnapshot.getString(key));
                                }
                            }
                        }
                    } else {
                        inicializarConveniosPorDefecto();
                    }
                })
                .addOnFailureListener(e -> inicializarConveniosPorDefecto());
    }

    private void inicializarConveniosPorDefecto() {
        // Convenios por defecto
        conveniosMap.put("Andalucía", "https://www.ccoo-servicios.es/andalucia/conveniosandalucia/pag1/");
        conveniosMap.put("Cataluña","https://es.ccoo.cat/convenis/");
        conveniosMap.put("Madrid","https://www.comunidad.madrid/servicios/empleo/convenios-colectivos");
        conveniosMap.put("Valencia","https://valencia.cnt.es/convenios-laborales/");
        conveniosMap.put("Castilla-La Mancha","https://www.castillalamancha.es/gobierno/haciendayaapp/estructura/dgpfp/actuaciones/viii-convenio-colectivo");
        conveniosMap.put("Galicia", "https://convenios.xunta.gal/consultaconvenios/busqueda-convenio/buscar");
        conveniosMap.put("País Vasco", "https://www.euskadi.eus/gobierno-vasco/-/convenios-colectivos");
        conveniosMap.put("Aragón", "https://www.aragon.es/documents/d/guest/listaweb-de-sector");
        conveniosMap.put("Castilla y León", "https://www.ccoo-servicios.es/castillayleon/convenios/pag76/");
        conveniosMap.put("Murcia", "https://www.carm.es/web/pagina?IDCONTENIDO=246&IDTIPO=200");
        conveniosMap.put("Extremadura", "https://www.ugtextremadura.org/convenios-colectivos-extremadura-servicios-publicos");

        // Otros documentos por defecto
        otrosDocumentosMap.put("Estatuto de los Trabajadores","https://www.boe.es/biblioteca_juridica/abrir_pdf.php?id=PUB-DT-2025-139");
        otrosDocumentosMap.put("Guía de Derechos","https://antoniosilva.es/derechos-laborales-en-espana-guia-completa-para-trabajadores/");
        otrosDocumentosMap.put("Simuladores Laborales","https://www.asinom.com/documentacion/caracteristicas_asinom.pdf");
        otrosDocumentosMap.put("Contactos Útiles","https://www.mites.gob.es/es/informacion/infgral/directorio/index.htm");
        otrosDocumentosMap.put("UGT","https://www.ugt.es/");
        otrosDocumentosMap.put("CCOO","https://www.ccoo.es/");
        otrosDocumentosMap.put("USO","https://www.uso.es/");
    }

    /**
     * Abre una URL externa con un Intent seguro.
     */
    private void abrirUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show();
        }
    }
}