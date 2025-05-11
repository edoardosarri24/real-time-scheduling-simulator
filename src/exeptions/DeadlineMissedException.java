package exeptions;

public class DeadlineMissedException extends RuntimeException {

    public DeadlineMissedException(String message) {
        super(message);
    }
    
}