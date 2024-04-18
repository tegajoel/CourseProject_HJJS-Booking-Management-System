package domain.mock;

import domain.entity.learner.Learner;
import domain.repository.LearnerRepository;
import domain.util.Result;

import java.util.ArrayList;
import java.util.List;

public class LearnerRepositoryMock implements LearnerRepository {
    private final List<Learner> learners = new ArrayList<>();
    @Override
    public boolean addNewLearner(Learner learner) {
        return learners.add(learner);
    }

    @Override
    public List<Learner> getAllLearners() {
        return learners;
    }

    @Override
    public Result<Learner, Error> getLearnerById(int id) {
        return null;
    }
}
