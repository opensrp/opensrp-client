package org.ei.opensrp.mcare.household;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.mcare.child.mCareChildSmartRegisterActivity;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.mcare.pnc.mCarePNCSmartRegisterActivity;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.activity.ANCSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class HouseholdDetailsSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public HouseholdDetailsSmartClientsProvider(Context context,
                                                View.OnClickListener onClickListener,
                                                CommonPersonObjectController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        txtColorBlack = context.getResources().getColor(R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView = viewGroup;

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if(pc.getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {

            itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_register_clients, null);
            TextView name = (TextView) itemView.findViewById(R.id.name);
            TextView age = (TextView) itemView.findViewById(R.id.age);
            TextView registerlink = (TextView) itemView.findViewById(R.id.registerlink);
//            registerlink.setPaintFlags(registerlink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);



            Button edit_form = (Button) itemView.findViewById(R.id.nidpic_capture);
            ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);
            if(pc.getDetails().get("profilepic")!=null){
                HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.womanimageload);
            }
            if (pc.getDetails().get("nidImage") != null) {
//               edit_form.setVisibility(View.INVISIBLE);
                try {
                    edit_form.setBackground(getDrawableFromPath(pc.getDetails().get("nidImage")));
                    edit_form.setText("");
                }catch (Exception e){

                }
            }
            registerlink.setOnClickListener(onClickListener);
            registerlink.setTag(smartRegisterClient);


            edit_form.setOnClickListener(onClickListener);
            edit_form.setTag(smartRegisterClient);

            profilepic.setOnClickListener(onClickListener);
            profilepic.setTag(smartRegisterClient);

            name.setText(humanize(pc.getColumnmaps().get("FWWOMFNAME") != null ? pc.getColumnmaps().get("FWWOMFNAME") : ""));

            age.setText("("+(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "")+")");
            DateUtil.setDefaultDateFormat("yyyy-MM-dd");
            try {
                int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("FWBIRTHDATE") != null ?  pc.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
                int calc_age = days / 365;
                age.setText("("+calc_age+")");
            }catch (Exception e){

            }

            LinearLayout child_parent_carrier = (LinearLayout)itemView.findViewById(R.id.child_parent_holder);
            ArrayList<String> stringList = new ArrayList<String>();
            stringList.add(pc.getCaseId());
            List <CommonPersonObject> commonPersonObjects = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByRelationalIDs(stringList);
            if(commonPersonObjects.size()>0) {
                CommonPersonObject mcaremother = commonPersonObjects.get(0);
                addchildrenifany(child_parent_carrier,mcaremother,smartRegisterClient);
            }
        }else{
            ArrayList<String> stringList = new ArrayList<String>();
            stringList.add(pc.getCaseId());
            List <CommonPersonObject> commonPersonObjects = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByRelationalIDs(stringList);
            if(commonPersonObjects.size()>0){
                CommonPersonObject mcaremother = commonPersonObjects.get(0);
                if(mcaremother.getDetails().get("FWWOMVALID").equalsIgnoreCase("1")) {
                    itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_register_clients, null);
                    TextView name = (TextView) itemView.findViewById(R.id.name);
                    TextView age = (TextView) itemView.findViewById(R.id.age);
                    TextView registerlink = (TextView) itemView.findViewById(R.id.registerlink);
//                    registerlink.setPaintFlags(registerlink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    if ((mcaremother.getColumnmaps().get("Is_PNC") != null ? mcaremother.getColumnmaps().get("Is_PNC") : "0").equalsIgnoreCase("0")) {
                        registerlink.setText("ANC Register");
                    }
                    if ((mcaremother.getColumnmaps().get("Is_PNC") != null ? mcaremother.getColumnmaps().get("Is_PNC") : "").equalsIgnoreCase("1")) {
                        registerlink.setText("PNC Register");
                    }


                    Button edit_form = (Button) itemView.findViewById(R.id.nidpic_capture);
                    ImageView profilepic = (ImageView) itemView.findViewById(R.id.profilepic);
                    if (pc.getDetails().get("profilepic") != null) {
                        HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.womanimageload);
                    }
                    if (pc.getDetails().get("nidImage") != null) {
//               edit_form.setVisibility(View.INVISIBLE);
                        try {
                            edit_form.setBackground(getDrawableFromPath(pc.getDetails().get("nidImage")));
                            edit_form.setText("");
                        } catch (Exception e) {

                        }
                    }
                    registerlink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(((TextView)v).getText().toString().contains("ANC")){
                                context.startActivity(new Intent(context, mCareANCSmartRegisterActivity.class));

                            }
                            if(((TextView)v).getText().toString().contains("PNC")){
                                context.startActivity(new Intent(context, mCarePNCSmartRegisterActivity.class));

                            }
                        }
                    });


                    edit_form.setOnClickListener(onClickListener);
                    edit_form.setTag(smartRegisterClient);

                    profilepic.setOnClickListener(onClickListener);
                    profilepic.setTag(smartRegisterClient);

                    name.setText(humanize(pc.getColumnmaps().get("FWWOMFNAME") != null ? pc.getColumnmaps().get("FWWOMFNAME") : ""));
                    age.setText("("+(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "")+")");
                    DateUtil.setDefaultDateFormat("yyyy-MM-dd");
                    try {
                        int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("FWBIRTHDATE") != null ?  pc.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
                        int calc_age = days / 365;
                        age.setText("("+calc_age+")");
                    }catch (Exception e){

                    }
                    LinearLayout child_parent_carrier = (LinearLayout)itemView.findViewById(R.id.child_parent_holder);
                    addchildrenifany(child_parent_carrier,mcaremother,smartRegisterClient);
                }else {

                    itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_nonregister_clients, null);
                    TextView name = (TextView) itemView.findViewById(R.id.name);
                    TextView age = (TextView) itemView.findViewById(R.id.age);
                    ImageView profilepic = (ImageView) itemView.findViewById(R.id.profilepic);

                    if (pc.getDetails().get("profilepic") != null) {
                        HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.womanimageload);
                    }

                    profilepic.setOnClickListener(onClickListener);
                    profilepic.setTag(smartRegisterClient);


                    Button editform = (Button) itemView.findViewById(R.id.edit_forms);
                    editform.setOnClickListener(onClickListener);
                    editform.setTag(smartRegisterClient);


                    name.setText(humanize(pc.getColumnmaps().get("FWWOMFNAME") != null ? pc.getColumnmaps().get("FWWOMFNAME") : ""));
                    age.setText("("+(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "")+")");
                    DateUtil.setDefaultDateFormat("yyyy-MM-dd");
                    try {
                        int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("FWBIRTHDATE") != null ?  pc.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
                        int calc_age = days / 365;
                        age.setText("("+calc_age+")");
                    }catch (Exception e){

                    }
                }
            }else {

                itemView = (ViewGroup) inflater().inflate(R.layout.household_inhabitants_nonregister_clients, null);
                TextView name = (TextView) itemView.findViewById(R.id.name);
                TextView age = (TextView) itemView.findViewById(R.id.age);
                ImageView profilepic = (ImageView) itemView.findViewById(R.id.profilepic);

                if (pc.getDetails().get("profilepic") != null) {
                    HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.womanimageload);
                }

                profilepic.setOnClickListener(onClickListener);
                profilepic.setTag(smartRegisterClient);


                Button editform = (Button) itemView.findViewById(R.id.edit_forms);
                editform.setOnClickListener(onClickListener);
                editform.setTag(smartRegisterClient);


                name.setText(humanize(pc.getColumnmaps().get("FWWOMFNAME") != null ? pc.getColumnmaps().get("FWWOMFNAME") : ""));
                age.setText("("+(pc.getDetails().get("FWWOMAGE") != null ? pc.getDetails().get("FWWOMAGE") : "")+")");

                DateUtil.setDefaultDateFormat("yyyy-MM-dd");
                try {
                    int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("FWBIRTHDATE") != null ?  pc.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
                    int calc_age = days / 365;
                    age.setText("("+calc_age+")");
                }catch (Exception e){

                }
            }
        }
