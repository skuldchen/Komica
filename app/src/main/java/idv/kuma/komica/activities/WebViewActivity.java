package idv.kuma.komica.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import idv.kuma.komica.R;
import idv.kuma.komica.activities.base.BaseOtherActivity;
import idv.kuma.komica.configs.BundleKeyConfigs;
import idv.kuma.komica.fragments.WebViewFragment;

public class WebViewActivity extends BaseOtherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String title = "";
        if (getIntent() != null) {
            title = getIntent().getStringExtra(BundleKeyConfigs.KEY_WEB_TITLE);
        }
        if (title != null) {
            setTitle(title);
        }
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction().add(R.id.relativeLayout_webView, WebViewFragment.newInstance(getIntent().getExtras())).commit();
        }
    }
}
