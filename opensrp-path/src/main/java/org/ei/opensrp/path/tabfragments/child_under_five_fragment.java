package org.ei.opensrp.path.tabfragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.SyncStatus;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildDetailTabbedActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.fragment.VaccinationEditDialogFragment;
import org.ei.opensrp.path.repository.BaseRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.ei.opensrp.path.viewComponents.ImmunizationRowGroup;
import org.ei.opensrp.path.viewComponents.WidgetFactory;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.DateUtils;
import util.Utils;
import util.VaccinateActionUtils;


public class child_under_five_fragment extends Fragment  {

    private LayoutInflater inflater;
    private ViewGroup container;
    private LinearLayout fragmentcontainer;
    private List<Vaccine> vaccineList;
    private CommonPersonObjectClient childDetails;
    private Map<String,String> detailmaps;
    private ArrayList<ImmunizationRowGroup> vaccineGroups;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String VACCINES_FILE = "vaccines.json";
    private static final String DIALOG_TAG = "ChildImmunoActivity_DIALOG_TAG";
    private Map<String, String> Detailsmap;
    private AlertService alertService;
    private List<Alert> alertList;
    private VaccineRepository vaccineRepository;

    public child_under_five_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        if (this.getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(ChildDetailTabbedActivity.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = (CommonPersonObjectClient) serializable;
            }
        }
        View fragmenttwo = inflater.inflate(R.layout.child_under_five_fragment, container, false);
         fragmentcontainer = (LinearLayout)fragmenttwo.findViewById(R.id.container);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
        vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
        alertService = Context.getInstance().alertService();

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        Detailsmap  = detailsRepository.getAllDetailsForClient(childDetails.entityId());

        loadview(false,false);
//        fragmentcontainer.addView(wd.createImmunizationWidget(inflater,container,vaccineList,false));


        // Inflate the layout for this fragment
        return fragmenttwo;
    }
    public void loadview(boolean editmode,boolean editweightmode){
//        View fragmenttwo = inflater.inflate(R.layout.child_under_five_fragment, container, false);
//        LinearLayout fragmentcontainer = (LinearLayout)fragmenttwo.findViewById(R.id.container);
        if(fragmentcontainer != null) {
            fragmentcontainer.removeAllViews();
            fragmentcontainer.addView(createPTCMTVIEW("PMTCT: ", Utils.getValue(childDetails.getColumnmaps(), "pmtct_status", true)));
//        weightmap.put("9 m","8.4");
//        weightmap.put("8 m","7.5 Kg");
//        weightmap.put("7 m","6.7 Kg");
//        weightmap.put("6 m","5.6 Kg");
//        weightmap.put("5 m","5.0 Kg");
        createWeightLayout(fragmentcontainer, editweightmode);
            View view = new View(getActivity());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, Context.getInstance().applicationContext().getResources().getDisplayMetrics());

            LinearLayout.LayoutParams barlayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            barlayout.setMargins(0, 10, 0, 10);
            view.setBackgroundColor(getResources().getColor(R.color.white));
            fragmentcontainer.addView(view, barlayout);
//        fragmentcontainer.addView(wd.createImmunizationWidget(inflater,container,new ArrayList<Vaccine>(),true));
            updateVaccinationViews(fragmentcontainer, editmode);
        }
//        fragmentcontainer.addView(wd.createImmunizationWidget(inflater,container,vaccineList,true));



    }

    private void createWeightLayout(LinearLayout fragmentcontainer, boolean editmode) {
        LinkedHashMap<String, String> weightmap = new LinkedHashMap<>();
        ArrayList<Boolean> weighteditmode = new ArrayList<Boolean>();
        ArrayList<View.OnClickListener> listeners = new ArrayList<View.OnClickListener>();

        WeightRepository wp = VaccinatorApplication.getInstance().weightRepository();
        List<Weight> weightlist = wp.findLast5(childDetails.entityId());
        ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(getActivity());


        for (int i = 0; i < weightlist.size(); i++) {
//            String formattedDob = "";
            String formattedAge = "";
            if (weightlist.get(i).getDate() != null) {

                Date weighttaken = weightlist.get(i).getDate();
                ;
//                formattedDob = DATE_FORMAT.format(weighttaken);
                String birthdate = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
                DateTime birthday = new DateTime(birthdate);
                Date birth = birthday.toDate();
                long timeDiff = weighttaken.getTime() - birth.getTime();
                Log.v("timeDiff is ",timeDiff+"");
                if (timeDiff >= 0) {
                    formattedAge = DateUtils.getDuration(timeDiff);
                    Log.v("age is ",formattedAge);
                }
            }
            if(!formattedAge.equalsIgnoreCase("0d")) {
                weightmap.put(formattedAge, weightlist.get(i).getKg() + " kg");

                ////////////////////////check 3 months///////////////////////////////
                boolean less_than_three_months_event_created = false;
                Weight weight = weightlist.get(i);
                org.ei.opensrp.path.db.Event event = null;
                PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();
                if(weight.getEventId()!=null){
                    event = ecUpdater.convert(db.getEventsByEventId(weight.getEventId()), org.ei.opensrp.path.db.Event.class);
                }else if (weight.getFormSubmissionId() != null){
                    event = ecUpdater.convert(db.getEventsByFormSubmissionId(weight.getFormSubmissionId()), org.ei.opensrp.path.db.Event.class);
                }
                if(event!=null) {
                    Date weight_create_date = event.getDateCreated().toDate();
                    if (!ChildDetailTabbedActivity.check_if_date_three_months_older(weight_create_date)) {
                        less_than_three_months_event_created = true;
                    }
                }else{
                    less_than_three_months_event_created = true;
                }
                ///////////////////////////////////////////////////////////////////////
                if(less_than_three_months_event_created) {
                    weighteditmode.add(editmode);
                }else{
                    weighteditmode.add(false);
                }

                final int finalI = i;
                View.OnClickListener onclicklistener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((ChildDetailTabbedActivity) getActivity()).showWeightDialog(finalI);
                    }
                };
                listeners.add(onclicklistener);
            }

        }
        if (weightmap.size() < 5) {
            weightmap.put(DateUtils.getDuration(0), Utils.getValue(Detailsmap, "Birth_Weight", true) + " kg");
            weighteditmode.add(false);
            listeners.add(null);
        }


