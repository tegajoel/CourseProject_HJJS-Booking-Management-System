package data;

import domain.entity.coach.Coach;
import domain.repository.CoachRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemCoachRepository implements CoachRepository {

    private final List<Coach> coaches = new ArrayList<>();

    private InMemCoachRepository() {
    }

    private static InMemCoachRepository INSTANCE = null;

    public static InMemCoachRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InMemCoachRepository();
        }
        return INSTANCE;
    }

    @Override
    public List<Coach> getAllCoaches() {
        return coaches;
    }

    @Override
    public void addNewCoach(Coach coach) {
        coaches.add(coach);
    }

    public void addCoaches(List<Coach> coaches) {
        this.coaches.addAll(coaches);
    }
}
