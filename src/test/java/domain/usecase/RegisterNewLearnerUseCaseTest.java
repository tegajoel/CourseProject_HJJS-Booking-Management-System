package domain.usecase;

import domain.entity.Learner;
import domain.repository.LearnerRepository;
import domain.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterNewLearnerUseCaseTest {
    private RegisterNewLearnerUseCase sut;
    private LearnersRepositoryMock repository;

    private Learner validLearner;

    @BeforeEach
    void setUp() {
        repository = new LearnersRepositoryMock();
        sut = new RegisterNewLearnerUseCase(repository);
        String validPhoneNumber = "08012345680";
        validLearner = new Learner("John doe", "Male", 5, 5, validPhoneNumber, validPhoneNumber);
    }

    @Test
    public void addValidLearner_successReturned() {
        Result<Object, RegisterNewLearnerUseCase.Error> result = sut.execute(validLearner);

        assertTrue(result.isSuccess());
    }

    @Test
    public void addValidLearner_repositoryIncremented() {
        int currentRepoSize = repository.size();
        sut.execute(validLearner);

        assertEquals(currentRepoSize + 1, repository.size());
    }

    @Test
    public void addLearner_onSuccess_leanerIDAssigned() {
        sut.execute(validLearner);

        assertDoesNotThrow(() -> {
            validLearner.getId();
        });
    }

    @Test
    public void addLearner_invalidAge_errorReturned() {
        Learner learner = validLearner;
        learner.setAge(60);

        Result<Object, RegisterNewLearnerUseCase.Error> result = sut.execute(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_AGE, result.getError());
    }

    @Test
    public void addInvalidLearner_repositorySizeSame() {
        int currentRepoSize = repository.size();
        Learner invalidLEarner = validLearner;
        invalidLEarner.setAge(60);
        sut.execute(invalidLEarner);

        assertEquals(currentRepoSize, repository.size());
    }

    @Test
    public void addLearner_invalidGrade_errorReturned() {
        Learner learner = validLearner;
        learner.setGrade(6);

        Result<Object, RegisterNewLearnerUseCase.Error> result = sut.execute(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_GRADE, result.getError());
    }

    @Test
    public void addLearner_invalidPhoneNumber_errorReturned() {
        Learner learner = validLearner;
        learner.setPhoneNumber("");

        Result<Object, RegisterNewLearnerUseCase.Error> result = sut.execute(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_PHONE_NUMBER, result.getError());
    }

    @Test
    public void addLearner_invalidEmergencyNumber_errorReturned() {
        Learner learner = validLearner;
        learner.setEmergencyContactNumber("");

        Result<Object, RegisterNewLearnerUseCase.Error> result = sut.execute(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_PHONE_NUMBER, result.getError());
    }


    private static class LearnersRepositoryMock implements LearnerRepository {
        private final List<Learner> learners = new ArrayList<>();

        @Override
        public boolean addNewLearner(Learner learner) {
            learners.add(learner);
            return true;
        }

        @Override
        public List<Learner> getAllLearners() {
            return learners;
        }

        @Override
        public Result<Learner, LearnerRepository.Error> getLearnerById(int id) {
            return null;
        }

        public int size() {
            return learners.size();
        }
    }
}