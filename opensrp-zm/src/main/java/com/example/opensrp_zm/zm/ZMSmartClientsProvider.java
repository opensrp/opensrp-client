package com.example.opensrp_zm.zm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.opensrp_child.child.ChildSmartRegisterActivity;
import com.example.opensrp_household.household.HouseholdSmartRegisterActivity;
import com.example.opensrp_woman.woman.WomanSmartRegisterActivity;
import com.example.opensrp_zm.R;
import com.google.common.base.Joiner;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.domain.Address;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.domain.Event;
import org.ei.opensrp.core.db.domain.Obs;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.core.widget.RadioAlertView;
import org.ei.opensrp.service.AlertService;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.isEligibleWoman;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.profilePicIcon;
import static org.ei.opensrp.core.db.repository.RegisterRepository.queryData;
import static org.ei.opensrp.core.utils.Utils.fillValue;
import static org.ei.opensrp.core.utils.Utils.formatValue;
import static org.ei.opensrp.core.utils.Utils.getColor;

/**
 * Created by Ahmed on 19-Oct-15.
 *
 * @author Maimoona
 */
public class ZMSmartClientsProvider implements RegisterClientsProvider<ClientEvent> {


    private final LayoutInflater inflater;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final ZMSmartRegisterFragment registerDataGridFragment;

