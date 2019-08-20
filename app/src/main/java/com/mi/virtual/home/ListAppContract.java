package com.mi.virtual.home;

import java.util.List;

import com.mi.virtual.home.models.AppInfo;
import com.mi.virtual.abs.BasePresenter;
import com.mi.virtual.abs.BaseView;

/**
 * @author Lody
 * @version 1.0
 */
/*package*/ class ListAppContract {
    interface ListAppView extends BaseView<ListAppPresenter> {

        void startLoading();

        void loadFinish(List<AppInfo> infoList);
    }

    interface ListAppPresenter extends BasePresenter {

    }
}
