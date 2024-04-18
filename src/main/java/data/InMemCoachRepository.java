package data;

import domain.entity.Coach;
import domain.repository.CoachRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemCoachRepository implements CoachRepository {
    private final List<Coach> coaches = new ArrayList<>();

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
