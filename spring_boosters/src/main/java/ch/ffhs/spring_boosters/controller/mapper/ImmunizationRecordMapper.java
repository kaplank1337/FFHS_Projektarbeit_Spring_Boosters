package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImmunizationRecordMapper {

    public ImmunizationRecordDto toDto(ImmunizationRecord immunizationRecord) {
        if (immunizationRecord == null) {
            return null;
        }
        return new ImmunizationRecordDto(
                immunizationRecord.getId(),
                immunizationRecord.getUserId(),
                immunizationRecord.getVaccineTypeId(),
                immunizationRecord.getImmunizationPlanId(),
                immunizationRecord.getAdministeredOn(),
                immunizationRecord.getDoseOrderClaimed(),
                immunizationRecord.getCreatedAt(),
                immunizationRecord.getUpdatedAt()
        );
    }

    public List<ImmunizationRecordDto> toDtoList(List<ImmunizationRecord> immunizationRecords) {
        return immunizationRecords.stream()
                .map(this::toDto)
                .toList();
    }

    public ImmunizationRecord fromCreateDto(ImmunizationRecordCreateDto dto) {
        if (dto == null) {
            return null;
        }
        ImmunizationRecord record = new ImmunizationRecord(
                dto.userId(),
                dto.vaccineTypeId(),
                dto.immunizationPlanId(),
                dto.administeredOn()
        );
        record.setDoseOrderClaimed(dto.doseOrderClaimed());
        return record;
    }

    public ImmunizationRecord fromUpdateDto(ImmunizationRecordUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        ImmunizationRecord record = new ImmunizationRecord(
                dto.userId(),
                dto.vaccineTypeId(),
                dto.immunizationPlanId(),
                dto.administeredOn()
        );
        record.setDoseOrderClaimed(dto.doseOrderClaimed());
        return record;
    }
}
