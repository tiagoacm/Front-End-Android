package br.com.boxer.applojatm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import br.com.boxer.applojatm.dao.LoginDAO;
import br.com.boxer.applojatm.model.Login;

// Classe responsável por montar a tela principal
public class PrincipalActivity extends AppCompatActivity {

    private TextView txtNomeLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Loja TM");
        setSupportActionBar(toolbar);

        // carrega objetos na tela
        txtNomeLogin = (TextView) findViewById(R.id.txtNomeLogin);

        // acessa SQLite para obter dados do usuário logado
        LoginDAO dao = new LoginDAO(getBaseContext());
        Login loginDao = dao.consultar();
        if (loginDao != null){
            txtNomeLogin.setText(loginDao.getNomeCliente());
          }else{
            txtNomeLogin.setText("visitante.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // tratamento da opção meus dados no menu
        if (id == R.id.action_meusDados){
            Intent intent = new Intent(PrincipalActivity.this, ClienteDadosActivity.class);
            startActivity(intent);
            return true;
        }

        // tratamento da opção sair no menu
        if (id == R.id.action_sair){
            //apagar registro de login na tabela SQLite
            LoginDAO dao = new LoginDAO(getBaseContext());
            dao.excluir();

            // direciona para tela de login
            Intent intent = new Intent(PrincipalActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
