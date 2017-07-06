package org.ei.opensrp.path.provider;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.adapter.StockProviderForCursorAdapter;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.DateUtils;
import util.ImageUtils;
import util.JsonFormUtils;
import util.VaccinateActionUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;
import static util.VaccinatorUtils.generateScheduleList;
import static util.VaccinatorUtils.nextVaccineDue;
import static util.VaccinatorUtils.receivedVaccines;

/**
 * Created by Raihan  on 29-05-17.
 */
public class StockRowSmartClientsProvider implements StockProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final StockRepository stockRepository;
    AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    public StockRowSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                        AlertService alertService, StockRepository stockRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.stockRepository = stockRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
    }

    @Override
    public void getView(Stock stock, View convertView) {

        TextView date = (TextView)convertView.findViewById(R.id.date);
        TextView to_from = (TextView)convertView.findViewById(R.id.to_from);
        TextView received = (TextView)convertView.findViewById(R.id.received);
        TextView issued = (TextView)convertView.findViewById(R.id.issued);
        TextView loss_adj = (TextView)convertView.findViewById(R.id.loss_adj);
        TextView balance = (TextView)convertView.findViewById(R.id.balance);


        if(stock.getTransaction_type().equalsIgnoreCase(Stock.received)){
            received.setText(""+stock.getValue());
            issued.setText("");
            loss_adj.setText("");
        }
        if(stock.getTransaction_type().equalsIgnoreCase(Stock.issued)){
            received.setText("");
            issued.setText(""+(-1*stock.getValue()));
            loss_adj.setText("");
        }
        if(stock.getTransaction_type().equalsIgnoreCase(Stock.loss_adjustment)){
            received.setText("");
            issued.setText("");
            loss_adj.setText(""+stock.getValue());
        }

        date.setText(JsonFormUtils.dd_MM_yyyy.format(new Date(stock.getDate_created())));
        to_from.setText(stock.getTo_from().replace("_"," "));

        balance.setText(""+(stock.getValue()+stockRepository.getBalanceBeforeCheck(stock)));



    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_stock_control_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }


}