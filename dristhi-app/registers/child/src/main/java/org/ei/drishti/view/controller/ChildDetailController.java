package org.ei.drishti.view.controller;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import org.ei.drishti.AllConstants;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllBeneficiaries;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllTimelineEvents;
import org.ei.drishti.util.DateUtil;
import org.ei.drishti.util.TimelineEventComparator;
import org.ei.drishti.view.activity.CameraLaunchActivity;
import org.ei.drishti.view.contract.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ocpsoft.pretty.time.Duration;
import org.ocpsoft.pretty.time.PrettyTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.min;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.AllConstants.CHILD_TYPE;
import static org.ei.drishti.AllConstants.ENTITY_ID;

public class ChildDetailController {
    private final Context context;
    private final String caseId;
    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;
    private final AllTimelineEvents allTimelineEvents;

    public ChildDetailController(Context context, String caseId, AllEligibleCouples allEligibleCouples, AllBeneficiaries allBeneficiaries, AllTimelineEvents allTimelineEvents) {
        this.context = context;
        this.caseId = caseId;
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.allTimelineEvents = allTimelineEvents;
    }

    public String get() {
        Child child = allBeneficiaries.findChild(caseId);
        Mother mother = allBeneficiaries.findMother(child.motherCaseId());
        EligibleCouple couple = allEligibleCouples.findByCaseID(mother.ecCaseId());

        LocalDate deliveryDate = LocalDate.parse(child.dateOfBirth());
        String photoPath = isBlank(child.photoPath()) ?
                (AllConstants.FEMALE_GENDER.equalsIgnoreCase(child.gender())
                        ? AllConstants.DEFAULT_GIRL_INFANT_IMAGE_PLACEHOLDER_PATH
                        : AllConstants.DEFAULT_BOY_INFANT_IMAGE_PLACEHOLDER_PATH)
                : child.photoPath();

        ChildDetail detail = new ChildDetail(caseId, mother.thayiCardNumber(),
                new CoupleDetails(couple.wifeName(), couple.husbandName(), couple.ecNumber(), couple.isOutOfArea()),
                new LocationDetails(couple.village(), couple.subCenter()),
                new BirthDetails(deliveryDate.toString(), calculateAge(deliveryDate), child.gender()), photoPath)
                .addTimelineEvents(getEvents())
                .addExtraDetails(child.details());

        return new Gson().toJson(detail);
    }

    public void takePhoto() {
        Intent intent = new Intent(context, CameraLaunchActivity.class);
        intent.putExtra(AllConstants.TYPE, CHILD_TYPE);
        intent.putExtra(ENTITY_ID, caseId);
        context.startActivity(intent);
    }

    private List<TimelineEvent> getEvents() {
        List<org.ei.drishti.domain.TimelineEvent> events = allTimelineEvents.forCase(caseId);
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();

        Collections.sort(events, new TimelineEventComparator());

        for (org.ei.drishti.domain.TimelineEvent event : events) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-YYYY");
            timelineEvents.add(new TimelineEvent(event.type(), event.title(), new String[]{event.detail1(), event.detail2()}, event.referenceDate().toString(dateTimeFormatter)));
        }

        return timelineEvents;
    }

    private String calculateAge(LocalDate deliveryDate) {
        PrettyTime time = new PrettyTime(DateUtil.today().toDate());
        List<Duration> durationComponents = time.calculatePreciseDuration(deliveryDate.toDate());
        return time.format(durationComponents.subList(0, min(durationComponents.size(), 1))).replaceAll(" ago", "");
    }
}
