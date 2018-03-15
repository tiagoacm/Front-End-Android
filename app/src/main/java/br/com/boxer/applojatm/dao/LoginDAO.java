package br.com.boxer.applojatm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import br.com.boxer.applojatm.model.Login;
import br.com.boxer.applojatm.util.DbGateway;

/**
 * Created by tiago on 01/03/18.
 */

// classe responsável por fazer persistencia de dados no SQLite
public class LoginDAO {

    private final String TABLE_LOGIN = "Login";
    private DbGateway gw;

    public LoginDAO(Context ctx){
        gw = DbGateway.getInstance(ctx);
    }

    // faz insert na tabela login
    public boolean salvar(String token, String cpf, String nome){
        ContentValues cv = new ContentValues();
        cv.put("token", token);
        cv.put("cpf", cpf);
        cv.put("nome", nome);
        return gw.getDatabase().insert(TABLE_LOGIN, null, cv) > 0;
    }

    // faz consulta na tabela de login
    public Login consultar(){
        Cursor cursor = gw.getDatabase().rawQuery("SELECT token, cpf, nome FROM Login ORDER BY id DESC", null);
        Login login = new Login();
        // pega somente um registro na tabela
        cursor.moveToFirst();
        String tokenLogin = cursor.getString(cursor.getColumnIndex("token"));
        String cpfLogin = cursor.getString(cursor.getColumnIndex("cpf"));
        String nomeLogin = cursor.getString(cursor.getColumnIndex("nome"));
        // carrega as informações no objeto login para retornar
        login.setToken(tokenLogin);
        login.setCpfCliente(cpfLogin);
        login.setNomeCliente(nomeLogin);
        cursor.close();
        return login;
    }

    // faz exclusão dos dados na tabela login
    public Login excluir(){
        gw.getDatabase().rawQuery("DELETE FROM Login", null);
        return null;
    }
}
