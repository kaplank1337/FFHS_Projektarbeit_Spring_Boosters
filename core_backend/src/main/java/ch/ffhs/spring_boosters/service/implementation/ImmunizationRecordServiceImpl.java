package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImmunizationRecordServiceImpl implements ImmunizationRecordService {

    private final ImmunizationRecordRepository immunizationRecordRepository;
    private final ImmunizationPlanRepository immunizationPlanRepository;
    private final UserRepository userRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    @Override
    public List<ImmunizationRecord> getAllImmunizationRecords(UUID userId) {
        return immunizationRecordRepository.findAllByUserId(userId);
    }

    @Override
    public ImmunizationRecord getImmunizationRecordById(UUID id) throws ImmunizationRecordNotFoundException {
        return immunizationRecordRepository.findById(id)
                .orElseThrow(() -> new ImmunizationRecordNotFoundException("Immunization record with id " + id + " not found"));
    }

    @Override
    public ImmunizationRecord createImmunizationRecord(ImmunizationRecord immunizationRecord) {

        AgeCategory ageCategoryOfVaccination = calcAgeOfUserWhenVaccination(immunizationRecord);

        List<ImmunizationPlan> plans = immunizationPlanRepository.findByVaccineTypeIdAndAgeCategoryId(immunizationRecord.getVaccineTypeId(), ageCategoryOfVaccination.getId());
        ImmunizationPlan matched = plans.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein passender ImmunizationPlan für vaccineTypeId=" + immunizationRecord.getVaccineTypeId()));

        immunizationRecord.setImmunizationPlanId(matched.getId());
        return immunizationRecordRepository.save(immunizationRecord);
    }

    @Override
    public ImmunizationRecord updateImmunizationRecord(UUID id, ImmunizationRecord immunizationRecord) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord existingRecord = getImmunizationRecordById(immunizationRecord.getId());

        existingRecord.setVaccineTypeId(immunizationRecord.getVaccineTypeId());
        existingRecord.setAdministeredOn(immunizationRecord.getAdministeredOn());
        existingRecord.setDoseOrderClaimed(immunizationRecord.getDoseOrderClaimed());

        return immunizationRecordRepository.save(existingRecord);
    }

    @Override
    @Transactional
    public void deleteImmunizationRecord(UUID userId, UUID immunizationRecordId) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord record = immunizationRecordRepository.findById(immunizationRecordId)
                .orElseThrow(() -> new ImmunizationRecordNotFoundException(
                        "Immunization record with id: " + immunizationRecordId + " not found"));

        immunizationRecordRepository.delete(record);
    }


    @Override
    public List<ImmunizationRecord> getImmunizationRecordsByUser(UUID userId) {
        return immunizationRecordRepository.findByUserId(userId);
    }

    @Override
    public List<ImmunizationRecord> getImmunizationRecordsByVaccineType(UUID vaccineTypeId) {
        return immunizationRecordRepository.findByVaccineTypeId(vaccineTypeId);
    }

    @Override
    public List<ImmunizationRecord> getImmunizationRecordsByUserAndVaccineType(UUID userId, UUID vaccineTypeId) {
        return immunizationRecordRepository.findByUserIdAndVaccineTypeId(userId, vaccineTypeId);
    }

    private AgeCategory calcAgeOfUserWhenVaccination(ImmunizationRecord immunizationRecord) {

        if (immunizationRecord.getAdministeredOn() == null) {
            throw new IllegalArgumentException("administeredOn ist null für immunizationRecord id=" + immunizationRecord.getId());
        }

        var user = userRepository.findById(immunizationRecord.getUserId())
                .orElseThrow(() -> new IllegalStateException("Kein User gefunden mit id=" + immunizationRecord.getUserId()));

        if (user.getBirthDate() == null) {
            throw new IllegalStateException("Geburtsdatum fehlt für user id=" + user.getId());
        }

        long ageDaysLong = java.time.temporal.ChronoUnit.DAYS.between(user.getBirthDate(), immunizationRecord.getAdministeredOn());

        if (ageDaysLong < 0) {
            throw new IllegalArgumentException("Alter in Tagen darf nicht negativ sein: " + ageDaysLong);
        }

        int ageDays = Math.toIntExact(ageDaysLong);

        return ageCategoryRepository.findAll().stream()
                .filter(cat -> ageDays >= cat.getAgeMinDays()
                        && (cat.getAgeMaxDays() == null || ageDays <= cat.getAgeMaxDays()))
                .findFirst()
                .orElse(null);
    }
}
