package presentation.controller;

import domain.mock.CoachRepositoryMock;
import domain.mock.LearnerRepositoryMock;
import domain.mock.LessonRepositoryMock;
import domain.usecase.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import presentation.view.CLIView;
import presentation.view.components.optionpicker.OptionPickedListener;
import presentation.view.components.optionpicker.OptionPickerStyle;
import presentation.view.components.text.InputConsumer;
import presentation.view.components.text.MessageType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeScreenViewControllerTest {

    private HomeScreenViewController homeScreenViewController;
    private HomeScreenViewMock viewMock;
    @BeforeEach
    void setUp() {
        viewMock = new HomeScreenViewMock();
        var mockLessonRepo = new LessonRepositoryMock();
        var mockLearnerRepo = new LearnerRepositoryMock();
        var mockCoachRepo = new CoachRepositoryMock();
        homeScreenViewController = new HomeScreenViewController(
                viewMock,
                new AttendLessonUseCase(),
                new BookLessonUseCase(),
                new CancelLessonUseCase(),
                new FilterLessonsUseCase(mockLessonRepo),
                new GenerateCoachReportUseCase(mockCoachRepo),
                new GenerateLearnerReportUseCase(mockLearnerRepo),
                new RegisterNewLearnerUseCase(mockLearnerRepo),
                mockLearnerRepo,
                mockCoachRepo
        );
    }

    @Test
    public void onInitialize_appNameDisplayed_returnsTrue() {
        assertTrue(viewMock.lastDisplayedMessage.contains("HJSS Booking Manager"));
    }

    @Test
    public void onInitialize_appNameDisplayedWithCorrectMessageType_returnsTrue() {
        assertEquals(MessageType.INFO, viewMock.lastDisplayedMessageType);
    }

    @Test
    public void onInitialize_displayMenuOptionsCalled_expectingNullNull() {
        assertNotNull(viewMock.optionPickerHeader);
    }

    @Test
    public void onInitialize_displayMenuOptionsCalledWithCorrectHeader_returnsTrue() {
        assertTrue(viewMock.optionPickerHeader.equalsIgnoreCase("Main menu"));
    }

    @Test
    public void onInitialize_displayMenuOptionsListContainsAtLeast6Items_returnsTrue() {
        assertTrue(viewMock.optionPickerHeaderOptions.size() >= 6);
    }



    private static class HomeScreenViewMock implements CLIView {
        String optionPickerHeader = null;
        String lastDisplayedMessage = null;
        MessageType lastDisplayedMessageType = null;
        List<String> optionPickerHeaderOptions = null;
        @Override
        public void showMessage(String message, MessageType messageType) {
            lastDisplayedMessage = message;
            lastDisplayedMessageType = messageType;
        }

        @Override
        public void requestUserInput(String prompt, InputConsumer inputConsumer) {

        }

        @Override
        public void showOptionsPicker(List<String> options, OptionPickerStyle style, String optionalHeader, OptionPickedListener listener) {
            optionPickerHeader = optionalHeader;
            optionPickerHeaderOptions = options;
        }
    }
}