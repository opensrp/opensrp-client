package org.ei.opensrp.path.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import org.ei.opensrp.path.R;

import java.util.HashMap;


/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<String> {
    private TextView tvValue;
    private CheckBox nodeSelector;
    private String levelLabel;
    private int level;
    private PrintView arrowView;
    private Context context;
    private TreeNode treeNode;
    private View canvas;

    private static final HashMap<String, Integer> LEVEL_ICONS;

    static {
        LEVEL_ICONS = new HashMap<>();
        LEVEL_ICONS.put("Health Facility", R.string.ic_health_facility);
        LEVEL_ICONS.put("Zone", R.string.ic_zone);
    }

    public SelectableItemHolder(Context context, String levelLabel) {
        super(context);

        this.context = context;
        this.levelLabel = levelLabel;

    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        canvas = inflater.inflate(R.layout.layout_selectable_item, null, false);


        this.treeNode = node;
        tvValue = (TextView) canvas.findViewById(R.id.node_value);
        tvValue.setText(value);
        tvValue.setTextSize(context.getResources().getDimension(R.dimen.tree_widget_text_size));
        arrowView = (PrintView) canvas.findViewById(R.id.arrowview);
        arrowView.setIconFont("fonts/material/fonts/material-icon-font.ttf");
        arrowView.setIconSizeDp(context.getResources().getDimension(R.dimen.default_text_size));

        nodeSelector = (CheckBox) canvas.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                if (isChecked) {
                    node.setExpanded(isChecked);
                }
            }
        });
        nodeSelector.setChecked(node.isSelected());

        canvas.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        canvas.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        if (node.isLeaf()) {
            arrowView.setIconText(context.getString(LEVEL_ICONS.get(levelLabel)));
        }

        if (node.isFirstChild()) {
            canvas.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        }

        return canvas;
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

    @Override
    public void toggle(boolean active) {
        if (!mNode.isLeaf()) {
            arrowView.setIconText(context.getResources().getString(active ?
                    R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
        }

        if (active) {
            canvas.setBackgroundColor(context.getResources().getColor(R.color.primary_background));
        } else {
            canvas.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }
    }
}