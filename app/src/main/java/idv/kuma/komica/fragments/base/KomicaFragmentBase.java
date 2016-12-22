package idv.kuma.komica.fragments.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.analytics.Tracker;

import idv.kuma.komica.KomicaApplication;
import idv.kuma.komica.manager.ThirdPartyManager;
import idv.kuma.komica.utils.KLog;


/**
 * Created by TakumaLee on 2016/5/22.
 */
public class KomicaFragmentBase extends Fragment {
    private static final String TAG = KomicaFragmentBase.class.getSimpleName();

    protected Menu menu;
    protected Tracker tracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = ((KomicaApplication) getActivity().getApplication()).getDefaultTracker();
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.v(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.v(TAG, "onActivityResult");
        ThirdPartyManager.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
