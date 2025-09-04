package br.com.personal.opencontact.api.common.exceptions;

public class AgendaNameAlreadyExistsException extends RuntimeException {
    public AgendaNameAlreadyExistsException(String message) {
        super(message);
    }
}
