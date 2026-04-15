package com.guilinares.clinikai.application.google.exceptions;

public class HorarioIndisponivelException extends RuntimeException {
    public HorarioIndisponivelException() {
        super("O horário solicitado não está disponível.");
    }
}
