package org.ei.opensrp.core.template;

public interface HeaderProvider {
        int count();
        int weightSum();
        int[] weights();
        int[] headerTextResourceIds();
    }