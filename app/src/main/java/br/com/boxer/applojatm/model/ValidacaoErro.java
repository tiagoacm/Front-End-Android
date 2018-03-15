package br.com.boxer.applojatm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiago on 06/03/18.
 */

// Classe responsável por tratar retorno de erro da API
public class ValidacaoErro {

    private List<String> errors = new ArrayList<>();

    private String errorMessage;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
