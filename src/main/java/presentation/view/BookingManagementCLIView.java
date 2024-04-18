package presentation.view;

import domain.util.Result;
import presentation.view.widgets.optionpicker.OptionPickedListener;
import presentation.view.widgets.optionpicker.OptionPickerStyle;

import java.util.List;

public interface BookingManagementCLIView {

    void displayAppHeader(String message);

    void displayMessage(String message, MessageType messageType);

    void requestUserInput(String optionalPrompt, InputConsumer inputConsumer);

    void showOptionsPicker(List<String> options , OptionPickerStyle style, OptionPickedListener listener);


    enum MessageType{
        INFO, ERROR
    }



}
