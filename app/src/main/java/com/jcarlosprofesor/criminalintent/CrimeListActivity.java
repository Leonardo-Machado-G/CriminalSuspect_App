package com.jcarlosprofesor.criminalintent;
import androidx.fragment.app.Fragment;
public class CrimeListActivity extends SingleFragmentActivity {

    //Metodo para instanciar un crimelistfragment
    @Override
    protected Fragment createFragment() {return new CrimeListFragment();}

}