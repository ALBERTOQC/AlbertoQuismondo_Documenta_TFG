package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgendaSociedadDialog extends DialogFragment {

    private EditText etDescripcion, etCliente;
    private TextView tvFechaHora;
    private ListView listaEventos;
    private SQLiteDatabase db;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Evento eventoSeleccionado = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_agenda_sociedad_dialog, container, false);

        etDescripcion = v.findViewById(R.id.etDescripcion);
        etCliente = v.findViewById(R.id.etCliente);
        tvFechaHora = v.findViewById(R.id.tvFechaHora);
        listaEventos = v.findViewById(R.id.listaEventos);

        Button btnSeleccionarFecha = v.findViewById(R.id.btnSeleccionarFecha);
        Button btnSeleccionarHora = v.findViewById(R.id.btnSeleccionarHora);
        Button btnAgregar = v.findViewById(R.id.btnAgregar);
        Button btnBorrar = v.findViewById(R.id.btnBorrar);
        Button btnCerrar = v.findViewById(R.id.btnCerrar);

        db = requireActivity().openOrCreateDatabase("AgendaSociedad", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS eventos_empresariales (fecha TEXT, descripcion TEXT, cliente TEXT);");

        listarEventos();

        btnSeleccionarFecha.setOnClickListener(v1 -> mostrarSelectorFecha());
        btnSeleccionarHora.setOnClickListener(v1 -> mostrarSelectorHora());

        btnAgregar.setOnClickListener(v1 -> {
            String fecha = tvFechaHora.getText().toString();
            String desc = etDescripcion.getText().toString();
            String cliente = etCliente.getText().toString();
            if (fecha.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getActivity(), "Fecha y descripción obligatorias", Toast.LENGTH_SHORT).show();
                return;
            }
            if (eventoSeleccionado != null) {
                // Editar
                db.execSQL("UPDATE eventos_empresariales SET fecha=?, descripcion=?, cliente=? WHERE fecha=? AND descripcion=?",
                        new Object[]{fecha, desc, cliente, eventoSeleccionado.fecha, eventoSeleccionado.descripcion});
            } else {
                // Nuevo
                db.execSQL("INSERT INTO eventos_empresariales (fecha, descripcion, cliente) VALUES (?, ?, ?);",
                        new Object[]{fecha, desc, cliente});
            }
            limpiarCampos();
            listarEventos();
        });

        btnBorrar.setOnClickListener(v1 -> {
            if (eventoSeleccionado != null) {
                db.execSQL("DELETE FROM eventos_empresariales WHERE fecha=? AND descripcion=?",
                        new Object[]{eventoSeleccionado.fecha, eventoSeleccionado.descripcion});
                limpiarCampos();
                listarEventos();
            }
        });

        btnCerrar.setOnClickListener(v1 -> dismiss());

        listaEventos.setOnItemClickListener((parent, view, position, id) -> {
            eventoSeleccionado = (Evento) parent.getItemAtPosition(position);
            tvFechaHora.setText(eventoSeleccionado.fecha);
            etDescripcion.setText(eventoSeleccionado.descripcion);
            etCliente.setText(eventoSeleccionado.cliente);
        });

        return v;
    }

    private void mostrarSelectorFecha() {
        int año = fechaSeleccionada.get(Calendar.YEAR);
        int mes = fechaSeleccionada.get(Calendar.MONTH);
        int dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), (view, y, m, d) -> {
            fechaSeleccionada.set(Calendar.YEAR, y);
            fechaSeleccionada.set(Calendar.MONTH, m);
            fechaSeleccionada.set(Calendar.DAY_OF_MONTH, d);
            actualizarFechaHora();
        }, año, mes, dia).show();
    }

    private void mostrarSelectorHora() {
        int hora = fechaSeleccionada.get(Calendar.HOUR_OF_DAY);
        int min = fechaSeleccionada.get(Calendar.MINUTE);
        new TimePickerDialog(getActivity(), (view, h, m) -> {
            fechaSeleccionada.set(Calendar.HOUR_OF_DAY, h);
            fechaSeleccionada.set(Calendar.MINUTE, m);
            actualizarFechaHora();
        }, hora, min, true).show();
    }

    private void actualizarFechaHora() {
        String texto = String.format("%02d/%02d/%04d %02d:%02d",
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH),
                fechaSeleccionada.get(Calendar.MONTH) + 1,
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.HOUR_OF_DAY),
                fechaSeleccionada.get(Calendar.MINUTE));
        tvFechaHora.setText(texto);
    }

    private void listarEventos() {
        List<Evento> eventos = new ArrayList<>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM eventos_empresariales", null);
            if (c.moveToFirst()) {
                do {
                    eventos.add(new Evento(c.getString(0), c.getString(1), c.getString(2)));
                } while (c.moveToNext());
            }
            c.close();
        } catch (SQLException e) { e.printStackTrace(); }

        ArrayAdapter<Evento> adapter = new ArrayAdapter<Evento>(requireActivity(),
                android.R.layout.simple_list_item_1, eventos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                Evento e = getItem(position);
                tv.setText(e.fecha + " - " + e.descripcion + (e.cliente.isEmpty() ? "" : " (" + e.cliente + ")"));
                return tv;
            }
        };
        listaEventos.setAdapter(adapter);
    }

    private void limpiarCampos() {
        tvFechaHora.setText("Selecciona fecha y hora");
        etDescripcion.setText("");
        etCliente.setText("");
        eventoSeleccionado = null;
    }

    static class Evento {
        String fecha;
        String descripcion;
        String cliente;

        Evento(String f, String d, String c) { fecha=f; descripcion=d; cliente=c; }

        @NonNull
        @Override
        public String toString() { return fecha + " - " + descripcion; }
    }
}
