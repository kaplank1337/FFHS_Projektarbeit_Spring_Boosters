package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordWithNameRecordDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImmunizationRecordMapper {
    private final UserMapper userMapper;
    private final VaccineTypeMapper vaccineTypeMapper;
    private final ImmunizationPlanMapper immunizationPlanMapper;

    public ImmunizationRecordDto toDto(ImmunizationRecord immunizationRecord) {
        if (immunizationRecord == null) {
            return null;
        }
        return new ImmunizationRecordDto(
                immunizationRecord.getId(),
                immunizationRecord.getAdministeredOn(),
                immunizationRecord.getDoseOrderClaimed(),
                immunizationRecord.getCreatedAt(),
                immunizationRecord.getUpdatedAt()
        );
    }

    public ImmunizationRecordWithNameRecordDto toDtoWithName(ImmunizationRecord immunizationRecord) {
        if (immunizationRecord == null) {
            return null;
        }
        return new ImmunizationRecordWithNameRecordDto(
                immunizationRecord.getId(),
                immunizationRecord.getAdministeredOn(),
                immunizationRecord.getVaccineType().getName(),
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

    public List<ImmunizationRecordWithNameRecordDto> toDtoListWithName(List<ImmunizationRecord> immunizationRecords) {
        return immunizationRecords.stream()
                .map(this::toDtoWithName)
                .toList();
    }

    public ImmunizationRecord fromCreateDto(ImmunizationRecordCreateDto dto, UUID userId) {
        if (dto == null) {
            return null;
        }
        ImmunizationRecord record = new ImmunizationRecord(
                userId,
                dto.vaccineTypeId(),
                dto.administeredOn()
        );
        record.setDoseOrderClaimed(dto.doseOrderClaimed());
        return record;
    }

    public ImmunizationRecord fromUpdateDto(ImmunizationRecordUpdateDto dto, UUID userId) {
        if (dto == null) {
            return null;
        }
        ImmunizationRecord record = new ImmunizationRecord(
                userId,
                dto.id(),
                dto.vaccineTypeId(),
                dto.administeredOn(),
                dto.doseOrderClaimed()
        );
        record.setDoseOrderClaimed(dto.doseOrderClaimed());
        return record;
    }
}
