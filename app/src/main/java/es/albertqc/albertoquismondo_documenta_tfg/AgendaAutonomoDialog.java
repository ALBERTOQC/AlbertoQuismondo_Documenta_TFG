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

public class AgendaAutonomoDialog extends DialogFragment {

    private EditText etDescripcion;
    private TextView tvFechaHora;
    private ListView listaEventos;
    private SQLiteDatabase db;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Evento eventoSeleccionado = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_agenda_autonomo_dialog, container, false);

        etDescripcion = v.findViewById(R.id.etDescripcion);
        tvFechaHora = v.findViewById(R.id.tvFechaHora);
        listaEventos = v.findViewById(R.id.listaEventos);

        Button btnSeleccionarFecha = v.findViewById(R.id.btnSeleccionarFecha);
        Button btnSeleccionarHora = v.findViewById(R.id.btnSeleccionarHora);
        Button btnAgregar = v.findViewById(R.id.btnAgregar);
        Button btnBorrar = v.findViewById(R.id.btnBorrar);
        Button btnCerrar = v.findViewById(R.id.btnCerrar);

        // Crear/abrir base de datos
        db = requireActivity().openOrCreateDatabase("AgendaAutonomo", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS eventos (fecha TEXT, descripcion TEXT);");

        listarEventos();

        // Fecha y hora
        btnSeleccionarFecha.setOnClickListener(v1 -> mostrarSelectorFecha());
        btnSeleccionarHora.setOnClickListener(v12 -> mostrarSelectorHora());

        // Añadir evento
        btnAgregar.setOnClickListener(v13 -> {
            String fecha = tvFechaHora.getText().toString();
            String desc = etDescripcion.getText().toString();
            if (fecha.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            db.execSQL("INSERT INTO eventos VALUES ('" + fecha + "', '" + desc + "');");
            Toast.makeText(getContext(), "Evento añadido", Toast.LENGTH_SHORT).show();
            listarEventos();
            etDescripcion.setText("");
            tvFechaHora.setText("Selecciona una fecha y hora");
        });

        // Seleccionar evento desde lista
        listaEventos.setOnItemClickListener((parent, view1, position, id) -> {
            EventoAdapter adaptador = (EventoAdapter) parent.getAdapter();
            eventoSeleccionado = adaptador.getItem(position);
            if (eventoSeleccionado != null) {
                tvFechaHora.setText(eventoSeleccionado.fecha);
                etDescripcion.setText(eventoSeleccionado.descripcion);
            }
        });

        // Borrar evento seleccionado
        btnBorrar.setOnClickListener(v14 -> {
            if (eventoSeleccionado == null) {
                Toast.makeText(getContext(), "Selecciona un evento de la lista para eliminarlo", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                db.execSQL("DELETE FROM eventos WHERE fecha=? AND descripcion=?",
                        new Object[]{eventoSeleccionado.fecha, eventoSeleccionado.descripcion});
                Toast.makeText(getContext(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                listarEventos();
                eventoSeleccionado = null;
                etDescripcion.setText("");
                tvFechaHora.setText("Selecciona una fecha y hora");
            } catch (SQLException e) {
                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        btnCerrar.setOnClickListener(v15 -> dismiss());

        return v;
    }

    private void mostrarSelectorFecha() {
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            fechaSeleccionada.set(year, month, dayOfMonth);
            actualizarTextoFechaHora();
        }, año, mes, dia);
        dialog.show();
    }

    private void mostrarSelectorHora() {
        final Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
            fechaSeleccionada.set(Calendar.MINUTE, minute);
            actualizarTextoFechaHora();
        }, hora, minuto, true);
        dialog.show();
    }

    private void actualizarTextoFechaHora() {
        int dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
        int mes = fechaSeleccionada.get(Calendar.MONTH) + 1;
        int año = fechaSeleccionada.get(Calendar.YEAR);
        int hora = fechaSeleccionada.get(Calendar.HOUR_OF_DAY);
        int minuto = fechaSeleccionada.get(Calendar.MINUTE);
        tvFechaHora.setText(String.format("%02d/%02d/%04d %02d:%02d", dia, mes, año, hora, minuto));
    }

    private void listarEventos() {
        List<Evento> lista = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM eventos ORDER BY fecha ASC", null);
        if (c.getCount() == 0) {
            lista.add(new Evento("No hay eventos", ""));
        } else {
            while (c.moveToNext()) {
                lista.add(new Evento(c.getString(0), c.getString(1)));
            }
        }
        EventoAdapter adaptador = new EventoAdapter(getContext(), lista);
        listaEventos.setAdapter(adaptador);
        c.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) db.close();
    }

    // Clases internas para adaptador personalizado
    private static class Evento {
        String fecha;
        String descripcion;

        Evento(String fecha, String descripcion) {
            this.fecha = fecha;
            this.descripcion = descripcion;
        }
    }

    private class EventoAdapter extends ArrayAdapter<Evento> {
        public EventoAdapter(Context context, List<Evento> eventos) {
            super(context, 0, eventos);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_evento, parent, false);
            }
            Evento evento = getItem(position);
            TextView tvFecha = convertView.findViewById(R.id.tvEventoFecha);
            TextView tvDesc = convertView.findViewById(R.id.tvEventoDescripcion);
            tvFecha.setText(evento.fecha);
            tvDesc.setText(evento.descripcion);
            return convertView;
        }
    }
}
