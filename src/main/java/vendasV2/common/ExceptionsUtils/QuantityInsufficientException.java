package vendasV2.common.ExceptionsUtils;

public class QuantityInsufficientException extends RuntimeException {

    public QuantityInsufficientException(String message) {
        super(message);
    }
}
