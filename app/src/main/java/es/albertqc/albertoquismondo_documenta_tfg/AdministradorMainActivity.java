package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministradorMainActivity extends AppCompatActivity {

    private RecyclerView recyclerUsuarios;
    private UsuariosAdapter adapter;
    private List<Usuario> listaUsuarios;
    private FirebaseFirestore db;

    private Button btnFiltrarAutonomos, btnFiltrarSociedades, btnVerTodos;
    private Button btnCrearMiembro, btnEliminar;
    private Button btnRestablecerContrasena;

    private int selectedPosition = -1;
    private String selectedUid = null;

    private Spinner spinnerDocumentos;
    private EditText etNuevaUrl;
    private Button btnActualizarUrl;

    // Mapa con los documentos y sus URLs actuales
    private final HashMap<String, String> documentosMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_main);

        db = FirebaseFirestore.getInstance();

        // --- Recycler y Adapter ---
        recyclerUsuarios = findViewById(R.id.recyclerUsuariosAdmin);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(listaUsuarios, usuario -> seleccionarUsuario(listaUsuarios.indexOf(usuario)));
        recyclerUsuarios.setAdapter(adapter);

        // --- Botones de filtrado ---
        btnFiltrarAutonomos = findViewById(R.id.btnFiltrarAutonomos);
        btnFiltrarSociedades = findViewById(R.id.btnFiltrarSociedades);
        btnVerTodos = findViewById(R.id.btnVerTodos);
        btnCrearMiembro = findViewById(R.id.btnCrearMiembro);
        btnEliminar = findViewById(R.id.btnEliminarMiembro);
        btnRestablecerContrasena = findViewById(R.id.btnRestablecerContrasena);

        cargarUsuarios();

        btnFiltrarAutonomos.setOnClickListener(v -> filtrarPorRol("autónomo"));
        btnFiltrarSociedades.setOnClickListener(v -> filtrarPorRol("sociedad"));
        btnVerTodos.setOnClickListener(v -> cargarUsuarios());
        btnCrearMiembro.setOnClickListener(v -> mostrarDialogoCrearMiembro());
        btnEliminar.setOnClickListener(v -> eliminarMiembro());
        btnRestablecerContrasena.setOnClickListener(v -> restablecerContrasena());

        // --- Bloque documentos ---
        spinnerDocumentos = findViewById(R.id.spinnerDocumentos);
        etNuevaUrl = findViewById(R.id.etNuevaUrl);
        btnActualizarUrl = findViewById(R.id.btnActualizarUrl);

        cargarDocumentosFirestore();

        btnActualizarUrl.setOnClickListener(v -> actualizarDocumentoFirestore());

        Button btnCerrar = findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> finish());

    }

    // -------------------------------
    // USUARIOS
    // -------------------------------
    private void seleccionarUsuario(int position) {
        selectedPosition = position;
        selectedUid = listaUsuarios.get(position).getUid();
        adapter.setSelectedPosition(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Seleccionado: " + listaUsuarios.get(position).getNombre(), Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoCrearMiembro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_crear_miembro, null);

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etCorreo = view.findViewById(R.id.etCorreo);
        EditText etPass = view.findViewById(R.id.etPass);
        RadioGroup grupoRoles = view.findViewById(R.id.grupoRoles);

        // --- NUEVO: EditText para descripción ---
        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Introduce descripción");
        ((LinearLayout) view).addView(etDescripcion, 3); // Insertamos después del grupo de roles

        builder.setView(view)
                .setTitle("Crear miembro")
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String correo = etCorreo.getText().toString().trim();
                    String pass = etPass.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    int selected = grupoRoles.getCheckedRadioButtonId();
                    String rol = selected == R.id.rbAdmin ? "administrador" :
                            selected == R.id.rbAutonomo ? "autónomo" : "sociedad";

                    if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || descripcion.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    crearMiembro(nombre, correo, pass, rol, descripcion);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void crearMiembro(String nombre, String correo, String pass, String rol, String descripcion) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(auth -> {
                    String uid = auth.getUser().getUid();
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", nombre);
                    datos.put("correo", correo);
                    datos.put("rol", rol);
                    datos.put("descripcion", descripcion); // <-- NUEVO CAMPO
                    if (rol.equals("administrador")) datos.put("actividad", "Gestión administrativa");

                    db.collection("usuarios").document(uid)
                            .set(datos)
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this, "Miembro creado", Toast.LENGTH_SHORT).show();
                                cargarUsuarios();
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void eliminarMiembro() {
        if (selectedUid == null) {
            Toast.makeText(this, "Selecciona un usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("usuarios").document(selectedUid)
                .delete()
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                    selectedUid = null;
                });
    }

    private void restablecerContrasena() {
        if (selectedUid == null) {
            Toast.makeText(this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
            return;
        }
        Usuario seleccionado = listaUsuarios.get(selectedPosition);
        FirebaseAuth.getInstance().sendPasswordResetEmail(seleccionado.getCorreo())
                .addOnSuccessListener(a -> Toast.makeText(this, "Correo de restablecimiento enviado a " + seleccionado.getCorreo(), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void cargarUsuarios() {
        db.collection("usuarios").get()
                .addOnSuccessListener(query -> {
                    listaUsuarios.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Usuario u = doc.toObject(Usuario.class);
                        u.setUid(doc.getId());
                        listaUsuarios.add(u);
                    }
                    adapter.setSelectedPosition(-1);
                    adapter.notifyDataSetChanged();
                });
    }

    private void filtrarPorRol(String rol) {
        db.collection("usuarios").whereEqualTo("rol", rol).get()
                .addOnSuccessListener(query -> {
                    listaUsuarios.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Usuario u = doc.toObject(Usuario.class);
                        u.setUid(doc.getId());
                        listaUsuarios.add(u);
                    }
                    adapter.setSelectedPosition(-1);
                    adapter.notifyDataSetChanged();
                });
    }

    // -------------------------------
    // DOCUMENTOS FIRESTORE
    // -------------------------------
    private void cargarDocumentosFirestore() {

        db.collection("documentos").document("estatuto")
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "El documento 'estatuto' no existe", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    documentosMap.clear();

                    for (String key : doc.getData().keySet()) {
                        documentosMap.put(key, doc.getString(key));
                    }

                    // Poner el nombre de cada campo en el spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            new ArrayList<>(documentosMap.keySet())
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDocumentos.setAdapter(adapter);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error leyendo Firestore", Toast.LENGTH_SHORT).show());
    }

  /*  private void inicializarDocumentosPorDefecto() {
        // Documentos generales
        documentosMap.put("Estatuto de los Trabajadores","https://www.boe.es/biblioteca_juridica/abrir_pdf.php?id=PUB-DT-2025-139");
        documentosMap.put("Guía de Derechos","https://antoniosilva.es/derechos-laborales-en-espana-guia-completa-para-trabajadores/");
        documentosMap.put("Simuladores Laborales","https://www.asinom.com/documentacion/caracteristicas_asinom.pdf");
        documentosMap.put("Contactos Útiles","https://www.mites.gob.es/es/informacion/infgral/directorio/index.htm");

        // Sindicatos
        documentosMap.put("UGT","https://www.ugt.es/");
        documentosMap.put("CCOO","https://www.ccoo.es/");
        documentosMap.put("USO","https://www.uso.es/");

        // Convenios por CCAA
        documentosMap.put("convenioAndalucía","https://www.ccoo-servicios.es/andalucia/conveniosandalucia/pag1/");
        documentosMap.put("convenioCataluña","https://es.ccoo.cat/convenis/");
        documentosMap.put("convenioMadrid","https://www.comunidad.madrid/servicios/empleo/convenios-colectivos");
        documentosMap.put("convenioValencia","https://valencia.cnt.es/convenios-laborales/");
        documentosMap.put("convenioGalicia","https://convenios.xunta.gal/consultaconvenios/busqueda-convenio/buscar");
        documentosMap.put("convenioPaísVasco","https://www.euskadi.eus/gobierno-vasco/-/convenios-colectivos");
        documentosMap.put("convenioAragón","https://www.aragon.es/documents/d/guest/listaweb-de-sector");
        documentosMap.put("convenioCastillaYLeón","https://www.ccoo-servicios.es/castillayleon/convenios/pag76/");
        documentosMap.put("convenioMurcia","https://www.carm.es/web/pagina?IDCONTENIDO=246&IDTIPO=200");
        documentosMap.put("convenioExtremadura","https://www.ugtextremadura.org/convenios-colectivos-extremadura-servicios-publicos");

        // Documentos Autónomos
        documentosMap.put("itemModelo036", "https://www.hacienda.gob.es/.../modelo036.pdf");
        documentosMap.put("itemModelo037", "https://www.hacienda.gob.es/.../modelo037.pdf");
        documentosMap.put("itemModeloTA0521", "https://amanecemetropolis.net/.../ta0521.pdf");
        documentosMap.put("itemContrato", "https://www.inmujeres.gob.es/.../contrato.pdf");
        documentosMap.put("itemBaja", "https://eal.economistas.es/.../baja.pdf");
        documentosMap.put("itemVacaciones", "https://www.sesametime.com/.../vacaciones.pdf");

        // Documentos Sociedades
        documentosMap.put("itemModelo600", "https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/GC12/600/mod600e.pdf");
        documentosMap.put("itemModeloTA6", "https://www.seg-social.es/.../TA-6.pdf");

    } */


    private void actualizarSpinnerDocumentos() {
        List<String> listaEstatuto = new ArrayList<>();
        List<String> listaConvenios = new ArrayList<>();
        List<String> listaSindicatos = new ArrayList<>();
        List<String> listaOtros = new ArrayList<>();

        for (String key : documentosMap.keySet()) {
            if (key.equals("Estatuto de los Trabajadores")) {
                listaEstatuto.add(key);
            } else if (key.toLowerCase().contains("convenio")) {
                listaConvenios.add(key);
            } else if (key.equals("UGT") || key.equals("CCOO") || key.equals("USO")) {
                listaSindicatos.add(key);
            } else {
                listaOtros.add(key);
            }
        }

        // Unir listas en el orden deseado
        List<String> listaOrdenada = new ArrayList<>();
        listaOrdenada.addAll(listaEstatuto);
        listaOrdenada.addAll(listaConvenios);
        listaOrdenada.addAll(listaSindicatos);
        listaOrdenada.addAll(listaOtros);

        ArrayAdapter<String> adapterDocs = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                listaOrdenada);
        adapterDocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocumentos.setAdapter(adapterDocs);
    }

    private void actualizarDocumentoFirestore() {

        String clave = spinnerDocumentos.getSelectedItem().toString();
        String nuevaUrl = etNuevaUrl.getText().toString().trim();

        if (nuevaUrl.isEmpty()) {
            Toast.makeText(this, "Introduce una URL", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("documentos").document("estatuto")
                .update(clave, nuevaUrl)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "URL actualizada correctamente", Toast.LENGTH_SHORT).show();
                    etNuevaUrl.setText("");
                    documentosMap.put(clave, nuevaUrl); // actualizar en memoria
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "ERROR al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}
