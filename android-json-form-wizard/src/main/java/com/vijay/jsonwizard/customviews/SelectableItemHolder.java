package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.vijay.jsonwizard.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<String> {
    private RelativeLayout canvas;
    private TextView tvValue;
    private CheckBox nodeSelector;
    private String levelLabel;
    private int level;
    private PrintView arrowView;
    private Context context;
    private TreeNode treeNode;

    public SelectableItemHolder(Context context, String levelLabel) {
        super(context);

        this.context = context;
        this.levelLabel = levelLabel;
    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_selectable_item, null, false);

        this.treeNode = node;
        tvValue = (TextView) view.findViewById(R.id.node_value);
        if(TextUtils.isEmpty(levelLabel)) {
            tvValue.setText(value);
        } else {
            tvValue.setText(levelLabel + ": " + value);
        }
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.default_text_size));
        arrowView = (PrintView) view.findViewById(R.id.arrowview);
        arrowView.setIconFont("fonts/material/fonts/material-icon-font.ttf");

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
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

        view.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        if (node.isLeaf()) {
            arrowView.setIconText(context.getResources().getString(R.string.ic_check_circle_blank));
        }

        if (node.isFirstChild()) {
            view.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        }

        return view;
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
    }
}