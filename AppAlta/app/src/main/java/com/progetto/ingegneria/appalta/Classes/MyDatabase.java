package com.progetto.ingegneria.appalta.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by matteo on 30/04/2015.
 */
public class MyDatabase {

    SQLiteDatabase mDb;
    DbHelper mDbHelper;
    Context mContext;
    private static final String DB_NAME="MyDB";//nome del db
    private static final int DB_VERSION=1; //numero di versione del nostro db

    //tabella news
    public static final String TABLE = "farmacie";
    public static final String ID = "cig";
    public static final String DENOMINAZIONE = "denominazione";
    public static final String VIA = "via";
    public static final String DESCR_COMUNE = "descr_comune";
    public static final String DESCR_PROVINCIA = "descr_provincia";
    public static final String FAX = "fax";
    public static final String TELEFONO = "telefono";
    public static final String LATITUDINE = "latitudine";
    public static final String LONGITUDINE = "longitudine";


    public MyDatabase(Context ctx){
        mContext=ctx;
        mDbHelper=new DbHelper(ctx, DB_NAME, null, DB_VERSION);   //quando istanziamo questa classe, istanziamo anche l'helper (vedi sotto)
    }

    public void open(){  //il database su cui agiamo è leggibile/scrivibile
        mDb=mDbHelper.getWritableDatabase();
    }

    public void close(){ //chiudiamo il database su cui agiamo
        mDb.close();
    }

    public boolean isOpen() {
        return mDb.isOpen();
    }

    public void insertData(String id, String denominazione, String via, String descr_comune,String descr_provincia, String fax, String telefono, Double latitudine, Double longitudine){     //metodo per inserire i dati
        ContentValues cv=new ContentValues();
        cv.put(ID, id);
        cv.put(DENOMINAZIONE, denominazione);
        cv.put(VIA, via);
        cv.put(DESCR_COMUNE, descr_comune);
        cv.put(DESCR_PROVINCIA, descr_provincia);
        cv.put(FAX, fax);
        cv.put(TELEFONO, telefono);
        cv.put(LATITUDINE, latitudine);
        cv.put(LONGITUDINE, longitudine);
        mDb.insert(TABLE, null, cv);
    }

    public void deleteData(String id) {
       mDb.delete(TABLE,ID + " = '"+id+"'" , null);
    }

    public void deleteAll(){
        mDb.delete(TABLE, null, null);
    }

    public void dropDb(SQLiteDatabase _db){
        _db.execSQL(DROP_DB);
    }

    public Cursor fetchDataById(String id){ //metodo per fare la query di tutti i dati
        return mDb.rawQuery("SELECT * FROM "+TABLE+" WHERE("+ID+" = '"+id+"') ORDER BY "+ID+" DESC",null);
    }

    public Cursor fetchDataByDenominazione(String denominazione){ //metodo per fare la query di tutti i dati
        return mDb.rawQuery("SELECT * FROM "+TABLE+" WHERE("+DENOMINAZIONE+" LIKE '%"+denominazione+"%') ORDER BY "+ID+" DESC ",null);
    }

    public Cursor fetchData(){ //metodo per fare la query di tutti i dati
        return mDb.rawQuery("SELECT * FROM "+TABLE+" ORDER BY "+ID+" DESC ",null);
    }

    public Cursor fetchIdById(String id){ //metodo per fare la query di tutti i dati
        return mDb.rawQuery("SELECT "+ID+" FROM "+TABLE+" WHERE "+ID+" = '"+id+"' ORDER BY "+ID+" DESC ",null);
    }

    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "  //codice sql di creazione della tabella
            + TABLE + " ("
            + ID + " text unique not null, "
            + DENOMINAZIONE + " text not null, "
            + VIA + " text not null, "
            + DESCR_COMUNE + " text not null,"
            + DESCR_PROVINCIA + " text not null,"
            + FAX + " text not null,"
            + TELEFONO + " text not null,"
            + LATITUDINE + " double not null,"
            + LONGITUDINE + " double not null);";

    private static final String DROP_DB = "DROP DATABASE "+DB_NAME;

    private class DbHelper extends SQLiteOpenHelper { //classe che ci aiuta nella creazione del db

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) { //solo quando il db viene creato, creiamo la tabella
            _db.execSQL(TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            //qui mettiamo eventuali modifiche al db, se nella nostra nuova versione della app, il db cambia numero di versione

        }

    }

}
