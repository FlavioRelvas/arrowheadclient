/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.ArrowheadConsumer.exception;

/**
 * Used by the <i>DatabaseManager</i> class when Hibernate <i>ConstraintViolationException</i>s happen. This can happen when trying to save a new
 * object with the same unique constraint fields as an already existing entry, or when trying to delete an entry which has foreign key constraints in
 * other tables.
 */
public class DuplicateEntryException extends ArrowheadException {

  public DuplicateEntryException(String msg, int errorCode, String exceptionType, String origin, Throwable cause) {
    super(msg, errorCode, exceptionType, origin, cause);
  }

  public DuplicateEntryException(String msg, int errorCode, String exceptionType, String origin) {
    super(msg, errorCode, exceptionType, origin);
  }

  public DuplicateEntryException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public DuplicateEntryException(String msg) {
    super(msg);
  }
}
