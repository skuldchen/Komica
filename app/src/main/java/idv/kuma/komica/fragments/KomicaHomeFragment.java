package idv.kuma.komica.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import idv.kuma.komica.R;
import idv.kuma.komica.activities.base.KomicaActivityBase;
import idv.kuma.komica.entity.KomicaMenuGroup;
import idv.kuma.komica.entity.KomicaMenuMember;
import idv.kuma.komica.entity.MyAccount;
import idv.kuma.komica.fragments.base.BaseFragment;
import idv.kuma.komica.manager.FacebookManager;
import idv.kuma.komica.manager.KomicaAccountManager;
import idv.kuma.komica.manager.KomicaManager;
import idv.kuma.komica.manager.ThirdPartyManager;
import idv.kuma.komica.utils.AppTools;
import idv.kuma.komica.utils.KLog;

/**
 * Created by TakumaLee on 2016/12/5.
 */

public class KomicaHomeFragment extends BaseFragment {
    private static final String TAG = KomicaHomeFragment.class.getSimpleName();

    private MyAccount myAccount;

    private Drawer drawer;
    private DrawerBuilder drawerBuilder;
    private Toolbar toolbar;
    private AccountHeader headerResult;
    private ProfileDrawerItem profileDrawerItem;
    private PrimaryDrawerItem loginItem;

