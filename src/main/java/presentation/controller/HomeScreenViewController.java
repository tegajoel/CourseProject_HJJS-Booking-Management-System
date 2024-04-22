package presentation.controller;

import data.InMemLearnersRepository;
import data.InMemLessonRepository;
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
import domain.util.IdGenerator;
import domain.util.LessonUtil;
import domain.util.Result;
import presentation.view.CLIView;
import presentation.view.components.text.InputConsumer;
import presentation.view.ReportPrinter;
import presentation.view.components.optionpicker.OptionPickerStyle;
import presentation.view.components.text.MessageType;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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


    private void initialize() {
        String appHeader = """

                +-----------------------------------+
                |    -  HJSS Booking Manager  -     |
                +-----------------------------------+
                """;

        view.showMessage(appHeader, MessageType.INFO);

        Coach coach1 = new Coach("Peter");
        Coach coach2 = new Coach("John");
        coachRepository.addNewCoach(coach1);
        coachRepository.addNewCoach(coach2);

        Lesson lesson = new Lesson("Diving 1", 3, coach1, LocalDate.now(), "3-4pm");
        Lesson lesson2 = new Lesson("Diving 2", 3, coach1, LocalDate.now(), "4-5pm");
        Lesson lesson3 = new Lesson("Diving 3", 4, coach2, LocalDate.now(), "6-7pm");
        Lesson lesson4 = new Lesson("Diving 4", 2, coach1, LocalDate.now(), "3-4pm");

        lesson.setId(IdGenerator.generateId(lesson));
        lesson2.setId(IdGenerator.generateId(lesson2));
        lesson3.setId(IdGenerator.generateId(lesson3));
        lesson4.setId(IdGenerator.generateId(lesson4));
        Learner learner = new Learner("Paul", "Male", 6, 3, "0819ja", "0819ja",
                List.of(new RegisteredLesson(lesson, LessonStatus.BOOKED), new RegisteredLesson(lesson2, LessonStatus.ATTENDED))).setId(1234);

        Learner learner1 = new Learner("Simon", "Male", 6, 2, "0819ja", "0819ja",
                List.of(new RegisteredLesson(lesson, LessonStatus.BOOKED), new RegisteredLesson(lesson3, LessonStatus.ATTENDED))).setId(123);


        InMemLearnersRepository.getInstance().addNewLearner(learner1);
        InMemLearnersRepository.getInstance().addNewLearner(learner);

        lesson3.addLearner(learner1);
        lesson.addLearner(learner1);
        lesson.addLearner(learner);
        lesson2.addLearner(learner);

        InMemLessonRepository.getInstance().addLessons(List.of(lesson2, lesson3, lesson4, lesson));

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

    private void onBookSwimmingLesson() {
        requestLearnerById(learner -> requestLessonFromAllLessons(lesson -> doBookLesson(lesson, learner)));
    }

    private void onRegisterNewUser() {
        view.showMessage("We'll need some details to get you registered", MessageType.INFO);

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

    private void onAttendSwimmingLesson() {
        requestLearnerById(learner -> {

            List<Lesson> bookedLessons = learner.getRegisteredLessons().stream()
                    .filter(lesson -> lesson.getLessonStatus() == LessonStatus.BOOKED)
                    .map(RegisteredLesson::getLesson)
                    .toList();

            if (bookedLessons.isEmpty()) {
                view.showMessage("You don't have any booked lesson to attend", MessageType.ERROR);
                var options = List.of("Book a lesson", "Return to Main menu", "Exit App");
                view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, "Choose an option", (index, value) -> {
                    switch (index) {
                        case 0 -> onBookSwimmingLesson();
                        case 1 -> showMainMenuOptions();
                        case 2 -> closeApp();
                    }
                });
            } else {
                requestPickFromLessons(
                        "Please select from one of your booked lesson to attend",
                        bookedLessons, false,
                        lesson -> doAttendLesson(lesson, learner));
            }
        });
    }

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
                var options = List.of("Book a lesson", "Return to Main menu", "Exit App");
                view.showOptionsPicker(options, OptionPickerStyle.HORIZONTAL, "Choose an option", (index, value) -> {
                    switch (index) {
                        case 0 -> onBookSwimmingLesson();
                        case 1 -> showMainMenuOptions();
                        case 2 -> closeApp();
                    }
                });
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

    private void closeApp() {
        view.showMessage("Exiting...", MessageType.INFO);
        System.exit(0);
    }

    private void showExitOrMainMenuOption() {
        var options = List.of("Return to Main menu");
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL_WITH_EXIT_APP_OPTION, null, (index, value) -> {
            if (index == 0) {
                showMainMenuOptions();
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
                    view.showMessage("\nEnter 0 to to return to the Main menu", MessageType.INFO);
                    return Result.error("No learner exits with this id.");
                }
            } catch (Exception e) {
                return Result.error("Learner IDs only contain numbers");
            }
        });
    }

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

    private void getCoachFromAllCoaches(String headerMessage, Consumer<Coach> consumer) {
        List<Coach> coaches = coachRepository.getAllCoaches();
        List<String> options = coaches.stream().map(this::getCoachDetails).toList();
        view.showOptionsPicker(options, OptionPickerStyle.VERTICAL, headerMessage, (index, value) -> consumer.accept(coaches.get(index)));
    }

    private String getCoachDetails(Coach coach) {
        Rating averageRating = LessonUtil.getAverageRating(coach.getAssignedLessons().
                stream().map(lesson -> LessonUtil.getAverageReviewRating(lesson.getReviews()))
                .toList());

        return coach.getName() + " (Average Rating: " + (averageRating.hasRating() ? averageRating.getRatingValue() : "No rating yet") + ")";
    }

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

    private String formatLessonDate(Lesson lesson) {
        return lesson.getLessonDate().format(DateTimeFormatter.ofPattern("EEEE - MMMM d", Locale.ENGLISH));
    }

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

    private void resetRegistrationDetails() {
        nameBeingRegistered = null;
        genderBeingRegistered = null;
        ageBeingRegistered = null;
        gradeBeingRegistered = null;
        phoneNumberBeingRegistered = null;
        emergencyContactBeingRegistered = null;
    }

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
