package com.example.opensrp_household.household;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import com.example.opensrp_household.R;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.core.utils.ByColumnAndByDetails;
import org.ei.opensrp.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.Size;
import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.ageInYears;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.setProfiePic;


/**
 * Created by Safwan on 4/21/2016.
 */

public class HouseholdDetailFragment extends DetailFragment {
    ArrayAdapter<CommonPersonObject> arrayAdapter;
    List<HouseholdMemberDetails> memberDetails = new ArrayList<HouseholdMemberDetails>();

    @Override
    protected int layoutResId() {
        return R.layout.fragment_household_detail;
    }

    @Override
    protected String pageTitle() {
        return "Household Details";
    }

    @Override
    protected String titleBarId() {
        return client.getColumnmaps().get("existing_household_id");
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.household_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        if(client == null || client.getColumnmaps().get("gender") == null){
            return null;
        }

        String gender = client.getColumnmaps().get("gender");
        if (gender.equalsIgnoreCase("male")) {
            return R.drawable.household_profile;
        }
        else if (gender.toLowerCase().contains("trans")) {
            return R.drawable.general_intersex;
        }
        else if (gender.equalsIgnoreCase("female")) {
            return R.drawable.pk_woman_icon;
        }

        return null;
    }

    @Override
    protected String bindType() {
        return "pkhousehold";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }

    @Override
    protected void generateView() {
        memberDetails.clear();
        //todo
        ((TextView)  currentView.findViewById(org.ei.opensrp.core.R.id.details_id_label)).setText(getValue(client.getColumnmaps(), "household_id", true));

        TableLayout dt = (TableLayout) currentView.findViewById(R.id.household_detail_info_table1);
        dt.removeAllViews();
        android.content.Context context = getActivity().getApplicationContext();

        //setting value in Household basic information textviews
        List<CommonPersonObject> individualList = RegisterRepository.queryData("pkindividual", null, "relationalid = '" + client.getCaseId() + "'", null, "dob DESC");

        addRow(getActivity(), dt, "Household Member ID", getValue(client.getColumnmaps(), "household_member_id", true)+" ("+individualList.size()+" dependents)", Size.MEDIUM);
        addRow(getActivity(), dt, "Program Client ID", getValue(client.getColumnmaps(), "program_client_id", true), Size.MEDIUM);
        addRow(getActivity(), dt, "Name",
                StringUtils.capitalize(getValue(client.getColumnmaps(), "first_name", true)) +
                        " " + StringUtils.capitalize(getValue(client.getColumnmaps(), "last_name", false))
                        +" ("+getValue(client.getColumnmaps(), "gender", true)+")", Size.MEDIUM);

        int age = ageInYears(client, "dob", ByColumnAndByDetails.byColumn, true);
        addRow(getActivity(), dt, "DoB (Age)", convertDateFormat(age<0?"No DoB":getValue(client.getColumnmaps(), "dob", false), true)+" ("+age+ " years)", Size.MEDIUM);
        addRow(getActivity(), dt, "Ethnicity", getValue(client.getColumnmaps(), "ethnicity", true), Size.MEDIUM);
        addRow(getActivity(), dt, "Contact", getValue(client.getColumnmaps(), "contact_phone_number", true), Size.MEDIUM);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.household_detail_info_table2);
        dt2.removeAllViews();

