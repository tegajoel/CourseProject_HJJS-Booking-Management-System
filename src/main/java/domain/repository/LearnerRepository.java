package domain.repository;

import domain.entity.Learner;

import java.util.List;

public interface LearnerRepository {
    /**
     * Add a new learner to the repository
     * @param learner learner to be added
     * @return true if the learner was added, false otherwise.
     */
    boolean addNewLearner(Learner learner);

    /**
     * Get all the list of learners
     * @return learners list
     */
    List<Learner> getAllLearners();

    /**
     * Get a learner by their id
     * @param id id
     * @return the learner if they exist, null otherwise
     */
    Learner getLearnerById(int id);
}
