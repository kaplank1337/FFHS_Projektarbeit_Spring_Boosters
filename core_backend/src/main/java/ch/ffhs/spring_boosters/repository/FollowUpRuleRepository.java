package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.FollowUpRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowUpRuleRepository extends JpaRepository<FollowUpRule, UUID> {
    Optional<FollowUpRule> findByFromPlanIdAndMinCompletedDoses(UUID fromPlanId, Integer minCompletedDoses);
    List<FollowUpRule> findByFromPlanId(UUID fromPlanId);
}

