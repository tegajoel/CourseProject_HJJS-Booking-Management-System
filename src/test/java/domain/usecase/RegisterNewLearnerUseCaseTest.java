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
    private RegisterNewLearnerUseCase useCase;
    private LearnersRepositoryMock repository;

    private Learner validLearner;

    @BeforeEach
    void setUp() {
        repository = new LearnersRepositoryMock();
        useCase = new RegisterNewLearnerUseCase(repository);
        String validPhoneNumber = "08012345680";
        validLearner = new Learner("John doe", "Male", 5, 5, validPhoneNumber, validPhoneNumber);
    }

    @Test
    public void registerLearner_ValidLearner_successReturned() {
        Result<Object, RegisterNewLearnerUseCase.Error> result = useCase.registerLearner(validLearner);

        assertTrue(result.isSuccess());
    }

    @Test
    public void registerLearner_ValidLearner_repositoryIncremented() {
        int currentRepoSize = repository.size();
        useCase.registerLearner(validLearner);

        assertEquals(currentRepoSize + 1, repository.size());
    }

    @Test
    public void registerLearner_onSuccess_leanerIDAssigned() {
        useCase.registerLearner(validLearner);

        assertDoesNotThrow(() -> {
            validLearner.getId();
        });
    }

    @Test
    public void registerLearner_invalidAge_errorReturned() {
        Learner learner = validLearner;
        learner.setAge(60);

        Result<Object, RegisterNewLearnerUseCase.Error> result = useCase.registerLearner(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_AGE, result.getError());
    }

    @Test
    public void registerLearner_invalidLearner_repositorySizeSame() {
        int currentRepoSize = repository.size();
        Learner invalidLEarner = validLearner;
        invalidLEarner.setAge(60);
        useCase.registerLearner(invalidLEarner);

        assertEquals(currentRepoSize, repository.size());
    }

    @Test
    public void registerLearner_invalidGrade_errorReturned() {
        Learner learner = validLearner;
        learner.setGrade(6);

        Result<Object, RegisterNewLearnerUseCase.Error> result = useCase.registerLearner(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_GRADE, result.getError());
    }

    @Test
    public void registerLearner_invalidPhoneNumber_errorReturned() {
        Learner learner = validLearner;
        learner.setPhoneNumber("");

        Result<Object, RegisterNewLearnerUseCase.Error> result = useCase.registerLearner(learner);
        assertFalse(result.isSuccess());
        assertEquals(RegisterNewLearnerUseCase.Error.INVALID_PHONE_NUMBER, result.getError());
    }

    @Test
    public void registerLearner_invalidEmergencyNumber_errorReturned() {
        Learner learner = validLearner;
        learner.setEmergencyContactNumber("");

        Result<Object, RegisterNewLearnerUseCase.Error> result = useCase.registerLearner(learner);
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