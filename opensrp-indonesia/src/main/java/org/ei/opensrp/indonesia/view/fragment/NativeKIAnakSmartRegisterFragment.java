package org.ei.opensrp.indonesia.view.fragment;

import android.view.View;
import android.widget.ImageButton;

import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.provider.AnakRegisterClientsProvider;
import org.ei.opensrp.indonesia.service.formSubmissionHandler.AnakRegistrationHandler;
import org.ei.opensrp.indonesia.view.activity.NativeKIAnakSmartRegisterActivity;
import org.ei.opensrp.indonesia.view.contract.AnakClient;
import org.ei.opensrp.indonesia.view.controller.AnakRegisterController;
import org.ei.opensrp.indonesia.view.dialog.AllHighRiskSort;
import org.ei.opensrp.indonesia.view.dialog.AnakImmunizationServiceMode;
import org.ei.opensrp.indonesia.view.dialog.AnakOverviewServiceMode;
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
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.BAYI_IMUNISASI;

/**
 * Created by koros on 10/29/15.
 */
public class NativeKIAnakSmartRegisterFragment extends BidanSecuredNativeSmartRegisterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private AnakRegisterController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private DialogOptionMapper dialogOptionMapper;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AnakOverviewServiceMode(clientsProvider());
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
                return getResources().getString(R.string.child_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(controller.villages());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{new AnakOverviewServiceMode(clientsProvider()),
                        new AnakImmunizationServiceMode(clientsProvider())};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new ReverseNameSort(), new AllHighRiskSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ki_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if(clientProvider == null) {
            clientProvider = new AnakRegisterClientsProvider((NativeKIAnakSmartRegisterActivity)getActivity(), clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new AnakRegisterController(
                ((Context)context).allKohort(),
                context.alertService(),
                context.serviceProvidedService(),
                context.listCache(),
                context.smartRegisterClientsCache(),
                context.villagesCache());

        clientsProvider().onServiceModeSelected(new AnakOverviewServiceMode(clientsProvider()));
        dialogOptionMapper = new DialogOptionMapper();

        context.formSubmissionRouter().getHandlerMap()
                .put(AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION,
                        new AnakRegistrationHandler(((Context)context).kartuAnakService()));
    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
        ImageButton registerButton = (ImageButton) view.findViewById(org.ei.opensrp.R.id.register_client);
        registerButton.setVisibility(View.GONE);
    }

    @Override
    protected void startRegistration() {
        //
    }

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

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    showProfileView((AnakClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
                case R.id.immunization_service_mode_views:
                    ((NativeKIAnakSmartRegisterActivity)getActivity()).startFormActivity(BAYI_IMUNISASI, "" + view.getTag(), null);
                    break;
            }
        }

        public void showProfileView(AnakClient client) {
            ((NativeKIAnakSmartRegisterActivity)getActivity()).startDetailFragment(client.entityId());
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
            onShowDialogOptionSelection((EditOption)option, client, controller.getRandomNameChars(client));
        }
    }

    private DialogOption[] getEditOptions() {
        return ((NativeKIAnakSmartRegisterActivity)getActivity()).getEditOptions();
    }

}
