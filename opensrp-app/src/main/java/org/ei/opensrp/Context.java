package org.ei.opensrp;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObjectClients;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.commonregistry.CommonRepositoryInformationHolder;
import org.ei.opensrp.repository.AlertRepository;
import org.ei.opensrp.repository.AllAlerts;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllEligibleCouples;
import org.ei.opensrp.repository.AllReports;
import org.ei.opensrp.repository.AllServicesProvided;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.ei.opensrp.repository.ChildRepository;
import org.ei.opensrp.repository.DrishtiRepository;
import org.ei.opensrp.repository.EligibleCoupleRepository;
import org.ei.opensrp.repository.FormDataRepository;
import org.ei.opensrp.repository.FormsVersionRepository;
import org.ei.opensrp.repository.MotherRepository;
import org.ei.opensrp.repository.ReportRepository;
import org.ei.opensrp.repository.Repository;
import org.ei.opensrp.repository.ServiceProvidedRepository;
import org.ei.opensrp.repository.SettingsRepository;
import org.ei.opensrp.repository.TimelineEventRepository;
import org.ei.opensrp.service.ANMService;
import org.ei.opensrp.service.ActionService;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.AllFormVersionSyncService;
import org.ei.opensrp.service.BeneficiaryService;
import org.ei.opensrp.service.ChildService;
import org.ei.opensrp.service.DrishtiService;
import org.ei.opensrp.service.EligibleCoupleService;
import org.ei.opensrp.service.FormSubmissionService;
import org.ei.opensrp.service.FormSubmissionSyncService;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.service.MotherService;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.service.UserService;
import org.ei.opensrp.service.ZiggyFileLoader;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.service.formSubmissionHandler.ANCCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCInvestigationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCRegistrationHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCVisitHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildIllnessHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildImmunizationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildRegistrationECHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.DeliveryOutcomeHandler;
import org.ei.opensrp.service.formSubmissionHandler.DeliveryPlanHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECEditHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECRegistrationHandler;
import org.ei.opensrp.service.formSubmissionHandler.FPChangeHandler;
import org.ei.opensrp.service.formSubmissionHandler.FPComplicationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionRouter;
import org.ei.opensrp.service.formSubmissionHandler.HBTestHandler;
import org.ei.opensrp.service.formSubmissionHandler.IFAHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCVisitHandler;
import org.ei.opensrp.service.formSubmissionHandler.RenewFPProductHandler;
import org.ei.opensrp.service.formSubmissionHandler.TTHandler;
import org.ei.opensrp.service.formSubmissionHandler.VitaminAHandler;
import org.ei.opensrp.sync.SaveANMLocationTask;
import org.ei.opensrp.sync.SaveUserInfoTask;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.Session;
import org.ei.opensrp.view.contract.ANCClients;
import org.ei.opensrp.view.contract.ECClients;
import org.ei.opensrp.view.contract.FPClients;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.contract.Villages;
import org.ei.opensrp.view.contract.pnc.PNCClients;
import org.ei.opensrp.view.controller.ANMController;
import org.ei.opensrp.view.controller.ANMLocationController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Context {
    private android.content.Context applicationContext;
    private static Context context = new Context();

    private Repository repository;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private AlertRepository alertRepository;
    private SettingsRepository settingsRepository;
    private ChildRepository childRepository;
    private MotherRepository motherRepository;
    private TimelineEventRepository timelineEventRepository;
    private ReportRepository reportRepository;
    private FormDataRepository formDataRepository;
    private ServiceProvidedRepository serviceProvidedRepository;
    private FormsVersionRepository formsVersionRepository;

    private AllSettings allSettings;
    private AllSharedPreferences allSharedPreferences;
    private AllAlerts allAlerts;
    private AllEligibleCouples allEligibleCouples;
    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelineEvents;
    private AllReports allReports;
    private AllServicesProvided allServicesProvided;
    private AllCommonsRepository allCommonPersonObjectsRepository;

    private DrishtiService drishtiService;
    private ActionService actionService;
    private FormSubmissionService formSubmissionService;
    private FormSubmissionSyncService formSubmissionSyncService;
    private ZiggyService ziggyService;
    private UserService userService;
    private AlertService alertService;
    private EligibleCoupleService eligibleCoupleService;
    private MotherService motherService;
    private ChildService childService;
    private ANMService anmService;
    private BeneficiaryService beneficiaryService;
    private ServiceProvidedService serviceProvidedService;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private AllFormVersionSyncService allFormVersionSyncService;

    private Session session;
    private Cache<String> listCache;
    private Cache<SmartRegisterClients> smartRegisterClientsCache;
    private Cache<HomeContext> homeContextCache;
    private Cache<ECClients> ecClientsCache;
    private Cache<FPClients> fpClientsCache;
    private Cache<ANCClients> ancClientsCache;
    private Cache<PNCClients> pncClientsCache;
    private Cache<Villages> villagesCache;
    private Cache<Typeface> typefaceCache;
    private Cache<CommonPersonObjectClients> personObjectClientsCache;

    private HTTPAgent httpAgent;
    private ZiggyFileLoader ziggyFileLoader;

    private FormSubmissionRouter formSubmissionRouter;
    private ECRegistrationHandler ecRegistrationHandler;
    private FPComplicationsHandler fpComplicationsHandler;
    private FPChangeHandler fpChangeHandler;
    private RenewFPProductHandler renewFPProductHandler;
    private ECCloseHandler ecCloseHandler;
    private ANCRegistrationHandler ancRegistrationHandler;
    private ANCRegistrationOAHandler ancRegistrationOAHandler;
    private ANCVisitHandler ancVisitHandler;
    private ANCCloseHandler ancCloseHandler;
    private TTHandler ttHandler;
    private IFAHandler ifaHandler;
    private HBTestHandler hbTestHandler;
    private DeliveryOutcomeHandler deliveryOutcomeHandler;
    private DeliveryPlanHandler deliveryPlanHandler;
    private PNCRegistrationOAHandler pncRegistrationOAHandler;
    private PNCCloseHandler pncCloseHandler;
    private PNCVisitHandler pncVisitHandler;
    private ChildImmunizationsHandler childImmunizationsHandler;
    private ChildRegistrationECHandler childRegistrationECHandler;
    private ChildRegistrationOAHandler childRegistrationOAHandler;
    private ChildCloseHandler childCloseHandler;
    private ChildIllnessHandler childIllnessHandler;
    private VitaminAHandler vitaminAHandler;
    private ECEditHandler ecEditHandler;
    private ANCInvestigationsHandler ancInvestigationsHandler;
    private SaveANMLocationTask saveANMLocationTask;
    private SaveUserInfoTask saveUserInfoTask;

    private ANMController anmController;
    private ANMLocationController anmLocationController;

    private DristhiConfiguration configuration;

    ///////////////////common bindtypes///////////////
    public static ArrayList<CommonRepositoryInformationHolder> bindtypes;
    /////////////////////////////////////////////////
    protected Context() {
    }

    public android.content.Context applicationContext() {
        return applicationContext;
    }


    public static Context getInstance() {
        return context;
    }

    public static Context setInstance(Context context) {
        Context.context = context;
        return context;
    }

    public BeneficiaryService beneficiaryService() {
        if (beneficiaryService == null) {
            beneficiaryService = new BeneficiaryService(allEligibleCouples(), allBeneficiaries());
        }
        return beneficiaryService;
    }

    public Context updateApplicationContext(android.content.Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    protected DrishtiService drishtiService() {
        if (drishtiService == null) {
            drishtiService = new DrishtiService(httpAgent(), configuration().dristhiBaseURL());
        }
        return drishtiService;
    }

    public ActionService actionService() {
        if (actionService == null) {
            actionService = new ActionService(drishtiService(), allSettings(), allSharedPreferences(), allReports());
        }
        return actionService;
    }

    public FormSubmissionService formSubmissionService() {
        initRepository();
        if (formSubmissionService == null) {
            formSubmissionService = new FormSubmissionService(ziggyService(), formDataRepository(), allSettings());
        }
        return formSubmissionService;
    }

    public AllFormVersionSyncService allFormVersionSyncService() {
        if(allFormVersionSyncService == null) {
            allFormVersionSyncService = new AllFormVersionSyncService(httpAgent(),
                    configuration(), formsVersionRepository());
        }
        return allFormVersionSyncService;
    }

    public FormSubmissionRouter formSubmissionRouter() {
        initRepository();
        if (formSubmissionRouter == null) {
            formSubmissionRouter = new FormSubmissionRouter(formDataRepository(), ecRegistrationHandler(),
                    fpComplicationsHandler(), fpChangeHandler(), renewFPProductHandler(), ecCloseHandler(),
                    ancRegistrationHandler(), ancRegistrationOAHandler(), ancVisitHandler(), ancCloseHandler(),
                    ttHandler(), ifaHandler(), hbTestHandler(), deliveryOutcomeHandler(), pncRegistrationOAHandler(),
                    pncCloseHandler(), pncVisitHandler(), childImmunizationsHandler(), childRegistrationECHandler(),
                    childRegistrationOAHandler(), childCloseHandler(), childIllnessHandler(), vitaminAHandler(),
                    deliveryPlanHandler(), ecEditHandler(), ancInvestigationsHandler());
        }
        return formSubmissionRouter;
    }

    private ChildCloseHandler childCloseHandler() {
        if (childCloseHandler == null) {
            childCloseHandler = new ChildCloseHandler(childService());
        }
        return childCloseHandler;
    }

    private ECRegistrationHandler ecRegistrationHandler() {
        if (ecRegistrationHandler == null) {
            ecRegistrationHandler = new ECRegistrationHandler(eligibleCoupleService());
        }
        return ecRegistrationHandler;
    }

    private FPComplicationsHandler fpComplicationsHandler() {
        if (fpComplicationsHandler == null) {
            fpComplicationsHandler = new FPComplicationsHandler(eligibleCoupleService());
        }
        return fpComplicationsHandler;
    }

    private FPChangeHandler fpChangeHandler() {
        if (fpChangeHandler == null) {
            fpChangeHandler = new FPChangeHandler(eligibleCoupleService());
        }
        return fpChangeHandler;
    }

    private RenewFPProductHandler renewFPProductHandler() {
        if (renewFPProductHandler == null) {
            renewFPProductHandler = new RenewFPProductHandler(eligibleCoupleService());
        }
        return renewFPProductHandler;
    }

    private ECCloseHandler ecCloseHandler() {
        if (ecCloseHandler == null) {
            ecCloseHandler = new ECCloseHandler(eligibleCoupleService());
        }
        return ecCloseHandler;
    }

    private ANCRegistrationHandler ancRegistrationHandler() {
        if (ancRegistrationHandler == null) {
            ancRegistrationHandler = new ANCRegistrationHandler(motherService());
        }
        return ancRegistrationHandler;
    }

    private ANCRegistrationOAHandler ancRegistrationOAHandler() {
        if (ancRegistrationOAHandler == null) {
            ancRegistrationOAHandler = new ANCRegistrationOAHandler(motherService());
        }
        return ancRegistrationOAHandler;
    }

    private ANCVisitHandler ancVisitHandler() {
        if (ancVisitHandler == null) {
            ancVisitHandler = new ANCVisitHandler(motherService());
        }
        return ancVisitHandler;
    }

    private ANCCloseHandler ancCloseHandler() {
        if (ancCloseHandler == null) {
            ancCloseHandler = new ANCCloseHandler(motherService());
        }
        return ancCloseHandler;
    }

    private TTHandler ttHandler() {
        if (ttHandler == null) {
            ttHandler = new TTHandler(motherService());
        }
        return ttHandler;
    }

    private IFAHandler ifaHandler() {
        if (ifaHandler == null) {
            ifaHandler = new IFAHandler(motherService());
        }
        return ifaHandler;
    }

    private HBTestHandler hbTestHandler() {
        if (hbTestHandler == null) {
            hbTestHandler = new HBTestHandler(motherService());
        }
        return hbTestHandler;
    }

    private DeliveryOutcomeHandler deliveryOutcomeHandler() {
        if (deliveryOutcomeHandler == null) {
            deliveryOutcomeHandler = new DeliveryOutcomeHandler(motherService(), childService());
        }
        return deliveryOutcomeHandler;
    }

    private DeliveryPlanHandler deliveryPlanHandler() {
        if (deliveryPlanHandler == null) {
            deliveryPlanHandler = new DeliveryPlanHandler(motherService());
        }
        return deliveryPlanHandler;
    }

    private PNCRegistrationOAHandler pncRegistrationOAHandler() {
        if (pncRegistrationOAHandler == null) {
            pncRegistrationOAHandler = new PNCRegistrationOAHandler(childService());
        }
        return pncRegistrationOAHandler;
    }

    private PNCCloseHandler pncCloseHandler() {
        if (pncCloseHandler == null) {
            pncCloseHandler = new PNCCloseHandler(motherService());
        }
        return pncCloseHandler;
    }

    private PNCVisitHandler pncVisitHandler() {
        if (pncVisitHandler == null) {
            pncVisitHandler = new PNCVisitHandler(motherService(), childService());
        }
        return pncVisitHandler;
    }

    private ChildImmunizationsHandler childImmunizationsHandler() {
        if (childImmunizationsHandler == null) {
            childImmunizationsHandler = new ChildImmunizationsHandler(childService());
        }
        return childImmunizationsHandler;
    }

    private ChildIllnessHandler childIllnessHandler() {
        if (childIllnessHandler == null) {
            childIllnessHandler = new ChildIllnessHandler(childService());
        }
        return childIllnessHandler;
    }

    private VitaminAHandler vitaminAHandler() {
        if (vitaminAHandler == null) {
            vitaminAHandler = new VitaminAHandler(childService());
        }
        return vitaminAHandler;
    }

    private ChildRegistrationECHandler childRegistrationECHandler() {
        if (childRegistrationECHandler == null) {
            childRegistrationECHandler = new ChildRegistrationECHandler(childService());
        }
        return childRegistrationECHandler;
    }

    private ChildRegistrationOAHandler childRegistrationOAHandler() {
        if (childRegistrationOAHandler == null) {
            childRegistrationOAHandler = new ChildRegistrationOAHandler(childService());
        }
        return childRegistrationOAHandler;
    }

    private ECEditHandler ecEditHandler() {
        if (ecEditHandler == null) {
            ecEditHandler = new ECEditHandler();
        }
        return ecEditHandler;
    }

    private ANCInvestigationsHandler ancInvestigationsHandler() {
        if (ancInvestigationsHandler == null) {
            ancInvestigationsHandler = new ANCInvestigationsHandler();
        }
        return ancInvestigationsHandler;
    }

    private ZiggyService ziggyService() {
        initRepository();
        if (ziggyService == null) {
            ziggyService = new ZiggyService(ziggyFileLoader(), formDataRepository(), formSubmissionRouter());
        }
        return ziggyService;
    }

    public ZiggyFileLoader ziggyFileLoader() {
        if (ziggyFileLoader == null) {
            ziggyFileLoader = new ZiggyFileLoader("www/ziggy", "www/form", applicationContext().getAssets());
        }
        return ziggyFileLoader;
    }

    public FormSubmissionSyncService formSubmissionSyncService() {
        if (formSubmissionSyncService == null) {
            formSubmissionSyncService = new FormSubmissionSyncService(formSubmissionService(), httpAgent(), formDataRepository(), allSettings(), allSharedPreferences(), configuration());
        }
        return formSubmissionSyncService;
    }

    private HTTPAgent httpAgent() {
        if (httpAgent == null) {
            httpAgent = new HTTPAgent(applicationContext, allSettings(), allSharedPreferences(), configuration());
        }
        return httpAgent;
    }

    private Repository initRepository() {
        if (repository == null) {
            assignbindtypes();
            ArrayList<DrishtiRepository> drishtireposotorylist = new ArrayList<DrishtiRepository>();
            drishtireposotorylist.add(settingsRepository());
            drishtireposotorylist.add(alertRepository());
            drishtireposotorylist.add(eligibleCoupleRepository());
            drishtireposotorylist.add(childRepository());
            drishtireposotorylist.add(timelineEventRepository());
            drishtireposotorylist.add(motherRepository());
            drishtireposotorylist.add(reportRepository());
            drishtireposotorylist.add(formDataRepository());
            drishtireposotorylist.add(serviceProvidedRepository());
            drishtireposotorylist.add(formsVersionRepository());
            for(int i = 0;i < bindtypes.size();i++){
                drishtireposotorylist.add(commonrepository(bindtypes.get(i).getBindtypename()));
            }
            DrishtiRepository[] drishtireposotoryarray = drishtireposotorylist.toArray(new DrishtiRepository[drishtireposotorylist.size()]);
            repository = new Repository(this.applicationContext, session(), drishtireposotoryarray);
        }
        return repository;
    }

    public AllEligibleCouples allEligibleCouples() {
        initRepository();
        if (allEligibleCouples == null) {
            allEligibleCouples = new AllEligibleCouples(eligibleCoupleRepository(), alertRepository(), timelineEventRepository());
        }
        return allEligibleCouples;
    }

    public AllAlerts allAlerts() {
        initRepository();
        if (allAlerts == null) {
            allAlerts = new AllAlerts(alertRepository());
        }
        return allAlerts;
    }

    public AllSettings allSettings() {
        initRepository();
        if (allSettings == null) {
            allSettings = new AllSettings(allSharedPreferences(), settingsRepository());
        }
        return allSettings;
    }

    public AllSharedPreferences allSharedPreferences() {
        if (allSharedPreferences == null) {
         //   allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this.applicationContext));
            PreferenceManager.setDefaultValues(this.applicationContext, R.xml.preferences, false);
            allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this.applicationContext));
        }
        return allSharedPreferences;
    }

    public AllBeneficiaries allBeneficiaries() {
        initRepository();
        if (allBeneficiaries == null) {
            allBeneficiaries = new AllBeneficiaries(motherRepository(), childRepository(), alertRepository(), timelineEventRepository());
        }
        return allBeneficiaries;
    }

    public AllTimelineEvents allTimelineEvents() {
        initRepository();
        if (allTimelineEvents == null) {
            allTimelineEvents = new AllTimelineEvents(timelineEventRepository());
        }
        return allTimelineEvents;
    }

    public AllReports allReports() {
        initRepository();
        if (allReports == null) {
            allReports = new AllReports(reportRepository());
        }
        return allReports;
    }

    public AllServicesProvided allServicesProvided() {
        initRepository();
        if (allServicesProvided == null) {
            allServicesProvided = new AllServicesProvided(serviceProvidedRepository());
        }
        return allServicesProvided;
    }

    private EligibleCoupleRepository eligibleCoupleRepository() {
        if (eligibleCoupleRepository == null) {
            eligibleCoupleRepository = new EligibleCoupleRepository();
        }
        return eligibleCoupleRepository;
    }

    private AlertRepository alertRepository() {
        if (alertRepository == null) {
            alertRepository = new AlertRepository();
        }
        return alertRepository;
    }

    private SettingsRepository settingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new SettingsRepository();
        }
        return settingsRepository;
    }

    private ChildRepository childRepository() {
        if (childRepository == null) {
            childRepository = new ChildRepository();
        }
        return childRepository;
    }

    private MotherRepository motherRepository() {
        if (motherRepository == null) {
            motherRepository = new MotherRepository();
        }
        return motherRepository;
    }

    private TimelineEventRepository timelineEventRepository() {
        if (timelineEventRepository == null) {
            timelineEventRepository = new TimelineEventRepository();
        }
        return timelineEventRepository;
    }

    private ReportRepository reportRepository() {
        if (reportRepository == null) {
            reportRepository = new ReportRepository();
        }
        return reportRepository;
    }

    public FormDataRepository formDataRepository() {
        if (formDataRepository == null) {
            formDataRepository = new FormDataRepository();
        }
        return formDataRepository;
    }

    private ServiceProvidedRepository serviceProvidedRepository() {
        if (serviceProvidedRepository == null) {
            serviceProvidedRepository = new ServiceProvidedRepository();
        }
        return serviceProvidedRepository;
    }

    private FormsVersionRepository formsVersionRepository() {
        if (formsVersionRepository == null) {
            formsVersionRepository = new FormsVersionRepository();
        }
        return formsVersionRepository;
    }

    public UserService userService() {
        if (userService == null) {
            Repository repo = initRepository();
            userService = new UserService(repo, allSettings(), allSharedPreferences(), httpAgent(), session(), configuration(), saveANMLocationTask(),saveUserInfoTask());
        }
        return userService;
    }

    private SaveANMLocationTask saveANMLocationTask() {
        if (saveANMLocationTask == null) {
            saveANMLocationTask = new SaveANMLocationTask(allSettings());
        }
        return saveANMLocationTask;
    }

    private SaveUserInfoTask saveUserInfoTask() {
        if(saveUserInfoTask == null) {
            saveUserInfoTask = new SaveUserInfoTask(allSettings());
        }
        return saveUserInfoTask;
    }

    public AlertService alertService() {
        if (alertService == null) {
            alertService = new AlertService(alertRepository());
        }
        return alertService;
    }

    public ServiceProvidedService serviceProvidedService() {
        if (serviceProvidedService == null) {
            serviceProvidedService = new ServiceProvidedService(allServicesProvided());
        }
        return serviceProvidedService;
    }

    public EligibleCoupleService eligibleCoupleService() {
        if (eligibleCoupleService == null) {
            eligibleCoupleService = new EligibleCoupleService(allEligibleCouples(), allTimelineEvents(), allBeneficiaries());
        }
        return eligibleCoupleService;
    }

    public MotherService motherService() {
        if (motherService == null) {
            motherService = new MotherService(allBeneficiaries(), allEligibleCouples(), allTimelineEvents(), serviceProvidedService());
        }
        return motherService;
    }

    public ChildService childService() {
        if (childService == null) {
            childService = new ChildService(allBeneficiaries(), motherRepository(), childRepository(), allTimelineEvents(), serviceProvidedService(), allAlerts());
        }
        return childService;
    }

    public Session session() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public ANMService anmService() {
        if (anmService == null) {
            anmService = new ANMService(allSharedPreferences(), allBeneficiaries(), allEligibleCouples());
        }
        return anmService;
    }

    public Cache<String> listCache() {
        if (listCache == null) {
            listCache = new Cache<String>();
        }
        return listCache;
    }

    public Cache<SmartRegisterClients> smartRegisterClientsCache() {
        if (smartRegisterClientsCache == null) {
            smartRegisterClientsCache = new Cache<SmartRegisterClients>();
        }
        return smartRegisterClientsCache;
    }

    public Cache<HomeContext> homeContextCache() {
        if (homeContextCache == null) {
            homeContextCache = new Cache<HomeContext>();
        }
        return homeContextCache;
    }

    public Boolean IsUserLoggedOut() {
        return userService().hasSessionExpired();
    }

    public DristhiConfiguration configuration() {
        if (configuration == null) {
            configuration = new DristhiConfiguration(getInstance().applicationContext().getAssets());
        }
        return configuration;
    }

    public PendingFormSubmissionService pendingFormSubmissionService() {
        if (pendingFormSubmissionService == null) {
            pendingFormSubmissionService = new PendingFormSubmissionService(formDataRepository());
        }
        return pendingFormSubmissionService;
    }

    public ANMController anmController() {
        if (anmController == null) {
            anmController = new ANMController(anmService(), listCache(), homeContextCache());
        }
        return anmController;
    }

    public ANMLocationController anmLocationController() {
        if (anmLocationController == null) {
            anmLocationController = new ANMLocationController(allSettings(), listCache());
        }
        return anmLocationController;
    }

    //#TODO: Refactor to use one cache object
    public Cache<ECClients> ecClientsCache() {
        if (ecClientsCache == null) {
            ecClientsCache = new Cache<ECClients>();
        }
        return ecClientsCache;

    }

    //#TODO: Refactor to use one cache object
    public Cache<FPClients> fpClientsCache() {
        if (fpClientsCache == null) {
            fpClientsCache = new Cache<FPClients>();
        }
        return fpClientsCache;

    }

    //#TODO: Refactor to use one cache object

    public Cache<ANCClients> ancClientsCache() {
        if (ancClientsCache == null) {
            ancClientsCache = new Cache<ANCClients>();
        }
        return ancClientsCache;
    }

    public Cache<PNCClients> pncClientsCache() {
        if (pncClientsCache == null) {
            pncClientsCache = new Cache<PNCClients>();
        }
        return pncClientsCache;
    }

    public Cache<Villages> villagesCache() {
        if (villagesCache == null) {
            villagesCache = new Cache<Villages>();
        }
        return villagesCache;
    }

    public Cache<Typeface> typefaceCache() {
        if (typefaceCache == null) {
            typefaceCache = new Cache<Typeface>();
        }
        return typefaceCache;
    }

    public String getStringResource(int id) {
        return applicationContext().getResources().getString(id);
    }

    public int getColorResource(int id) {
        return applicationContext().getResources().getColor(id);
    }

    public Drawable getDrawable(int id) {
        return applicationContext().getResources().getDrawable(id);
    }

    public Drawable getDrawableResource(int id) {
        return applicationContext().getResources().getDrawable(id);
    }


    ///////////////////////////////// common methods ///////////////////////////////
    public Cache<CommonPersonObjectClients> personObjectClientsCache(){
        this.personObjectClientsCache = null;
        personObjectClientsCache = new Cache<CommonPersonObjectClients>();
        return personObjectClientsCache;
    }
    public AllCommonsRepository allCommonsRepositoryobjects(String tablename){
        initRepository();
        allCommonPersonObjectsRepository = new AllCommonsRepository(commonrepository(tablename),alertRepository(),timelineEventRepository());
        return allCommonPersonObjectsRepository;
    }

    private HashMap <String ,CommonRepository> MapOfCommonRepository;

    public long countofcommonrepositroy(String tablename){
        return commonrepository(tablename).count();
    }

    public CommonRepository commonrepository(String tablename){
        if(MapOfCommonRepository == null){
            MapOfCommonRepository = new HashMap<String, CommonRepository>();
        }
        if(MapOfCommonRepository.get(tablename) == null){
            int index = 0;
            for(int i = 0;i<bindtypes.size();i++){
                if(bindtypes.get(i).getBindtypename().equalsIgnoreCase(tablename)){
                    index = i;
                }
            }
            MapOfCommonRepository.put(bindtypes.get(index).getBindtypename(),new CommonRepository(bindtypes.get(index).getBindtypename(),bindtypes.get(index).getColumnNames()));
        }

        return  MapOfCommonRepository.get(tablename);
    }
    public void assignbindtypes(){
        bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        AssetManager assetManager = getInstance().applicationContext().getAssets();

        try {
            String str = ReadFromfile("bindtypes.json",getInstance().applicationContext);
            JSONObject jsonObject = new JSONObject(str);
            JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

            for(int i = 0 ;i<bindtypeObjects.length();i++){
                String bindname = bindtypeObjects.getJSONObject(i).getString("name");
                String [] columNames = new String[ bindtypeObjects.getJSONObject(i).getJSONArray("columns").length()];
                for(int j = 0 ; j < columNames.length;j++){
                  columNames[j] =  bindtypeObjects.getJSONObject(i).getJSONArray("columns").getJSONObject(j).getString("name");
                }
                bindtypes.add(new CommonRepositoryInformationHolder(bindname,columNames));
                Log.v("bind type logs",bindtypeObjects.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public String ReadFromfile(String fileName, android.content.Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, android.content.Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }


    ///////////////////////////////////////////////////////////////////////////////
}
