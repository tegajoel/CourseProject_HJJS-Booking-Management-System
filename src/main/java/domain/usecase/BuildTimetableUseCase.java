package domain.usecase;

import domain.entity.coach.Coach;
import domain.entity.lesson.Lesson;
import domain.util.IdGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class BuildTimetableUseCase {
    private final List<String> preLessonNames = List.of(
            "Water Safety",
            "Backstroke Training",
            "Sidestroke Training",
            "Freestyle Training",
            "Butterfly Technique",
            "Breaststroke Technique",
            "Open Water Endurance",
            "Diving Fundamentals",
            "Synchronized Swimming"
    );

    LocalDate startDate = LocalDate.of(2024, Month.APRIL, 1);// Monday April 1st

    /**
     * Generate a list of lessons
     *
     * @param coaches coaches to teach the lesson
     * @return a list of generated lessons with a coach assigned to each of them
     */
    public List<Lesson> buildAndGetLessons(List<Coach> coaches) {
        if (coaches.isEmpty())
            throw new IllegalArgumentException("The list of coaches must not be empty. There should be at lease one coach");

        List<LessonNameGradePair> lessonNameGradePairs = generateLessonNames(preLessonNames);

        Collections.shuffle(lessonNameGradePairs); // shuffle the list to randomize it


        List<Lesson> lessons = new ArrayList<>();
        int nextCoachIndex = 0;
        for (LessonNameGradePair p : lessonNameGradePairs) {
            var dateTime = getNext();
            Lesson lesson = new Lesson(p.lessonName, p.grade, coaches.get(nextCoachIndex), dateTime.date, dateTime.time);
            lesson.setId(IdGenerator.generateId(lesson));

            lessons.add(lesson);
            nextCoachIndex++;
            if (nextCoachIndex == coaches.size()) {
                nextCoachIndex = 0;
            }
        }


        return lessons;
    }


    private LessonDateTimeData lessonTimeCreationData;

    private DataCreationResult getNext() {
        if (lessonTimeCreationData == null) {
            lessonTimeCreationData = new LessonDateTimeData(1, CurrentDay.MON, new DataCreationResult(startDate, "4-5pm"));
            return lessonTimeCreationData.data;

        }

        lessonTimeCreationData.countIntDay++;
        if (lessonTimeCreationData.day == CurrentDay.SAT) {
            if (lessonTimeCreationData.countIntDay > 2) {
                // should no longer be saturday
                // now monday
                lessonTimeCreationData.data.time = "4-5pm";
                lessonTimeCreationData.data.date = lessonTimeCreationData.data.date.plusDays(2);
                lessonTimeCreationData.countIntDay = 1;
                lessonTimeCreationData.day = CurrentDay.MON;
            } else {
                if (lessonTimeCreationData.countIntDay == 1) {
                    lessonTimeCreationData.data.time = "2-3pm";
                } else if (lessonTimeCreationData.countIntDay == 2) {
                    lessonTimeCreationData.data.time = "3-4pm";
                }
            }
        } else {
            if (lessonTimeCreationData.countIntDay > 3) {
                lessonTimeCreationData.countIntDay = 1;
                //moving to next day
                if (lessonTimeCreationData.day == CurrentDay.FRI) {
                    // moving to saturday
                    lessonTimeCreationData.data.time = "2-3pm";
                    lessonTimeCreationData.data.date = lessonTimeCreationData.data.date.plusDays(1);
                    lessonTimeCreationData.day = CurrentDay.SAT;
                } else {
                    // moving to another 3 lesson day
                    lessonTimeCreationData.data.time = "4-5pm";
                    lessonTimeCreationData.data.date = lessonTimeCreationData.data.date.plusDays(2); // moving from mon to wed or wed to fri is 2 days

                    if (lessonTimeCreationData.day == CurrentDay.MON) {
                        //moving to wed
                        lessonTimeCreationData.day = CurrentDay.WED;
                    } else if (lessonTimeCreationData.day == CurrentDay.WED) {
                        //moving to friday
                        lessonTimeCreationData.day = CurrentDay.FRI;
                    }
                }
            } else {
                if (lessonTimeCreationData.countIntDay == 1) {
                    lessonTimeCreationData.data.time = "4-5pm";
                } else if (lessonTimeCreationData.countIntDay == 2) {
                    lessonTimeCreationData.data.time = "5-6pm";
                } else if (lessonTimeCreationData.countIntDay == 3) {
                    lessonTimeCreationData.data.time = "6-7pm";
                }
            }
        }

        return lessonTimeCreationData.data;
    }

    private List<LessonNameGradePair> generateLessonNames(List<String> preLessonNames) {
        List<LessonNameGradePair> result = new ArrayList<>();

        for (String preLessonName : preLessonNames) {
            for (int i = 1; i <= 5; i++) {
                result.add(new LessonNameGradePair(preLessonName + " " + i, i));
            }
        }
        return result;
    }

    private static final class DataCreationResult {
        LocalDate date;
        String time;

        private DataCreationResult(LocalDate date, String time) {
            this.date = date;
            this.time = time;
        }

        @Override
        public String toString() {
            return "TrackData{" +
                    "date=" + date +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    private static class LessonDateTimeData {
        int countIntDay;
        CurrentDay day;
        DataCreationResult data;

        public LessonDateTimeData(int countIntDay, CurrentDay day, DataCreationResult data) {
            this.countIntDay = countIntDay;
            this.day = day;
            this.data = data;
        }
    }

    private enum CurrentDay {
        MON, WED, FRI, SAT
    }

    private record LessonNameGradePair(String lessonName, int grade) {
    }
}
