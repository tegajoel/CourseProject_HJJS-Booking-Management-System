package presentation.view;

import domain.util.Result;
import presentation.view.components.optionpicker.OptionPickedListener;
import presentation.view.components.optionpicker.OptionPickerStyle;
import presentation.view.components.text.InputConsumer;
import presentation.view.components.text.MessageType;

import java.util.List;
import java.util.Scanner;

public class HomeScreenView implements CLIView {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public void showMessage(String message, MessageType messageType) {
        displayTextInternal(message, messageType == MessageType.INFO ? Style.NORMAL : Style.SENT);
    }

    @Override
    public void requestUserInput(String prompt, InputConsumer inputConsumer) {
        Result<Object, String> result;
        do {
            result = inputConsumer.onInput(requestInputInternal(prompt));
            if (!result.isSuccess()) {
                displayTextInternal(result.getError(), Style.SENT);
            }
        } while (!result.isSuccess());
    }

    @Override
    public void showOptionsPicker(List<String> options, OptionPickerStyle style, String optionalHeader, OptionPickedListener listener) {
        if (options.isEmpty()) {
            displayTextInternal("Internal Error", Style.SENT);
            return;
        }

        if (optionalHeader != null) {
            displayTextInternal("\n" + optionalHeader + " :", Style.NORMAL);
        }


        String formattedOptions = formatOptions(options, style);
        displayTextInternal(formattedOptions, true, Style.NORMAL);

        boolean retry = false;

        do {
            try {
                int value = Integer.parseInt(requestInputInternal("Pick an option"));
                if (value == 0 && style == OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION) {
                    System.out.println("Exiting...");
                    System.exit(0);
                    return;
                }

                if (value > options.size()) {
                    retry = true;
                    displayInvalidInputMessage(options.size(), style == OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION);
                } else {
                    retry = false;
                    listener.onOptionPicked(value - 1, options.get(value - 1));
                }
            } catch (NumberFormatException e) {
                displayInvalidInputMessage(options.size(), style == OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION);
                retry = true;
            }
        } while (retry);

    }

    private static String formatOptions(List<String> options, OptionPickerStyle style) {
        StringBuilder sb = new StringBuilder();
        int count = 1;


        if (style == OptionPickerStyle.HORIZONTAL) {
            sb.append("[");
            for (int i = 1; i <= options.size(); i++) {
                sb.append(count);
                sb.append(": ");
                sb.append(options.get(i - 1));
                if (i != options.size()) sb.append(", ");
                count++;
            }
            sb.append("]");
        } else if (style == OptionPickerStyle.VERTICAL || style == OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION) {
            for (int i = 1; i <= options.size(); i++) {
                sb.append(count);
                sb.append(". ");
                sb.append(options.get(i - 1));
                sb.append("\n");
                count++;
            }

            if (style == OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION) {
                sb.append("0. Exit\n");
            }
        }
        return sb.toString();
    }

    /**
     * Display an invalid input message
     *
     * @param optionsSize        size of the options
     * @param displayExitMessage whether the option to show exit option item is enabled
     */
    private void displayInvalidInputMessage(int optionsSize, boolean displayExitMessage) {
        displayTextInternal("You have selected an invalid input:", Style.SENT);
        if (displayExitMessage) {

            displayTextInternal("Please choose an option between 0 and " + optionsSize, Style.SENT);
        } else {
            if (optionsSize == 1) {
                displayTextInternal("There's only one option to choose from. Please select 1", Style.SENT);
            } else {
                displayTextInternal("Please choose an option between 1 and " + optionsSize, Style.SENT);
            }
        }
    }

    /**
     * Internal method to display message
     */
    private String requestInputInternal(String message) {
        System.out.print(message + ": ");
        String result = sc.nextLine().trim();
        if (result.isBlank()) {
            displayTextInternal("You didn't provide any value", Style.SENT);
            return requestInputInternal(message);
        }
        return result;
    }

    /**
     * Internal method to display message
     *
     * @param addNewLine whether a new line char should be appended to the end of the message
     */
    private void displayTextInternal(String message, boolean addNewLine, Style style) {
        StringBuilder sb = new StringBuilder();
        if (style == Style.RECEIVED) {
            sb.append("<== ");
            sb.append(message);
            if (addNewLine) sb.append("\n");
        } else if (style == Style.SENT) {
            sb.append("==> ");
            sb.append(message);
            if (addNewLine) sb.append("\n");
        } else if (style == Style.INPUT_SELECTION) {
            sb.append("************************");
            sb.append("** ").append(message);
            sb.append("************************");
        } else {
            sb.append(message);
            if (addNewLine) sb.append("\n");
        }

        System.out.print(sb);
    }

    /**
     * Internal method to display message
     */
    private void displayTextInternal(String message, Style style) {
        displayTextInternal(message, true, style);
    }

    /**
     * Internal message style
     */
    private enum Style {
        NORMAL, RECEIVED, SENT, INPUT_SELECTION
    }
}
