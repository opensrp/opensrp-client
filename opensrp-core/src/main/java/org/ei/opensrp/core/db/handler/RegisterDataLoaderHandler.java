package org.ei.opensrp.core.db.handler;

import android.support.v4.app.LoaderManager;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Maimoona on 1/5/2017.
 */
public interface RegisterDataLoaderHandler<T> extends LoaderManager.LoaderCallbacks<T> {
    Pagination pager();
    boolean fullyLoaded();
    void setLoadListener(LoadListener loadListener);
    List currentPageList();
    BaseAdapter adapter();

    abstract class LoadListener{
        public abstract void before();
        public abstract void after();
    }

    abstract class Pagination {
        public abstract Integer totalCount();
        public abstract Integer pageSize();
        public abstract Integer currentOffset();

        public int pageCount() {
            if(totalCount() == 0){
                return 1;
            }
            return (int) Math.ceil(1.0*totalCount()/pageSize());
        }

        public int currentPage() {
            if(currentOffset() != 0) {
                return pageCount()-(int)Math.floor((totalCount()-currentOffset())/(1.0*pageSize()));
            }else{
                return 1;
            }
        }
        public boolean hasNextPage() {
            return ((totalCount()>(currentOffset()+pageSize())));
        }

        public boolean hasPreviousPage() {
            return currentOffset()!=0;
        }

        public int nextPageOffset() {
            return currentOffset()+pageSize();
        }

        public int previousPageOffset() {
            return currentOffset()-pageSize();
        }
    }
}
