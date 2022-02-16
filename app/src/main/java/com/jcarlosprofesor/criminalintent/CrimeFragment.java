package com.jcarlosprofesor.criminalintent;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.util.Date;
import java.util.UUID;
public class CrimeFragment extends Fragment {

    //Declaro los widgets necesarios
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private EditText mSuspectField;

    //Instancio un variable para intercambiar informacion
    private static final String ARG_CRIME_ID = "crime_id";

    //Constante que nos va a etiquetar el DataPickerFragment a mostrar
    private static final String DIALOG_DATE = "DialogDate";

    //Constante para el código de petición
    private static final int REQUEST_DATE = 0;

    //Constante para el código de peticion del contacto
    private static final int REQUEST_CONTACT = 1;

    //Metodo para instanciar un fragment con un ID
    public static CrimeFragment newInstance(UUID crimeId) {

        //Instancio un bundle para guardar informacion
        Bundle args = new Bundle();

        //Inserto su ID en el bundle
        args.putSerializable(ARG_CRIME_ID, crimeId);

        //Instancio un crimefragment
        CrimeFragment fragment = new CrimeFragment();

        //Asocio el bundle al fragment
        fragment.setArguments(args);

        //Devuelvo el fragment
        return fragment;

    }

    //Metodo que se ejecuta segun el ciclo de vida de una fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Instancio un UUID obteniendo el ID del intent
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        this.mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }

    //Metodo que se ejecuta segun el ciclo de vida de una fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Instancio una view y la asocio a un layout
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        //Serializo un widget
        this.mTitleField = (EditText) view.findViewById(R.id.crime_title);

        //Establezco un texto para el widget
        this.mTitleField.setText(mCrime.getTitle());

        //Defino un comportamiento para el textview
        this.mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            //Metodo que se ejecuta cuando cambia el valor del edittext
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });

        //Serializo un widget
        this.mDateButton = (Button) view.findViewById(R.id.crime_date);

        //Establezco un texto para el widget
        this.mDateButton.setText(mCrime.getDate().toString());

        //Establecemos el listener que nos mostrara el Dialog
        this.mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instanciamos un fragmentmanager
                FragmentManager fragmentManager = getParentFragmentManager();

                //Instaciamos un dialog
                DatePickerFragment dialog = DatePickerFragment.newInstante(mCrime.getDate());

                //Establecemos el fragment destino
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                //Mostramos el dialog
                dialog.show(fragmentManager, DIALOG_DATE);

            }
        });

        //Serializo el widget
        this.mSuspectField = (EditText) view.findViewById(R.id.edit_text_suspect);

        //Añado el texto del sospechoso
        this.mSuspectField.setText(this.mCrime.getSuspect());

        //Le doy un comportamiento al cambiar el texto
        this.mSuspectField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            //Metodo para detectar el cambio del widget
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //Establezco el valor del sospechoso
                mCrime.setSuspect(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        //Serializo un widget
        this.mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);

        //Cambio el widget en funcion del crime
        this.mSolvedCheckBox.setChecked(mCrime.isSolved());

        //Establezco un comportamiento al cambiar el estado del widget
        this.mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Establecemos el crime como true o false
                mCrime.setSolved(isChecked);

            }
        });

        //Serializamos el button para el boton de envio del informe
        this.mReportButton = (Button) view.findViewById(R.id.crime_report);

        //En la implementación de su listener añadimos el intent implicito de envio de text
        this.mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Instanciamos el intent con la accion a realizar
                Intent intent = new Intent(Intent.ACTION_SEND);

                //Establecemos un tipo de dato
                intent.setType("text/plain");

                //Inserto en el intent el reporte con la informacion
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());

                //Inserto en el intent el asunto
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                //Creamos un chooser para asegurarnos que el usuario usa
                //siempre le aparezcan opciones de eleccion
                intent = Intent.createChooser(intent, getString(R.string.send_report));

                //Iniciamos el intent
                startActivity(intent);

            }

        });

        //Codigo para iniciar la aplicacion de contacts
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //Serializo el button
        this.mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);

        //Le doy un comportamiento al button
        this.mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Inserto el intent y el tipo de dato
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }

        });
        return view;

    }

    //Sobreescribimos onActivityResult para recuperar el extra
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //Devolvemos al resultcode al valer result_ok
        if (resultCode != Activity.RESULT_OK) {return;}

        //Si resultcode vale 0 insertamos una nueva fecha al crimen y al button
        if (requestCode == REQUEST_DATE) {

            //Establezco una fecha y lo inserto en crime
            this.mCrime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));

            //Establezco el texto del button con la fecha
            this.mDateButton.setText(this.mCrime.getDate().toString());

        }

        //Añadimos el tratamiento del resultado devuelto por la app Contactos
        else if (requestCode == REQUEST_CONTACT && data != null) {

            //Secuencia compacta de caracteres de un recurso abstracto
            Uri contactUri = data.getData();

            //Especificamos el campo para el que la consulta devuelva valores
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            //Instancio un cursor y ejecutamos la consulta
            Cursor c = getActivity().getContentResolver().query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null);

            try {

                //Comprobamos que hemos obtenidos resultados
                if (c.getCount() == 0) {return;}

                //Extraemos la primera columna
                c.moveToFirst();

                //Obtenemos del cursor el nombre del sospechoso
                String suspect = c.getString(0);

                //Establecemos los datos del crime, button y edit text
                this.mCrime.setSuspect(suspect);
                this.mSuspectField.setText(suspect);

            } finally {

                //Cerramos el cursor
                c.close();

            }

        }

    }

    /*Sobreescribimos onPause para que las instancias modificadas de Crime
      sean guardadas antes de que CrimeFragment finalice*/
    @Override
    public void onPause() {
        super.onPause();

        //Llamamos al método para actualizar un crimen
        CrimeLab.get(getActivity()).updateCrime(this.mCrime);

    }

    //Metodo que construira el informe de un crime en ejecucion
    private String getCrimeReport() {

        //Si el crimen esta resuelto obtengo un string u otro
        String solvedString = (this.mCrime.isSolved()) ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        //Creamos el formato de la fecha
        String dateString = DateFormat.format(
                "EEE MMM dd",
                mCrime.getDate()).toString();

        //Obtengo el nombre del sospechoso
        String suspect = this.mCrime.getSuspect();

        //Si el sospechoso es nulo indicamos que no es sospechoso
        suspect = (suspect == null) ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, suspect);

        //Creo el mensaje que he de devolver con los datos
        return getString(
                R.string.crime_report,
                mCrime.getTitle(),
                dateString, solvedString,
                suspect);

    }

}