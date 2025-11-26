package es.albertqc.albertoquismondo_documenta_tfg;


public class Usuario {

    private String uid;
    private String nombre;
    private String correo;
    private String rol;
    private String actividad;

    public Usuario() {}

    public Usuario(String uid, String nombre, String correo, String rol, String actividad) {
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.actividad = actividad;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ---- CORREO ----
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }


    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }
}
