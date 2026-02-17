package com.guilinares.clinikai.domain.exceptions;

public class WhatsappSubscriptionRequiredException extends RuntimeException {
  public WhatsappSubscriptionRequiredException() {
    super("To continue sending a message, you must subscribe to this instance again");
  }
}