package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogoDocumentosAutonomos extends DialogFragment{
    OnCerrarDialogo listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Documentos para autónomos");

        // Texto explicativo
        String mensaje = "ALTA COMO AUTÓNOMO\n\n" +
                "Para darse de alta como trabajador autónomo en España, es necesario realizar dos trámites fundamentales: uno ante la Agencia Tributaria (Hacienda) y otro ante la Seguridad Social.\n\n" +

                "1. Alta en Hacienda:\n" +
                "   - Presentar el Modelo 036 o 037 (Declaración censal de alta, modificación o baja en el Censo de Empresarios, Profesionales y Retenedores).\n" +
                "   - En este modelo se indican los datos personales, la actividad económica que se va a desarrollar, el domicilio fiscal y el régimen de IVA o IRPF correspondiente.\n" +
                "   - Este trámite puede realizarse de forma presencial en la Agencia Tributaria o telemáticamente a través de su sede electrónica.\n\n" +

                "2. Alta en la Seguridad Social:\n" +
                "   - Presentar el Modelo TA0521 (Solicitud de alta en el Régimen Especial de Trabajadores Autónomos, RETA).\n" +
                "   - Este formulario se presenta en la Tesorería General de la Seguridad Social o de forma online mediante el sistema RED o el portal Import@ss.\n" +
                "   - Se debe acompañar del DNI o NIE y del justificante de haber presentado el modelo 036 o 037 en Hacienda.\n" +
                "   - Es obligatorio elegir una base de cotización y declarar la actividad desde la fecha exacta de inicio.\n\n" +

                "3. Otros documentos y recomendaciones:\n" +
                "   - Documento Nacional de Identidad (DNI) o Número de Identidad de Extranjero (NIE) en vigor.\n" +
                "   - En caso de no estar afiliado a la Seguridad Social, también se debe tramitar la afiliación (Modelo TA.1).\n" +
                "   - Guardar siempre una copia de todos los documentos y justificantes de presentación.\n\n" +

                "BAJA COMO AUTÓNOMO\n\n" +
                "Cuando se desea cesar la actividad profesional, también es necesario tramitar la baja en los mismos organismos:\n\n" +

                "1. Baja en Hacienda:\n" +
                "   - Se utiliza el mismo Modelo 036 o 037 para comunicar la baja en el Censo de Empresarios.\n" +
                "   - Debe presentarse en el plazo de un mes desde el cese de la actividad.\n" +
                "   - Es importante conservar el justificante de baja, ya que acredita la fecha de finalización ante Hacienda.\n\n" +

                "2. Baja en la Seguridad Social:\n" +
                "   - Presentar el Modelo TA0521, marcando la opción de baja en el Régimen Especial de Trabajadores Autónomos.\n" +
                "   - El plazo para comunicar la baja es de tres días naturales desde el cese efectivo de la actividad.\n" +
                "   - A partir de la fecha de baja, se deja de cotizar y pagar la cuota mensual de autónomos.\n\n" +

                "3. Consejos prácticos:\n" +
                "   - No olvides revisar si hay obligaciones pendientes con Hacienda, como la presentación de modelos trimestrales o anuales.\n" +
                "   - Es recomendable realizar ambas bajas (Hacienda y Seguridad Social) el mismo día o con un intervalo mínimo, para evitar incidencias administrativas.\n" +
                "   - Conserva durante al menos 4 años todos los justificantes y declaraciones presentadas.";


        // Configuración del diálogo
        builder.setMessage(mensaje);
        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onCerrar();
            }
        });

        return builder.create();
    }

    // Interfaz para comunicación con la actividad
    public interface OnCerrarDialogo {
        void onCerrar();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnCerrarDialogo) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar OnCerrarDialogo");
        }
    }
}
