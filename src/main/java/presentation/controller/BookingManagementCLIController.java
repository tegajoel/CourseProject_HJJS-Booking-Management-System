package presentation.controller;

import data.InMemLearnersRepository;
import data.InMemLessonRepository;
import domain.entity.Review;
import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import domain.repository.CoachRepository;
import domain.repository.LearnerRepository;
import domain.usecase.*;
import domain.util.Result;
import presentation.view.BookingManagementCLIView;
import presentation.view.InputConsumer;
import presentation.view.ReportPrinter;
import presentation.view.widgets.optionpicker.OptionPickerStyle;
import presentation.view.widgets.text.MessageType;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

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
    private final CoachRepository coachRepository;

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
                                          LearnerRepository learnerRepository,
                                          CoachRepository coachRepository) {
        this.view = view;
        this.attendLessonUseCase = attendLessonUseCase;
        this.bookLessonUseCase = bookLessonUseCase;
        this.cancelLessonUseCase = cancelLessonUseCase;
        this.filterLessonsUseCase = filterLessonsUseCase;
        this.generateCoachReportUseCase = generateCoachReportUseCase;
        this.generateLearnerReportUseCase = generateLearnerReportUseCase;
        this.registerNewLearnerUseCase = registerNewLearnerUseCase;
        this.learnerRepository = learnerRepository;
        this.coachRepository = coachRepository;

        initialize();
    }


    private void initialize() {
        view.displayAppHeader("Booking Management System");
        Coach coach1 = new Coach("Peter");
        Coach coach2 = new Coach("John");
        coachRepository.addNewCoach(coach1);
        coachRepository.addNewCoach(coach2);

        Lesson lesson = new Lesson("Diving 1", 3, coach1, LocalDate.now());
        Lesson lesson2 = new Lesson("Diving 2", 3, coach1, LocalDate.now());
        Lesson lesson3 = new Lesson("Diving 3", 4, coach2, LocalDate.now());
        Lesson lesson4 = new Lesson("Diving 4", 2, coach1, LocalDate.now());

        Learner learner = new Learner("Paul", "Male", 6, 3, "0819ja", "0819ja",
                List.of(new RegisteredLesson(lesson, LessonStatus.BOOKED), new RegisteredLesson(lesson2, LessonStatus.ATTENDED))).setId(1234);

        Learner learner1 = new Learner("Simon", "Male", 6, 3, "0819ja", "0819ja",
                List.of(new RegisteredLesson(lesson, LessonStatus.BOOKED), new RegisteredLesson(lesson3, LessonStatus.ATTENDED))).setId(123);

        InMemLearnersRepository.getInstance().addNewLearner(learner1);
        InMemLearnersRepository.getInstance().addNewLearner(learner);

        lesson3.addLearner(learner1);
        lesson.addLearner(learner1);
        lesson.addLearner(learner);
        lesson2.addLearner(learner);

        InMemLessonRepository.getInstance().addLessons(List.of(lesson2, lesson2, lesson3, lesson4, lesson));

        showMainMenuOptions();
    }

    private void showMainMenuOptions() {
        List<String> menuOptions = List.of(
                "Register a new Learner",
                "Book a swimming lesson",
                "Manage/Cancel a Booking",
                "Attend a swimming lesson",
                "Print Learner Report",
                "Print Coach Report",
                "Display all Learners"
        );

        view.showOptionsPicker(menuOptions, OptionPickerStyle.VERTICAL, "Main menu", (index, value) -> {
            switch (index) {
                case 0 -> {
                    view.displayMessage("We'll need some details to get you registered", MessageType.INFO);
                    onRegisterNewUser();
                }
                case 1 -> onBookSwimmingLesson();
                case 2 -> onManageBooking();
                case 3 -> onAttendSwimmingLesson();
                case 4 -> onPrintLearnerReport();
                case 5 -> onPrintCoachReport();
                default -> {
                    view.displayMessage("Invalid Selection", MessageType.ERROR);
                    showMainMenuOptions();
                }
            }
        });
    }

    private void onBookSwimmingLesson() {
        requestLearnerById(learner -> {
            requestLessonFromLessons(lesson -> {
                var result = bookLessonUseCase.bookLesson(lesson, learner);

                if (result.isSuccess()) {
                    printBookingDetails(learner, lesson);
                    showExitOrMainMenuOption();
                } else {
                    String errorMsg = switch (result.getError()){
                        case LESSON_ABOVE_LEARNER_GRADE -> "This grade (" + lesson.getGrade()+ ") lesson is above your current grade of " + learner.getGrade();
                        case LESSON_BELOW_LEARNER_GRADE -> "This grade (" + lesson.getGrade()+ ") lesson is below your current grade of " + learner.getGrade();
                        case DUPLICATE_BOOKING -> "You have already booked this lesson";
                        case LESSON_FULLY_BOOKED -> "This lesson is fully booked";
                    };
                    view.displayMessage("Booking Failed", MessageType.ERROR);
                    view.displayMessage(errorMsg, MessageType.ERROR);

                    showExitOrMainMenuOption();
                }
            });
        });
    }

    private void showExitOrMainMenuOption() {
        var options = List.of("Return to main menu", "Exit App");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0) {
                showMainMenuOptions();
            } else {
                closeApp();
            }
        });
    }

    private void closeApp() {
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

    private void onAttendSwimmingLesson() {
        requestLearnerById(learner -> {

            List<Lesson> bookedLessons = learner.getRegisteredLessons().stream()
                    .filter(lesson -> lesson.getLessonStatus() == LessonStatus.BOOKED)
                    .map(RegisteredLesson::getLesson)
                    .toList();

            if (bookedLessons.isEmpty()){
                view.displayMessage("You don't have any booked lesson to attend", MessageType.ERROR);
                var options = List.of("Book a lesson", "Return to Main Menu", "Exit App");
                view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, "Choose an option", (index, value) -> {
                    switch (index){
                        case 0 -> onBookSwimmingLesson();
                        case 1 -> showMainMenuOptions();
                        case 2 -> closeApp();
                    }
                });
            } else {
                requestPickFromLessons("Please select from one of your booked lesson to attend", bookedLessons, lesson -> {
                    var result = attendLessonUseCase.attendLesson(lesson, learner, this::provideLessonReview);

                    if (result.isSuccess()) {
                        view.displayMessage("Lesson attended!", MessageType.INFO);
                        view.displayMessage(getLearnerDetails(learner, true), MessageType.INFO);
                        showExitOrMainMenuOption();
                    } else {
                        String errorMsg = switch (result.getError()){
                            case LEARNER_NOT_REGISTERED_TO_LESSON -> "You cannot attend this lesson as you are not registered for it";
                            case LESSON_ALREADY_ATTENDED -> "You have already attended this lesson";
                            case INVALID_REVIEW_RATING -> "Review rating invalid. it should be between 1 to 5";
                            case EMPTY_REVIEW_MESSAGE -> "You provided an empty review message";
                        };
                        view.displayMessage(errorMsg, MessageType.ERROR);
                        showExitOrMainMenuOption();
                    }
                });
            }
        });
    }

    private void requestLearnerById(Consumer<Learner> consumer) {
        view.requestUserInput("Please enter a learner ID", data -> {
            if (data.trim().equals("0")) {
                showMainMenuOptions();
                return InputConsumer.success;
            }

            try {
                int id = Integer.parseInt(data);
                var result = learnerRepository.getLearnerById(id);
                if (result.isSuccess()) {
                    consumer.accept(result.getData());
                    return InputConsumer.success;
                } else {
                    view.displayMessage("\nEnter 0 to to return to the Main Menu", MessageType.INFO);
                    return Result.error("No learner exits with this id.");
                }
            } catch (Exception e) {
                return Result.error("Learner IDs only contain numbers");
            }
        });
    }

    private void requestPickFromLessons(String message, List<Lesson> lessons, Consumer<Lesson> lessonConsumer){
        List<String> options = lessons.stream().map(this::getLessonDetails).toList();
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL, message, (index, value) -> {
            lessonConsumer.accept(lessons.get(index));
        });
    }

    private void requestLessonFromLessons(Consumer<Lesson> lessonConsumer) {
        showOptionToPickLesson(lessons -> {
            if (lessons.isEmpty()) {
                view.displayMessage("There are no lesson to choose from", MessageType.INFO);
                showExitOrMainMenuOption();
                return;
            }

            List<String> options = lessons.stream().map(this::getLessonDetails).toList();
            view.showOptionsPicker(options, OptionPickerStyle.VERTICAL, null, (index, value) -> {
                lessonConsumer.accept(lessons.get(index));
            });
        });
    }

    private void showOptionToPickLesson(Consumer<List<Lesson>> caller) {
        var options = List.of("By Swimming Grade", "By Coach", "By Day");
        view.showOptionsPicker(
                options,
                OptionPickerStyle.HORIZONTAL,
                "How would you like to view the available lessons?",
                (index, value) -> {
                    switch (index) {
                        case 0 -> onFilterLessonByGrade(caller);
                        case 1 -> onFilterLessonByCoach(caller);
                        case 2 -> onFilterLessonByDay(caller);
                    }
                });
    }

    private void onFilterLessonByGrade(Consumer<List<Lesson>> caller) {
        view.requestUserInput("Please enter a grade level [1-5]", data -> {
            try {
                int grade = Integer.parseInt(data);
                var result = filterLessonsUseCase.filterByGrade(grade);

                if (result.isSuccess()) {
                    caller.accept(result.getData());
                    return InputConsumer.success;
                } else {
                    return Result.error("Grade is invalid");
                }
            } catch (Exception e) {
                return Result.error("Grade must be a numeric value");
            }
        });
    }

    private void onFilterLessonByDay(Consumer<List<Lesson>> caller) {
        view.requestUserInput("Please a week day e.g Wednesday", data -> {
            var result = filterLessonsUseCase.filterByDay(data);
            if (result.isSuccess()) {
                caller.accept(result.getData());
                return InputConsumer.success;
            } else {
                return Result.error("Day is not valid");
            }
        });
    }

    private void onFilterLessonByCoach(Consumer<List<Lesson>> caller) {
        view.requestUserInput("Please the name of the coach. 0 to show all coaches", data -> {
            if (data.trim().equals("0")) {
                getCoachFromAllCoaches(coach -> {
                    var result = filterLessonsUseCase.filterByCoach(data);
                    if (result.isSuccess()) {
                        caller.accept(result.getData());
                    } else {
                        view.displayMessage("System Error", MessageType.ERROR);
                        showMainMenuOptions();
                    }
                });
                return InputConsumer.success;
            }

            var result = filterLessonsUseCase.filterByCoach(data);
            if (result.isSuccess()) {
                caller.accept(result.getData());
                return InputConsumer.success;
            } else {
                return Result.error("There is no coach with the name: " + data);
            }
        });
    }

    private void getCoachFromAllCoaches(Consumer<Coach> consumer) {
        List<Coach> coaches = coachRepository.getAllCoaches();
        List<String> options = coaches.stream().map(this::getCoachDetails).toList();
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL, null, (index, value) -> consumer.accept(coaches.get(index)));
    }

    private String getCoachDetails(Coach coach) {
        // TODO: 4/18/2024 add good details
        return coach.toString();
    }

    private String getLessonDetails(Lesson lesson) {
        // TODO: 4/18/2024 add good details
        return lesson.toString();
    }

    private String getLearnerDetails(Learner learner, boolean prettyPrint) {
        // TODO: 4/18/2024 add good details
        return learner.toString();
    }

    private void printBookingDetails(Learner learner, Lesson lesson){
        // TODO: 4/19/2024
        view.displayMessage("Lesson Booked!", MessageType.INFO);

        view.displayMessage(getLearnerDetails(learner, true), MessageType.INFO);
    }

    private Review provideLessonReview() {
        final boolean[] hasCompletedReview = {false};
        Review review = new Review("", 0);
        var options = List.of("Very dissatisfied", "Dissatisfied", "Ok", "Satisfied", "Very Satisfied");

        view.displayMessage("Please leave a review for this lesson", MessageType.INFO);

        view.requestUserInput("Review Message", data -> {
            review.updateMessage(data);
            view.showOptionsPicker(
                    options,
                    OptionPickerStyle.HORIZONTAL,
                    "How would you rate this lesson?",
                    (index, value) -> {
                        review.updateRating(index + 1);
                        hasCompletedReview[0] = true;
                    });
            return InputConsumer.success;
        });

        while (!hasCompletedReview[0]){
            // delay until we have received the review
        }

        return review;

    }


    private void onManageBooking() {

    }

    private void onPrintCoachReport() {

    }

    private void onPrintLearnerReport() {
        var options = List.of("Print for all learners", "Print for specific learners");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0) {
                String msg = ReportPrinter.prettyPrintLearnerReports(generateLearnerReportUseCase.getReportForAllLearners());
                view.displayMessage(msg, MessageType.INFO);
                showExitOrMainMenuOption();
            } else {
                requestLearnerById(learner -> {
                    String msg = ReportPrinter.prettyPrintLearnerReport(generateLearnerReportUseCase.getReportForLearner(learner));
                    view.displayMessage(msg, MessageType.INFO);
                    showExitOrMainMenuOption();
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
