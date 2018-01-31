/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.ArrowheadProvider.common.exception;

/**
 * Usually thrown by the Core System resources if a crucial database query (for example the query of the Service Registry or the Authorization) comes
 * back empty during the servicing of the request.
 */
public class DataNotFoundException extends ArrowheadException {

  public DataNotFoundException(String msg, int errorCode, String exceptionType, String origin, Throwable cause) {
    super(msg, errorCode, exceptionType, origin, cause);
  }

  public DataNotFoundException(String msg, int errorCode, String exceptionType, String origin) {
    super(msg, errorCode, exceptionType, origin);
  }

  public DataNotFoundException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public DataNotFoundException(String msg) {
    super(msg);
  }
}
