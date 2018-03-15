package br.com.boxer.applojatm.service;

import br.com.boxer.applojatm.model.Cliente;
import br.com.boxer.applojatm.model.Login;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by tiago on 01/03/18.
 */
// responsável por montar as url's para chamada da api java
public interface ClienteService {

    // endereco da api java
    public static final String BASE_URL = "https://apiloja-tiago.herokuapp.com/";

    // consultar cliente por CPF
    @GET("cliente/{cpf}")
    Call<Cliente> consultaClientePorCpf(@Path("cpf") String cpf);

    // incluir cliente
    @POST("cliente")
    Call<Cliente> inserirCliente(@Body Cliente cliente);

    // alterar cliente
    @PUT("cliente")
    Call<Cliente> alterarCliente(@Body Cliente cliente);

    // excluir cliente
    @DELETE("cliente/{cpf}")
    Call<Void> excluirCliente(@Path("cpf") String cpf);

    // autenticar cliente no sistema
    @POST("autenticar")
    Call<Login> autenticarCliente(@Body Cliente cliente);
}
