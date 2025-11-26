package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogoGastosIngresos extends DialogFragment {

    private TextView tvBalance;
    private LinearLayout layoutIngresos, layoutGastos;
    private SQLiteDatabase db;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ScrollView dialogView = (ScrollView) requireActivity().getLayoutInflater()
                .inflate(R.layout.activity_dialogo_gastos_ingresos, null);

        tvBalance = dialogView.findViewById(R.id.tvBalance);
        layoutIngresos = dialogView.findViewById(R.id.layoutIngresos);
        layoutGastos = dialogView.findViewById(R.id.layoutGastos);

        db = requireActivity().openOrCreateDatabase("AutonomoDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS ingresos (id INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, cliente TEXT, concepto TEXT, importe REAL, metodo TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS gastos (id INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, proveedor TEXT, concepto TEXT, importe REAL, metodo TEXT)");

        // Añadir columna factura si no existe (SQLite ignora el error si ya está creada)
        try { db.execSQL("ALTER TABLE ingresos ADD COLUMN factura TEXT"); } catch (Exception ignored) {}
        try { db.execSQL("ALTER TABLE gastos ADD COLUMN factura TEXT"); } catch (Exception ignored) {}

        Button btnAgregarIngreso = dialogView.findViewById(R.id.btnAgregarIngreso);
        btnAgregarIngreso.setOnClickListener(v -> abrirDialogoIngreso());

        Button btnAgregarGasto = dialogView.findViewById(R.id.btnAgregarGasto);
        btnAgregarGasto.setOnClickListener(v -> abrirDialogoGasto());

        // Botón para calcular impuestos
        Button btnCalcularCuota = dialogView.findViewById(R.id.btnCalcularCuota);
        btnCalcularCuota.setOnClickListener(v -> mostrarDialogoCalculo());

        listarIngresos();
        listarGastos();
        actualizarBalance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void abrirDialogoIngreso() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,40,40,40);

        // FECHA con DatePicker
        Button btnFecha = new Button(getContext());
        btnFecha.setText("Seleccionar fecha");
        layout.addView(btnFecha);

        final String[] fechaSeleccionada = {""};
        btnFecha.setOnClickListener(v -> {
            DatePickerDialog dp = new DatePickerDialog(
                    getContext(),
                    (view, year, month, day) -> {
                        fechaSeleccionada[0] = day + "/" + (month+1) + "/" + year;
                        btnFecha.setText("Fecha: " + fechaSeleccionada[0]);
                    },
                    2025, 0, 1
            );
            dp.show();
        });

        EditText etCliente = new EditText(getContext());
        etCliente.setHint("Cliente");
        layout.addView(etCliente);

        EditText etConcepto = new EditText(getContext());
        etConcepto.setHint("Concepto");
        layout.addView(etConcepto);

        // NUEVO: Número de factura
        EditText etFactura = new EditText(getContext());
        etFactura.setHint("Número de factura");
        layout.addView(etFactura);

        EditText etImporte = new EditText(getContext());
        etImporte.setHint("Importe");
        etImporte.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etImporte);

        EditText etMetodo = new EditText(getContext());
        etMetodo.setHint("Método de pago");  // cambiado
        layout.addView(etMetodo);


        new AlertDialog.Builder(getContext())
                .setTitle("Añadir ingreso")
                .setView(layout)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put("fecha", fechaSeleccionada[0]);
                        cv.put("cliente", etCliente.getText().toString());
                        cv.put("concepto", etConcepto.getText().toString());
                        cv.put("factura", etFactura.getText().toString());   // nuevo campo
                        cv.put("importe", Double.parseDouble(etImporte.getText().toString()));
                        cv.put("metodo", etMetodo.getText().toString());
                        db.insert("ingresos", null, cv);
                        listarIngresos();
                        actualizarBalance();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al añadir ingreso", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirDialogoGasto() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40,40,40,40);

        // FECHA con DatePicker
        Button btnFecha = new Button(getContext());
        btnFecha.setText("Seleccionar fecha");
        layout.addView(btnFecha);

        final String[] fechaSeleccionada = {""};
        btnFecha.setOnClickListener(v -> {
            DatePickerDialog dp = new DatePickerDialog(
                    getContext(),
                    (view, year, month, day) -> {
                        fechaSeleccionada[0] = day + "/" + (month+1) + "/" + year;
                        btnFecha.setText("Fecha: " + fechaSeleccionada[0]);
                    },
                    2025, 0, 1
            );
            dp.show();
        });

        EditText etProveedor = new EditText(getContext());
        etProveedor.setHint("Proveedor");
        layout.addView(etProveedor);

        EditText etConcepto = new EditText(getContext());
        etConcepto.setHint("Concepto");
        layout.addView(etConcepto);

        // NUEVO: Número de factura
        EditText etFactura = new EditText(getContext());
        etFactura.setHint("Número de factura");
        layout.addView(etFactura);

        EditText etImporte = new EditText(getContext());
        etImporte.setHint("Importe");
        etImporte.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etImporte);

        EditText etMetodo = new EditText(getContext());
        etMetodo.setHint("Método de pago");
        layout.addView(etMetodo);

        new AlertDialog.Builder(getContext())
                .setTitle("Añadir gasto")
                .setView(layout)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    try {
                        ContentValues cv = new ContentValues();
                        cv.put("fecha", fechaSeleccionada[0]);
                        cv.put("proveedor", etProveedor.getText().toString());
                        cv.put("concepto", etConcepto.getText().toString());
                        cv.put("factura", etFactura.getText().toString()); // nuevo campo
                        cv.put("importe", Double.parseDouble(etImporte.getText().toString()));
                        cv.put("metodo", etMetodo.getText().toString());
                        db.insert("gastos", null, cv);
                        listarGastos();
                        actualizarBalance();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al añadir gasto", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void listarIngresos() {
        layoutIngresos.removeAllViews();
        Cursor c = db.rawQuery("SELECT * FROM ingresos", null);
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("id"));
            String fecha = c.getString(c.getColumnIndex("fecha"));
            String cliente = c.getString(c.getColumnIndex("cliente"));
            String concepto = c.getString(c.getColumnIndex("concepto"));
            double importe = c.getDouble(c.getColumnIndex("importe"));
            String metodo = c.getString(c.getColumnIndex("metodo"));

            LinearLayout item = new LinearLayout(getContext());
            item.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getContext());
            tv.setText(fecha + " | " + cliente + " - " + concepto + ": " + importe + "€ (" + metodo + ")");
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            item.addView(tv);

            Button btnEliminar = new Button(getContext());
            btnEliminar.setText("🗑");
            btnEliminar.setOnClickListener(v -> {
                db.delete("ingresos", "id=?", new String[]{String.valueOf(id)});
                listarIngresos();
                actualizarBalance();
            });
            item.addView(btnEliminar);

            layoutIngresos.addView(item);
        }
        c.close();
    }

    private void listarGastos() {
        layoutGastos.removeAllViews();
        Cursor c = db.rawQuery("SELECT * FROM gastos", null);
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex("id"));
            String fecha = c.getString(c.getColumnIndex("fecha"));
            String proveedor = c.getString(c.getColumnIndex("proveedor"));
            String concepto = c.getString(c.getColumnIndex("concepto"));
            double importe = c.getDouble(c.getColumnIndex("importe"));
            String metodo = c.getString(c.getColumnIndex("metodo"));

            LinearLayout item = new LinearLayout(getContext());
            item.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(getContext());
            tv.setText(fecha + " | " + proveedor + " - " + concepto + ": " + importe + "€ (" + metodo + ")");
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            item.addView(tv);

            Button btnEliminar = new Button(getContext());
            btnEliminar.setText("🗑");
            btnEliminar.setOnClickListener(v -> {
                db.delete("gastos", "id=?", new String[]{String.valueOf(id)});
                listarGastos();
                actualizarBalance();
            });
            item.addView(btnEliminar);

            layoutGastos.addView(item);
        }
        c.close();
    }

    private void actualizarBalance() {
        Cursor cIngresos = db.rawQuery("SELECT SUM(importe) FROM ingresos", null);
        Cursor cGastos = db.rawQuery("SELECT SUM(importe) FROM gastos", null);
        double totalIngresos = 0, totalGastos = 0;
        if (cIngresos.moveToFirst()) totalIngresos = cIngresos.getDouble(0);
        if (cGastos.moveToFirst()) totalGastos = cGastos.getDouble(0);
        cIngresos.close();
        cGastos.close();

        double balance = totalIngresos - totalGastos;
        tvBalance.setText("Balance: " + balance + "€");
        tvBalance.setTextColor(balance >= 0 ? 0xFF388E3C : 0xFFD32F2F);
    }

    private void mostrarDialogoCalculo() {

        // Obtener ingresos y gastos actuales
        Cursor cIngresos = db.rawQuery("SELECT SUM(importe) FROM ingresos", null);
        Cursor cGastos = db.rawQuery("SELECT SUM(importe) FROM gastos", null);

        double totalIngresos = 0, totalGastos = 0;
        if (cIngresos.moveToFirst()) totalIngresos = cIngresos.getDouble(0);
        if (cGastos.moveToFirst()) totalGastos = cGastos.getDouble(0);
        cIngresos.close();
        cGastos.close();

        double beneficio = totalIngresos - totalGastos;

        // Layout del diálogo
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        // Información profesional
        TextView tvInfo = new TextView(getContext());
        tvInfo.setText(
                "📘 Cómo se calcula el impuesto trimestral:\n\n" +
                        "• Este cálculo corresponde al IRPF trimestral (Modelo 130).\n" +
                        "• Se aplica un 20% sobre el beneficio obtenido:\n" +
                        "     Beneficio = Ingresos – Gastos.\n\n" +
                        "📝 Nota importante:\n" +
                        "La cuota de autónomos mensual NO forma parte de este cálculo, " +
                        "ya que se paga directamente a la Seguridad Social."
        );
        tvInfo.setPadding(0, 0, 0, 30);
        layout.addView(tvInfo);

        // Resumen de ingresos, gastos y beneficio
        TextView tvResumen = new TextView(getContext());
        tvResumen.setText(
                "Ingresos totales: " + totalIngresos + " €\n" +
                        "Gastos totales: " + totalGastos + " €\n" +
                        "Beneficio del trimestre: " + beneficio + " €"
        );
        tvResumen.setPadding(0, 0, 0, 20);
        layout.addView(tvResumen);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cálculo de IRPF Trimestral")
                .setView(layout)
                .setPositiveButton("Calcular", (dialog, which) -> {

                    if (beneficio <= 0) {
                        mostrarResultadoCalculo(
                                beneficio,
                                0,
                                0,
                                "No hay obligación de pago. Beneficio negativo o nulo."
                        );
                        return;
                    }

                    double irpf = beneficio * 0.20; // 20%

                    mostrarResultadoCalculo(
                            beneficio,
                            irpf,
                            0,
                            "Cálculo completado."
                    );

                })
                .setNegativeButton("Cerrar", null)
                .show();
    }


    private void mostrarResultadoCalculo(double beneficio, double irpf, double cuota, String mensaje) {

        String texto =
                mensaje + "\n\n" +
                        "Beneficio: " + beneficio + "€\n" +
                        "IRPF (20%): " + irpf + "€\n" +
                        "Cuota autónomos (3 meses): " + cuota + "€\n" +
                        "---------------------------------\n" +
                        "TOTAL A PAGAR: " + (irpf + cuota) + "€";

        new AlertDialog.Builder(getContext())
                .setTitle("Resultado del cálculo")
                .setMessage(texto)
                .setPositiveButton("Aceptar", null)
                .show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