//        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void addchildrenifany(LinearLayout child_parent_carrier, CommonPersonObject mcaremother,SmartRegisterClient smartRegisterClient) {
        ArrayList<String> listofmcaremother = new ArrayList<String>();
        listofmcaremother.add(mcaremother.getCaseId());
        List <CommonPersonObject> mcarechildren = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcarechild").findByRelationalIDs(listofmcaremother);
        for(int i = 0;i<mcarechildren.size();i++){
            Log.v("here is log!!", "children here");
            LinearLayout childrenLayout = (LinearLayout)inflater().inflate(R.layout.household_inhabitants_child_clients, null);
            ImageView childpic = (ImageView)childrenLayout.findViewById(R.id.profilepic_child);
            TextView childname = (TextView)childrenLayout.findViewById(R.id.childname);
            TextView age = (TextView)childrenLayout.findViewById(R.id.age);
            TextView childRegisterLink = (TextView)childrenLayout.findViewById(R.id.linktoregister);
            ImageView ischildrelation = (ImageView)childrenLayout.findViewById(R.id.child_relation_image);
            if(i != (mcarechildren.size()-1)){
                ischildrelation.setImageResource(R.mipmap.extended_is_child_of);
            }
            if(i == (mcarechildren.size()-1)){
                ischildrelation.setImageResource(R.mipmap.ischildof);
            }

//            childRegisterLink.setPaintFlags(childRegisterLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            childRegisterLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, mCareChildSmartRegisterActivity.class));

                }
            });
            age.setText("(" + childage(mcarechildren.get(i)) + " days)");

            childname.setText(mcarechildren.get(i).getDetails().get("FWBNFCHILDNAME") != null ? mcarechildren.get(i).getDetails().get("FWBNFCHILDNAME") : "");

            if(mcarechildren.get(i).getColumnmaps().get("FWBNFGEN")!=null){
                if(mcarechildren.get(i).getColumnmaps().get("FWBNFGEN").equalsIgnoreCase("1")){
                    childpic.setImageResource(R.drawable.child_boy_infant);
                    if (mcarechildren.get(i).getDetails().get("profilepic") != null) {
                        HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, mcarechildren.get(i).getDetails().get("profilepic"), childpic, R.drawable.child_boy_infant);
                    }
                }
                if(mcarechildren.get(i).getColumnmaps().get("FWBNFGEN").equalsIgnoreCase("2")){
                    childpic.setImageResource(R.drawable.child_girl_infant);
                    if (mcarechildren.get(i).getDetails().get("profilepic") != null) {
                        HouseHoldDetailActivity.setImagetoHolderFromUri((Activity) context, mcarechildren.get(i).getDetails().get("profilepic"), childpic, R.drawable.child_girl_infant);
                    }
                }
            }

            childpic.setOnClickListener(onClickListener);
            childpic.setTag(mcarechildren.get(i));

            child_parent_carrier.addView(childrenLayout);
//            TextView txt = (new TextView(context));
//            txt.setText("children");
//            child_parent_carrier.addView(txt);
        }
    }
    private Long childage(CommonPersonObject ancclient) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getDetails().get("FWBNFDOB")!=null?ancclient.getDetails().get("FWBNFDOB"):"");
            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(edd_date);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();

            long days = diff / (24 * 60 * 60 * 1000);

            return days;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }
    public Drawable getDrawableFromPath(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //Here you can make logic for decode bitmap for ignore oom error.
        return new BitmapDrawable(bitmap);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
