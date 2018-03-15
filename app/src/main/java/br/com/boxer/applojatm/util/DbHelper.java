package br.com.boxer.applojatm.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tiago on 01/03/18.
 */

// classe responsável por criar o banco de dados SQLite
public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lojaTM.db";
    private static final int DATABASE_VERSION = 1;
    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Login " +
                                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        " token TEXT NOT NULL, " +
                                        " cpf TEXT, " +
                                        " nome TEXT NOT NULL);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
