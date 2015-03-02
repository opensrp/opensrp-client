package org.ei.drishti.mamoni.pw;

import android.util.Log;
import android.view.View;

import org.ei.drishti.R;
import org.ei.drishti.adapter.SmartRegisterPaginatedAdapter;
import org.ei.drishti.commonregistry.PersonObjectController;
import org.ei.drishti.mamoni.elco.MamoniEligibleCoupleClientsProvider;
import org.ei.drishti.mamoni.elco.MamoniModeOption;
import org.ei.drishti.provider.SmartRegisterClientsProvider;
import org.ei.drishti.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.drishti.view.contract.ECClient;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.ei.drishti.view.controller.VillageController;
import org.ei.drishti.view.dialog.AllClientsFilter;
import org.ei.drishti.view.dialog.BPLSort;
import org.ei.drishti.view.dialog.DialogOption;
import org.ei.drishti.view.dialog.DialogOptionMapper;
import org.ei.drishti.view.dialog.DialogOptionModel;
import org.ei.drishti.view.dialog.ECNumberSort;
import org.ei.drishti.view.dialog.EditOption;
import org.ei.drishti.view.dialog.FilterOption;
import org.ei.drishti.view.dialog.HighPrioritySort;
import org.ei.drishti.view.dialog.NameSort;
import org.ei.drishti.view.dialog.OpenFormOption;
import org.ei.drishti.view.dialog.SCSort;
import org.ei.drishti.view.dialog.STSort;
import org.ei.drishti.view.dialog.ServiceModeOption;
import org.ei.drishti.view.dialog.SortOption;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;

public class MamoniANCRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private SmartRegisterClientsProvider clientProvider = null;
    private PersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

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
                return new MamoniANCModeOption(clientsProvider());
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
            clientProvider = new MamoniANCClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    private DialogOption[] getEditOptions() {
        return new DialogOption[]{

                new OpenFormOption("Register Pregnant Woman", "mamoni_register_pw", formController),
                new OpenFormOption("Register Death", "death_registration", formController),
                new OpenFormOption("Edit ELCO", "elco_registration", formController)
        };
    }

    @Override
    protected void onInitialization() {
        controller = new PersonObjectController(context.allCommonsRepositoryobjects("mamoni_pregnant_woman"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(),"PW_Name","mamoni_pregnant_woman");
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
    protected void startRegistration() {
//        FieldOverrides fieldOverrides = new FieldOverrides(context.anmLocationController().getLocationJSON());
        startFormActivity("elco_registration", null,null);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    showProfileView((ECClient) view.getTag());
                    break;
                case R.id.edit:
                    Log.v("follow up check","a lot is done");
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
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
