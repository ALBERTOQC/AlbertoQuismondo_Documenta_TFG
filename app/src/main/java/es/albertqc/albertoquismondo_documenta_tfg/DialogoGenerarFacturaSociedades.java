package es.albertqc.albertoquismondo_documenta_tfg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DialogoGenerarFacturaSociedades extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_dialogo_generar_factura_sociedades, null);
        builder.setView(view);

        //  Nº FACTURA AUTOINCREMENTAL
        SharedPreferences prefs = getContext().getSharedPreferences("FACTURAS_SOCIEDADES", Context.MODE_PRIVATE);
        int contador = prefs.getInt("contadorFacturaSociedad", 1);  // empieza en 1
        String numeroFactura = "2025" + contador;

        EditText etNumeroFactura = view.findViewById(R.id.etNumeroFactura);
        if (etNumeroFactura != null) etNumeroFactura.setText(numeroFactura);

        //  FECHA ACTUAL
        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        EditText etFechaFactura = view.findViewById(R.id.etFechaFactura);
        if (etFechaFactura != null) etFechaFactura.setText(fechaHoy);

        AlertDialog dialog = builder.create();

        // Spinner IVA
        Spinner spIva = view.findViewById(R.id.spIva);
        ArrayAdapter<String> adapterIva = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{"IVA 21%", "IVA 10%", "IVA 4%", "IVA 0%"});
        adapterIva.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIva.setAdapter(adapterIva);

        // Spinner tipo documento
        Spinner spTipo = view.findViewById(R.id.spTipoDocumento);
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{"Factura", "Albarán"});
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterTipo);

        //  BOTÓN GENERAR
        Button btnGenerar = view.findViewById(R.id.btnGenerarFacturaDialogSoc);
        btnGenerar.setOnClickListener(v -> {
            EditText etCliente = view.findViewById(R.id.etCliente);
            EditText etConcepto = view.findViewById(R.id.etConcepto);
            EditText etBase = view.findViewById(R.id.etBase);

            String cliente = etCliente.getText().toString().trim();
            String concepto = etConcepto.getText().toString().trim();
            String baseStr = etBase.getText().toString().trim();
            String tipoDocumento = (String) spTipo.getSelectedItem();

            if(cliente.isEmpty() || concepto.isEmpty() || baseStr.isEmpty()) {
                Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double base;
            try {
                base = Double.parseDouble(baseStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Base no válida", Toast.LENGTH_SHORT).show();
                return;
            }

            // IVA
            String ivaSeleccionado = (String) spIva.getSelectedItem();
            double ivaPercent = Double.parseDouble(ivaSeleccionado.replace("IVA ", "").replace("%",""));
            double iva = Math.round(base * ivaPercent / 100.0 * 100.0) / 100.0;
            double total = Math.round((base + iva) * 100.0) / 100.0;

            try {
                File pdf = generarPdf(cliente, concepto, base, ivaPercent, iva, total,
                        numeroFactura, fechaHoy, tipoDocumento);

                abrirPdf(pdf); // abrir PDF automáticamente

                Toast.makeText(getContext(), tipoDocumento + " generado: " + pdf.getName(), Toast.LENGTH_SHORT).show();

                // Guardar incremento del número de factura
                prefs.edit().putInt("contadorFacturaSociedad", contador + 1).apply();
                dialog.dismiss();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(getContext(), "Error al generar PDF: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return dialog;
    }

    private File generarPdf(String cliente, String concepto, double base,
                            double ivaPer, double iva, double total,
                            String numFactura, String fecha, String tipoDocumento) throws IOException {

        File dir = getContext().getExternalFilesDir("FacturasSociedades");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, tipoDocumento.toLowerCase() + "_" + System.currentTimeMillis() + ".pdf");

        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = 40, y = 40;

        // CABECERA
        paint.setTextSize(28);
        paint.setFakeBoldText(true);
        paint.setColor(0xFF00796B);
        canvas.drawText("DOCUMENTA - " + tipoDocumento.toUpperCase(), x, y, paint);

        y += 35;

        // Línea separadora
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(2);
        canvas.drawLine(x, y, x + 500, y, paint);
        y += 25;

        // FECHA y Nº FACTURA
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("Fecha:", x, y, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(fecha, x + 120, y, paint);

        y += 25;
        paint.setFakeBoldText(true);
        canvas.drawText("Factura Nº:", x, y, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(numFactura, x + 120, y, paint);

        y += 40;

        // CLIENTE y CONCEPTO
        paint.setFakeBoldText(true);
        canvas.drawText("Cliente:", x, y, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(cliente, x + 80, y, paint);
        y += 25;

        paint.setFakeBoldText(true);
        canvas.drawText("Concepto:", x, y, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(concepto, x + 90, y, paint);
        y += 40;

        // TABLA IMPORTES
        paint.setFakeBoldText(true);
        canvas.drawText("Base", x + 10, y, paint);
        canvas.drawText("IVA (" + ivaPer + "%)", x + 180, y, paint);
        canvas.drawText("Total", x + 350, y, paint);

        y += 20;
        paint.setFakeBoldText(false);
        canvas.drawText(base + " €", x + 10, y, paint);
        canvas.drawText(iva + " €", x + 180, y, paint);
        canvas.drawText(total + " €", x + 350, y, paint);

        y += 50;

        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText("TOTAL: " + total + " €", x + 350, y, paint);

        doc.finishPage(page);

        FileOutputStream fos = new FileOutputStream(file);
        doc.writeTo(fos);
        doc.close();
        fos.close();

        return file;
    }

    private void abrirPdf(File pdf) {
        try {
            Uri uri = FileProvider.getUriForFile(getContext(),
                    getContext().getPackageName() + ".fileprovider", pdf);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "No hay aplicación para abrir PDFs", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

