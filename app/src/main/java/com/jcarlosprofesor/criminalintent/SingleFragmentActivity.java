package com.jcarlosprofesor.criminalintent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
public abstract class SingleFragmentActivity extends AppCompatActivity {

    //Defino un metodo abstracto para heredar
    protected abstract Fragment createFragment();

    //Metodo que se ejecuta segun el ciclo de vida de una activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asocio la view al layout
        setContentView(R.layout.activity_fragment);

        //Instancio un fragmentmanager
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Declaro un fragment y mediante el manager le asocio su ID
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        //Si el fragment es null instacio el fragment y lo añado a su ID en el layout
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

}
