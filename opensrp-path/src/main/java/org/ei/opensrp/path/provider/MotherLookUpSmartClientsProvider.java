package org.ei.opensrp.path.provider;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class MotherLookUpSmartClientsProvider {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");


    public MotherLookUpSmartClientsProvider(Context context, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getView(CommonPersonObject commonPersonObject, List<CommonPersonObject> children, View convertView) {
        CommonPersonObjectClient pc = convert(commonPersonObject);

        List<CommonPersonObjectClient> childList = new ArrayList<CommonPersonObjectClient>();
        for (CommonPersonObject childCommonPersonObject : children) {
            childList.add(convert(childCommonPersonObject));
        }

        String childName = name(pc);

        fillValue((TextView) convertView.findViewById(R.id.name), childName);

        String birthDateString = "Birthdate missing";
        DateTime birthDateTime = dob(pc);
        if (birthDateTime != null) {
            birthDateString = dateFormat.format(birthDateTime.toDate());
        }


        String childListString = "";
        if (!childList.isEmpty()) {
            if (childList.size() > 1) {
                //sort youngest to oldest
                sortList(childList);
            }

            for (int i = 0; i < childList.size(); i++) {
                String name = name(childList.get(i));
                if (i == (childList.size() - 1)) {
                    childListString += name;
                } else {
                    childListString += name + ", ";
                }
            }

        }

        fillValue((TextView) convertView.findViewById(R.id.details), birthDateString + " - " + childListString);
    }


    public View inflatelayoutForCursorAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.mother_child_lookup_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    public static CommonPersonObjectClient convert(CommonPersonObject commonPersonObject) {
        CommonPersonObjectClient pc = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), commonPersonObject.getColumnmaps().get("first_name"));
        pc.setColumnmaps(commonPersonObject.getColumnmaps());
        return pc;
    }

    private void sortList(List<CommonPersonObjectClient> childList) {
        Collections.sort(childList, new Comparator<CommonPersonObjectClient>() {
            @Override
            public int compare(CommonPersonObjectClient lhs, CommonPersonObjectClient rhs) {
                DateTime lhsTime = dob(lhs);
                DateTime rhsTime = dob(rhs);

                if (lhsTime == null && rhsTime == null) {
                    return 0;
                } else if (lhsTime == null) {
                    return 1;
                } else if (rhsTime == null) {
                    return -1;
                }

                long diff = lhsTime.getMillis() - rhsTime.getMillis();
                if (diff > 0) {
                    return -1;
                } else if (diff < 0) {
                    return 1;
                }

                return 0;
            }
        });
    }

    private DateTime dob(CommonPersonObjectClient pc) {
        String dobString = getValue(pc.getColumnmaps(), "dob", false);
        if (StringUtils.isNotBlank(dobString)) {
            try {
                return new DateTime(dobString);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        return null;
    }

    private String name(CommonPersonObjectClient pc) {
        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
        return getName(firstName, lastName);
    }

}