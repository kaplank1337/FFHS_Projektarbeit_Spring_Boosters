package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.repository.ActiveSubstanceRepository;
import ch.ffhs.spring_boosters.service.ActiveSubstanceService;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ActiveSubstanceServiceImpl implements ActiveSubstanceService {

    private final ActiveSubstanceRepository activeSubstanceRepository;

    @Override
    public List<ActiveSubstance> getAllActiveSubstances() {
        return activeSubstanceRepository.findAll();
    }

    @Override
    public ActiveSubstance getActiveSubstanceById(UUID id) throws ActiveSubstanceNotFoundException {
        return activeSubstanceRepository.findById(id)
                .orElseThrow(() -> new ActiveSubstanceNotFoundException("Active substance with id " + id + " not found"));
    }

    @Override
    public ActiveSubstance createActiveSubstance(ActiveSubstance activeSubstance) throws ActiveSubstanceAlreadyExistsException {
        if (activeSubstanceRepository.existsByName(activeSubstance.getName())) {
            throw new ActiveSubstanceAlreadyExistsException("Active substance with name '" + activeSubstance.getName() + "' already exists");
        }
        return activeSubstanceRepository.save(activeSubstance);
    }

    @Override
    public ActiveSubstance updateActiveSubstance(UUID id, ActiveSubstance activeSubstance) throws ActiveSubstanceNotFoundException, ActiveSubstanceAlreadyExistsException {
        ActiveSubstance existingActiveSubstance = getActiveSubstanceById(id);

        // Check if name is being changed and if new name already exists
        if (!existingActiveSubstance.getName().equals(activeSubstance.getName())
            && activeSubstanceRepository.existsByName(activeSubstance.getName())) {
            throw new ActiveSubstanceAlreadyExistsException("Active substance with name '" + activeSubstance.getName() + "' already exists");
        }

        existingActiveSubstance.setName(activeSubstance.getName());
        existingActiveSubstance.setSynonyms(activeSubstance.getSynonyms());

        return activeSubstanceRepository.save(existingActiveSubstance);
    }

    @Override
    public void deleteActiveSubstance(UUID id) throws ActiveSubstanceNotFoundException {
        if (!activeSubstanceRepository.existsById(id)) {
            throw new ActiveSubstanceNotFoundException("Active substance with id " + id + " not found");
        }
        activeSubstanceRepository.deleteById(id);
    }
}
