package util.uniqueIdGenerator;

import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.SettingsRepository;

/**
  * Created by Dimas on 9/17/2015.
  */
public class AllSettingsINA extends AllSettings {

    private static final String LAST_USED_UNIQUE_ID = "lastUsedId";
    private static final String CURRENT_UNIQUE_ID = "currentUniqueId";

    public AllSettingsINA(AllSharedPreferences preferences, SettingsRepository settingsRepository) {
            super(preferences, settingsRepository);
        }

    public void saveLastUsedId(String lastUsedId) {
            settingsRepository.updateSetting(LAST_USED_UNIQUE_ID, lastUsedId);
        }

    public String fetchLastUsedId() {
            return settingsRepository.querySetting(LAST_USED_UNIQUE_ID, "0");
        }

    public void saveCurrentId(String currentId) {
            settingsRepository.updateSetting(CURRENT_UNIQUE_ID, currentId);
        }

    public String fetchCurrentId() {
            return settingsRepository.querySetting(CURRENT_UNIQUE_ID, "0");
        }

}