        addRow(getActivity(), dt2, "Household ID", getValue(client.getColumnmaps(), "household_id", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Drinking Water Source", getValue(client.getColumnmaps(), "water_source", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Latrine System", getValue(client.getColumnmaps(), "latrine_system", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Address",
                getValue(client.getColumnmaps(), "address1", true) + ", \n" +
                        getValue(client.getColumnmaps(), "union_council", true).replace("Uc", "UC") + ", " +
                        getValue(client.getColumnmaps(), "town", true) + ",\n" +
                        getValue(client.getColumnmaps(), "city_village", true) + ", " +
                        getValue(client.getColumnmaps(), "province", true), Size.MEDIUM);

        setProfiePic(currentView.getContext(), (ImageView) currentView.findViewById(R.id.household_profilepic), "pkhousehold",  client.entityId(), null);

        int originalNum = VaccinatorUtils.totalHHMembers(client.getColumnmaps(), individualList);
        int unregistered = originalNum-individualList.size()-1;

        ((TextView)currentView.findViewById(R.id.hh_members_count)).setText(originalNum+" members");
        ((TextView)currentView.findViewById(R.id.hh_unregistered_count)).setText(unregistered+" unlisted");
        ((TextView)currentView.findViewById(R.id.hh_women_count)).setText(VaccinatorUtils.numOfWomenOfRepAge(individualList)+" el.women");
        ((TextView)currentView.findViewById(R.id.hh_children_count)).setText(VaccinatorUtils.numOfChildrenOfImmAge(individualList)+" child(ren)");

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, individualList);

        ListView list = (ListView) currentView.findViewById(R.id.individualList);

        TextView addMemberText = (TextView) currentView.findViewById(R.id.other_members);
        addMemberText.setText("(" + individualList.size() + ")");

        for (CommonPersonObject individual : individualList) {
            HouseholdMemberDetails member = new HouseholdMemberDetails();
            int memberAge = ageInYears(individual, "dob", ByColumnAndByDetails.byColumn, true);

            String gender = getValue(individual.getColumnmaps(), "gender", false);
            if (memberAge < 7 && gender.equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.infant_male);
            else if (memberAge < 7 && gender.equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.infant_female);
            else if (memberAge < 7 && gender.toLowerCase().contains("trans"))
                member.setMemberImageId(R.drawable.infant_intersex);
            else if (memberAge >= 7 && gender.equalsIgnoreCase("male")) {
                member.setMemberImageId(R.drawable.household_profile);
            }
            else if (memberAge >= 7 && gender.toLowerCase().contains("trans")) {
                member.setMemberImageId(R.drawable.general_intersex);
            }
            else if (memberAge >= 7 && gender.equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.pk_woman_icon);

            //refactor
            String programId = getValue(individual.getColumnmaps(), "program_client_id", false);
            member.setProgramId(programId);
            member.setMemberName(StringUtils.capitalize(getValue(individual.getColumnmaps(), "first_name", false)) + " " + StringUtils.capitalize(getValue(individual.getColumnmaps(), "last_name", false)));
            member.setMemberRelationWithHousehold(StringUtil.humanize(getValue(individual.getColumnmaps(), "relationship", true)));
            member.setMemberAge(convertDateFormat(getValue(individual.getColumnmaps(), "dob", true), true) + " (" + memberAge + " yr)");
            member.setMemberGender(getValue(individual.getColumnmaps(), "gender", false));
            member.setContact(getValue(individual.getColumnmaps(), "contact_phone_number", false));
            member.setClient(individual);

            member.setCantBeEnrolled(true);
            member.setAncEnrollment(false);//add
            if (memberAge <= 5) {
                member.setCantBeEnrolled(false);
            }
            else if (memberAge >= 15 && memberAge <= 49 && gender.equalsIgnoreCase("female")) {
                member.setAncEnrollment(true);//add
                member.setCantBeEnrolled(false);
            }

            List<CommonPersonObject> l = RegisterRepository.queryData("pkwoman", null, "id='"+individual.getCaseId()+"' OR program_client_id='" + programId + "'", null, null);
            l.addAll(RegisterRepository.queryData("pkchild", null, "id='"+individual.getCaseId()+"' OR  program_client_id='" + programId + "'", null, null));
            Log.d("caseID",individual.getCaseId());
            l.addAll(RegisterRepository.queryData("pkanc", null, "id='"+individual.getCaseId()+"' OR  program_client_id='" + programId + "'", null, null));

            if (l.size() > 0) {
                if (StringUtils.isBlank(programId)){
                    member.setProgramId(l.get(0).getColumnmaps().get("program_client_id"));
                }

                for(Map.Entry<String, String> e : l.get(0).getColumnmaps().entrySet()){
                    if(!member.getClient().getColumnmaps().containsKey(e.getKey())
                            && StringUtils.isNotBlank(e.getValue())) {
                        member.getClient().getColumnmaps().put(e.getKey(), e.getValue());
                    }
                }
                member.setMemberExists(true);
                member.setCantBeEnrolled(true);
                member.setAncEnrollment(false);//add
               //todo member.getClient().getDetails().putAll(l.get(0).getDetails());
            }
            /* todo handle from CE else if (StringUtils.isNotBlank(member.getProgramId())){
                try {
                    CESQLiteHelper.getClientEventDb(getActivity()).getClient(member.getProgramId());
                    List<Obs> cl = CESQLiteHelper.getClientEventDb(getActivity()).getObs(individual.getCaseId(), null, "eventId DESC", "vaccines_2");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/
            memberDetails.add(member);
        }
        list.setAdapter(new HouseholdMemberAdapter(this, context, memberDetails));
    }
}
