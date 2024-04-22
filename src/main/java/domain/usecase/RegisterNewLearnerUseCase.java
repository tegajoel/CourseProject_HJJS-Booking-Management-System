package domain.usecase;

import domain.entity.learner.Learner;
import domain.repository.LearnerRepository;
import domain.util.IdGenerator;
import domain.util.Result;

public class RegisterNewLearnerUseCase {
    private final LearnerRepository repository;

    private final Validator validator = new Validator();

    public RegisterNewLearnerUseCase(LearnerRepository repository) {
        this.repository = repository;
    }

    /**
     * Register a new learner
     *
     * @param learner learner to be registered
     * @return {@link Result#success(Object)} Learner ID on success,
     * otherwise, {@link Result#error(Object)} with {@link Error} as its error data
     */
    public Result<Integer, Error> registerLearner(Learner learner) {
        if (!validator.validateAge(learner.getAge())) {
            return Result.error(Error.INVALID_AGE);
        }

        if (!validator.validateGrade(learner.getGrade())) {
            return Result.error(Error.INVALID_GRADE);
        }

        if (!validator.validatePhoneNumber(learner.getPhoneNumber()) || !validator.validatePhoneNumber(learner.getEmergencyContactNumber())) {
            return Result.error(Error.INVALID_PHONE_NUMBER);
        }

        if (!validator.validateGender(learner.getGender())){
            return Result.error(Error.INVALID_GENDER);
        }

        if (repository.addNewLearner(learner.setId(IdGenerator.generateId(learner)))) {
            return Result.success(learner.getId());
        } else {
            return Result.error(Error.REPOSITORY_ERROR);
        }
    }

    public Validator getValidator() {
        return validator;
    }

    public enum Error {
        INVALID_AGE, INVALID_GRADE, INVALID_PHONE_NUMBER, INVALID_GENDER, REPOSITORY_ERROR
    }

    public static class Validator{
        public boolean validateAge(int age){
            return age >=  4 && age <= 11;
        }

        public boolean validateGrade(int grade){
            return grade >= 0 && grade <= 5;
        }

        public boolean validatePhoneNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.isBlank()) return false;

            if (phoneNumber.length() < 11) return false;

            return phoneNumber.matches("\\+?[0-9]+");
        }

        public boolean validateGender(String gender){
            return gender.trim().equalsIgnoreCase("Male") ||  gender.trim().equalsIgnoreCase("Female");
        }
    }
}
