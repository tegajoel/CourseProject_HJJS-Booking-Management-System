package presentation.view;

import domain.util.Result;

/**
 * Consumes input from the CLI
 */
public interface InputConsumer {
    /**
     * Called when the user has provided an input after one was requested
     *
     * @param data the input provided by the user
     * @return a {@link Result#success(Object)} / {@link #success} if the provided user input is valid,
     * otherwise a {@link Result#error(Object)} with a string value for the reason for failure.
     * This will be error will be displayed to the user and the input will be requested again
     */
     Result<Object, String> onInput(String data);

    Result<Object, String> success = Result.success(Result.NO_VALUE);
}
