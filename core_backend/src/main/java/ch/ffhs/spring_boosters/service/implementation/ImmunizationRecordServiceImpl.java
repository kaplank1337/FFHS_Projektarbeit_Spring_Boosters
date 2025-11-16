package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImmunizationRecordServiceImpl implements ImmunizationRecordService {

    private final ImmunizationRecordRepository immunizationRecordRepository;

    @Override
    public List<ImmunizationRecord> getAllImmunizationRecords() {
        return immunizationRecordRepository.findAll();
    }

    @Override
    public ImmunizationRecord getImmunizationRecordById(UUID id) throws ImmunizationRecordNotFoundException {
        return immunizationRecordRepository.findById(id)
                .orElseThrow(() -> new ImmunizationRecordNotFoundException("Immunization record with id " + id + " not found"));
    }

    @Override
    public ImmunizationRecord createImmunizationRecord(ImmunizationRecord immunizationRecord) {
        return immunizationRecordRepository.save(immunizationRecord);
    }

    @Override
    public ImmunizationRecord updateImmunizationRecord(UUID id, ImmunizationRecord immunizationRecord) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord existingRecord = getImmunizationRecordById(id);

        existingRecord.setUserId(immunizationRecord.getUserId());
        existingRecord.setVaccineTypeId(immunizationRecord.getVaccineTypeId());
        existingRecord.setImmunizationPlanId(immunizationRecord.getImmunizationPlanId());
        existingRecord.setAdministeredOn(immunizationRecord.getAdministeredOn());
        existingRecord.setDoseOrderClaimed(immunizationRecord.getDoseOrderClaimed());

        return immunizationRecordRepository.save(existingRecord);
    }

    @Override
    public void deleteImmunizationRecord(UUID userId, UUID vaccinationId) throws ImmunizationRecordNotFoundException {
        if (!immunizationRecordRepository.existsByUserIdAndVaccineTypeId(userId,vaccinationId)) {
            throw new ImmunizationRecordNotFoundException("Immunization record with userId: " + userId + " and VaccinationId: " + vaccinationId + " not found");
        }
        immunizationRecordRepository.deleteByUserIdAndVaccineTypeId(userId, vaccinationId);
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
}
