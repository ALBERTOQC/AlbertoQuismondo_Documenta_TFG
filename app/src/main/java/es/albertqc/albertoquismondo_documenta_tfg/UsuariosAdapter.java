package es.albertqc.albertoquismondo_documenta_tfg;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private int selectedPos = RecyclerView.NO_POSITION;

    // Listener para notificar al Activity qué usuario está seleccionado
    public interface OnUsuarioSeleccionadoListener {
        void onUsuarioSeleccionado(Usuario usuario);
    }

    private OnUsuarioSeleccionadoListener listener;

    // Constructor principal (con listener)
    public UsuariosAdapter(List<Usuario> listaUsuarios, OnUsuarioSeleccionadoListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    // Constructor secundario (solo lista, listener vacío)
    public UsuariosAdapter(List<Usuario> listaUsuarios) {
        this(listaUsuarios, usuario -> { /* listener vacío */ });
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_miembros, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) return;

        Usuario usuario = listaUsuarios.get(pos);

        holder.txtNombre.setText(usuario.getNombre());
        holder.txtRol.setText(" Soy: " + usuario.getRol());
        holder.txtActividad.setText(usuario.getActividad());

        // cambiaará el icono según el rol
        if (usuario.getRol().equalsIgnoreCase("autónomo")) {
            holder.imgAvatar.setImageResource(R.drawable.ic_autonomo);
        } else if (usuario.getRol().equalsIgnoreCase("sociedad")) {
            holder.imgAvatar.setImageResource(R.drawable.ic_sociedad);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_administrador);
        }

        // Cambiar color del fondo si está seleccionado
        holder.itemView.setBackgroundColor(pos == selectedPos ? Color.parseColor("#289A1B") : Color.WHITE);

        // Selección
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPos);
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(selectedPos);

            if (listener != null) listener.onUsuarioSeleccionado(usuario);
        });

        // Botón enviar correo
        holder.btnCorreo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + usuario.getCorreo()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde Documenta");
            intent.putExtra(Intent.EXTRA_TEXT, "Hola " + usuario.getNombre() + ",\n\n");

            v.getContext().startActivity(Intent.createChooser(intent, "Enviar correo"));
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    // Permite que la Activity pueda cambiar la posición seleccionada
    public void setSelectedPosition(int pos) {
        int oldPos = selectedPos;
        selectedPos = pos;
        notifyItemChanged(oldPos);
        notifyItemChanged(selectedPos);
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtRol, txtActividad;
        ImageView imgAvatar;
        ImageButton btnCorreo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtRol = itemView.findViewById(R.id.txtRol);
            txtActividad = itemView.findViewById(R.id.txtActividad);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnCorreo = itemView.findViewById(R.id.btnCorreo);
        }
    }
}