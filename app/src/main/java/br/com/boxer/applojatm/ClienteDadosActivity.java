package br.com.boxer.applojatm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import br.com.boxer.applojatm.dao.LoginDAO;
import br.com.boxer.applojatm.model.Cliente;
import br.com.boxer.applojatm.model.Login;
import br.com.boxer.applojatm.service.ClienteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// classe responsável por tratar a consulta dos dados
public class ClienteDadosActivity extends AppCompatActivity {

    private Button btnExcluir;
    private Button btnAlterar;
    private String cpfDAO;
    private TextView txtCpf;
    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtSenha;
    private TextView txtEndereco;
    private TextView txtCidade;
    private TextView txtEstado;
    private TextView txtTelefone;
    private ProgressBar pgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_dados);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Dados");
        setSupportActionBar(toolbar);

        if (toolbar != null){

            toolbar.setNavigationIcon(R.drawable.arrow_left);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavUtils.navigateUpFromSameTask(ClienteDadosActivity.this);
                }
            });
        }

        // carregar objetos na tela
        txtCpf = (TextView) findViewById(R.id.txtCpf);
        txtNome = (TextView) findViewById(R.id.txtNome);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtSenha = (TextView) findViewById(R.id.txtSenha);
        txtEndereco = (TextView) findViewById(R.id.txtEndereco);
        txtCidade = (TextView) findViewById(R.id.txtCidade);
        txtEstado = (TextView) findViewById(R.id.txtEstado);
        txtTelefone = (TextView) findViewById(R.id.txtTelefone);
        btnAlterar = (Button) findViewById(R.id.btnAlterar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);
        pgBar = (ProgressBar) findViewById(R.id.progressBar);
        pgBar.setVisibility(View.GONE);

        //iniciar Progressbar
        pgBar.setVisibility(View.VISIBLE);

        //consulta SQLite para obter cpf do cliente logado
        LoginDAO dao = new LoginDAO(getBaseContext());
        Login loginDao = dao.consultar();
        if (loginDao != null){
            cpfDAO = loginDao.getCpfCliente();
        }else{
            cpfDAO = "Por favor, realizar login no sistema";
        }

        // chamar api para consultar dados do cliente
        consultarClienteAPI(cpfDAO);

        // tratamento do click botão Alterar
        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CliqueBotaoAlterar();            }
        });

        // tratamento do click botão Excluir
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertaExcluirCliente();
            }
        });
    }

    private void CliqueBotaoAlterar() {
        // chama outra tela, transferindo informações entre telas
        Intent intent = new Intent(ClienteDadosActivity.this, ClienteAlterarActivity.class);
        Bundle dados = new Bundle();
        dados.putString("cpfCli" , txtCpf.getText().toString());
        dados.putString("nomeCli" , txtNome.getText().toString());
        dados.putString("emailCli" , txtEmail.getText().toString());
        dados.putString("senhaCli" , txtSenha.getText().toString());
        dados.putString("enderecoCli" , txtEndereco.getText().toString());
        dados.putString("cidadeCli" , txtCidade.getText().toString());
        dados.putString("estadoCli" , txtEstado.getText().toString());
        dados.putString("telefoneCli" , txtTelefone.getText().toString());
        intent.putExtras(dados);
        startActivity(intent);
    }

    // tela de aviso para confirmação
    private void AlertaExcluirCliente() {

        new AlertDialog.Builder(ClienteDadosActivity.this)
                .setTitle("Aviso!")
                .setMessage("Deseja excluir seus dados?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String cpf = txtCpf.getText().toString();
                        excluirClienteAPI(cpf);
                    }
                })
                .setNegativeButton("Não", null).show();


    }

    // chama api de exclusão
    private void excluirClienteAPI(String cpfExcluir) {

        // montar chamada da api
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClienteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // criar service
        ClienteService service = retrofit.create(ClienteService.class);

        // chamar api de forma assincrona
        Call<Void> excluirCliente = service.excluirCliente(cpfExcluir);

        excluirCliente.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // retorno sucesso da api
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Cadastro excluido com sucesso!"
                            , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ClienteDadosActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else{
                    switch (response.code()){
                        case 400:
                            Toast.makeText(getApplicationContext(), "Dados inconsistentes." , Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(getApplicationContext(), "Cliente não cadastrado" , Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Sistema indisponível, tente novamente." , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Sistema indisponível " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // chama api de consulta
    private void consultarClienteAPI(String cpf){

        // montar chamada da api
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClienteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // criar service
        ClienteService service = retrofit.create(ClienteService.class);

        // chamar api de forma assincrona
        Call<Cliente> consultarCliente = service.consultaClientePorCpf(cpf);

        consultarCliente.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                // finalizar progressBar
                pgBar.setVisibility(View.GONE);

                //tratamento do sucesso e nao sucesso da api
                if (response.isSuccessful()){

                    // preencher os campos da tela com os dados de resposta em caso de sucesso
                    Cliente clienteRetorno = response.body();
                    txtCpf.setText(response.body().getCpf());
                    txtNome.setText(response.body().getNome());
                    txtEmail.setText(response.body().getEmail());
                    txtSenha.setText(response.body().getSenha());
                    txtEndereco.setText(response.body().getEndereco());
                    txtCidade.setText(response.body().getMunicipio());
                    txtEstado.setText(response.body().getEstado());
                    txtTelefone.setText(response.body().getTelefone());

                }else{
                    switch (response.code()){
                        case 400:
                            Toast.makeText(getApplicationContext(), "Dados inconsistentes" , Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(getApplicationContext(), "Cliente não cadastrado" , Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Sistema indisponível, tente novamente" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                // finalizar progressBar
                pgBar.setVisibility(View.GONE);
                // mensagem de problema da chamada da api
                Toast.makeText(getApplicationContext(), "Sistema indisponível " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
