package es.albertqc.albertoquismondo_documenta_tfg;

import java.util.HashMap;

public class Documentos {

    public static final HashMap<String, String> documentosAutonomos = new HashMap<>();
    public static final HashMap<String, String> conveniosCCAA = new HashMap<>();
    public static final HashMap<String, String> sindicatos = new HashMap<>();
    public static final HashMap<String, String> otrosDocumentos = new HashMap<>();

    static {
        // Autonomos
        documentosAutonomos.put("Modelo036", "https://www.hacienda.gob.es/SGT/NormativaDoctrina/main/main_2017/anexo%20ii%20-%20modelo%20036.pdf");
        documentosAutonomos.put("Modelo037", "https://www.hacienda.gob.es/SGT/NormativaDoctrina/main/main_2017/anexo%20iii%20-%20modelo%20037.pdf");
        documentosAutonomos.put("TA0521", "https://amanecemetropolis.net/wp-content/uploads/2014/06/alta-autonomos-seguridad-social-modelo-ta0521.pdf");
        documentosAutonomos.put("Contrato", "https://www.inmujeres.gob.es/servRecursos/formacion/Pymes/docs/La_insercion/24_Contrato_Indefinido.pdf");
        documentosAutonomos.put("Baja", "https://eal.economistas.es/wp-content/uploads/sites/11/2022/03/Baja-voluntaria-con-preaviso.pdf");
        documentosAutonomos.put("Vacaciones", "https://www.sesametime.com/assets/wp-content/uploads/2020/01/formulario-de-solicitud-de-vacaciones.pdf");

        // Convenios
        conveniosCCAA.put("Andalucía","https://www.ccoo-servicios.es/andalucia/conveniosandalucia/pag1/");
        conveniosCCAA.put("Cataluña","https://es.ccoo.cat/convenis/");
        conveniosCCAA.put("Madrid","https://www.comunidad.madrid/servicios/empleo/convenios-colectivos");
        conveniosCCAA.put("Valencia","https://valencia.cnt.es/convenios-laborales/");
        conveniosCCAA.put("Galicia","https://convenios.xunta.gal/consultaconvenios/busqueda-convenio/buscar");
        conveniosCCAA.put("País Vasco","https://www.euskadi.eus/gobierno-vasco/-/convenios-colectivos/");
        conveniosCCAA.put("Aragón","https://www.aragon.es/documents/d/guest/listaweb-de-sector");
        conveniosCCAA.put("Castilla y León","https://www.ccoo-servicios.es/castillayleon/convenios/pag76/");
        conveniosCCAA.put("Murcia","https://www.carm.es/web/pagina?IDCONTENIDO=246&IDTIPO=200");
        conveniosCCAA.put("Extremadura","https://www.ugtextremadura.org/convenios-colectivos-extremadura-servicios-publicos");
        conveniosCCAA.put("Castilla-La Mancha","https://www.ccoo-servicios.es/castillalamancha/convenioscastillalamancha/");

        // Sindicatos
        sindicatos.put("UGT", "https://www.ugt.es/");
        sindicatos.put("CCOO", "https://www.ccoo.es/");
        sindicatos.put("USO", "https://www.uso.es/");

        // Otros documentos generales
        otrosDocumentos.put("Estatuto de los Trabajadores","https://www.boe.es/biblioteca_juridica/abrir_pdf.php?id=PUB-DT-2025-139");
        otrosDocumentos.put("Guía de Derechos","https://antoniosilva.es/derechos-laborales-en-espana-guia-completa-para-trabajadores/");
        otrosDocumentos.put("Simuladores Laborales","https://www.asinom.com/documentacion/caracteristicas_asinom.pdf");
        otrosDocumentos.put("Contactos Útiles","https://www.mites.gob.es/es/informacion/infgral/directorio/index.htm");
    }
}
