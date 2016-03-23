package org.ei.opensrp.indonesia.view.card;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.controller.ReportingController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Dimas Ciputra on 6/23/15.
 */
public class ReportingNativeCard extends CardWithList {

    private final ReportingController reportingController;

    public ReportingNativeCard(Context context, ReportingController reportingController) {
        super(context);
        this.reportingController = reportingController;
    }

    @Override
    protected CardHeader initCardHeader() {
        // Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.list_card_header) {
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                super.setupInnerViewElements(parent, view);
                TextView subTitle = (TextView) view.findViewById(R.id.carddemo_googlenow_main_inner_lastupdate);
                if (subTitle != null) {
                    subTitle.setText("Ringkasan laporan");
                }
            }
        };

        header.setTitle("Laporan");
        return header;
    }

    @Override
    protected void initCard() {

    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> mObjects = new ArrayList<>();

        List<Map<String, String>> data = reportingController.getReports();

        for(Map<String, String> d : data) {
            StockObject stockObject = new StockObject(this);
            String subject = d.get("subject")+"";
            String value = d.get("value")+"";
            stockObject.subject = subject;
            stockObject.value =  value;
            mObjects.add(stockObject);
        }

        return mObjects;
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View convertView, ViewGroup viewGroup) {
        //Setup the ui elements inside the item
        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textViewCode);
        TextView textViewValue = (TextView) convertView.findViewById(R.id.textViewPerc);

        if(i%2>0) {
            convertView.setBackgroundColor(Color.parseColor("#FFFFCC"));
        }

        //Retrieve the values from the object
        StockObject stockObject = (StockObject) listObject;
        textViewSubject.setText(stockObject.subject);
        textViewValue.setText(stockObject.value);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.detail_list_card_inner_main;
    }

    public class StockObject extends DefaultListObject {

        public String subject;
        public String value;

        public StockObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), getObjectId() + " : " + getValue(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public String getObjectId() {
            return subject;
        }

        public String getValue() {
            return value;
        }
    }
}