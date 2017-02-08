package org.ei.opensrp.core.template;

import org.ei.opensrp.view.dialog.DialogOption;

public interface NavBarOptionsProvider {
        DialogOption[] filterOptions();
        DialogOption[] serviceModeOptions();
        DialogOption[] sortingOptions();
        String searchHint();
    }