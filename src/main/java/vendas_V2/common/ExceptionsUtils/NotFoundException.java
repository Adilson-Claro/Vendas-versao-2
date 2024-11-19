package vendas_V2.common.ExceptionsUtils;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}