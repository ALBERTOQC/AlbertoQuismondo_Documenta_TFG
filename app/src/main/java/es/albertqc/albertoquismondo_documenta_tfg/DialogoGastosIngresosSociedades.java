package es.albertqc.albertoquismondo_documenta_tfg;

import android.os.Bundle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.text.InputType;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogoGastosIngresosSociedades extends DialogFragment {

    private TextView tvBalance;
    private LinearLayout layoutIngresos, layoutGastos;
    private SQLiteDatabase db;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ScrollView view = (ScrollView) requireActivity()
                .getLayoutInflater()
                .inflate(R.layout.activity_dialogo_gastos_ingresos_sociedades, null);

        tvBalance = view.findViewById(R.id.tvBalanceSoc);
        layoutIngresos = view.findViewById(R.id.layoutIngresosSoc);
        layoutGastos = view.findViewById(R.id.layoutGastosSoc);

        // Crear BD para empresas
        db = requireActivity().openOrCreateDatabase("SociedadesDB", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS facturas_ingresos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numero TEXT," +
                "fecha TEXT," +
                "cliente TEXT," +
                "concepto TEXT," +
                "importe_total REAL," +
                "importe_pendiente REAL," +
                "estado TEXT," +
                "metodo TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS facturas_gastos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numero TEXT," +
                "fecha TEXT," +
                "proveedor TEXT," +
                "concepto TEXT," +
                "importe_total REAL," +
                "importe_pendiente REAL," +
                "estado TEXT," +
                "metodo TEXT)");


        Button btnNuevoIngreso = view.findViewById(R.id.btnAgregarIngresoSoc);
        btnNuevoIngreso.setOnClickListener(v -> abrirDialogoFactura(true));

        Button btnNuevoGasto = view.findViewById(R.id.btnAgregarGastoSoc);
        btnNuevoGasto.setOnClickListener(v -> abrirDialogoFactura(false));

        Button btnCalculo = view.findViewById(R.id.btnCalcularImpuestosSoc);
        btnCalculo.setOnClickListener(v -> mostrarDialogoCalculoSociedades());


        listarIngresos();
        listarGastos();
        actualizarBalance();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton("Cerrar", null)
                .create();
    }


    // Diálogo para crear factura
    private void abrirDialogoFactura(boolean esIngreso) {

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);

        EditText etNum = crearCampo("Número factura");
        EditText etFecha = crearCampo("Fecha");
        EditText etEntidad = crearCampo(esIngreso ? "Cliente" : "Proveedor");
        EditText etConcepto = crearCampo("Concepto / descripción");

        EditText etImporteTotal = crearCampo("Importe total");
        etImporteTotal.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText etMetodo = crearCampo("Método de pago");

        layout.addView(etNum);
        layout.addView(etFecha);
        layout.addView(etEntidad);
        layout.addView(etConcepto);
        layout.addView(etImporteTotal);
        layout.addView(etMetodo);

        new AlertDialog.Builder(getContext())
                .setTitle(esIngreso ? "Registrar factura de ingreso" : "Registrar factura de gasto")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    double total = Double.parseDouble(etImporteTotal.getText().toString());

                    ContentValues cv = new ContentValues();
                    cv.put("numero", etNum.getText().toString());
                    cv.put("fecha", etFecha.getText().toString());
                    if (esIngreso) cv.put("cliente", etEntidad.getText().toString());
                    else cv.put("proveedor", etEntidad.getText().toString());
                    cv.put("concepto", etConcepto.getText().toString());
                    cv.put("importe_total", total);
                    cv.put("importe_pendiente", total);
                    cv.put("estado", "PENDIENTE");
                    cv.put("metodo", etMetodo.getText().toString());

                    db.insert(esIngreso ? "facturas_ingresos" : "facturas_gastos",
                            null, cv);

                    if (esIngreso) listarIngresos();
                    else listarGastos();

                    actualizarBalance();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private EditText crearCampo(String hint) {
        EditText e = new EditText(getContext());
        e.setHint(hint);
        return e;
    }


    // lista los ingressos
    private void listarIngresos() {
        layoutIngresos.removeAllViews();
        Cursor c = db.rawQuery("SELECT * FROM facturas_ingresos", null);
        while (c.moveToNext()) mostrarFactura(c, true);
        c.close();
    }

    // lista los gastos
    private void listarGastos() {
        layoutGastos.removeAllViews();
        Cursor c = db.rawQuery("SELECT * FROM facturas_gastos", null);
        while (c.moveToNext()) mostrarFactura(c, false);
        c.close();
    }


    private void mostrarFactura(Cursor c, boolean esIngreso) {

        int id = c.getInt(c.getColumnIndex("id"));
        String numero = c.getString(c.getColumnIndex("numero"));
        String fecha = c.getString(c.getColumnIndex("fecha"));
        String entidad = esIngreso ?
                c.getString(c.getColumnIndex("cliente")) :
                c.getString(c.getColumnIndex("proveedor"));
        String concepto = c.getString(c.getColumnIndex("concepto"));
        double total = c.getDouble(c.getColumnIndex("importe_total"));
        double pendiente = c.getDouble(c.getColumnIndex("importe_pendiente"));
        String estado = c.getString(c.getColumnIndex("estado"));

        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(20, 20, 20, 20);
        item.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

        TextView tv = new TextView(getContext());
        tv.setText(
                "Factura Nº: " + numero + "\n" +
                        fecha + " | " + entidad + "\n" +
                        concepto + "\n" +
                        "Total: " + total + "€ | Pendiente: " + pendiente + "€\n" +
                        "Estado: " + estado
        );
        item.addView(tv);

        Button btnPago = new Button(getContext());
        btnPago.setText("Registrar pago parcial");
        btnPago.setOnClickListener(v -> registrarPagoParcial(id, esIngreso));
        item.addView(btnPago);

        Button btnEliminar = new Button(getContext());
        btnEliminar.setText("Eliminar");
        btnEliminar.setOnClickListener(v -> {
            db.delete(esIngreso ? "facturas_ingresos" : "facturas_gastos",
                    "id=?", new String[]{String.valueOf(id)});
            if (esIngreso) listarIngresos();
            else listarGastos();
            actualizarBalance();
        });
        item.addView(btnEliminar);

        if (esIngreso) layoutIngresos.addView(item);
        else layoutGastos.addView(item);
    }


    private void registrarPagoParcial(int id, boolean esIngreso) {

        EditText etPago = new EditText(getContext());
        etPago.setHint("Importe pagado");
        etPago.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(getContext())
                .setTitle("Registrar pago parcial")
                .setView(etPago)
                .setPositiveButton("Aplicar", (dialog, which) -> {

                    double pago = Double.parseDouble(etPago.getText().toString());

                    Cursor c = db.rawQuery(
                            "SELECT importe_pendiente FROM " +
                                    (esIngreso ? "facturas_ingresos" : "facturas_gastos") +
                                    " WHERE id=" + id,
                            null);

                    if (c.moveToFirst()) {
                        double pendiente = c.getDouble(0);
                        double nuevoPendiente = pendiente - pago;

                        ContentValues cv = new ContentValues();
                        cv.put("importe_pendiente", Math.max(nuevoPendiente, 0));

                        if (nuevoPendiente <= 0) cv.put("estado", "PAGADA");
                        else cv.put("estado", "PARCIAL");

                        db.update(
                                esIngreso ? "facturas_ingresos" : "facturas_gastos",
                                cv, "id=?", new String[]{String.valueOf(id)}
                        );
                    }
                    c.close();

                    if (esIngreso) listarIngresos();
                    else listarGastos();
                    actualizarBalance();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void actualizarBalance() {
        double ingresos = sumar("facturas_ingresos");
        double gastos = sumar("facturas_gastos");
        double balance = ingresos - gastos;

        tvBalance.setText("Balance: " + balance + "€");
        tvBalance.setTextColor(balance >= 0 ? 0xFF2E7D32 : 0xFFC62828);
    }

    private double sumar(String tabla) {
        Cursor c = db.rawQuery("SELECT SUM(importe_total) FROM " + tabla, null);
        double r = 0;
        if (c.moveToFirst()) r = c.getDouble(0);
        c.close();
        return r;
    }

    //función completa para calcular impuestos en Sociedades
    private void mostrarDialogoCalculoSociedades() {

        double ingresos = sumar("facturas_ingresos");
        double gastos = sumar("facturas_gastos");
        double beneficio = ingresos - gastos;

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView tvInfo = new TextView(getContext());
        tvInfo.setText(
                "📘 Cálculo del Impuesto de Sociedades\n\n" +
                        "• El Impuesto de Sociedades grava el beneficio de la empresa.\n" +
                        "• Beneficio = Ingresos – Gastos.\n\n" +
                        "Tipos habituales:\n" +
                        "• SL / SLU / SA → 25%\n" +
                        "• Empresas de nueva creación → 15%\n"
        );
        tvInfo.setPadding(0, 0, 0, 30);
        layout.addView(tvInfo);

        TextView tvResumen = new TextView(getContext());
        tvResumen.setText(
                "Ingresos totales: " + ingresos + " €\n" +
                        "Gastos totales: " + gastos + " €\n" +
                        "Beneficio actual: " + beneficio + " €"
        );
        tvResumen.setPadding(0, 0, 0, 20);
        layout.addView(tvResumen);


        new AlertDialog.Builder(getContext())
                .setTitle("Cálculo del Impuesto de Sociedades")
                .setView(layout)
                .setPositiveButton("Calcular", (dialog, which) -> {

                    if (beneficio <= 0) {
                        mostrarResultadoCalculoSociedades(
                                beneficio,
                                0,
                                "No hay obligación de pagar. El beneficio es negativo o nulo."
                        );
                        return;
                    }

                    double tipo = 0.25;

                    double impuesto = beneficio * tipo;

                    mostrarResultadoCalculoSociedades(
                            beneficio,
                            impuesto,
                            "Cálculo completado correctamente."
                    );

                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void mostrarResultadoCalculoSociedades(double beneficio, double impuesto, String mensaje) {

        String texto =
                mensaje + "\n\n" +
                        "Beneficio: " + beneficio + "€\n" +
                        "Impuesto (25%): " + impuesto + "€\n" +
                        "---------------------------------\n" +
                        "TOTAL A PAGAR: " + impuesto + "€";

        new AlertDialog.Builder(getContext())
                .setTitle("Resultado del Impuesto de Sociedades")
                .setMessage(texto)
                .setPositiveButton("Aceptar", null)
                .show();
    }


}