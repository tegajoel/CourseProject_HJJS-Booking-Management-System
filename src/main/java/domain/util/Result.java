package domain.util;

public class Result<T, E> {
    private final T data;
    private final E error;
    private final boolean isSuccess;

    private Result(T data, E error, boolean isSuccess) {
        this.data = data;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> success(T data) {
        return new Result<>(data, null, true);
    }

    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getData() {
        if (!isSuccess) {
            throw new IllegalStateException("Result is an error");
        }
        return data;
    }

    public E getError() {
        if (isSuccess) {
            throw new IllegalStateException("Result is a success");
        }
        return error;
    }
}
