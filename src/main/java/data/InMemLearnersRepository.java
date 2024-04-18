package data;

import domain.entity.Learner;
import domain.repository.LearnerRepository;
import domain.util.Result;

import java.util.ArrayList;
import java.util.List;

public class InMemLearnersRepository implements LearnerRepository {
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
    public Result<Learner, LearnerRepository.Error> getLearnerById(int id) {
        for (Learner learner : learners) {
            if (learner.getId() == id) return Result.success(learner);
        }

        return Result.error(Error.LEARNER_NOT_FOUND);
    }

    public void addLearners(List<Learner> learners) {
        this.learners.addAll(learners);
    }
}
