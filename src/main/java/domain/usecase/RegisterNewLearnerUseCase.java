package domain.usecase;

import domain.entity.Learner;
import domain.repository.LearnerRepository;
import domain.util.IdGenerator;
import domain.util.Result;

public class RegisterNewLearnerUseCase {
    private final LearnerRepository repository;

    public RegisterNewLearnerUseCase(LearnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Register a new learner
     *
     * @param learner learner to be registered
     * @return Result with {@link Result#NO_VALUE} on success,
     * otherwise, {@link Result#error(Object)} with {@link Error} as its error data
     */
    public Result<Object, Error> registerLearner(Learner learner) {
        if (learner.getAge() < 4 || learner.getAge() > 11) {
            return Result.error(Error.INVALID_AGE);
        }

        if (learner.getGrade() < 0 || learner.getGrade() > 5) {
            return Result.error(Error.INVALID_GRADE);
        }

        if (!validatePhoneNumber(learner.getPhoneNumber()) || !validatePhoneNumber(learner.getEmergencyContactNumber())) {
            return Result.error(Error.INVALID_PHONE_NUMBER);
        }

        if (repository.addNewLearner(learner.setId(IdGenerator.generateId(learner)))) {
            return Result.success(Result.NO_VALUE);
        } else {
            return Result.error(Error.REPOSITORY_ERROR);
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) return false;

        if (phoneNumber.length() != 11) return false;

        return phoneNumber.matches("\\d+");
    }

    public enum Error {
        INVALID_AGE, INVALID_GRADE, INVALID_PHONE_NUMBER, REPOSITORY_ERROR
    }
}
