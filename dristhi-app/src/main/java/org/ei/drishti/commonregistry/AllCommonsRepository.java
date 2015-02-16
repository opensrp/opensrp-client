package org.ei.drishti.commonregistry;

import org.ei.drishti.person.Person;
import org.ei.drishti.person.PersonRepository;
import org.ei.drishti.repository.AlertRepository;
import org.ei.drishti.repository.TimelineEventRepository;

import java.util.List;
import java.util.Map;

public class AllCommonsRepository {
    private PersonRepository personRepository;
    private final TimelineEventRepository timelineEventRepository;
    private final AlertRepository alertRepository;

    public AllCommonsRepository(PersonRepository personRepository, AlertRepository alertRepository, TimelineEventRepository timelineEventRepository) {
        this.personRepository = personRepository;
        this.timelineEventRepository = timelineEventRepository;
        this.alertRepository = alertRepository;
    }

    public List<Person> all() {
        return personRepository.allPerson();
    }

    public Person findByCaseID(String caseId) {
        return personRepository.findByCaseID(caseId);
    }

    public long count() {
        return personRepository.count();
    }




    public List<Person> findByCaseIDs(List<String> caseIds) {
        return personRepository.findByCaseIDs(caseIds.toArray(new String[caseIds.size()]));
    }



    public void close(String entityId) {
        alertRepository.deleteAllAlertsForEntity(entityId);
        timelineEventRepository.deleteAllTimelineEventsForEntity(entityId);
        personRepository.close(entityId);
    }

    public void mergeDetails(String entityId, Map<String, String> details) {
        personRepository.mergeDetails(entityId, details);
    }
}
