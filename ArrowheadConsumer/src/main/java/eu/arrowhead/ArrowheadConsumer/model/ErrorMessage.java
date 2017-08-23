package eu.arrowhead.ArrowheadConsumer.model;

public class ErrorMessage {

  private String errorMessage;
  private int errorCode;
  //TODO modify this once javadocs are published, it could even go to the specific exception doc
  private String documentation = "No documentation yet.";

  public ErrorMessage() {
  }

  public ErrorMessage(String errorMessage, int errorCode) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

}
