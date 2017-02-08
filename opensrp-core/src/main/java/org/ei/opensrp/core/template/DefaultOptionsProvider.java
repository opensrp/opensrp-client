package org.ei.opensrp.core.template;

import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SortOption;

public interface DefaultOptionsProvider {
        SearchFilterOption searchFilterOption();
        ServiceModeOption serviceMode();
        FilterOption villageFilter();
        SortingOption sortOption();
        String nameInShortFormForTitle();
        SearchType searchType();
    }