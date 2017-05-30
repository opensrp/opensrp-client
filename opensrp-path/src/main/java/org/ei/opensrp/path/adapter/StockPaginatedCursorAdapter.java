package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.repository.StockRepository;

/**
 * Created by raihan on 3/9/16.
 */
public class StockPaginatedCursorAdapter extends CursorAdapter {
    private final StockProviderForCursorAdapter listItemProvider;
    Context context;
    StockRepository stockRepository;

    public StockPaginatedCursorAdapter(Context context, Cursor c, StockProviderForCursorAdapter listItemProvider, StockRepository stockRepository) {
        super(context, c);
        this.listItemProvider = listItemProvider;
        this.context= context;
        this.stockRepository = stockRepository;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return  listItemProvider.inflatelayoutForCursorAdapter();
//        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Stock personinlist = stockRepository.readAllStockforCursorAdapter(cursor);

       listItemProvider.getView(personinlist,view);

    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }
}
