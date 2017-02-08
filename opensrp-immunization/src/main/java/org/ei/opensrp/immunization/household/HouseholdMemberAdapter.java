package org.ei.opensrp.immunization.household;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.utils.ByColumnAndByDetails;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.immunization.child.ChildSmartRegisterActivity;
import org.ei.opensrp.immunization.woman.WomanSmartRegisterActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.*;


/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberAdapter extends ArrayAdapter<HouseholdMemberDetails> implements Serializable {

    private final Context context;
    private final List<HouseholdMemberDetails> list;
    private final Fragment fragment;    

    public HouseholdMemberAdapter(Fragment fragment, Context context, List<HouseholdMemberDetails> list){
        super(context, R.layout.list_individual, list);
        this.fragment = fragment;
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_individual, parent, false);
        }

        View row = convertView;

        ((TextView) row.findViewById(R.id.serial_number)).setText((position+1)+" )");
        ((TextView) row.findViewById(R.id.programId)).setText(list.get(position).programId);
        ((TextView) row.findViewById(R.id.hhmemberId)).setText(list.get(position).client.getColumnmaps().get("household_member_id"));
        ((TextView) row.findViewById(R.id.memberName)).setText(list.get(position).memberName);
        ((TextView) row.findViewById(R.id.memberDate)).setText(list.get(position).memberAge);
        ((TextView) row.findViewById(R.id.relationship)).setText(list.get(position).memberRelationWithHousehold);
        ((ImageView) row.findViewById(R.id.individual_profilepic)).setImageResource(list.get(position).memberImageId);
        ((TextView) row.findViewById(R.id.hh_member_contact_number)).setText(list.get(position).contact);

        ImageView btnAction = (ImageView) row.findViewById(R.id.other_register_action);

        Log.v(getClass().getName(), "programId "+list.get(position).programId+
                "; hhmemberId "+list.get(position).client.getColumnmaps().get("household_member_id")+
                "; isMemberExists "+list.get(position).isMemberExists()+
                "; isCantBeEnrolled "+list.get(position).isCantBeEnrolled());

        if(list.get(position).isMemberExists()) {
            btnAction.setVisibility(View.VISIBLE);
        } else {
            btnAction.setVisibility(View.GONE);
            if(list.get(position).isCantBeEnrolled() == false){
                btnAction.setVisibility(View.VISIBLE);
            }
        }

        final int age = ageInYears(list.get(position).getClient(), "dob", ByColumnAndByDetails.byColumn, true);

        if(list.get(position).isMemberExists()) {
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonPersonObject person = list.get(position).getClient();

                    HashMap<String, String> map = new HashMap<>();
                    map.putAll(followupOverrides(person));

                    final Intent intent;
                    if (age >= 15 && person.getColumnmaps().get("gender").equalsIgnoreCase("female")
                            || person.getColumnmaps().get("gender").equalsIgnoreCase("f")) {
                        intent = new Intent(fragment.getActivity(), WomanSmartRegisterActivity.class);
                    } else {
                        intent = new Intent(fragment.getActivity(), ChildSmartRegisterActivity.class);
                    }

                    intent.putExtra("program_client_id", person.getColumnmaps().get("program_client_id"));

                    fragment.getActivity().startActivity(intent);
                    fragment.getActivity().finish();
                }
            });
        }
        else {
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (age <= 5) {
                        Toast.makeText(fragment.getActivity(), "Scan QR Coded EPI Card", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((HouseholdSmartRegisterFragment)((RegisterActivity)(fragment).getActivity()).getBaseFragment()).startChildRegistration(list.get(position).client.getCaseId(), list.get(position).getClient());
                            }
                        }, 0);
                    }
                    else if (age >= 15 && age <= 45 && list.get(position).client.getColumnmaps().get("gender").equalsIgnoreCase("female")) {
                        Toast.makeText(fragment.getActivity(), "Scan QR Coded EPI Card", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((HouseholdSmartRegisterFragment)((RegisterActivity)(fragment).getActivity()).getBaseFragment()).startWomanRegistration(list.get(position).client.getCaseId(), list.get(position).getClient());
                            }
                        }, 0);
                    }
                    else {
                        Toast.makeText(fragment.getActivity(), "Can not enroll person in any register", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        return row;
    }

    private Map<String, String> followupOverrides(CommonPersonObject client){
        Map<String, String> map = new HashMap<>();

        //map.put("relationalid", client.getCaseId());
        map.put("existing_first_name", getValue(client.getDetails(), "first_name", true));
        map.put("existing_last_name", getValue(client.getDetails(), "last_name", true));
        map.put("program_client_id", getValue(client.getColumnmaps(), "program_client_id", true));
        map.put("existing_union_councilname", getValue(client.getDetails(), "union_council", true));
        map.put("existing_townname", getValue(client.getDetails(), "town", true));
        map.put("existing_city_villagename", getValue(client.getDetails(), "city_village", true));
        map.put("existing_provincename", getValue(client.getDetails(), "province", true));
        map.put("existing_landmark", getValue(client.getDetails(), "landmark", true));
        map.put("existing_address1", getValue(client.getDetails(), "adderss1", true));

        return map;
    }
}
