package es.albertqc.albertoquismondo_documenta_tfg;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroLogin extends AppCompatActivity {

    private EditText etNombre, etActividad, etCorreo, etContrasenia;
    private RadioGroup rgRoles;
    private Button btnLogin, btnRegistrarse;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etActividad = findViewById(R.id.etActividad);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasenia = findViewById(R.id.etContrasenia);
        rgRoles = findViewById(R.id.rgRoles);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        btnRegistrarse.setOnClickListener(v -> registrarUsuario());
        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String actividad = etActividad.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasenia = etContrasenia.getText().toString().trim();

        int idRol = rgRoles.getCheckedRadioButtonId();
        RadioButton rbSeleccionado = findViewById(idRol);
        String rol = (rbSeleccionado != null) ? rbSeleccionado.getText().toString().toLowerCase() : "";

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(actividad) || TextUtils.isEmpty(correo)
                || TextUtils.isEmpty(contrasenia) || TextUtils.isEmpty(rol)) {
            Toast.makeText(this, "Rellena todos los campos y selecciona un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("nombre", nombre);
                        datosUsuario.put("actividad", actividad);
                        datosUsuario.put("correo", correo);
                        datosUsuario.put("rol", rol);

                        db.collection("usuarios").document(userId)
                                .set(datosUsuario)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(this, "Error al registrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String contrasenia = etContrasenia.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasenia)) {
            Toast.makeText(this, "Introduce el correo y la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        db.collection("usuarios").document(userId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {

                                        String rol = documentSnapshot.getString("rol");

                                        if ("autónomo".equalsIgnoreCase(rol)) {
                                            startActivity(new Intent(this, AutonomoMainActivity.class));

                                        } else if ("sociedad".equalsIgnoreCase(rol)) {
                                            startActivity(new Intent(this, SociedadMainActivity.class));

                                        } else if ("administrador".equalsIgnoreCase(rol)) {
                                            startActivity(new Intent(this, AdministradorMainActivity.class));

                                        } else {
                                            Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}