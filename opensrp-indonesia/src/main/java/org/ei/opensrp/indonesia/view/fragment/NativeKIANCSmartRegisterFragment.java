package org.ei.opensrp.indonesia.view.fragment;

import android.view.View;
import android.widget.ImageButton;

import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.provider.KIANCClientsProvider;
import org.ei.opensrp.indonesia.view.activity.NativeKIANCSmartRegisterActivity;
import org.ei.opensrp.indonesia.view.contract.KIANCClient;
import org.ei.opensrp.indonesia.view.controller.BidanVillageController;
import org.ei.opensrp.indonesia.view.controller.KIANCRegisterController;
import org.ei.opensrp.indonesia.view.dialog.AllHighRiskSort;
import org.ei.opensrp.indonesia.view.dialog.EstimatedDateOfDeliverySortKIANC;
import org.ei.opensrp.indonesia.view.dialog.KIANCOverviewServiceMode;
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

/**
 * Created by koros on 10/29/15.
 */
public class NativeKIANCSmartRegisterFragment extends BidanSecuredNativeSmartRegisterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private KIANCRegisterController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private DialogOptionMapper dialogOptionMapper;
    private BidanVillageController villageController;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                return new KIANCOverviewServiceMode(clientsProvider());
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
                return getResources().getString(R.string.anc_label);
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
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new EstimatedDateOfDeliverySortKIANC(), new AllHighRiskSort()};
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
            clientProvider = new KIANCClientsProvider((NativeKIANCSmartRegisterActivity)getActivity(), clientActionHandler, controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        controller = new KIANCRegisterController(((Context)context).allKohort(), context.alertService(), context.serviceProvidedService(),
                context.listCache(), ((Context)context).kartuIbuANCClientsCache(), context.villagesCache());
        villageController = new BidanVillageController(context.villagesCache(), ((Context)context).allKartuIbus());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    protected void startRegistration() {
        //
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void setupViews(View view) {
        super.setupViews(view);
        ImageButton registerButton = (ImageButton) view.findViewById(org.ei.opensrp.R.id.register_client);
        registerButton.setVisibility(View.GONE);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout_ki:
                    showProfileView((KIANCClient) view.getTag());
                    break;
                case R.id.btn_edit:
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(KIANCClient ancClient) {
            ((NativeKIANCSmartRegisterActivity)getActivity()).startDetailFragment(ancClient.getKartuIbuCaseId());
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
            onShowDialogOptionSelection((EditOption) option, client, controller.getRandomNameChars(client));
        }
    }

    private DialogOption[] getEditOptions() {
        return ((NativeKIANCSmartRegisterActivity)getActivity()).getEditOptions();
    }
}
