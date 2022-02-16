package com.jcarlosprofesor.criminalintent;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Clase que va a definir la instancia de nuestro dialogo
public class DatePickerFragment extends DialogFragment {

    //Instancio una constante para intercambiar informacion dentro del bundle
    private static final String ARG_DATE = "date";

    //Instancio una constante para identificar la fecha que pasamos en el intent
    public static final String EXTRA_DATE = "intent_date";

    //Declaro un datepicker
    private DatePicker mDatePicker;

    //Creamos el metodo newInstance para crear un fragment y transferir la fecha en el bundle
    public static DatePickerFragment newInstante(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //onCreateDialog será llamado como parte del proceso para presentar el DialogFragment
    //Este llama a una interfaz fluida
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Para inicializar el DataPicker con la fecha del objeto Date
        //DataPicker necesita numeros enteros para el mes, dia y año
        //Debemos tratar el objeto Date para obtener estos números y
        //luego asignarlos
        //DataPicker necesita numeros enteros para el mes, dia y año
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        //Instancio un calendar
        Calendar calendar = Calendar.getInstance();

        //Asocio date a calendar
        calendar.setTime(date);

        //Obtengo datos de calendar
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Declaro una view y la asocio a su layout mediante su ID
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        //Serializo el datepicker
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_data_picker);

        //Inserto los datos de la fecha
        mDatePicker.init(year, month, day, null);

        //Usa la clase AlertDialog para construir una instancia de AlertDialog
        //Pasamos un Context al constructor de AlertDialog
        //Invocamos dos metodos de AlertDialog.Builder para configurar el dialogo.
        //setPositiveButton acepta un id de cadena, un objeto que implementa onClickListener
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    //Metodo que se ejecuta al pulsar
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Obtengo los datos del datepicker
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();

                        //Inserto los datos en un date
                        Date date = new GregorianCalendar(year, month, day).getTime();

                        //Llamamos al metodo
                        sendResult(Activity.RESULT_OK, date);

                    }

                }).create();

    }

    //Metodo que actualiza el fragment llamando a activityresult
    private void sendResult(int resultCode, Date date) {

        //Devuelve el fragmento de destino establecido por setTargetFragment
        if (getTargetFragment() == null) {
            return;
        }

        //Recibe el resultado de una llamada anterior a startActivityForResult
        getTargetFragment().onActivityResult(getTargetRequestCode(),
                resultCode,
                new Intent().putExtra(EXTRA_DATE, date));

    }

}
