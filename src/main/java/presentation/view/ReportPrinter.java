package presentation.view;

import domain.entity.learner.LearnerReport;
import domain.entity.lesson.Lesson;

import java.util.List;
import java.util.stream.Collectors;

public class ReportPrinter {
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

    public static String prettyPrintLearnerReport(LearnerReport report) {
        return new StringBuilder()
                .append("\n+-------------------------------------------------+\n")
                .append("|                 Learner Report                  |\n")
                .append("+-------------------------------------------------+\n")
                .append(String.format("| Name: %-41s |\n", report.learnerName()))
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

    private static String formatLessons(List<Lesson> lessons) {
        return lessons.isEmpty() ? "| None                                            |\n" :
                lessons.stream()
                        .map(lesson -> String.format("| - %-44s |\n", lesson.getName()))
                        .collect(Collectors.joining());
    }
}
