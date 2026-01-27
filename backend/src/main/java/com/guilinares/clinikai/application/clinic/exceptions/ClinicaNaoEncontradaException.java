package com.guilinares.clinikai.application.clinic.exceptions;

public class ClinicaNaoEncontradaException extends RuntimeException {
    public ClinicaNaoEncontradaException(String phone) {
        super(String.format("Clinica com o telefone %s não encontrada", phone));
    }
}
