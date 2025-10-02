/**
 * MIT License - 2021
 */
package claudiosoft.commons;

/**
 * A convenient Java exception to identify errors
 */
public class CTException extends Exception {

    private CTError error;

    public CTException(Exception ex, CTError error) {
        this(ex.getMessage(), ex, error);
    }

    public CTException(String errorMessage, CTError error) {
        this(errorMessage, null, error);
    }

    public CTException(String errorMessage, Throwable ex, CTError error) {
        super(errorMessage, ex);
        this.error = error;
    }

    public CTError getError() {
        return error;
    }

    public String getCode() {
        return error.code();
    }
}
