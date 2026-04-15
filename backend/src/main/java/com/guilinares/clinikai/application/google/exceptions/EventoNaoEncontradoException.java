package com.guilinares.clinikai.application.google.exceptions;

public class EventoNaoEncontradoException extends RuntimeException {
    public EventoNaoEncontradoException(String eventId) {
        super("Evento não encontrado: " + eventId);
    }
}
