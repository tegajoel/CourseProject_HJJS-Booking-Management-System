package presentation.view;

import presentation.view.components.optionpicker.OptionPickedListener;
import presentation.view.components.optionpicker.OptionPickerStyle;
import presentation.view.components.text.InputConsumer;
import presentation.view.components.text.MessageType;

import java.util.List;

/**
 * Base interface for any Command Line Interface(CLI) View
 */
public interface CLIView {

    /**
     * Show a message to the command line
     *
     * @param message     message to be shown
     * @param messageType The type of the message
     */
    void showMessage(String message, MessageType messageType);

    /**
     * Request an input from the command line
     *
     * @param prompt        this will be shown in the command line when the input is requested
     * @param inputConsumer an input consumer to consume the provided input.
     *                      This is especially useful and the input consumer can
     *                      return false/error with an error message if the input was invalid and
     *                      re-request the input from the user again until a true/success is returned from the consumer
     */
    void requestUserInput(String prompt, InputConsumer inputConsumer);

    /**
     * Display a list of options to the user to select one from
     *
     * @param options        list of options to be displayed
     * @param style          the style of the displayed list. Can be {@link OptionPickerStyle#VERTICAL}, {@link OptionPickerStyle#VERTICAL_WITH_EXIT_APP_OPTION} or {@link OptionPickerStyle#HORIZONTAL}
     * @param optionalHeader an optional header/prompt to be displayed when the list is shown. Leave null to not display anything
     * @param listener       an {@link OptionPickedListener} to listen for when an option has been selected
     */
    void showOptionsPicker(List<String> options, OptionPickerStyle style, String optionalHeader, OptionPickedListener listener);

}
