package pe.edu.utp.pf_api.model;

public class ErrorResponse {
    private String error;
    private String message;

    // Constructor vacío necesario para JSON binding
    public ErrorResponse() {
    }

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 