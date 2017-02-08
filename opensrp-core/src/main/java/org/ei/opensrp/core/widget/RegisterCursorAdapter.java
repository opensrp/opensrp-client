package org.ei.opensrp.core.widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.RegisterClientsProvider;

import java.util.ArrayList;
import java.util.List;

public class RegisterCursorAdapter extends CursorAdapter {
    private final RegisterClientsProvider listItemProvider;
    private Context context;
    private List<CommonPersonObjectClient> currentPage;

    public RegisterCursorAdapter(Context context, RegisterClientsProvider listItemProvider) {
        super(context, null, false);
        this.listItemProvider = listItemProvider;
        this.context = context;
    }

    public List<CommonPersonObjectClient> getCurrentPage(){
        return currentPage;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        currentPage = new ArrayList<>();
        Log.i(getClass().getName(), "New view of cursor adapter");
        return listItemProvider.inflateLayoutForAdapter();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(getClass().getName(), "Creating view from cursor");

        CommonPersonObject personinlist = RegisterRepository.convertToCommonObject(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
        pClient.setColumnmaps(personinlist.getColumnmaps());

        currentPage.add(pClient);

        listItemProvider.getView(pClient, view, null/*todo*/);
    }

/*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: WARNING .. IT SHOULD BE CHANGED, IT WAS DONE FOR TRANSITIONING FROM HH REGISTER TO OTHERS
        if (getCursor() == null || getCursor().isClosed()) {
            return null;
        }
        if (!getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(context, getCursor(), parent);
        } else {
            v = convertView;
        }
        bindView(v, context, getCursor());
        return v;
    }*/

   /* @Override
    public int getCount() {
        if (getCursor() != null && getCursor().isClosed() == false) {
            Log.v(getClass().getName(), "0 count returned as cursor was found closed");
            return 0;
        }
        return getCursor().getCount();
    }*/
}
