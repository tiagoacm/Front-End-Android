package br.com.boxer.applojatm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

import br.com.boxer.applojatm.model.Cliente;
import br.com.boxer.applojatm.service.ClienteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// classe responsável por tratar alteração dos dados
public class ClienteAlterarActivity extends AppCompatActivity {

    private Button btnConfirmar;
    private TextView txtCpf;
    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtSenha;
    private EditText edtEndereco;
    private EditText edtCidade;
    private EditText edtTelefone;
    private Spinner  spEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_alterar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Alterar");
        setSupportActionBar(toolbar);

        if (toolbar != null){

            toolbar.setNavigationIcon(R.drawable.arrow_left);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavUtils.navigateUpFromSameTask(ClienteAlterarActivity.this);
                }
            });
        }

        // carregar objetos na tela
        txtCpf = (TextView) findViewById(R.id.txtCpf);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        edtEndereco = (EditText) findViewById(R.id.edtEndereco);
        edtCidade = (EditText) findViewById(R.id.edtCidade);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        btnConfirmar = (Button)findViewById(R.id.btnConfirmar);
        spEstado = (Spinner) findViewById(R.id.spinnerEstado);


        // carrega dados da tela anterior
        Intent inovo = getIntent();
        Bundle dados = inovo.getExtras();

        String txtCpfAlterar = dados.getString("cpfCli").toString();
        String txtNomeAlterar = dados.getString("nomeCli").toString();
        String txtEmailAlterar = dados.getString("emailCli").toString();
        String txtSenhaAlterar = dados.getString("senhaCli").toString();
        String txtEnderecoAlterar = dados.getString("enderecoCli").toString();
        String txtCidadeAlterar = dados.getString("cidadeCli").toString();
        String txtEstadoAlterar = dados.getString("estadoCli").toString();
        String txtTelefoneAlterar = dados.getString("telefoneCli").toString();

        // seta as informações nos objetos
        txtCpf.setText(txtCpfAlterar);
        edtNome.setText(txtNomeAlterar);
        edtEmail.setText(txtEmailAlterar);
        edtSenha.setText(txtSenhaAlterar);
        edtEndereco.setText(txtEnderecoAlterar);
        edtCidade.setText(txtCidadeAlterar);
        edtTelefone.setText(txtTelefoneAlterar);

        // criando Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item);
        // setando valor
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapter);
        if (!txtEstadoAlterar.equals(null)){
            int spinnerPosition = adapter.getPosition(txtEstadoAlterar);
            spEstado.setSelection(spinnerPosition);
            spinnerPosition = 0;
        }

        // tratamento do click do botão confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //consiste campos formulário de cadastro
                String email = edtEmail.getText().toString();
                String senha = edtSenha.getText().toString();
                String telefone = edtTelefone.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (edtNome.getText().toString().length() == 0 ){
                    edtNome.setError("Informe o nome.");
                    focusView = edtNome;
                    cancel = true;
                }

                if (edtEmail.getText().toString().length() == 0 ){
                    edtEmail.setError("Informe o e-mail.");
                    focusView = edtEmail;
                    cancel = true;
                }else if (!isEmailValid(email)){
                    edtEmail.setError("E-mail inválido.");
                    focusView = edtEmail;
                    cancel = true;
                }

                if (edtSenha.getText().toString().length() == 0){
                    edtSenha.setError("Informe a senha.");
                    focusView = edtSenha;
                    cancel = true;
                } else if (!isSenhaValid(senha)){
                    edtSenha.setError("Senha deve ter no mínimo 6 e máximo 10 caracteres.");
                    focusView = edtSenha;
                    cancel = true;
                }

                if (edtEndereco.getText().toString().length() == 0 ){
                    edtEndereco.setError("Informe o endereço.");
                    focusView = edtEndereco;
                    cancel = true;
                }

                if (edtCidade.getText().toString().length() == 0 ){
                    edtCidade.setError("Informe a cidade.");
                    focusView = edtCidade;
                    cancel = true;
                }


                if (edtTelefone.getText().toString().length() == 0 ){
                    edtTelefone.setError("Informe o telefone.");
                    focusView = edtTelefone;
                    cancel = true;
                }else if (!isTelefoneValid(telefone)){
                    edtTelefone.setError("Telefone inválido.");
                    focusView = edtTelefone;
                    cancel = true;
                }

                if (cancel){
                    focusView.requestFocus();
                }else{
                    // nenhuma inconsistência chama a api
                    alteraClienteAPI();
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

    // chamar API
    private void alteraClienteAPI() {

        // criar objeto cliente e pegar dados formulário para chamar API
        Cliente clienteForm = new Cliente();
        clienteForm.setCpf(txtCpf.getText().toString());
        clienteForm.setNome(edtNome.getText().toString());
        clienteForm.setEmail(edtEmail.getText().toString());
        clienteForm.setSenha(edtSenha.getText().toString());
        clienteForm.setEndereco(edtEndereco.getText().toString());
        clienteForm.setMunicipio(edtCidade.getText().toString());
        clienteForm.setEstado(spEstado.getSelectedItem().toString());
        clienteForm.setTelefone(edtTelefone.getText().toString());

        // montar chamada da api
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ClienteService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // criar service
        ClienteService service = retrofit.create(ClienteService.class);

        // chamar api de forma assincrona
        Call<Cliente> alterarCliente = service.alterarCliente(clienteForm);

        alterarCliente.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                // retorno sucesso da api
                if (response.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Cadastro atualizado com sucesso!"
                            , Toast.LENGTH_SHORT).show();

                    // retorna para tela com os dados do cliente
                    Intent intent = new Intent(ClienteAlterarActivity.this , ClienteDadosActivity.class);
                    startActivity(intent);
                }else{
                    switch (response.code()){
                        case 400:
                            Toast.makeText(getApplicationContext(), "Dados inconsistentes" , Toast.LENGTH_SHORT).show();
                            break;
                        case 404:
                            Toast.makeText(getApplicationContext(), "Cliente não cadastrado" , Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Sistema indisponível, tente mais tarde" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Sistema indisponível " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
