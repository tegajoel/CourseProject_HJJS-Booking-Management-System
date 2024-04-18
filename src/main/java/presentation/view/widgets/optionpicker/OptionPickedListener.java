package presentation.view.widgets.optionpicker;

/**
 * Listen to option picked events
 */
public interface OptionPickedListener {
    /**
     * Called when an option was picked
     */
    void onOptionPicked(int index, String value);
}