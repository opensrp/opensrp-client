package org.ei.opensrp.indonesia.view.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.util.StringUtil;
import org.ei.opensrp.indonesia.view.contract.AnakClient;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Dimas Ciputra on 9/21/15.
 */
public class DetailsNativeCard extends CardWithList {

    SmartRegisterClient client;
    private final Map<String, String> detailList;
    int pivotList = 10;

    public DetailsNativeCard(Context context, Map<String,String> detailList) {
        super(context);
        this.detailList = detailList;
    }

    public void setClient(SmartRegisterClient client) {
        this.client = client;
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
                    subTitle.setText("Ringkasan detail");
                }
            }
        };

        header.setTitle(client.name());
        return header;
    }

    @Override
    protected void initCard() {
        setSwipeable(false);
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<>();

        Iterator it = detailList.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String subject = pair.getKey() + "";
            String value = pair.getValue()+"";

            DetailListObject stockObject = new DetailListObject(this);
            stockObject.subject = StringUtil.splitCamelCase(subject);
            stockObject.value = DateUtil.isValidDate(value) ? DateUtil.formatDate(value) : StringUtil.humanize(value);
            mObjects.add(stockObject);
        }

        List<ListObject> cObjects = new ArrayList<>(mObjects);

        if(detailList.size() > 10) {
            cObjects = cObjects.subList(0, pivotList);

            DetailListObject stockObject1 = new DetailListObject(this);
            stockObject1.clickableText = org.ei.opensrp.indonesia.Context.getInstance().getStringResource(R.string.show_more_button);
            ShowMoreButtonListener itemClickListener = new ShowMoreButtonListener(pivotList, mObjects);
            stockObject1.setOnItemClickListener(itemClickListener);
            cObjects.add(stockObject1);
        }

        return cObjects;
    }

    public class ShowMoreButtonListener implements OnItemClickListener {
        private int position;
        private List<ListObject> objects;

        public ShowMoreButtonListener(int position, List<ListObject> objects) {
            this.position = position;
            this.objects = objects;
        }

        @Override
        public void onItemClick(LinearListView linearListView, View view, int i, ListObject listObject) {
            mLinearListAdapter.remove(mLinearListAdapter.getItem(position));
            int lsize = objects.size();
            mLinearListAdapter.addAll(new ArrayList<>(objects.subList(position, lsize)));
        }
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View convertView, ViewGroup viewGroup) {
        //Setup the ui elements inside the item
        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textViewCode);
        TextView textViewValue = (TextView) convertView.findViewById(R.id.textViewPerc);
        TextView textClick = (TextView)convertView.findViewById(R.id.txt_clickable);

        //Retrieve the values from the object
        DetailListObject stockObject = (DetailListObject) listObject;
        textViewSubject.setText(stockObject.subject);
        textViewValue.setText(stockObject.value);
        textClick.setText(stockObject.clickableText);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.detail_list_card_inner_main;
    }

    private boolean isAnKIClient(SmartRegisterClient client) {
        return client instanceof KartuIbuClient;
    }

    private boolean isAnakClient(SmartRegisterClient client) {
        return client instanceof AnakClient;
    }
}
