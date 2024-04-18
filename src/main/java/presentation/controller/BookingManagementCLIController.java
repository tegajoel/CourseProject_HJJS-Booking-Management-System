package presentation.controller;

import domain.entity.learner.Learner;
import domain.repository.LearnerRepository;
import domain.usecase.*;
import domain.util.Result;
import presentation.view.BookingManagementCLIView;
import presentation.view.InputConsumer;
import presentation.view.ReportPrinter;
import presentation.view.widgets.optionpicker.OptionPickerStyle;
import presentation.view.widgets.text.MessageType;

import java.util.List;

public class BookingManagementCLIController {
    private BookingManagementCLIView view;
    private final AttendLessonUseCase attendLessonUseCase;
    private final BookLessonUseCase bookLessonUseCase;
    private final CancelLessonUseCase cancelLessonUseCase;
    private final FilterLessonsUseCase filterLessonsUseCase;
    private final GenerateCoachReportUseCase generateCoachReportUseCase;
    private final GenerateLearnerReportUseCase generateLearnerReportUseCase;
    private final RegisterNewLearnerUseCase registerNewLearnerUseCase;

    private final LearnerRepository learnerRepository;

    String nameBeingRegistered = null;
    String genderBeingRegistered = null;
    Integer ageBeingRegistered = null;
    Integer gradeBeingRegistered = null;
    String phoneNumberBeingRegistered = null;
    String emergencyContactBeingRegistered = null;

    public BookingManagementCLIController(BookingManagementCLIView view,
                                          AttendLessonUseCase attendLessonUseCase,
                                          BookLessonUseCase bookLessonUseCase,
                                          CancelLessonUseCase cancelLessonUseCase,
                                          FilterLessonsUseCase filterLessonsUseCase,
                                          GenerateCoachReportUseCase generateCoachReportUseCase,
                                          GenerateLearnerReportUseCase generateLearnerReportUseCase,
                                          RegisterNewLearnerUseCase registerNewLearnerUseCase,
                                          LearnerRepository learnerRepository) {
        this.view = view;
        this.attendLessonUseCase = attendLessonUseCase;
        this.bookLessonUseCase = bookLessonUseCase;
        this.cancelLessonUseCase = cancelLessonUseCase;
        this.filterLessonsUseCase = filterLessonsUseCase;
        this.generateCoachReportUseCase = generateCoachReportUseCase;
        this.generateLearnerReportUseCase = generateLearnerReportUseCase;
        this.registerNewLearnerUseCase = registerNewLearnerUseCase;
        this.learnerRepository = learnerRepository;

        initialize();
    }


    private void initialize() {
        view.displayAppHeader("Booking Management System");

        showMainMenuOptions();
    }

    private void showMainMenuOptions() {
        List<String> menuOptions = List.of(
                "Register new Learner",
                "Manage/Cancel Booking",
                "Attend a swimming lesson",
                "Print Learner Report",
                "Print Coach Report",
                "Display all Learners"
        );

        view.showOptionsPicker(menuOptions, OptionPickerStyle.VERTICAL, "Main menu", (index, value) -> {
            switch (index) {
                case 0 -> {
                    view.displayMessage("We'll need some details to get you registered\n", MessageType.INFO);
                    onRegisterNewUser();
                }
                case 1 -> onManageBooking();
                case 2 -> onAttendSwimmingLesson();
                case 3 -> onPrintLearnerReport();
                case 4 -> onPrintCoachReport();
                default -> {
                    view.displayMessage("Invalid Selection", MessageType.ERROR);
                    showMainMenuOptions();
                }
            }
        });
    }

