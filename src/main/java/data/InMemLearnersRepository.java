package data;

import domain.entity.learner.Learner;
import domain.repository.LearnerRepository;
import domain.util.Result;

import java.util.ArrayList;
import java.util.List;

public class InMemLearnersRepository implements LearnerRepository {
    private final List<Learner> learners = new ArrayList<>();

    private static InMemLearnersRepository INSTANCE = null;

    public static InMemLearnersRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InMemLearnersRepository();
        }
        return INSTANCE;
    }

    private InMemLearnersRepository(){

    }

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

    public void generateSampleLearners() {
        addNewLearner(new Learner("Divine", "Male", 5, 0, "08172742510", "08172432510").setId(203101));
        addNewLearner(new Learner("James", "Male", 6, 1, "08142442510", "04372442510").setId(203102));
        addNewLearner(new Learner("Felix", "Male", 7, 2, "08172442510", "08172442430").setId(203103));
        addNewLearner(new Learner("Johnson", "Male", 8, 3, "08172442510", "08432442510").setId(203104));
        addNewLearner(new Learner("Emmanuel", "Male", 9, 4, "03172442510", "08143442510").setId(203105));
        addNewLearner(new Learner("Jerry", "Male", 10, 5, "08174342510", "08143442510").setId(203106));
        addNewLearner(new Learner("Paul", "Male", 11, 0, "08172443510", "08174342510").setId(203107));
        addNewLearner(new Learner("Joel", "Male", 4, 1, "08174342510", "08172442430").setId(203108));
        addNewLearner(new Learner("Pamela", "Female", 5, 2, "08174342510", "04372442510").setId(203109));
        addNewLearner(new Learner("Sarah", "Female", 6, 3, "08172443510", "08172444310").setId(203110));
        addNewLearner(new Learner("Maria", "Female", 7, 4, "08432442510", "08143442510").setId(203111));
        addNewLearner(new Learner("Sindy", "Female", 8, 5, "08172442510", "08174342510").setId(203112));
        addNewLearner(new Learner("Deborah", "Female", 9, 0, "08432442510", "08174342510").setId(203113));
        addNewLearner(new Learner("Gift", "Female", 10, 1, "08172444310", "08174342510").setId(203114));
        addNewLearner(new Learner("Rukky", "Female", 11, 2, "08174342510", "08172443510").setId(203115));
        addNewLearner(new Learner("Sandra", "Female", 6, 3, "08174342510", "08174342510").setId(203116));
    }
}
