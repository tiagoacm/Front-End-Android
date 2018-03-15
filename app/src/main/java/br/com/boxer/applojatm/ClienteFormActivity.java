package br.com.boxer.applojatm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.boxer.applojatm.model.Cliente;
import br.com.boxer.applojatm.model.ClienteDes;
import br.com.boxer.applojatm.model.ValidacaoErro;
import br.com.boxer.applojatm.service.ClienteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// classe responsável por tratar a inclusão de dados
public class ClienteFormActivity extends AppCompatActivity {

    private EditText edtCpf;
    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;
    private EditText edtEndereco;
    private EditText edtCidade;
    private EditText edtTelefone;
    private Spinner  spEstado;
    private Button btnCadastrar;
    private ProgressBar pgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastrar");
        setSupportActionBar(toolbar);

        if (toolbar != null){

            toolbar.setNavigationIcon(R.drawable.arrow_left);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavUtils.navigateUpFromSameTask(ClienteFormActivity.this);
                }
            });
        }

        // carregar objetos na tela
        edtCpf = (EditText) findViewById(R.id.edtCpf);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        edtEndereco = (EditText) findViewById(R.id.edtEndereco);
        edtCidade = (EditText) findViewById(R.id.edtCidade);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        spEstado = (Spinner) findViewById(R.id.spinnerEstado);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        pgBar = (ProgressBar) findViewById(R.id.pgbar);
        pgBar.setVisibility(View.GONE);

        // tratamento do click botão Cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //consiste campos formulário de cadastro
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();
                String telefone = edtTelefone.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (edtCpf.getText().toString().length() == 0 ){
                    edtCpf.setError("Informe o CPF");
                    focusView = edtCpf;
                    cancel = true;
                }

                if (edtNome.getText().toString().length() == 0 ){
                    edtNome.setError("Informe o nome");
                    focusView = edtNome;
                    cancel = true;
                }

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

                if (edtEndereco.getText().toString().length() == 0 ){
                    edtEndereco.setError("Informe o endereço");
                    focusView = edtEndereco;
                    cancel = true;
                }

                if (edtCidade.getText().toString().length() == 0 ){
                    edtCidade.setError("Informe a cidade");
                    focusView = edtCidade;
                    cancel = true;
                }

                if (edtTelefone.getText().toString().length() == 0 ){
                    edtTelefone.setError("Informe o telefone");
                    focusView = edtTelefone;
                    cancel = true;
                }else if (!isTelefoneValid(telefone)){
                    edtTelefone.setError("Telefone inválido");
                    focusView = edtTelefone;
                    cancel = true;
                }

                if (cancel){
                    focusView.requestFocus();
                }else{
                    // nenhuma inconsistência chama a api
                    incluirClienteAPI();
                }

            }
        });

    }

    // validação de campos
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

    private boolean isTelefoneValid(String telefone) {
        return telefone.length() < 12;
    }

    // chamar API de inclusão
    private void incluirClienteAPI(){
        //inicia progressBar
        pgBar.setVisibility(View.VISIBLE);

        // monta chamada da API
        Gson g = new GsonBuilder().registerTypeAdapter(Cliente.class, new ClienteDes()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClienteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        // criar service
        ClienteService service = retrofit.create(ClienteService.class);

        // criar objeto cliente pegando as informações do formulário
        Cliente clienteForm = new Cliente();
        clienteForm.setCpf(edtCpf.getText().toString());
        clienteForm.setNome(edtNome.getText().toString());
        clienteForm.setEmail(edtEmail.getText().toString());
        clienteForm.setSenha(edtSenha.getText().toString());
        clienteForm.setEndereco(edtEndereco.getText().toString());
        clienteForm.setMunicipio(edtCidade.getText().toString());
        clienteForm.setEstado(spEstado.getSelectedItem().toString());
        clienteForm.setTelefone(edtTelefone.getText().toString());


        // chamar api de forma assincrona
        Call<Cliente> incluirCliente;
        incluirCliente = service.inserirCliente(clienteForm);

        incluirCliente.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {

                //finaliza progressBar
                pgBar.setVisibility(View.GONE);

                // tratamento de sucesso e não sucesso da api
                if (response.isSuccessful()){

                    // exibe mensagem de sucesso
                    Toast.makeText(getApplicationContext(), "Cadastro efetuado com sucesso!" , Toast.LENGTH_SHORT).show();
                    limparDadosFormCliente();

                    // retorna para tela de login
                    Intent intent = new Intent(ClienteFormActivity.this , LoginActivity.class);
                    startActivity(intent);

                }else{
                    switch (response.code()){
                        case 400:

                            // pega informações de erro do response
                            String validacaoErro = response.errorBody().toString();
                            Gson gson = new Gson();
                            ValidacaoErro mensagem = gson.fromJson(response.errorBody().charStream(), ValidacaoErro.class);

                            Toast.makeText(getApplicationContext(), " " + mensagem.getErrors().toString() , Toast.LENGTH_LONG).show();
                                                        View focusView = null;
                            break;

                        case 404:
                            Toast.makeText(getApplicationContext(), "Cliente não cadastrado" , Toast.LENGTH_SHORT).show();
                            break;

                        case 409:
                            Toast.makeText(getApplicationContext(), "CPF já cadastrado. Por favor, efetuar login" , Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "Sistema indisponível, tente mais tarde" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                //finaliza progressBar
                pgBar.setVisibility(View.GONE);

                // mensagem de problema ao chamar a API
                Toast.makeText(getApplicationContext(), "Sistema indisponível " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // limpa os campos do formulário para um novo cadastro
    private void limparDadosFormCliente() {
        edtCpf.setText("");
        edtNome.setText("");
        edtEmail.setText("");
        edtSenha.setText("");
        edtEndereco.setText("");
        edtCidade.setText("");
        edtTelefone.setText("");
    }


}
