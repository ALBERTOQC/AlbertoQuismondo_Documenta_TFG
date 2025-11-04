package es.albertqc.albertoquismondo_documenta_tfg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogoDocumentosSociedades extends DialogFragment {
    OnCerrarDialogo listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Documentación para sociedades");

        String mensaje = "ALTA DE UNA SOCIEDAD\n\n" +

                "1) Certificación negativa de denominación social (Registro Mercantil Central)\n" +
                "   - Qué es: un documento que acredita que el nombre elegido para la sociedad no está registrado por otra empresa.\n" +
                "   - Para qué sirve: es necesario solicitarlo antes de elevar la escritura de constitución al notario; garantiza la exclusividad del nombre.\n" +
                "   - Dónde se gestiona: Registro Mercantil Central (se solicita telemáticamente o por registro).\n\n" +

                "2) Estatutos sociales\n" +
                "   - Qué es: el documento que regula el funcionamiento interno de la sociedad (objeto social, domicilio, capital social, distribución de participaciones, órganos de administración, reglas de convocatoria, etc.).\n" +
                "   - Para qué sirve: se incorporan a la escritura pública y serán la norma básica de la empresa.\n" +
                "   - Recomendación: redactarlos con claridad y, si es posible, con asesoramiento jurídico o de gestoría.\n\n" +

                "3) Escritura pública de constitución (notaría)\n" +
                "   - Qué es: el acto formal ante notario donde los socios firman la constitución y se elevan los estatutos a público.\n" +
                "   - Qué incluye: identidad de socios, aportaciones (capital social), estatutos, nombramiento de administradores y certificación negativa del nombre.\n" +
                "   - Observación práctica: tras la firma, la notaría suele tramitar el NIF provisional y los documentos para su inscripción en el Registro Mercantil.\n\n" +

                "4) Liquidación del Impuesto sobre Transmisiones Patrimoniales y Actos Jurídicos Documentados (Modelo 600)\n" +
                "   - Qué es: autoliquidación que, según el tipo de constitución y la normativa autonómica, puede ser exigible; en muchos casos existe exención pero debe acreditarse.\n" +
                "   - Para qué sirve: justifica ante la Hacienda autonómica el cumplimiento o la exención del impuesto derivado de la escritura pública.\n" +
                "   - Recomendación: consulta en la oficina de la Hacienda autonómica o con una gestoría, porque el trámite y la documentación requerida varían por comunidad.\n\n" +

                "5) Inscripción en el Registro Mercantil Provincial\n" +
                "   - Qué es: una vez elevada la escritura pública, hay que inscribir la sociedad en el Registro Mercantil provincial correspondiente al domicilio social.\n" +
                "   - Para qué sirve: otorga personalidad jurídica a la sociedad y permite operar oficialmente (contratos, cuentas bancarias a nombre de la sociedad, etc.).\n\n" +

                "6) Alta en la Agencia Tributaria (Modelo 036)\n" +
                "   - Qué es: declaración censal para darse de alta ante Hacienda, solicitar el NIF definitivo y comunicar la actividad económica.\n" +
                "   - Para qué sirve: permite cumplir obligaciones fiscales (IVA, retenciones, obligaciones informativas).\n" +
                "   - Observaciones: el NIF provisional suele gestionarse en el proceso de constitución; el Modelo 036 formaliza y comunica datos fiscales ante la Agencia Tributaria.\n\n" +

                "7) Inscripción en la Seguridad Social (si procede)\n" +
                "   - Qué es: si la sociedad va a tener empleados, la empresa debe registrarse como empresario y solicitar un código de cuenta de cotización (modelos tipo TA.6 / TA.7 según caso).\n" +
                "   - Para qué sirve: es obligatorio para dar de alta a los trabajadores y cumplir con las obligaciones de cotización.\n\n" +

                "BAJA / DISOLUCIÓN DE UNA SOCIEDAD\n\n" +

                "1) Acuerdo de disolución y escritura pública\n" +
                "   - Qué es: la junta de socios debe aprobar la disolución; el acuerdo se eleva a escritura pública ante notario y se nombra a los liquidadores.\n\n" +

                "2) Liquidación de la sociedad\n" +
                "   - Qué es: los liquidadores cierran las cuentas, pagan deudas, venden activos y reparten el remanente entre los socios.\n\n" +

                "3) Inscripción de la disolución en el Registro Mercantil\n" +
                "   - Qué es: una vez finalizada la liquidación, se inscribe la escritura de extinción y la sociedad queda cancelada en el registro.\n\n" +

                "4) Baja en Hacienda (Modelo 036)\n" +
                "   - Qué es: comunicar la baja censal para indicar el cese de la actividad y la fecha efectiva de finalización.\n\n" +

                "5) Baja en la Seguridad Social (Modelos TA.6 / TA.7)\n" +
                "   - Qué es: dar de baja la empresa en la Seguridad Social y comunicar la baja de los trabajadores si los hubiera.\n\n" +

                "CONSEJOS PRÁCTICOS FINALES\n\n" +
                "- Plazos: coordina las comunicaciones (Hacienda, Seguridad Social y Registro Mercantil) para evitar solapamientos o pagos innecesarios.\n" +
                "- Justificantes: conserva durante al menos 4 años todos los justificantes, escrituras y acreditaciones de presentación.\n" +
                "- Asesoramiento: para sociedades es muy recomendable trabajar con una gestoría o asesoría (notarial, fiscal y laboral) para evitar errores formales que retrasen la inscripción.\n" +
                "- Costes: ten en cuenta costes notariales, registrales y, en su caso, el coste de la gestoría; el capital mínimo para una Sociedad Limitada (SL) suele ser un requisito a aportar en el momento de constitución.\n\n";


        builder.setMessage(mensaje);
        builder.setPositiveButton("Cerrar", (dialog, id) -> listener.onCerrar());
        return builder.create();
    }

    public interface OnCerrarDialogo {
        void onCerrar();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnCerrarDialogo) context;
    }
}