    private void showExitOrMainMenuOption() {
        var options = List.of("Return to main menu", "Exit App");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0){
                showMainMenuOptions();
            } else {
                closeApp();
            }
        });
    }

    private void closeApp(){
        view.displayMessage("Exiting...", MessageType.INFO);
        System.exit(0);
    }

    private void onRegisterNewUser() {
        if (nameBeingRegistered == null) {
            view.requestUserInput("Learner's name", data -> {
                if (data.isBlank()) {
                    return Result.error("Please enter a name");
                }
                nameBeingRegistered = data;
                return InputConsumer.success;
            });
        }

        if (genderBeingRegistered == null) {
            view.showOptionsPicker(List.of("Male", "Female"), OptionPickerStyle.HORIZONTAL, "Learner's gender", (index, value) -> {
                genderBeingRegistered = value;
            });
        }

        if (ageBeingRegistered == null) {
            view.requestUserInput("Learner's age", data -> {
                try {
                    ageBeingRegistered = Integer.parseInt(data);
                    if (!registerNewLearnerUseCase.getValidator().validateAge(ageBeingRegistered)) {
                        return Result.error("Only ages 4 to 11 can be registered");
                    }
                    return InputConsumer.success;
                } catch (Exception e) {
                    return Result.error("Age must be a numeric value");
                }
            });
        }

        if (gradeBeingRegistered == null) {
            view.requestUserInput("Do you have any swimming experience? [y/n]", data -> {
                if (data.equalsIgnoreCase("n")) {
                    gradeBeingRegistered = 0;
                    return InputConsumer.success;
                } else if (data.equalsIgnoreCase("y")) {
                    view.requestUserInput("Please enter your swimming level [0-5]", data2 -> {
                        try {
                            gradeBeingRegistered = Integer.parseInt(data2);
                            if (!registerNewLearnerUseCase.getValidator().validateGrade(gradeBeingRegistered)) {
                                return Result.error("Swimming level should be between 0 and 5");
                            }
                            return InputConsumer.success;
                        } catch (Exception e) {
                            return Result.error("Swimming level must be a numeric value");
                        }
                    });
                    return InputConsumer.success;
                } else {
                    return Result.error("Please enter 'y' or 'n'");
                }
            });
        }

        if (phoneNumberBeingRegistered == null) {
            view.requestUserInput("Learner's phone number", data -> {
                phoneNumberBeingRegistered = data;
                if (!registerNewLearnerUseCase.getValidator().validatePhoneNumber(phoneNumberBeingRegistered)) {
                    return Result.error("Please enter a valid phone number that is 11 digits. e.g 07874813069");
                }
                return InputConsumer.success;
            });
        }

        if (emergencyContactBeingRegistered == null) {
            view.requestUserInput("Learner's emergency Contact number", data -> {
                emergencyContactBeingRegistered = data;
                if (!registerNewLearnerUseCase.getValidator().validatePhoneNumber(emergencyContactBeingRegistered)) {
                    return Result.error("Please enter a valid phone number that is 11 digits. e.g 07874813069");
                }
                return InputConsumer.success;
            });
        }

        Learner learner = new Learner(nameBeingRegistered, genderBeingRegistered, ageBeingRegistered, gradeBeingRegistered, phoneNumberBeingRegistered, emergencyContactBeingRegistered);

        var result = registerNewLearnerUseCase.registerLearner(learner);
        resetRegistrationDetails();

        if (result.isSuccess()) {
            view.displayMessage(prettyPrintSuccessfulLearnerRegistrationDetails(learner), MessageType.INFO);
        } else {
            String errorMessage = switch (result.getError()) {
                case INVALID_AGE -> "Please enter a valid age between 4 and 11";
                case INVALID_GRADE -> "Please enter a valid grade between 0 and 5";
                case INVALID_GENDER -> "Please enter a valid gender";
                case INVALID_PHONE_NUMBER, REPOSITORY_ERROR ->
                        "Please enter a valid phone number that is 11 digits. e.g 07874813069";
            };
            view.displayMessage(errorMessage, MessageType.ERROR);
        }

        showMainMenuOptions();
    }

    private void onAttendSwimmingLesson(){

    }

    private void onManageBooking() {

    }

    private void onPrintCoachReport() {

    }

    private void onPrintLearnerReport() {
        var options = List.of("Print for all learners", "Print for specific learners");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0){
                String msg = ReportPrinter.prettyPrintLearnerReports(generateLearnerReportUseCase.getReportForAllLearners());
                view.displayMessage(msg, MessageType.INFO);
                showExitOrMainMenuOption();
            } else {
                view.requestUserInput("Please provide the id of the learner", data -> {
                    if (data.trim().equals("0")){
                        showMainMenuOptions();
                        return InputConsumer.success;
                    }

                    try {
                        int id = Integer.parseInt(data);
                        var result = learnerRepository.getLearnerById(id);
                        if (result.isSuccess()){
                            String msg = ReportPrinter.prettyPrintLearnerReport(generateLearnerReportUseCase.getReportForLearner(result.getData()));
                            view.displayMessage(msg, MessageType.INFO);
                            showExitOrMainMenuOption();
                            return InputConsumer.success;
                        } else {
                            view.displayMessage("\nEnter 0 to to return to the Main Menu", MessageType.INFO);
                            return Result.error("No learner exits with this id.");
                        }
                    } catch (Exception e){
                        return Result.error("Learner IDs only contain numbers");
                    }
                });
            }
        });
    }

    private void resetRegistrationDetails() {
        nameBeingRegistered = null;
        genderBeingRegistered = null;
        ageBeingRegistered = null;
        gradeBeingRegistered = null;
        phoneNumberBeingRegistered = null;
        emergencyContactBeingRegistered = null;
    }

    private String prettyPrintSuccessfulLearnerRegistrationDetails(Learner learner) {
        return "You have been successfully Registered. Your learner ID is: " + learner.getId();
        // TODO: 4/18/2024 Implement
    }

}
