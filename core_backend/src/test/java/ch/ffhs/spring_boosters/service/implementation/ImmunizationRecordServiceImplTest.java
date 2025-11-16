package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImmunizationRecordServiceImplTest {

    @Mock
    private ImmunizationRecordRepository immunizationRecordRepository;

    @InjectMocks
    private ImmunizationRecordServiceImpl immunizationRecordService;

    private ImmunizationRecord rec1;
    private ImmunizationRecord rec2;

    @BeforeEach
    void setUp() {
        rec1 = new ImmunizationRecord();
        rec1.setId(UUID.randomUUID());
        rec1.setUserId(UUID.randomUUID());
        rec1.setVaccineTypeId(UUID.randomUUID());
        rec1.setImmunizationPlanId(UUID.randomUUID());
        rec1.setAdministeredOn(LocalDate.now().minusDays(10));
        rec1.setDoseOrderClaimed(1);

        rec2 = new ImmunizationRecord();
        rec2.setId(UUID.randomUUID());
        rec2.setUserId(rec1.getUserId());
        rec2.setVaccineTypeId(UUID.randomUUID());
        rec2.setImmunizationPlanId(UUID.randomUUID());
        rec2.setAdministeredOn(LocalDate.now().minusDays(5));
        rec2.setDoseOrderClaimed(2);
    }

    @Test
    void getAllImmunizationRecords_returnsAll() {
        List<ImmunizationRecord> list = Arrays.asList(rec1, rec2);
        when(immunizationRecordRepository.findAll()).thenReturn(list);

        List<ImmunizationRecord> result = immunizationRecordService.getAllImmunizationRecords();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        verify(immunizationRecordRepository, times(1)).findAll();
    }

    @Test
    void getImmunizationRecordById_found() throws ImmunizationRecordNotFoundException {
        UUID id = rec1.getId();
        when(immunizationRecordRepository.findById(id)).thenReturn(Optional.of(rec1));

        ImmunizationRecord result = immunizationRecordService.getImmunizationRecordById(id);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(rec1.getUserId(), result.getUserId());
        verify(immunizationRecordRepository, times(1)).findById(id);
    }

    @Test
    void getImmunizationRecordById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(immunizationRecordRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ImmunizationRecordNotFoundException.class, () -> immunizationRecordService.getImmunizationRecordById(id));
        verify(immunizationRecordRepository, times(1)).findById(id);
    }

    @Test
    void createImmunizationRecord_success() {
        ImmunizationRecord toCreate = new ImmunizationRecord();
        toCreate.setUserId(rec1.getUserId());
        toCreate.setVaccineTypeId(UUID.randomUUID());
        toCreate.setAdministeredOn(LocalDate.now());

        when(immunizationRecordRepository.save(any(ImmunizationRecord.class))).thenAnswer(invocation -> {
            ImmunizationRecord arg = invocation.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        ImmunizationRecord result = immunizationRecordService.createImmunizationRecord(toCreate);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(toCreate.getUserId(), result.getUserId());
        verify(immunizationRecordRepository, times(1)).save(any(ImmunizationRecord.class));
    }

    @Test
    void updateImmunizationRecord_success() throws ImmunizationRecordNotFoundException {
        UUID id = rec1.getId();
        ImmunizationRecord updated = new ImmunizationRecord();
        updated.setUserId(rec1.getUserId());
        updated.setVaccineTypeId(rec1.getVaccineTypeId());
        updated.setAdministeredOn(LocalDate.now());
        updated.setDoseOrderClaimed(5);

        when(immunizationRecordRepository.findById(id)).thenReturn(Optional.of(rec1));
        when(immunizationRecordRepository.save(any(ImmunizationRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImmunizationRecord result = immunizationRecordService.updateImmunizationRecord(id, updated);

        Assertions.assertEquals(5, result.getDoseOrderClaimed());
        verify(immunizationRecordRepository, times(1)).findById(id);
        verify(immunizationRecordRepository, times(1)).save(any(ImmunizationRecord.class));
    }

    @Test
    void deleteImmunizationRecord_success() throws ImmunizationRecordNotFoundException {
        UUID userId = rec1.getUserId();
        UUID vaccineTypeId = rec1.getVaccineTypeId();

        when(immunizationRecordRepository.existsByUserIdAndVaccineTypeId(userId, vaccineTypeId)).thenReturn(true);

        immunizationRecordService.deleteImmunizationRecord(userId, vaccineTypeId);

        verify(immunizationRecordRepository, times(1)).existsByUserIdAndVaccineTypeId(userId, vaccineTypeId);
        verify(immunizationRecordRepository, times(1)).deleteByUserIdAndVaccineTypeId(userId, vaccineTypeId);
    }

    @Test
    void deleteImmunizationRecord_notFound_throws() {
        UUID userId = UUID.randomUUID();
        UUID vaccineTypeId = UUID.randomUUID();

        when(immunizationRecordRepository.existsByUserIdAndVaccineTypeId(userId, vaccineTypeId)).thenReturn(false);

        Assertions.assertThrows(ImmunizationRecordNotFoundException.class, () -> immunizationRecordService.deleteImmunizationRecord(userId, vaccineTypeId));
        verify(immunizationRecordRepository, times(1)).existsByUserIdAndVaccineTypeId(userId, vaccineTypeId);
        verify(immunizationRecordRepository, never()).deleteByUserIdAndVaccineTypeId(any(), any());
    }

    @Test
    void getImmunizationRecordsByUser_andByVaccineType_andByUserAndVaccineType() {
        UUID userId = rec1.getUserId();
        UUID vaccineTypeId = rec1.getVaccineTypeId();

        when(immunizationRecordRepository.findByUserId(userId)).thenReturn(Arrays.asList(rec1, rec2));
        when(immunizationRecordRepository.findByVaccineTypeId(vaccineTypeId)).thenReturn(Arrays.asList(rec1));
        when(immunizationRecordRepository.findByUserIdAndVaccineTypeId(userId, vaccineTypeId)).thenReturn(Arrays.asList(rec1));

        List<ImmunizationRecord> byUser = immunizationRecordService.getImmunizationRecordsByUser(userId);
        List<ImmunizationRecord> byVaccine = immunizationRecordService.getImmunizationRecordsByVaccineType(vaccineTypeId);
        List<ImmunizationRecord> byBoth = immunizationRecordService.getImmunizationRecordsByUserAndVaccineType(userId, vaccineTypeId);

        Assertions.assertEquals(2, byUser.size());
        Assertions.assertEquals(1, byVaccine.size());
        Assertions.assertEquals(1, byBoth.size());

        verify(immunizationRecordRepository, times(1)).findByUserId(userId);
        verify(immunizationRecordRepository, times(1)).findByVaccineTypeId(vaccineTypeId);
        verify(immunizationRecordRepository, times(1)).findByUserIdAndVaccineTypeId(userId, vaccineTypeId);
    }
}

