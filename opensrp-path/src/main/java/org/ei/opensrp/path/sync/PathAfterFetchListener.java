package org.ei.opensrp.path.sync;

import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.sync.AfterFetchListener;

import static org.ei.opensrp.event.Event.ON_DATA_FETCHED;

public class PathAfterFetchListener implements AfterFetchListener {

    @Override
    public void afterFetch(FetchStatus fetchStatus) {
    }

    void partialFetch(FetchStatus fetchStatus) {
        ON_DATA_FETCHED.notifyListeners(fetchStatus);
    }
}
