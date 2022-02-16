package com.jcarlosprofesor.criminalintent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jcarlosprofesor.criminalintent.database.CrimeBaseHelper;
import com.jcarlosprofesor.criminalintent.database.CrimeCursorWrapper;
import com.jcarlosprofesor.criminalintent.database.CrimeDbSchema.CrimeTable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class CrimeLab {

    //Declaro un crimelab estatico
    private static CrimeLab sCrimeLab;

    //Declaro un contexto
    private Context mContext;

    //Declaro una BD
    private SQLiteDatabase mDatabase;

    //Defino un constructor
    private CrimeLab(Context context) {

        //Código para abrir la base de datos.
        this.mContext = context.getApplicationContext();

        //En la llamada al metodo getWritableDatabase, CrimeBaseHelper hace:
        //1ºAbre /data/data/com.jcarlosprofesor.android.criminalintent/databases/crimeBase.db
        //2º Si es la primera vez que se crea la base de datos llama a onCreate(SQLiteDataBase)
        //3º Si no es la primera vez, comprueba el numero de versión en la base de datos
        //si el número de versión en CrimeOpenHelper es mayor, llama a onUpgrade y lo actualiza
        this.mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    //Metodo para devolver una lista con crimes
    public List<Crime> getCrimes() {

        //Return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();

        //Instanciamos un cursor para desplazarnos por la tabla
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {

            //Nos desplazamos al comienzo de la tabla
            cursor.moveToFirst();

            //Si no estamos en la ultima fila recorremos la tabla
            while (!cursor.isAfterLast()) {

                //Añado un crime y me desplazo al siguiente
                crimes.add(cursor.getCrime());
                cursor.moveToNext();

            }

        } finally {
            cursor.close();
        }
        return crimes;

    }

    //Metodo para obtener un crime
    public Crime getCrime(UUID id) {

        //Instancio un wrapper y obtengo los resultados de la condicion
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {

            //Devuelve el numero de columnas si es 0 devulve un nulo
            if (cursor.getCount() == 0) {
                return null;
            }

            //Desplazamos el cursor a la primera fila
            cursor.moveToFirst();

            //Llamamos al cursor para devolver el crime
            return cursor.getCrime();

        } finally {
            cursor.close();
        }

    }

    //Metodo para obtener un crimelab
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //Metodo que se encarga de pasar un Crime a una instancia de ContentValues
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    //Metodo para añadir un Crime
    public void addCrime(Crime c) {

        //Creamos el "paquete" ContentValues y despues lo añadimos a la BD
        ContentValues values = getContentValues(c);
        this.mDatabase.insert(CrimeTable.NAME, null, values);

    }

    //Metodo para actualizar un crimen
    public void updateCrime(Crime crime) {

        //Obtengo el ID
        String uuidString = crime.getId().toString();

        //Obtengo los valores del crime
        ContentValues values = getContentValues(crime);

        //Actualizo los datos
        this.mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});

    }

    //private Cursor queryCrimes(String whereClause, String[] whereArgs)
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {

        //Declaro un cursor y lo asocio a una tabla
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null

        );

        //Devuelvo el cursor;
        return new CrimeCursorWrapper(cursor);

    }

}
