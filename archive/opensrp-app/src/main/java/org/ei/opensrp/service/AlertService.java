package org.ei.opensrp.service;

import org.ei.opensrp.domain.Alert;
import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.repository.AlertRepository;

import java.util.List;

public class AlertService {
    private AlertRepository repository;

    public AlertService(AlertRepository repository) {
        this.repository = repository;
    }

    public void create(Action action) {
        if (action.isActionActive() == null || action.isActionActive()) {
            repository.createAlert(new Alert(action.caseID(), action.get("scheduleName"), action.get("visitCode"),
                    AlertStatus.from(action.get("alertStatus")), action.get("startDate"), action.get("expiryDate")));
        }
    }

    public void create(Alert alert) {
        if (alert != null) {
            repository.createAlert(alert);
        }
    }

    public void close(Action action) {
        repository.markAlertAsClosed(action.caseID(), action.get("visitCode"), action.get("completionDate"));
    }

    public void deleteAll(Action action) {
        repository.deleteAllAlertsForEntity(action.caseID());
    }

    public List<Alert> findByEntityIdAndAlertNames(String entityId, String... names) {
        return repository.findByEntityIdAndAlertNames(entityId, names);
    }

    public void changeAlertStatusToInProcess(String entityId, String alertName) {
        repository.changeAlertStatusToInProcess(entityId, alertName);
    }
    public void changeAlertStatusToComplete(String entityId, String alertName) {
        repository.changeAlertStatusToComplete(entityId, alertName);
    }
}
