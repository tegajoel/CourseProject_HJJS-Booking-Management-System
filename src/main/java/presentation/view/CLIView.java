package presentation.view;

import presentation.view.widgets.optionpicker.OptionPickedListener;
import presentation.view.widgets.optionpicker.OptionPickerStyle;
import presentation.view.widgets.text.MessageType;

import java.util.List;

public interface CLIView {

    void showMessage(String message, MessageType messageType);

    void requestUserInput(String prompt, InputConsumer inputConsumer);

    void showOptionsPicker(List<String> options , OptionPickerStyle style, String optionalHeader, OptionPickedListener listener);

}
