package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
                dto.ageCategoryId(),
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