    private ContentFrameLayout frameLayout;
    private BaseFragment contentFragment;
//    private WebViewFragment webViewFragment;
//    private SectionPreviewFragment sectionFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAccount = KomicaAccountManager.getInstance().getMyAccount();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_komica_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initDrawer();
        contentFragment = IndexFragment.newInstance();
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction().add(R.id.contentFrameLayout_home, contentFragment).commit();
        }
        ThirdPartyManager.getInstance().registerProfileListener(new FacebookManager.OnGetProfileListener() {
            @Override
            public void onGetProfile() {
                checkFacebookLogin();
            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ThirdPartyManager.getInstance().registerProfileListener(null);
    }

    private void initView() {
        toolbar = findViewById(getView(), R.id.toolbar_home);
        ((KomicaActivityBase) getActivity()).setSupportActionBar(toolbar);
        frameLayout = findViewById(getView(), R.id.contentFrameLayout_home);
    }

    private void initDrawer() {
        profileDrawerItem = new ProfileDrawerItem().withIcon(R.drawable.anonymous).withName(myAccount.getUsername()).withEmail(myAccount.getEmail());
        loginItem = new PrimaryDrawerItem()
                .withIdentifier(-1)
                .withSelectable(false)
                .withName(ThirdPartyManager.getInstance().isFacebookLogin() ? R.string.logout_facebook : R.string.login_facebook)
                .withTextColor(ThirdPartyManager.getInstance().isFacebookLogin() ? Color.GRAY : Color.BLUE)
                .withIcon(CommunityMaterial.Icon.cmd_logout);

        headerResult = new AccountHeaderBuilder()
                .withActivity(getActivity())
                .withHeaderBackground(R.drawable.wallpaper)
                .addProfiles(
                        profileDrawerItem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                }).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable

        //create the drawer and remember the `Drawer` result object
        String version = AppTools.getVersionName(getContext());
        if (version == null || version.isEmpty()) {
            version = "";
        }

        drawerBuilder = new DrawerBuilder()
                .withActivity(getActivity())
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withStickyFooterShadow(false)
                .addStickyDrawerItems(
                        new DividerDrawerItem(),
                        loginItem,
                        new SecondaryDrawerItem()
                                .withIdentifier(-2)
                                .withSelectable(false)
                                .withName(getString(R.string.version, version))
                                .withTextColor(ContextCompat.getColor(getContext(), R.color.gray_light))
                                .withIcon(GoogleMaterial.Icon.gmd_details)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, final IDrawerItem drawerItem) {
                        KLog.v(TAG, "Drawer item: " + drawerItem.getIdentifier());
                        Intent intent = null;
                        switch ((int) drawerItem.getIdentifier()) {
                            case -1:
                                if (ThirdPartyManager.getInstance().isFacebookLogin()) {
                                    logout();
                                } else {
                                    login();
                                }
                                break;
//                            case 7:
//                                try {
//                                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
//
//                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + MMMConfig.FACEBOOK_FANS_ID));
//                                    startActivity(intent);
//                                } catch (Exception e) {
//                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MMMConfig.FACEBOOK_FANS_URL)));
//                                }
//                                break;
                            case 1001:
                                contentFragment = IndexFragment.newInstance();
                                replaceChildFragment(R.id.contentFrameLayout_home, contentFragment);
                                break;
                            case 1003:
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                                }
                                break;
                            case 1004:
                                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:vmgsahm1@gmail.com"));
                                intent.putExtra(Intent.EXTRA_SUBJECT, "[Komica+]");
                                startActivity(intent);
                                break;
//                            case 10:
//                                try {
//                                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
//
//                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + MMMConfig.FACEBOOK_FANS_ID));
//                                    startActivity(intent);
//                                } catch (Exception e) {
//                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MMMConfig.FACEBOOK_FANS_URL)));
//                                }
//                                break;
                            default:
                                int count = 0;
                                if (drawerItem.getIdentifier() > 0 && drawerItem.getIdentifier() < 1000) {
                                    KomicaMenuMember clickMember = KomicaManager.getInstance().findMemberByMemberId((int) drawerItem.getIdentifier());
                                    if (clickMember != null) {
                                        BaseFragment tmpFragment = getWebFormatFragment(clickMember);
                                        if (contentFragment == null || !contentFragment.isAdded()) {
                                            contentFragment = tmpFragment;
                                            replaceChildFragment(R.id.contentFrameLayout_home, contentFragment);
                                        } else if ((contentFragment != null && contentFragment.isAdded())) {
                                            if (!checkTwoFragmentInstance(contentFragment, tmpFragment)) {
                                                contentFragment = tmpFragment;
                                                replaceChildFragment(R.id.contentFrameLayout_home, contentFragment);
                                            } else {
                                                loadUrl(clickMember.getLinkUrl());
                                                getActivity().setTitle(clickMember.getTitle());
                                            }
                                        }
                                    }
                                } else {
                                    return false;
                                }
                                break;
                        }
                        drawer.closeDrawer();
                        return true;
                    }
                });

    }

    private boolean checkTwoFragmentInstance(BaseFragment a, BaseFragment b) {
        if (a instanceof SectionPreviewFragment && b instanceof SectionPreviewFragment) {
            return true;
        } else if (a instanceof WebViewFragment && b instanceof WebViewFragment) {
            return true;
        }
        return false;
    }

    private BaseFragment getWebFormatFragment(KomicaMenuMember clickMember) {
        switch (KomicaManager.getInstance().checkWebType(clickMember.getTitle())) {
            case KomicaManager.WebType.NORMAL:
                return SectionPreviewFragment.newInstance(clickMember.getLinkUrl());
            case KomicaManager.WebType.WEB:
            default:
                getActivity().setTitle(clickMember.getTitle());
                return WebViewFragment.newInstance(clickMember.getLinkUrl());
        }

    }

    private void loadUrl(String url) {
        if (contentFragment instanceof SectionPreviewFragment) {
            ((SectionPreviewFragment) contentFragment).loadNewSection(url);
        } else if (contentFragment instanceof WebViewFragment) {
            ((WebViewFragment) contentFragment).loadUrl(url);
        }
    }

    public void notifyDrawerBuild() {
        if (null == getActivity()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawer = drawerBuilder
                        .addDrawerItems(getDynamicalDrawerItems())
                        .build();
            }
        });
    }

    private PrimaryDrawerItem getColorSetting(PrimaryDrawerItem item) {
        return item.withIconColor(ContextCompat.getColor(getContext(), R.color.material_drawer_primary_text))
                .withSelectedIconColor(ContextCompat.getColor(getContext(), R.color.material_drawer_dark_selected_text))
                .withIconTintingEnabled(true);
    }

    private List<IDrawerItem> getMainDrawerItems() {
        List<IDrawerItem> drawerItemList = new ArrayList<>();
        drawerItemList.add(getColorSetting(new PrimaryDrawerItem().withIdentifier(1001).withName(R.string.home_page).withIcon(R.drawable.ic_home)));
        drawerItemList.add(getColorSetting(new PrimaryDrawerItem().withIdentifier(1002).withName(R.string.sponsor).withIcon(R.drawable.ic_sponsor)));
        drawerItemList.add(new DividerDrawerItem());
        drawerItemList.add(new SecondaryDrawerItem().withName(R.string.others).withSelectable(false));
        drawerItemList.add(getColorSetting(new PrimaryDrawerItem().withIdentifier(1003).withName(R.string.rating).withIcon(R.drawable.ic_rate).withSelectable(false)));
        drawerItemList.add(getColorSetting(new PrimaryDrawerItem().withIdentifier(1004).withName(R.string.feedback).withIcon(R.drawable.ic_feedback).withSelectable(false)));
        return drawerItemList;
    }

    private IDrawerItem[] getDynamicalDrawerItems() {
        List<IDrawerItem> drawerItemList = getMainDrawerItems();
        for (KomicaMenuGroup group : KomicaManager.getInstance().getMenuGroupList()) {
            drawerItemList.add(new DividerDrawerItem());
//            drawerItemList.add(new SecondaryDrawerItem().withName(group.getTitle()).withTextColor(Color.RED));
            List<IDrawerItem> memberList = new ArrayList<>();
            for (KomicaMenuMember member : group.getMemberList()) {
                if ("Komica2".equals(member.getTitle())) {
                    continue;
                }
                memberList.add(
                        getColorSetting(
                                new PrimaryDrawerItem().withIdentifier(member.getMemberId()).withName(member.getTitle())
                        )
                );
            }
            ExpandableDrawerItem expandableDrawerItem =
                    new ExpandableDrawerItem().withArrowColorRes(R.color.colorPrimary)
                            .withName(group.getTitle())
                            .withTextColor(Color.RED)
                            .withSelectable(false).withSubItems(memberList);
            drawerItemList.add(expandableDrawerItem);
        }
        IDrawerItem[] items = new IDrawerItem[drawerItemList.size()];
        return drawerItemList.toArray(items);
    }

    private void checkFacebookLogin() {
        refreshLoginItem();
        refreshAccountPhoto();
    }

    private void login() {
        ThirdPartyManager.getInstance().loginFacebook(this);
    }

    private void logout() {
        ThirdPartyManager.getInstance().logoutFacebook();
        KomicaAccountManager.getInstance().logout();
        checkFacebookLogin();
    }

    private void refreshLoginItem() {
        loginItem
                .withIdentifier(-1)
                .withName(ThirdPartyManager.getInstance().isFacebookLogin() ? R.string.logout_facebook : R.string.login_facebook)
                .withTextColor(ThirdPartyManager.getInstance().isFacebookLogin() ? Color.GRAY : Color.BLUE)
                .withIcon(CommunityMaterial.Icon.cmd_logout);
        drawer.updateStickyFooterItemAtPosition(loginItem, 1);
    }

    private void refreshAccountPhoto() {
        final MyAccount myAccount = KomicaAccountManager.getInstance().getMyAccount();
        if (!ThirdPartyManager.getInstance().isFacebookLogin()) {
            profileDrawerItem.withIcon(R.drawable.anonymous).withName(myAccount.getUsername()).withEmail(myAccount.getEmail());
            headerResult.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.wallpaper));
            headerResult.updateProfile(profileDrawerItem);
            return;
        }
        Glide.with(this).load(myAccount.getHeaderPic())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        profileDrawerItem.withIcon(resource).withName(myAccount.getUsername()).withEmail(myAccount.getEmail());
                        headerResult.updateProfile(profileDrawerItem);
                    }
                });

        Glide.with(this).load(ThirdPartyManager.getInstance().isFacebookLogin() ? myAccount.getCoverPic() : R.color.md_blue_500).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                headerResult.setBackground(new BitmapDrawable(getResources(), resource));
                headerResult.updateProfile(headerResult.getActiveProfile());
            }
        });

    }

    @Override
    public boolean isBackPressed() {
        if (null != contentFragment) {
            return contentFragment.isBackPressed();
        }
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return false;
        }
        return super.isBackPressed();
    }
}
