package domain.repository;

import domain.entity.coach.Coach;

import java.util.List;

public interface CoachRepository {
    /**
     * Get all the coaches in this repository
     * @return the list of coaches
     */
    List<Coach> getAllCoaches();

    /**
     * Add a new coach to this repository
     * @param coach the coach to be added
     */
    void addNewCoach(Coach coach);
}
