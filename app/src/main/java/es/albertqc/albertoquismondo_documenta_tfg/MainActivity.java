package es.albertqc.albertoquismondo_documenta_tfg;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import es.albertqc.albertoquismondo_documenta_tfg.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //  CONFIGURACIÓN DEL TOOLBAR
        setSupportActionBar(binding.appBarMain.toolbar);

        // Colores corporativos
        binding.appBarMain.toolbar.setBackgroundColor(Color.parseColor("#1E3A5F"));
        binding.appBarMain.toolbar.setTitleTextColor(Color.WHITE);
        binding.appBarMain.toolbar.setTitle("Documenta");


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Destinos principales
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_inicio, R.id.nav_autonomos, R.id.nav_sociedades,
                R.id.nav_documentacion, R.id.nav_requisitos, R.id.nav_configuracion,
                R.id.nav_acerca, R.id.nav_admin)
                .setOpenableLayout(drawer)
                .build();

        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Lógica del menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                navController.popBackStack(R.id.nav_inicio, false);
                navController.navigate(R.id.nav_inicio);
            } else {
                navController.navigate(id);
            }

            drawer.closeDrawers();
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
