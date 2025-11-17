package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
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
        // Erwartet: immunizationRecord hat userId, vaccineTypeId, administeredOn; PlanId noch null.
        // Wir benötigen zusätzlich ageCategoryId aus dem Kontext. Diese ist aktuell nicht im Entity vorhanden.
        // Annahme: Temporär wird ageCategoryId über doseOrderClaimed missbraucht? -> Besser: später Entity erweitern.
        // Da CreateDto jetzt ageCategoryId liefert, muss dies hier durchgereicht werden. Lösung: erweitere Entity oder verwende einen ThreadLocal/ temporären Ansatz.
        // Vereinfachung: Wir setzen Plan basierend auf erstem Plan mit vaccineTypeId UND AgeCategory passend (findByVaccineTypeId -> filtern).

        List<ImmunizationPlan> plans = immunizationPlanRepository.findByVaccineTypeId(immunizationRecord.getVaccineTypeId());
        ImmunizationPlan matched = plans.stream()
                .filter(p -> p.getAgeCategoryId() != null) // rudimentäre Filterung; genaue AgeCategory muss übergeben werden -> TODO
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Kein passender ImmunizationPlan für vaccineTypeId=" + immunizationRecord.getVaccineTypeId()));

        immunizationRecord.setImmunizationPlanId(matched.getId());
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
}
