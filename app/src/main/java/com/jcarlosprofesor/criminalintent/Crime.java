package com.jcarlosprofesor.criminalintent;
import java.util.Date;
import java.util.UUID;
public class Crime {

    //Defino los atributos de la clase
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    //Añadimos el campo suspect a nuestro modelo de datos
    private String mSuspect;

    //Modificamos el constructor para el cursor que hemos creado.
    public Crime() {this(UUID.randomUUID());}

    //Sobrecarga de constructores
    public Crime(UUID id) {
        this.mId = id;
        this.mDate = new Date();
    }

    //Definimos los get de la clase
    public UUID getId() {return this.mId;}
    public String getTitle() {return this.mTitle;}
    public Date getDate() {return this.mDate;}
    public boolean isSolved() {return this.mSolved;}
    public String getSuspect() {return this.mSuspect;}

    //Definimos los set de la clase
    public void setTitle(String title) {this.mTitle = title;}
    public void setDate(Date date) {this.mDate = date;}
    public void setSolved(boolean solved) {this.mSolved = solved;}
    public void setSuspect(String suspect) {this.mSuspect = suspect;}

}
