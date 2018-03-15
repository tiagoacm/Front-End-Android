package br.com.boxer.applojatm.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tiago on 01/03/18.
 */

// classe responsável por gerar uma instancia de conexão com o banco de dados
public class DbGateway {

    private static DbGateway gw;
    private SQLiteDatabase db;

    private DbGateway(Context ctx){
        DbHelper helper = new DbHelper(ctx);
        db = helper.getWritableDatabase();
    }

    public static DbGateway getInstance(Context ctx){
        if(gw == null)
            gw = new DbGateway(ctx);
        return gw;
    }

    public SQLiteDatabase getDatabase(){
        return this.db;
    }
}