    public ZMSmartClientsProvider(ZMSmartRegisterFragment registerDataGridFragment, View.OnClickListener onClickListener,
                                  AlertService alertService) {
        this.onClickListener = onClickListener;
        this.registerDataGridFragment = registerDataGridFragment;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) registerDataGridFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) registerDataGridFragment.getResources().getDimension(org.ei.opensrp.core.R.dimen.list_item_height));
    }

    @Override
    public View getView(final ClientEvent pc, final View convertView, ViewGroup viewGroup) {
        convertView.setLayoutParams(clientViewLayoutParams);

        final FragmentActivity context = registerDataGridFragment.getActivity();

        // iop9;fillValue((TextView)convertView.findViewById(R.id.identifier), Joiner.on("\n").join(pc.getClient().getIdentifiers().values()));

        fillValue((TextView) convertView.findViewById(R.id.name), formatValue(pc.getClient().fullName(), true));

        String husband = pc.findObsValue(null, true, "husband_name");
        final String father = pc.findObsValue(null, true, "father_name");

        String guardian = "";
        if (StringUtils.isNotBlank(husband)) {
            guardian = ("W/O- " + husband);
        } else if (StringUtils.isNotBlank(father)) {
            guardian = "C/O- " + father;
        }

        fillValue((TextView) convertView.findViewById(R.id.guardian_name), formatValue(guardian, true));

        int age = -1;
        try {
            age = Years.yearsBetween(pc.getClient().getBirthdate(), DateTime.now()).getYears();
        } catch (Exception e) {
        }
        DateTime birthDate = pc.getClient().getBirthdate();

        fillValue((TextView) convertView.findViewById(R.id.age), birthDate == null ? "No DoB" : birthDate.toString("dd/MM/yyyy"));

       /* fillValue((TextView) convertView.findViewById(R.id.contact_number), pc.findObsValue(null, true, "contact_phone_number"));*/

      /*  Address ad = pc.getClient().getAddress("usual_residence");
        fillValue((TextView) convertView.findViewById(R.id.address), ad==null?"":
                Joiner.on(", ").join(
                        formatValue(ad.getAddressField("address1"), true),
                        formatValue(ad.getSubTown(), true),
                        formatValue(ad.getTown(), true),
                        formatValue(ad.getCityVillage(), true)));*/

        String lastEvent = "";
        for (Event e : pc.getEvents()) {
            if(e.getEventType()!=null){
                lastEvent += "* " + e.getEventType().replaceAll("(?i)\\sform", "") + "<br/>";
            }
        }

        fillValue((TextView) convertView.findViewById(R.id.last_events), lastEvent);

        ImageView profileCont = (ImageView) convertView.findViewById(R.id.profilepic);

        int profilePic = profilePicIcon(age, pc.getClient().getGender());

        if (profilePic != -1) {
            profileCont.setImageResource(profilePic);
        } else profileCont.setImageResource(R.drawable.profile_user);

        boolean isHHH = pc.getClient().getAttributes().toString().matches("(?i).*household.head.*");

        if (isHHH) {
            convertView.findViewById(R.id.hhh_flag).setVisibility(View.VISIBLE);
        } else convertView.findViewById(R.id.hhh_flag).setVisibility(View.GONE);

        final List<Obs> ol = new ArrayList<>();
        for (Event e : pc.getEvents()) {
            ol.addAll(e.getObs());
        }

       /* final TableLayout t = (TableLayout) convertView.findViewById(R.id.obs_table);
        t.removeAllViews();
        t.setOnClickListener(null);

        fillObs(t, pc.getClient());
        fillObs(t, ol);

       if (t.getChildCount() > 0) {
        convertView.findViewById(R.id.obs_container).setBackgroundColor(getColor(context, R.color.transparent_blue));*/
        convertView.findViewById(R.id.obs_container).setBackgroundColor(getColor(context, R.color.dull_white));
        final String finalGuardian = guardian;
        final int finalProfilePic = profilePic;
        LinearLayout li = (LinearLayout) convertView.findViewById(R.id.obs_container);
        li.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ald = new AlertDialog.Builder(context);

                View tl = context.getLayoutInflater().inflate(R.layout.obs_list, null);
                Address ad = pc.getClient().getAddress("usual_residence");
                ((TextView) tl.findViewById(R.id.address)).setText(ad == null ? "" :
                        Joiner.on(", ").join(
                                formatValue(ad.getAddressField("address1"), true),
                                formatValue(ad.getSubTown(), true),
                                formatValue(ad.getTown(), true),
                                formatValue(ad.getCityVillage(), true)));
                ((TextView) tl.findViewById(R.id.contact_number)).setText(pc.findObsValue(null, true, "contact_phone_number"));
                ((TextView) tl.findViewById(R.id.name)).setText(pc.getClient().fullName());
                ((TextView) tl.findViewById(R.id.guardian_name)).setText(finalGuardian);
                ((TextView) tl.findViewById(R.id.age)).setText(getAgeString(pc.getClient().getBirthdate()));
                if (finalProfilePic != -1) {
                    ((ImageView) tl.findViewById(R.id.profile_pic)).setImageResource(finalProfilePic);
                }

                TableLayout tb = (TableLayout) tl.findViewById(R.id.obs_table);
                tb.removeAllViews();

                fillObs(tb, pc.getClient());
                fillObs(tb, ol);

                ald.setView(tl);
                ald.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ald.show();
            }
        });
       /* } else {
            convertView.findViewById(R.id.obs_container).setBackgroundColor(getColor(context, R.color.dull_white));
        }*/

        convertView.findViewById(R.id.register_household).setVisibility(View.GONE);
        convertView.findViewById(R.id.register_vaccination).setVisibility(View.GONE);

        // is a woman
        if (isEligibleWoman(pc.getClient())) {
            setupRegisterActions(convertView, R.id.register_vaccination, "pkwoman", "(?i).*tt\\s+.*(enrollment)|follow.*up.*", "Woman", pc, WomanSmartRegisterActivity.class);
        }
        // is a child
        else if (age >= 0 && age <= 5) {
            fillValue((TextView) convertView.findViewById(R.id.age), getAgeString(pc.getClient().getBirthdate()));

            setupRegisterActions(convertView, R.id.register_vaccination, "pkchild", "(?i).*child\\s+.*(enrollment)|follow.*up.*", "Child", pc, ChildSmartRegisterActivity.class);
        }

        try {
            if ((Utils.providerDetails().has("permissions")
                    && Utils.providerDetails().getJSONArray("permissions").toString().toLowerCase().contains("view household register"))
                    && pc.getClient().getAttribute("Household ID") != null
                    && queryData("pkhousehold", null, "household_id='" + pc.getClient().getAttribute("Household ID") + "' OR id='" + pc.getClient().getBaseEntityId() + "'", null, null).size() > 0) {

                convertView.findViewById(R.id.register_household).setVisibility(View.VISIBLE);

                openExistingRecord(convertView, R.id.register_household, HouseholdSmartRegisterActivity.class, "household_id", (String) pc.getClient().getAttribute("Household ID"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //convertView.setTag(viewHolder);


        return convertView;
    }

    private String getAgeString(DateTime birthdate) {
        if (birthdate == null) {
            return "No DoB";
        }

        if (birthdate.plusYears(5).isAfterNow()) {
            int age = Months.monthsBetween(birthdate, DateTime.now()).getMonths();
            return birthdate.toString("dd/MM/yyyy") + " (" + age + " M)";
        }

        int age = Years.yearsBetween(birthdate, DateTime.now()).getYears();
        return birthdate.toString("dd/MM/yyyy") + " (" + age + " Y)";
    }

    private void openExistingRecord(View convertView, int buttonId, final Class registerActivity, final String idProperty, final String id) {
        final FragmentActivity context = registerDataGridFragment.getActivity();
        convertView.findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, registerActivity);
                intent.putExtra(idProperty, id);
                context.startActivity(intent);
                context.finish();
            }
        });
    }

    private void setupRegisterActions(View convertView, int actionButtonId, String registerBindType, String eventMatcher, final String entityName,
                                      final ClientEvent pc, Class registerActivity) {
        convertView.findViewById(actionButtonId).setVisibility(View.VISIBLE);

        final List<CommonPersonObject> entity;
        if ((entity = queryData(registerBindType, null, "id='" + pc.getClient().getBaseEntityId() + "'", null, null)).size() > 0) {
            openExistingRecord(convertView, actionButtonId, registerActivity, "program_client_id", entity.get(0).getColumnmaps().get("program_client_id"));
        } else {
            String message = "N/A";
            Map<String, View.OnClickListener> map = new HashMap<>();

            if (pc.getLatestEvent(eventMatcher) != null) {
                message = entityName + " is enrolled into another center with Program Client ID " + pc.getClient().getIdentifier("Program Client ID") + ". " +
                        "Do you want to fill Offsite Followup form and enroll person into your vaccination register?";
                map.put("Yes, fill Offsite Followup form", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDataGridFragment.startOffsiteFollowupForm(entityName.toLowerCase() + "_offsite_followup", pc.getClient().getBaseEntityId(), pc);
                    }
                });
            } else {
                message = entityName + " is not enrolled in your register. Do you want to enroll person into vaccination register? " +
                        "If so, provide an EPI Card and choose option below to assign Program Client ID: ";
                map.put("Scan QR code ID", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDataGridFragment.startEnrollmentForm(entityName.toLowerCase() + "_enrollment", pc.getClient().getBaseEntityId(), pc, true);
                    }
                });
                map.put("Manually enter Program ID", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        registerDataGridFragment.startEnrollmentForm(entityName.toLowerCase() + "_enrollment", pc.getClient().getBaseEntityId(), pc, false);
                    }
                });
            }
            final RadioAlertView rav = new RadioAlertView(registerDataGridFragment.getActivity(), "Alert",
                    message, "Vaccinate", "Cancel", map);
            convertView.findViewById(actionButtonId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rav.show();
                }
            });
        }
    }

    private void fillRow(TableLayout table, String label, List<Obs> ol, String... fields) {
        for (String s : fields) {
            for (Obs o : ol) {
                if (StringUtils.isNotBlank(o.getFormSubmissionField())
                        && o.getFormSubmissionField().equalsIgnoreCase(s)
                        && StringUtils.isNotBlank(o.getValue(true))) {
                    Utils.addRow(registerDataGridFragment.getActivity(), table, label, o.getValue(true), Utils.Size.SMALL);
                    return;
                }
            }
        }
    }

    private void fillObs(TableLayout t, Client c) {
        if (c.getIdentifier("Program Client ID") != null) {
            Utils.addRow(registerDataGridFragment.getActivity(), t, "PID", c.getIdentifier("Program Client ID"), Utils.Size.SMALL);
        }
        if (c.getIdentifier("Household ID") != null) {
            Utils.addRow(registerDataGridFragment.getActivity(), t, "HHID", c.getIdentifier("Household ID"), Utils.Size.SMALL);
        } else if (c.getAttribute("Household ID") != null) {
            Utils.addRow(registerDataGridFragment.getActivity(), t, "HHID", c.getAttribute("Household ID").toString(), Utils.Size.SMALL);
        }
    }

    private void fillObs(TableLayout t, List<Obs> ol) {
        fillRow(t, "TT5", ol, "tt5", "tt5_retro");
        fillRow(t, "TT4", ol, "tt4", "tt4_retro");
        fillRow(t, "TT3", ol, "tt3", "tt3_retro");
        fillRow(t, "TT2", ol, "tt2", "tt2_retro");
        fillRow(t, "TT1", ol, "tt1", "tt1_retro");

        fillRow(t, "Married", ol, "marriage");
        fillRow(t, "EDD", ol, "final_edd");

        fillRow(t, "Measles2", ol, "measles2", "measles2_retro");
        fillRow(t, "Measles1", ol, "measles1", "measles1_retro");
        fillRow(t, "Penta3", ol, "penta3", "penta3_retro");
        fillRow(t, "Penta2", ol, "penta2", "penta2_retro");
        fillRow(t, "Penta1", ol, "penta1", "penta1_retro");
        fillRow(t, "BCG", ol, "bcg", "bcg_retro");
    }

    @Override
    public List<ClientEvent> getClients() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public View inflateLayoutForAdapter() {
        return inflater().inflate(R.layout.smart_register_zm_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }

}
