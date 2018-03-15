package br.com.boxer.applojatm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

// classe responsável por autenticar acesso
public class LoginActivity extends AppCompatActivity {

    private ImageView imgView;
    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private TextView txtCadastrar;
    private TextView txtMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // carregando os campos na tela
        imgView = (ImageView) findViewById(R.id.imageView2);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        txtMensagem = (TextView) findViewById(R.id.txtMensagem);
        txtCadastrar = (TextView) findViewById(R.id.txtCadastrar);

        // Setar informações na tela
        txtMensagem.setText("Ainda não é cliente?");
        txtCadastrar.setText("Realizar cadastro.");


        // tratamento do click no botão entrar
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // consistencia do formulário de login
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (edtEmail.getText().toString().length() == 0 ){
                    edtEmail.setError("Informe o e-mail");
                    focusView = edtEmail;
                    cancel = true;
                }else if (!isEmailValid(email)){
                    edtEmail.setError("E-mail inválido");
                    focusView = edtEmail;
                    cancel = true;
                }
                if (edtSenha.getText().toString().length() == 0){
                    edtSenha.setError("Informe a senha");
                    focusView = edtSenha;
                    cancel = true;
                } else if (!isSenhaValid(senha)){
                    edtSenha.setError("Senha deve ter no mínimo 6 e máximo 10 caracteres");
                    focusView = edtSenha;
                    cancel = true;
                }

                if (cancel){
                    focusView.requestFocus();
                }else{
                    // nenhuma inconsistência chama a api
                    autenticarClienteAPI(view);
                }

            }
        });


        // tratamento do click na literal "Realizar cadastro"
        txtCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ClienteFormActivity.class);
                startActivity(intent);
            }
        });

    }

    // validação Email e senha
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isSenhaValid(String senha) {
        if ((senha.length() > 5) && (senha.length() < 11)){
            return true;
        } else {
            return false;
        }
    }

    // chamar api para autenticação
    private void autenticarClienteAPI(final View view){

         // criar objeto cliente pegando as informações do formulário
        Cliente clienteForm = new Cliente();
        clienteForm.setEmail(edtEmail.getText().toString());
        clienteForm.setSenha(edtSenha.getText().toString());

        // montar chamada da API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClienteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ClienteService service = retrofit.create(ClienteService.class);

        // chamar api de forma assincrona
        Call<Login> autenticarCliente;
        autenticarCliente = service.autenticarCliente(clienteForm);

        autenticarCliente.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                // trata sucesso da api
                if (response.isSuccessful()){

                    // pegar informações do response
                    String tokenLogin = response.body().getToken();
                    String cpfLogin = response.body().getCpfCliente();
                    String nomeLogin = response.body().getNomeCliente();

                    // armazenar os dados do login no banco SQLite
                    LoginDAO dao = new LoginDAO(getBaseContext());
                    boolean sucesso = dao.salvar(tokenLogin, cpfLogin, nomeLogin);
                    if (sucesso){
                        // direciona para tela principal do aplicativo
                        Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);
                        startActivity(intent);
                    }else{
                        Snackbar.make(view, "Erro ao efetuar login. Tente novamente.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }else{
                    // trata erro da api
                    switch (response.code()){
                        case 400:
                            Toast.makeText(getApplicationContext(), " E-mail ou senha inválidos" , Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(getApplicationContext(), "Cliente não encontrado" , Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Sistema indisponível, tente mais tarde" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Sistema indisponível " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
