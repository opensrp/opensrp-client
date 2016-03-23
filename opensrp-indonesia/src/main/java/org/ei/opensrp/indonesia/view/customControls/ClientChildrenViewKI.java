package org.ei.opensrp.indonesia.view.customControls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.KIChildClient;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;

import java.util.List;

import static java.text.MessageFormat.format;

public class ClientChildrenViewKI extends LinearLayout {

    private TextView ageView1;
    private TextView ageView2;

    @SuppressWarnings("UnusedDeclaration")
    public ClientChildrenViewKI(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientChildrenViewKI(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientChildrenViewKI(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        ageView1 = (TextView) findViewById(R.id.txt_children_age_left);
        ageView2 = (TextView) findViewById(R.id.txt_children_age_right);
    }

    public void bindData(KartuIbuClient client, String maleChildAgeFormatString, String femaleChildAgeFormatString) {
        List<KIChildClient> children = client.children();
        if (children.size() == 0) {
            ageView1.setVisibility(GONE);
            ageView2.setVisibility(GONE);
        } else if (children.size() == 1) {
            setupKIChildView(children.get(0), ageView1, maleChildAgeFormatString, femaleChildAgeFormatString);
            ((LayoutParams) ageView1.getLayoutParams()).weight = 100;
            ageView2.setVisibility(GONE);
        } else {
            setupKIChildView(children.get(0), ageView1, maleChildAgeFormatString, femaleChildAgeFormatString);
            setupKIChildView(children.get(1), ageView2, maleChildAgeFormatString, femaleChildAgeFormatString);
            ((LayoutParams) ageView1.getLayoutParams()).weight = 50;
            ((LayoutParams) ageView2.getLayoutParams()).weight = 50;
        }
    }

    private void setupKIChildView(KIChildClient child, TextView ageView,
                                String maleChildAgeFormatString, String femaleChildAgeFormatString) {
        ageView.setVisibility(VISIBLE);
        ageView.setText(
                format(child.isMale() ? maleChildAgeFormatString : femaleChildAgeFormatString,
                        child.getAgeInString()));
    }
}
