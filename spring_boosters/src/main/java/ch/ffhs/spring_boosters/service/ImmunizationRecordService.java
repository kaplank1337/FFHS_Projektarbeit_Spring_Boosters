package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;

import java.util.List;
import java.util.UUID;

public interface ImmunizationRecordService {
    List<ImmunizationRecord> getAllImmunizationRecords();
    ImmunizationRecord getImmunizationRecordById(UUID id) throws ImmunizationRecordNotFoundException;
    ImmunizationRecord createImmunizationRecord(ImmunizationRecord immunizationRecord);
    ImmunizationRecord updateImmunizationRecord(UUID id, ImmunizationRecord immunizationRecord) throws ImmunizationRecordNotFoundException;
    void deleteImmunizationRecord(UUID vaccinationId, UUID userID) throws ImmunizationRecordNotFoundException;
    List<ImmunizationRecord> getImmunizationRecordsByUser(UUID userId);
    List<ImmunizationRecord> getImmunizationRecordsByVaccineType(UUID vaccineTypeId);
    List<ImmunizationRecord> getImmunizationRecordsByUserAndVaccineType(UUID userId, UUID vaccineTypeId);
}
