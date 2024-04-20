package presentation.view;

import domain.entity.Rating;
import domain.entity.coach.CoachReport;
import domain.entity.learner.LearnerReport;
import domain.entity.lesson.Lesson;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportPrinter {
    /**
     * Pretty print a list of learner report
     *
     * @param reports reports
     * @return formatted report
     */
    public static String prettyPrintLearnerReports(List<LearnerReport> reports) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+-------------------------------------------------+\n");
        sb.append("|                Learner Reports List             |\n");
        sb.append("+-------------------------------------------------+\n");

        if (reports.isEmpty()) {
            sb.append("+-------------- NO REPORTS TO SHOW ---------------+\n");
            sb.append("+-------------------------------------------------+\n");
        }

        for (LearnerReport report : reports) {
            sb.append(prettyPrintLearnerReport(report));
            sb.append("+-------------------------------------------------+\n");
        }

        return sb.toString();
    }

    /**
     * Pretty print a learner report
     *
     * @param report report
     * @return formatted report
     */
    public static String prettyPrintLearnerReport(LearnerReport report) {
        return new StringBuilder()
                .append("\n+-------------------------------------------------+\n")
                .append("|                 Learner Report                  |\n")
                .append("+-------------------------------------------------+\n")
                .append(String.format("| Name: %-41s |\n", report.learnerName()))
                .append(String.format("| ID: %-43s |\n", report.learnerId()))
                .append(String.format("| Age: %-42s |\n", report.learnerAge()))
                .append(String.format("| Gender: %-39s |\n", report.learnerGender()))
                .append(String.format("| Current Grade: %-32s |\n", report.currentGrade()))
                .append(String.format("| Total Registered Lessons: %-21s |\n", report.totalRegisteredLessons()))
                .append("+-------------------------------------------------+\n")
                .append("| Booked Lessons:                                 |\n")
                .append(formatLessons(report.bookedLessons()))
                .append("|                                                 |\n")
                .append("+-------------------------------------------------+\n")
                .append("| Attended Lessons:                               |\n")
                .append(formatLessons(report.attendedLessons()))
                .append("|                                                 |\n")
                .append("+-------------------------------------------------+\n")
                .append("| Cancelled Lessons:                              |\n")
                .append(formatLessons(report.cancelledLessons()))
                .append("+-------------------------------------------------+\n")
                .toString();
    }

    /**
     * Pretty print a list of coach report
     *
     * @param reports reports
     * @return formatted report
     */
    public static String prettyPrintCoachReports(List<CoachReport> reports) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n+-------------------------------------------------+\n");
        sb.append("|               Coach Reports List                |\n");
        sb.append("+-------------------------------------------------+\n");

        if (reports.isEmpty()) {
            sb.append("+-------------- NO REPORTS TO SHOW ---------------+\n");
            sb.append("+-------------------------------------------------+\n");
        }

        for (CoachReport report : reports) {
            sb.append(prettyPrintCoachReport(report));
            sb.append("+-------------------------------------------------+\n");
        }

        return sb.toString();
    }

    /**
     * Pretty print a list of coach report
     *
     * @param coachReport report
     * @return formatted report
     */
    public static String prettyPrintCoachReport(CoachReport coachReport) {
        return new StringBuilder()
                .append("\n+-------------------------------------------------+\n")
                .append("|                  Coach Report                   |\n")
                .append("+-------------------------------------------------+\n")
                .append(String.format("| Name: %-41s |\n", coachReport.coachName()))
                .append(String.format("| Number of Lessons Taught: %-21s |\n", coachReport.numberOfLessonsTaught()))
                .append("| Lessons Taught:                                 |\n")
                .append(formatLessons(coachReport.lessonsTaught()))
                .append(String.format("| Average Lesson Rating: %-24s |\n", coachReport.averageLessonRating().hasRating() ? coachReport.averageLessonRating().getRatingValue() : "None"))
                .append("| Average Rating per Lesson:                      |\n")
                .append(formatAverageRatingPerLesson(coachReport.averageRatingPerLesson()))
                .append("+-------------------------------------------------+\n")
                .toString();
    }

    /**
     * format list of average rating
     *
     * @param averageRatingPerLesson list of average rating
     * @return formatted rating
     */
    private static String formatAverageRatingPerLesson(List<Map<Rating, Lesson>> averageRatingPerLesson) {
        if (averageRatingPerLesson.isEmpty()) {
            return "| None                                            |\n";
        }
        return averageRatingPerLesson.stream()
                .map(entry -> entry.entrySet().stream()
                        .map(e -> String.format("| - %-30s : Rating %-5s |\n", e.getValue().getName(), e.getKey().hasRating() ? e.getKey().getRatingValue() : "None"))
                        .collect(Collectors.joining()))
                .collect(Collectors.joining());
    }

    /**
     * Format a list of lesson
     *
     * @param lessons lesson
     * @return formatted lesson
     */
    private static String formatLessons(List<Lesson> lessons) {
        return lessons.isEmpty() ? "| None                                            |\n" :
                lessons.stream()
                        .map(lesson -> String.format("| - %-45s |\n", lesson.getName()))
                        .collect(Collectors.joining());
    }

}
