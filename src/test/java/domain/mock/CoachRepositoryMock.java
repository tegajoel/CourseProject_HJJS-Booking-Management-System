package domain.mock;

import domain.entity.coach.Coach;
import domain.repository.CoachRepository;

import java.util.ArrayList;
import java.util.List;

public class CoachRepositoryMock implements CoachRepository {
    private final List<Coach> coaches = new ArrayList<>();

    @Override
    public List<Coach> getAllCoaches() {
        return coaches;
    }

    @Override
    public void addNewCoach(Coach coach) {
        coaches.add(coach);
    }
}
