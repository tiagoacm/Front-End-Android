package br.com.boxer.applojatm.model;

/**
 * Created by tiago on 01/03/18.
 */
// classe responsável por representar Login
public class Login {

    private String token;
    private String cpfCliente;
    private String nomeCliente;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }
}
