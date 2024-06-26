package presentation.controller;

import domain.entity.Rating;
import domain.entity.Review;
import domain.entity.coach.Coach;
import domain.entity.learner.Learner;
import domain.entity.lesson.Lesson;
import domain.entity.lesson.LessonStatus;
import domain.entity.lesson.RegisteredLesson;
import domain.repository.CoachRepository;
import domain.repository.LearnerRepository;
import domain.usecase.*;
import domain.util.LessonUtil;
import domain.util.Result;
import presentation.view.CLIView;
import presentation.view.components.text.InputConsumer;
import presentation.view.ReportPrinter;
import presentation.view.components.optionpicker.OptionPickerStyle;
import presentation.view.components.text.MessageType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class HomeScreenViewController {
    private CLIView view;
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

    public HomeScreenViewController(CLIView view,
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


    /**
     * Do initializations
     */
    private void initialize() {
        String appHeader = """

                +-----------------------------------+
                |    -  HJSS Booking Manager  -     |
                +-----------------------------------+
                """;

        view.showMessage(appHeader, MessageType.INFO);

        showMainMenuOptions();
    }

    /**
     * Show the main menu options
     */
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

        view.showOptionsPicker(menuOptions, OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION, "Main menu", (index, value) -> {
            view.showMessage("\n----- * " + value + " * -----\n", MessageType.INFO);

            switch (index) {
                case 0 -> onRegisterNewUser();
                case 1 -> onBookSwimmingLesson();
                case 2 -> onManageOrCancelBooking();
                case 3 -> onAttendSwimmingLesson();
                case 4 -> onPrintLearnerReport();
                case 5 -> onPrintCoachReport();
                case 6 -> onDisplayAllLearners();
                default -> {
                    view.showMessage("Invalid Selection", MessageType.ERROR);
                    showMainMenuOptions();
                }
            }
        });
    }

    /**
     * call when user selects the option to book a swimming lesson
     */
    private void onBookSwimmingLesson() {
        requestLearnerById(learner -> requestLessonFromAllLessons(lesson -> doBookLesson(lesson, learner)));
    }

    /**
     * call when user selects the option to register a new user
     */
    private void onRegisterNewUser() {
        view.showMessage("We'll need you to provide some details for registration", MessageType.INFO);

        resetRegistrationDetails();
        view.requestUserInput("Learner's name", data -> {
            if (data.isBlank()) {
                return Result.error("Please enter a name");
            }
            nameBeingRegistered = data;
            return InputConsumer.success;
        });
        view.showOptionsPicker(List.of("Male", "Female"),
                OptionPickerStyle.HORIZONTAL,
                "Learner's gender",
                (index, value) -> genderBeingRegistered = value);

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

        view.requestUserInput("Do you have any swimming experience? [y/n]", data -> {
            if (data.trim().equalsIgnoreCase("n") || data.trim().equalsIgnoreCase("no")) {
                gradeBeingRegistered = 0;
                return InputConsumer.success;
            } else if (data.trim().equalsIgnoreCase("y") || data.trim().equalsIgnoreCase("yes")) {
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

        view.requestUserInput("Learner's phone number", data -> {
            phoneNumberBeingRegistered = data;
            if (!registerNewLearnerUseCase.getValidator().validatePhoneNumber(phoneNumberBeingRegistered)) {
                return Result.error("Please enter a valid phone number that is 11 digits. e.g 07874813069");
            }
            return InputConsumer.success;
        });

        view.requestUserInput("Learner's emergency Contact number", data -> {
            emergencyContactBeingRegistered = data;
            if (!registerNewLearnerUseCase.getValidator().validatePhoneNumber(emergencyContactBeingRegistered)) {
                return Result.error("Please enter a valid phone number that is 11 digits. e.g 07874813069");
            }
            return InputConsumer.success;
        });

        Learner learner = new Learner(nameBeingRegistered, genderBeingRegistered, ageBeingRegistered, gradeBeingRegistered, phoneNumberBeingRegistered, emergencyContactBeingRegistered);

        var result = registerNewLearnerUseCase.registerLearner(learner);

        if (result.isSuccess()) {
            printSuccessfulLearnerRegistrationDetails(learner);
        } else {
            String errorMessage = switch (result.getError()) {
                case INVALID_AGE -> "Please enter a valid age between 4 and 11";
                case INVALID_GRADE -> "Please enter a valid grade between 0 and 5";
                case INVALID_GENDER -> "Please enter a valid gender";
                case INVALID_PHONE_NUMBER, REPOSITORY_ERROR ->
                        "Please enter a valid phone number that is 11 digits. e.g 07874813069";
            };
            view.showMessage(errorMessage, MessageType.ERROR);
        }

        showMainMenuOptions();
    }

    /**
     * call when user selects the option to attend a lesson
     */
    private void onAttendSwimmingLesson() {
        requestLearnerById(learner -> {

            List<Lesson> bookedLessons = learner.getRegisteredLessons().stream()
                    .filter(lesson -> lesson.getLessonStatus() == LessonStatus.BOOKED)
                    .map(RegisteredLesson::getLesson)
                    .toList();

            if (bookedLessons.isEmpty()) {
                view.showMessage("You don't have any booked lesson to attend", MessageType.ERROR);
                showBookLessonOrExitToMainMenuOption();
            } else {
                requestPickFromLessons(
                        "Please select from one of your booked lesson to attend",
                        bookedLessons, false,
                        lesson -> doAttendLesson(lesson, learner));
            }
        });
    }

    /**
     * call when user selects the option to manage/cancel a booking
     */
    private void onManageOrCancelBooking() {
        requestLearnerById(learner -> {

            var filteredLessons = learner.getRegisteredLessons()
                    .stream()
                    .filter(lesson -> (lesson.getLessonStatus() == LessonStatus.CANCELLED || lesson.getLessonStatus() == LessonStatus.BOOKED))
                    .toList();

            if (filteredLessons.isEmpty()) {
                var msg = learner.getRegisteredLessons().isEmpty() ? "You have not yet booked any lesson to cancel or amend"
                        : "You have attended all you booked lessons, and you can only amend lessons that you have not attended.";
                view.showMessage(msg, MessageType.ERROR);
                showBookLessonOrExitToMainMenuOption();
            } else {
                var options = filteredLessons
                        .stream()
                        .map(RegisteredLesson::getLesson)
                        .map(lesson -> getLessonDetails(lesson, true) + " (" + learner.getLessonStatus(lesson) + ")")
                        .toList();
                view.showOptionsPicker(
                        options,
                        OptionPickerStyle.VERTICAL,
                        "Please select from one of your booked or cancelled lessons to modify",
                        (index, value) -> {
                            var registeredLesson = filteredLessons.get(index);

                            if (registeredLesson.getLessonStatus() == LessonStatus.CANCELLED) {
                                var options1 = List.of("Rebook");
                                view.showOptionsPicker(
                                        options1,
                                        OptionPickerStyle.HORIZONTAL,
                                        "Select an option",
                                        (index1, value1) -> doBookLesson(registeredLesson.getLesson(), learner));
                            } else {
                                var option1 = List.of("Cancel Booking", "Attend Lesson");
                                view.showOptionsPicker(option1, OptionPickerStyle.HORIZONTAL, "Select an option", (index1, value1) -> {
                                    if (index1 == 1) {
                                        doAttendLesson(registeredLesson.getLesson(), learner);
                                    } else if (index1 == 0) {
                                        var result = cancelLessonUseCase.cancelLesson(registeredLesson.getLesson(), learner);
                                        if (result.isSuccess()) {
                                            view.showMessage("Booking cancelled successfully!", MessageType.INFO);
                                        } else {
                                            String msg = switch (result.getError()) {
                                                case LESSON_ALREADY_ATTENDED -> "You have already attended this lesson";
                                                case LESSON_ALREADY_CANCELLED ->
                                                        "This lesson has already been cancelled";
                                                case NO_BOOKING_FOR_LESSON -> "You are not registered for this lesson";
                                            };
                                            view.showMessage(msg, MessageType.ERROR);
                                        }
                                        showExitOrMainMenuOption();
                                    }
                                });
                            }
                        });
            }
        });
    }

    /**
     * call when user selects the option to print coach report
     */
    private void onPrintCoachReport() {
        var options = List.of("Print for all coaches", "Print for a specific coach");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0) {
                String msg = ReportPrinter.prettyPrintCoachReports(generateCoachReportUseCase.getReportForAllCoaches());
                view.showMessage(msg, MessageType.INFO);
                showExitOrMainMenuOption();
            } else {
                getCoachFromAllCoaches("Please select a coach", coach -> {
                    String msg = ReportPrinter.prettyPrintCoachReport(generateCoachReportUseCase.getReportForCoach(coach));
                    view.showMessage(msg, MessageType.INFO);
                    showExitOrMainMenuOption();
                });
            }
        });
    }

    /**
     * call when user selects the option to print learner report
     */
    private void onPrintLearnerReport() {
        var options = List.of("Print for all learners", "Print for a specific learner");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, null, (index, value) -> {
            if (index == 0) {
                String msg = ReportPrinter.prettyPrintLearnerReports(generateLearnerReportUseCase.getReportForAllLearners());
                view.showMessage(msg, MessageType.INFO);
                showExitOrMainMenuOption();
            } else {
                requestLearnerById(learner -> {
                    String msg = ReportPrinter.prettyPrintLearnerReport(generateLearnerReportUseCase.getReportForLearner(learner));
                    view.showMessage(msg, MessageType.INFO);
                    showExitOrMainMenuOption();
                });
            }
        });
    }

    /**
     * call when user selects the option to display all learners
     */
    private void onDisplayAllLearners() {
        var learners = learnerRepository.getAllLearners();
        if (learners.isEmpty()) {
            String msg = """

                    +-------------------------------------------------+
                    |              No Registered Learner              |
                    +-------------------------------------------------+
                    """;

            view.showMessage(msg, MessageType.INFO);
        } else {
            learners.forEach(this::printLearnerDetails);
        }

        showExitOrMainMenuOption();
    }

    /**
     * Do attending of a lesson with use case
     *
     * @param lesson  lesson
     * @param learner learner
     */
    private void doAttendLesson(Lesson lesson, Learner learner) {
        var result = attendLessonUseCase.attendLesson(lesson, learner, this::provideLessonReview);

        if (result.isSuccess()) {
            printSuccessfulLessonAttendedMsg(lesson, learner);
        } else {
            String errorMsg = switch (result.getError()) {
                case LEARNER_NOT_REGISTERED_TO_LESSON ->
                        "You cannot attend this lesson as you are not registered for it";
                case LESSON_ALREADY_ATTENDED -> "You have already attended this lesson";
                case INVALID_REVIEW_RATING -> "Review rating invalid. it should be between 1 to 5";
                case EMPTY_REVIEW_MESSAGE -> "You provided an empty review message";
            };
            view.showMessage(errorMsg, MessageType.ERROR);
        }
        showExitOrMainMenuOption();
    }

    /**
     * Do the booking of a lesson with use case
     *
     * @param lesson  lesson
     * @param learner learner
     */
    private void doBookLesson(Lesson lesson, Learner learner) {
        var result = bookLessonUseCase.bookLesson(lesson, learner);

        if (result.isSuccess()) {
            printBookingDetails(learner, lesson);
            showExitOrMainMenuOption();
        } else {
            String errorMsg = switch (result.getError()) {
                case LESSON_ABOVE_LEARNER_GRADE ->
                        "This grade (" + lesson.getGrade() + ") lesson is above your current grade of " + learner.getGrade();
                case LESSON_BELOW_LEARNER_GRADE ->
                        "This grade (" + lesson.getGrade() + ") lesson is below your current grade of " + learner.getGrade();
                case DUPLICATE_BOOKING -> "You have already booked this lesson";
                case LESSON_FULLY_BOOKED -> "This lesson is fully booked";
            };
            view.showMessage("Booking Failed", MessageType.ERROR);
            view.showMessage(errorMsg, MessageType.ERROR);

            showExitOrMainMenuOption();
        }
    }

    /**
     * Close the app
     */
    private void closeApp() {
        view.showMessage("Exiting...", MessageType.INFO);
        System.exit(0);
    }

    /**
     * Show the option to exit or go to the main menu
     */
    private void showExitOrMainMenuOption() {
        var options = List.of("Return to Main menu");
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION, null, (index, value) -> {
            if (index == 0) {
                showMainMenuOptions();
            }
        });
    }

    /**
     * Show the option to book a lesson, return to main menu, or exit the app
     */
    private void showBookLessonOrExitToMainMenuOption() {
        var options = List.of("Book a lesson", "Return to Main menu", "Exit App");
        view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, "Choose an option", (index, value) -> {
            switch (index) {
                case 0 -> onBookSwimmingLesson();
                case 1 -> showMainMenuOptions();
                case 2 -> closeApp();
            }
        });
    }

    /**
     * Request the learner to enter their id and return the learner.
     * The consumer will only br called if the id is valid and the learner is found in the repo
     *
     * @param consumer consumer to consume the found learner
     */
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
                    view.showMessage("\nEnter 0 to to return to the Main menu", MessageType.INFO);
                    return Result.error("No learner exits with this id.");
                }
            } catch (Exception e) {
                return Result.error("Learner IDs only contain numbers");
            }
        });
    }

    /**
     * Request the user to pick a lesson from the list of all lessons in the lesson list
     *
     * @param message            prompt message
     * @param lessons            list of lesson that the user will choose from
     * @param showLessonCapacity whether to display the lesson capacity
     * @param lessonConsumer     consumer to consume the selected lesson
     */
    private void requestPickFromLessons(
            String message,
            List<Lesson> lessons,
            boolean showLessonCapacity,
            Consumer<Lesson> lessonConsumer
    ) {
        List<String> options = lessons.stream().map(lesson -> getLessonDetails(lesson, showLessonCapacity)).toList();
        view.showOptionsPicker(
                options,
                OptionPickerStyle.VERTICAL, message,
                (index, value) -> lessonConsumer.accept(lessons.get(index))
        );
    }

    /**
     * Request the user to pick a lesson from the list of all lessons in the repo
     *
     * @param lessonConsumer consumer for the lesson
     */
    private void requestLessonFromAllLessons(Consumer<Lesson> lessonConsumer) {
        showOptionToPickLessonFromAllLessons(lessons -> {
            if (lessons.isEmpty()) {
                view.showMessage("There are no lessons to choose from", MessageType.INFO);
                showExitOrMainMenuOption();
                return;
            }

            requestPickFromLessons(null, lessons, true, lessonConsumer);
        });
    }

    /**
     * Show the option the user to pick a lesson from the list of all lessons in the repo
     * <p>
     * This will first prompt the user to select a filter option
     *
     * @param caller consumer for the lessons
     */
    private void showOptionToPickLessonFromAllLessons(Consumer<List<Lesson>> caller) {
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

    /**
     * Get a list lesson filtered by grade, from the list of all lessons in the repo
     *
     * @param caller consumer for the lessons
     */
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

    /**
     * Get a list lesson filtered by day, from the list of all lessons in the repo
     *
     * @param caller consumer for the lessons
     */
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

    /**
     * Get a list lesson filtered by coach, from the list of all lessons in the repo
     *
     * @param caller consumer for the lessons
     */
    private void onFilterLessonByCoach(Consumer<List<Lesson>> caller) {
        view.requestUserInput("Please the name of the coach. 0 to show all coaches", data -> {
            if (data.trim().equals("0")) {
                getCoachFromAllCoaches(null, coach -> {
                    var result = filterLessonsUseCase.filterByCoach(coach.getName());
                    if (result.isSuccess()) {
                        caller.accept(result.getData());
                    } else {
                        view.showMessage("System Error", MessageType.ERROR);
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

    /**
     * Request the user to pick a coach from the list of all coaches in the coach repo
     *
     * @param headerMessage message to be displayed to the user when prompting
     * @param consumer      consume the selected coach
     */
    private void getCoachFromAllCoaches(String headerMessage, Consumer<Coach> consumer) {
        List<Coach> coaches = coachRepository.getAllCoaches();
        List<String> options = coaches.stream().map(this::getCoachDetails).toList();
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL, headerMessage, (index, value) -> consumer.accept(coaches.get(index)));
    }

    /**
     * Get the details of a coach to be displayed
     *
     * @param coach coach
     * @return coach details
     */
    private String getCoachDetails(Coach coach) {
        Rating averageRating = LessonUtil.getAverageRating(coach.getAssignedLessons().
                stream().map(lesson -> LessonUtil.getAverageReviewRating(lesson.getReviews()))
                .toList());

        return coach.getName() + " (Average Rating: " + (averageRating.hasRating() ? averageRating.getRatingValue() : "No rating yet") + ")";
    }

    /**
     * Get the details of a lesson to be displayed
     *
     * @param lesson             lesson
     * @param showLessonCapacity whether to include the lesson capacity
     * @return the lesson details
     */
    private String getLessonDetails(Lesson lesson, boolean showLessonCapacity) {
        Rating averageRating = LessonUtil.getAverageReviewRating(lesson.getReviews());
        StringBuilder sb = new StringBuilder();
        sb.append(lesson.getName());
        sb.append(" (Grade: ").append(lesson.getGrade());
        sb.append(", Date: ").append(formatLessonDate(lesson));
        sb.append(", Time: ").append(lesson.getLessonTime());
        sb.append(", Coach: ").append(lesson.getCoach().getName());
        sb.append(", Avg. Rating: ").append(averageRating.hasRating() ? averageRating.getRatingValue() : "Not rated").append(")");
        if (showLessonCapacity) {
            int activeLessons = lesson.getRegisteredLearners().stream().filter(learner -> learner.getLessonStatus(lesson) != LessonStatus.CANCELLED).toList().size();
            boolean fullyBooked = activeLessons >= 4;
            sb.append(" - ").append(fullyBooked ? "Fully booked" : (4 - activeLessons) + " slots available");
        }
        return sb.toString();
    }

    /**
     * Request the user to provide a review
     *
     * @return review object
     */
    private Review provideLessonReview() {
        final boolean[] hasCompletedReview = {false};
        Review review = new Review("", 0);
        var options = List.of("Very dissatisfied", "Dissatisfied", "Ok", "Satisfied", "Very Satisfied");

        view.showMessage("Please leave a review for this lesson", MessageType.INFO);

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

        while (!hasCompletedReview[0]) {
            // delay until we have received the review
        }

        return review;

    }

    /**
     * format lesson date to be displayed
     *
     * @param lesson lesson
     * @return formatted date
     */
    private String formatLessonDate(Lesson lesson) {
        return lesson.getLessonDate().format(DateTimeFormatter.ofPattern("EEEE - MMMM d", Locale.ENGLISH));
    }

    /**
     * display a message on successfully booked lesson
     *
     * @param lesson  lesson
     * @param learner learner
     */
    private void printBookingDetails(Learner learner, Lesson lesson) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+-------------------------------------------------+\n");
        sb.append("|                 Lesson Booked!                  |\n");
        sb.append("+-------------------------------------------------+\n");
        sb.append(String.format("| Lesson ID: %-36d |\n", lesson.getId()));
        sb.append(String.format("| Lesson Name: %-34s |\n", lesson.getName()));
        sb.append(String.format("| Lesson Date: %-34s |\n", (formatLessonDate(lesson) + ", " + lesson.getLessonTime())));
        sb.append(String.format("| Lesson Grade: %-33s |\n", lesson.getGrade()));
        sb.append(String.format("| Your current Grade: %-27d |\n", learner.getGrade()));
        if (lesson.getGrade() > learner.getGrade()) {
            sb.append(String.format("| Additional Info: You'd be upgraded to grade %-3d |\n", lesson.getGrade()));
            sb.append("|                  after attending this lesson    |\n");
        }
        sb.append("+-------------------------------------------------+\n");
        sb.append("|                 Booking details                 |\n");
        sb.append("+-------------------------------------------------+\n");

        view.showMessage(sb.toString(), MessageType.INFO);
    }

    /**
     * display a message on successful registration
     *
     * @param learner learner
     */
    private void printSuccessfulLearnerRegistrationDetails(Learner learner) {

        var message = new StringBuilder()
                .append("\n+-------------------------------------------------+\n")
                .append("|       Registration Successful! Welcome!         |\n")
                .append("+-------------------------------------------------+\n")
                .append(String.format("| ID: %-43d |\n", learner.getId()))
                .append(String.format("| Name: %-41s |\n", learner.getName()))
                .append(String.format("| Gender: %-39s |\n", learner.getGender()))
                .append(String.format("| Age: %-42d |\n", learner.getAge()))
                .append(String.format("| Grade: %-40d |\n", learner.getGrade()))
                .append(String.format("| Phone Number: %-33s |\n", learner.getPhoneNumber()))
                .append(String.format("| Emergency Contact: %-28s |\n", learner.getEmergencyContactNumber()))
                .append("+-------------------------------------------------+\n")
                .append("| Here are your registration details. Please keep |\n")
                .append("| this information for your records.              |\n")
                .append("+-------------------------------------------------+\n")
                .toString();

        view.showMessage(message, MessageType.INFO);
    }

    /**
     * display a message on successful attended lesson
     *
     * @param lesson  lesson
     * @param learner learner
     */
    private void printSuccessfulLessonAttendedMsg(Lesson lesson, Learner learner) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+-------------------------------------------------+\n");
        sb.append("|                Lesson Attended!                 |\n");
        sb.append("+-------------------------------------------------+\n");
        sb.append(String.format("| Lesson ID: %-36d |\n", lesson.getId()));
        sb.append(String.format("| Lesson Name: %-34s |\n", lesson.getName()));
        sb.append(String.format("| Your current Grade: %-27d |\n", learner.getGrade()));
        sb.append("+-------------------------------------------------+\n");

        view.showMessage(sb.toString(), MessageType.INFO);
    }

    /**
     * Reset details used in registration
     */
    private void resetRegistrationDetails() {
        nameBeingRegistered = null;
        genderBeingRegistered = null;
        ageBeingRegistered = null;
        gradeBeingRegistered = null;
        phoneNumberBeingRegistered = null;
        emergencyContactBeingRegistered = null;
    }

    /**
     * print learner details
     *
     * @param learner learner
     */
    private void printLearnerDetails(Learner learner) {
        String msg = new StringBuilder()
                .append("\n+-------------------------------------------------+\n")
                .append("|                Learner Details                  |\n")
                .append("+-------------------------------------------------+\n")
                .append(String.format("| ID: %-43s |\n", learner.getId()))
                .append(String.format("| Name: %-41s |\n", learner.getName()))
                .append(String.format("| Age: %-42s |\n", learner.getAge()))
                .append(String.format("| Gender: %-39s |\n", learner.getGender()))
                .append(String.format("| Current Grade: %-32s |\n", learner.getGrade()))
                .append(String.format("| Total Registered Lessons: %-21s |\n", learner.getRegisteredLessons().size()))
                .append("+-------------------------------------------------+")
                .toString();
        view.showMessage(msg, MessageType.INFO);
    }
}
