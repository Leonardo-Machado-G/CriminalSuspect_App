package com.jcarlosprofesor.criminalintent;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import java.util.UUID;
public class CrimeActivity extends SingleFragmentActivity {

    //Instancio un variable estatica para intercambiar informacion
    public static final String EXTRA_CRIME_ID = "crime_id";

    //Metodo heredado de singleton para devolver un fragment con un ID
    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance((UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID));
    }

    //Metodo para devolver un intent hacia crimeactivity con el ID de fragment
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        return new Intent(packageContext, CrimeActivity.class).putExtra(EXTRA_CRIME_ID, crimeId);
    }

}