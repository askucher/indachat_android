/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Web3 Space, 2019.
 */

package org.indachat.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import android.widget.FrameLayout;

import org.indachat.messenger.LocaleController;
import org.indachat.messenger.NotificationCenter;
import org.indachat.messenger.R;
import org.indachat.ui.ActionBar.ActionBar;
import org.indachat.ui.ActionBar.ActionBarMenu;
import org.indachat.ui.ActionBar.BaseFragment;
import org.indachat.ui.ActionBar.Theme;
import org.indachat.ui.ActionBar.ThemeDescription;
import org.indachat.ui.Components.EmptyTextProgressView;
import org.indachat.ui.Components.LayoutHelper;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import org.indachat.jsbridge.BridgeHandler;
import org.indachat.jsbridge.BridgeWebView;
import org.indachat.jsbridge.CallBackFunction;
import org.indachat.jsbridge.DefaultHandler;
import com.google.gson.Gson;



public class WalletActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private EmptyTextProgressView emptyView;

    private BridgeWebView webView;

    static class Request {
        String token;
        String method;
        String[] args;
    }

    public WalletActivity(Bundle args) {
        super(args);


    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

        //delegate = null;
    }

    @Override
    public View createView(Context context) {


        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Wallet", R.string.Wallet));



        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == 1) {
                    //refresh
                    //presentFragment(new NewContactActivity());
                    refreshPressed();
                } else if (id == 2) {
                    //lock
                    lockPressed();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(1, R.drawable.update);
        menu.addItem(2, R.drawable.ic_lock_white);


        fragmentView = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);

            }
        };
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        webView = new BridgeWebView(context);
        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebChromeClient(new WebChromeClient() {});
        webView.loadUrl("file:///android_asset/wallet.html");

        frameLayout.addView(webView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));



        return fragmentView;
    }


    public void refreshPressed() {


            Request request = new Request();
            request.method = "refresh";
            request.args = new String[]{};
            webView.callHandler("walletRPC", new Gson().toJson(request), (CallBackFunction) data -> {

            });
        //}
        //catch(Exception err) {
        //    new AlertDialog.Builder(context).setTitle("Delete entry").setMessage("Are you sure you want to delete this entry?").show();
        //}
    }

    public void lockPressed() {
        Request request = new Request();
        request.method = "lock";
        request.args = new String[]{};
        webView.callHandler("walletRPC", new Gson().toJson(request), (CallBackFunction) data -> {

        });
    }

    public void chooseNetwork(String net, CallBackFunction callback) {
        Request request = new Request();
        request.method = "use";
        request.args = new String[]{net};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);
    }



    public void setTheme(String theme, CallBackFunction callback) {
        Request request = new Request();
        request.method = "setTheme";
        request.args = new String[]{theme};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);
    }


    public void getBalance(String token, CallBackFunction callback) {
        Request request = new Request();
        request.token = token;
        request.method = "getBalance";
        request.args = new String[]{};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);
    }

    public void getAddress(String token, CallBackFunction callback) {
        Request request = new Request();
        request.token = token;
        request.method = "getAddress";
        request.args = new String[]{};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);
    }


    public void getSupportedTokens(CallBackFunction callback) {
        Request request = new Request();
        request.method = "getSupportedTokens";
        request.args = new String[]{};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);
    }

    public void sendTransaction(String token, String to, String amount, CallBackFunction callback) {

        Request request = new Request();
        request.token = token;
        request.method = "sendTransaction";
        request.args = new String[]{to, amount};
        webView.callHandler("walletRPC", new Gson().toJson(request), callback);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);

    }



    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
    }




    @Override
    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = () -> {
        };

        return new ThemeDescription[]{
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder),


                new ThemeDescription(emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder),

                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue),
                new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink),

                };
    }
}
