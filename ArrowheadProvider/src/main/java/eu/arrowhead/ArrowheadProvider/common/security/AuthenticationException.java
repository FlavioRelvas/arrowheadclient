package eu.arrowhead.ArrowheadProvider.common.security;

public class AuthenticationException extends RuntimeException {

  private static final long serialVersionUID = -6404483587525575152L;

  public AuthenticationException(String message) {
    super(message);
  }
}