//        weightlist.size();
        WidgetFactory wd = new WidgetFactory();
        if (weightmap.size() > 0) {
            fragmentcontainer.addView(wd.createWeightWidget(inflater, container, weightmap,listeners,weighteditmode));
        }
    }

    private View createPTCMTVIEW(String labelString,String valueString) {
        View rows = inflater.inflate(R.layout.tablerows_ptcmt, container, false);
        TextView label = (TextView)rows.findViewById(R.id.label);
        TextView value = (TextView)rows.findViewById(R.id.value);

        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }

    private void updateVaccinationViews(ViewGroup v,boolean editmode) {
        if (vaccineGroups != null) {
            vaccineGroups.clear();
        }

            vaccineGroups = new ArrayList<>();
            vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
            LinearLayout vaccineGroupCanvasLL = new LinearLayout(getActivity());
            vaccineGroupCanvasLL.setOrientation(LinearLayout.VERTICAL);
            v.addView(vaccineGroupCanvasLL,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            CustomFontTextView title = new CustomFontTextView(getActivity());
            title.setAllCaps(true);
            title.setTextAppearance(getActivity(),  android.R.style.TextAppearance_Medium);
            title.setTextColor(getResources().getColor(R.color.text_black));
            title.setText("Immunisations");
            vaccineGroupCanvasLL.addView(title);
        if (alertService != null) {
            alertList = alertService.findByEntityIdAndAlertNames(childDetails.entityId(),
                    VaccinateActionUtils.allAlertNames("child"));
        }

        String supportedVaccinesString = readAssetContents(VACCINES_FILE);
            try {
                JSONArray supportedVaccines = new JSONArray(supportedVaccinesString);
                for (int i = 0; i < supportedVaccines.length(); i++) {
                    ImmunizationRowGroup curGroup = new ImmunizationRowGroup(getActivity(),editmode);
                    curGroup.setData(supportedVaccines.getJSONObject(i), childDetails, vaccineList,alertList);
                    curGroup.setOnVaccineUndoClickListener(new ImmunizationRowGroup.OnVaccineUndoClickListener() {
                        @Override
                        public void onUndoClick(ImmunizationRowGroup vaccineGroup, VaccineWrapper vaccine) {
                            addVaccinationDialogFragment(Arrays.asList(vaccine), vaccineGroup);

                        }
                    });
//                    curGroup.setOnRecordAllClickListener(new VaccineGroup.OnRecordAllClickListener() {
//                        @Override
//                        public void onClick(VaccineGroup vaccineGroup, ArrayList<VaccineWrapper> dueVaccines) {
////                            addVaccinationDialogFragment(dueVaccines, vaccineGroup);
//                        }
//                    });
//                    curGroup.setOnVaccineClickedListener(new VaccineGroup.OnVaccineClickedListener() {
//                        @Override
//                        public void onClick(VaccineGroup vaccineGroup, VaccineWrapper vaccine) {
////                            addVaccinationDialogFragment(Arrays.asList(vaccine), vaccineGroup);
//                        }
//                    });
//                    curGroup.setOnVaccineUndoClickListener(new VaccineGroup.OnVaccineUndoClickListener() {
//                        @Override
//                        public void onUndoClick(VaccineGroup vaccineGroup, VaccineWrapper vaccine) {
////                            addVaccineUndoDialogFragment(vaccineGroup, vaccine);
//                        }
//                    });
                    vaccineGroupCanvasLL.addView(curGroup);
                    vaccineGroups.add(curGroup);
                }
            } catch (JSONException e) {
//                Log.e(TAG, Log.getStackTraceString(e));
            }

    }
    private String readAssetContents(String path) {
        String fileContents = null;
        try {
            InputStream is = getActivity().getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
//            android.util.Log.e(TAG, ex.toString(), ex);
        }

        return fileContents;
    }


    public void addVaccinationDialogFragment(List<VaccineWrapper> vaccineWrappers, ImmunizationRowGroup vaccineGroup) {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        android.app.Fragment prev =  getActivity().getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        VaccinationEditDialogFragment vaccinationDialogFragment = VaccinationEditDialogFragment.newInstance(getActivity(), vaccineWrappers, vaccineGroup);
        vaccinationDialogFragment.show(ft, DIALOG_TAG);
    }
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }

}
