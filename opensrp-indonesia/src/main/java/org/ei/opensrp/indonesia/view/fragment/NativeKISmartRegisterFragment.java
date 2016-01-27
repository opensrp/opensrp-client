package org.ei.opensrp.indonesia.view.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.provider.KIClientsProvider;
import org.ei.opensrp.indonesia.service.formSubmissionHandler.KIRegistrationHandler;
import org.ei.opensrp.indonesia.view.activity.NativeKISmartRegisterActivity;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.indonesia.view.controller.BidanVillageController;
import org.ei.opensrp.indonesia.view.controller.KartuIbuRegisterController;
import org.ei.opensrp.indonesia.view.dialog.AllHighRiskSort;
import org.ei.opensrp.indonesia.view.dialog.AllKartuIbuServiceMode;
import org.ei.opensrp.indonesia.view.dialog.EstimatedDateOfDeliverySortKI;
import org.ei.opensrp.indonesia.view.dialog.NoIbuSort;
import org.ei.opensrp.indonesia.view.dialog.ReverseNameSort;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_EDIT;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;

/**
 * Created by koros on 10/23/15.
 */
public class NativeKISmartRegisterFragment extends BidanSecuredNativeSmartRegisterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private KartuIbuRegisterController controller;
    private DialogOptionMapper dialogOptionMapper;
    private BidanVillageController villageController;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    public static final String locationDialogTAG = "locationDialogTAG";

    @Override
    protected void onCreation() {
        //error handling
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_kb_form), KOHORT_KB_PELAYANAN,
                        formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), KARTU_IBU_ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_edit_ki_form), KARTU_IBU_EDIT, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form),KARTU_IBU_CLOSE, formController),
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKartuIbuServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.ki_register_title_in_short);
            }
        };
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {

        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(villageController.getVillagesIndonesia());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(),
                        new ReverseNameSort(), new NoIbuSort(),
                        new EstimatedDateOfDeliverySortKI(), new AllHighRiskSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new KIClientsProvider(
                    getActivity(), clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KartuIbuRegisterController(
                ((Context)context).allKartuIbus(),
                context.listCache(),
                context.serviceProvidedService(),
                context.alertService(),
                ((Context)context).kiClientsCache(),
                ((Context)context).allKohort());
        villageController = new BidanVillageController(context.villagesCache(), ((Context)context).allKartuIbus());
        dialogOptionMapper = new DialogOptionMapper();
        context.formSubmissionRouter().getHandlerMap()
                .put(AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION,
                        new KIRegistrationHandler(((Context)context).kartuIbuService()));
    }

    @Override
    protected void startRegistration() {
        String uniqueIdJson = ((Context)context).uniqueIdController().getUniqueIdJson();
        if(uniqueIdJson == null || uniqueIdJson.isEmpty()) {
            Toast.makeText(getActivity(), "No Unique Id", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((SecuredNativeSmartRegisterActivity)getActivity(), new EditDialogOptionModel(), context.anmLocationController().get(), KARTU_IBU_REGISTRATION)
                .show(ft, locationDialogTAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryFacade.logEvent("kohort_ibu_dashboard");
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout_ki:
                    showProfileView((KartuIbuClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(KartuIbuClient kartuIbuClient) {
            ((NativeKISmartRegisterActivity)getActivity()).startDetailFragment(kartuIbuClient.entityId());
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            SmartRegisterClient client = (SmartRegisterClient) tag;

            if(option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form)) ) {
                if(controller.isMotherInANCorPNC(client.entityId())) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            onShowDialogOptionSelection((EditOption) option, client, controller.getRandomNameChars(client));
        }
    }

}
