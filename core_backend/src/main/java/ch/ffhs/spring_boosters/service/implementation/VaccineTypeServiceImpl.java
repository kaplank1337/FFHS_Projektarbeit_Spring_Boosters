package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.repository.VaccineTypeRepository;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import ch.ffhs.spring_boosters.service.VaccineTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VaccineTypeServiceImpl implements VaccineTypeService {

    private final VaccineTypeRepository vaccineTypeRepository;

    @Override
    public List<VaccineType> getVaccineTypes() {
        return vaccineTypeRepository.findAll();
    }

    @Override
    public VaccineType getVaccineType(UUID id) throws VaccineTypeNotFoundException {
        return vaccineTypeRepository.findById(id)
                .orElseThrow(() -> new VaccineTypeNotFoundException(id.toString()));
    }
}
