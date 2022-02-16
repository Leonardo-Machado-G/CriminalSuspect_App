package com.jcarlosprofesor.criminalintent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class CrimeListFragment extends Fragment {

    //Defino una recyclerview y un adapter y un subtitulo
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    //Metodo que se ejecuta segun el ciclo de vida del fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Indico que posee un menu
        setHasOptionsMenu(true);

    }

    //Metodo que se ejecuta segun el ciclo de vida del fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Instancio una view y le asocio un layout
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        //Serializo el recyclerview
        this.mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        //Instacio un layout y lo asocio al recyclerview
        this.mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Instancio un crimelab
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Obtengo la lista de crimenes del crimelab
        List<Crime> crimes = crimeLab.getCrimes();

        //Instancio el crimeadapter
        this.mAdapter = new CrimeAdapter(crimes);

        //Le asocio un adapter al recyclerview
        this.mCrimeRecyclerView.setAdapter(mAdapter);
        return view;

    }

    //Metodo para actualizar el fragment al regresar a él
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    //Metodo par actualizar la lista al volver a ella
    private void updateUI() {

        //Instancio un crimelab
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Obtengo la lista de crimes
        List<Crime> crimes = crimeLab.getCrimes();

        //Asocio un adapter al recyclerview
        if (this.mAdapter == null) {

            this.mAdapter = new CrimeAdapter(crimes);
            this.mCrimeRecyclerView.setAdapter(this.mAdapter);

        } else {

            //Actualizamos la lista que debe usar el adaptador para mostrar los crimenes
            this.mAdapter.setCrimes(crimes);
            this.mAdapter.notifyDataSetChanged();

        }

    }

    //Inyectamos el archivo de layout del menu, en el objeto menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //Asociamos el menu a su layout
        inflater.inflate(R.menu.fragment_crime_list, menu);

        //Serializamos un item del menu
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);

        //Si el subtitulo se tiene que ver toma un string u otro
        subtitleItem = this.mSubtitleVisible ?
                subtitleItem.setTitle(R.string.hide_subtitle) :
                subtitleItem.setTitle(R.string.show_subtitle);

    }

    //Metodo para definir el comportamiento al hacer clic en un item del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Definimos un switch con el item
        switch (item.getItemId()) {

            //Definimos el comportamiento del item new crime
            case R.id.new_crime:

                //Añadimos un nuevo crime
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                //Comenzamos una activity
                startActivity(CrimePagerActivity
                        .newIntent(getActivity(), crime.getId()));
                return true;

            //Definimos el comportamiento del item show subtitle
            case R.id.show_subtitle:

                //Cambiamos el valor del subtitulo y actualizamos el subtitulo
                this.mSubtitleVisible = !this.mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    //Metodo para actualizar el mensaje del menu
    private void updateSubtitle() {

        //Obtenemos un crimelab con este context
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Obtenemos el tamaño de la lista
        int crimeCount = crimeLab.getCrimes().size();

        //Obtengo el texto con un formato
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        //Si el subtitulo es invisible lo transforma es null
        subtitle = !mSubtitleVisible ? null : subtitle;

        //Instancio una activity
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        //Asocio a esta activity un subtitulo
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    //Defino una clase interna
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declaro 3 widget y un crime
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        //Defino un constructor, serializo los widget y asocio el adapter a la clase
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            this.mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            this.mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            this.mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved_img);
            this.itemView.setOnClickListener(this);
        }

        //Defino un metodo para obtener fragments y definir como se situan en la view
        public void bind(Crime crime) {
            this.mCrime = crime;
            this.mTitleTextView.setText(mCrime.getTitle());
            this.mDateTextView.setText(mCrime.getDate().toString());
            this.mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        //Defino el metodo que reacciona al hacer click sobre un elemento instanciando un nuevo fragment
        @Override
        public void onClick(View v) {
            startActivity(CrimePagerActivity.newIntent(getActivity(), this.mCrime.getId()));
        }

    }

    //Declaro una clase interna
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        //Declaro una lista de crimenes
        private List<Crime> mCrimes;

        //Declaro su constructor
        public CrimeAdapter(List<Crime> crimes) {this.mCrimes = crimes;}

        //Metodo que devuelve un crimeholder
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CrimeHolder(LayoutInflater.from(getActivity()), parent);
        }

        //Metodo que actualiza la lista cada cierto numero de elementos
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(this.mCrimes.get(position));
        }

        //Metodo que define la longitud de la lista
        @Override
        public int getItemCount() {return this.mCrimes.size();}

        //Metodo que reemplaza la lista de crimenes que se muestran
        public void setCrimes(List<Crime> crimes) {this.mCrimes = crimes;}

    }

}
