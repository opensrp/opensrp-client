package org.ei.opensrp.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View;

import org.ei.opensrp.R;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.provider.ECSmartRegisterClientsProvider;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.ECSmartRegisterController;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.AllEligibleCoupleServiceMode;
import org.ei.opensrp.view.dialog.BPLSort;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.ECNumberSort;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.HighPrioritySort;
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.dialog.SCSort;
import org.ei.opensrp.view.dialog.STSort;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.ei.opensrp.AllConstants.FormNames.ANC_REGISTRATION;
import static org.ei.opensrp.AllConstants.FormNames.CHILD_REGISTRATION_EC;
import static org.ei.opensrp.AllConstants.FormNames.EC_CLOSE;
import static org.ei.opensrp.AllConstants.FormNames.EC_EDIT;
import static org.ei.opensrp.AllConstants.FormNames.EC_REGISTRATION;
import static org.ei.opensrp.AllConstants.FormNames.FP_CHANGE;

public class NativeECSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private ECSmartRegisterController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
    public static final String locationDialogTAG = "locationDialogTAG";
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllEligibleCoupleServiceMode(clientsProvider());
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
                return getResources().getString(R.string.ec_register_title_in_short);
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(villageController.getVillages());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new ECNumberSort(),
                        new HighPrioritySort(), new BPLSort(),
                        new SCSort(), new STSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ec_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new ECSmartRegisterClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_anc_form), ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_register_fp_form), FP_CHANGE, formController),
                new OpenFormOption(getString(R.string.str_register_child_form), CHILD_REGISTRATION_EC, formController),
                new OpenFormOption(getString(R.string.str_edit_ec_form), EC_EDIT, formController),
                new OpenFormOption(getString(R.string.str_close_ec_form), EC_CLOSE, formController),
        };
    }

    @Override
    protected void onInitialization() {
        controller = new ECSmartRegisterController(context.allEligibleCouples(),
                context.allBeneficiaries(), context.listCache(),
                context.ecClientsCache());
        villageController = new VillageController(context.allEligibleCouples(),
                context.listCache(), context.villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    public void setupViews() {
        super.setupViews();

        setServiceModeViewDrawableRight(null);
    }

    @Override
    public void startRegistration() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
            LocationSelectorDialogFragment
                .newInstance(this, new EditDialogOptionModel(), context.anmLocationController().get(),EC_REGISTRATION)
                .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.profile_info_layout) {
                showProfileView((ECClient) view.getTag());

            } else if (i == R.id.btn_edit) {
                showFragmentDialog(new EditDialogOptionModel(), view.getTag());

            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
}
