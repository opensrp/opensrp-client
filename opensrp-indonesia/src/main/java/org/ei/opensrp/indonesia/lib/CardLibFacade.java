package org.ei.opensrp.indonesia.lib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by Dimas Ciputra on 9/21/15.
 */
public class CardLibFacade {

    public class CardListObject implements CardWithList.ListObject {
        @Override
        public String getObjectId() {
            return null;
        }

        @Override
        public Card getParentCard() {
            return null;
        }

        @Override
        public void setOnItemClickListener(CardWithList.OnItemClickListener onItemClickListener) {

        }

        @Override
        public CardWithList.OnItemClickListener getOnItemClickListener() {
            return null;
        }

        @Override
        public boolean isSwipeable() {
            return false;
        }

        @Override
        public void setSwipeable(boolean b) {

        }

        @Override
        public CardWithList.OnItemSwipeListener getOnItemSwipeListener() {
            return null;
        }

        @Override
        public void setOnItemSwipeListener(CardWithList.OnItemSwipeListener onItemSwipeListener) {

        }
    }

    public abstract class CardList extends CardWithList {

        public CardList(Context context) {
            super(context);
        }

        @Override
        protected CardHeader initCardHeader() {
            return onInitCardHeader();
        }

        @Override
        protected void initCard() {
            onInitCard();
        }

        @Override
        protected List<ListObject> initChildren() {
            return onInitChildren();
        }

        @Override
        public View setupChildView(int i, ListObject listObject, View view, ViewGroup viewGroup) {
            return onSetupChildView(i, listObject, view, viewGroup);
        }

        @Override
        public int getChildLayoutId() {
            return onGetChildLayoutId();
        }

        protected abstract CardHeader onInitCardHeader();
        protected abstract void onInitCard();
        protected abstract List<ListObject> onInitChildren();
        protected abstract View onSetupChildView(int i, ListObject listObject, View convView, ViewGroup viewGroup);
        protected abstract int onGetChildLayoutId();
    }


}
