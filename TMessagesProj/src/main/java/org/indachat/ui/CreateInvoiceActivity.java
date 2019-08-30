/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2017.
 */

package org.indachat.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.indachat.jsbridge.CallBackFunction;
import org.indachat.messenger.AndroidUtilities;
import org.indachat.PhoneFormat.PhoneFormat;
import org.indachat.messenger.ContactsController;
import org.indachat.messenger.LocaleController;
import org.indachat.tgnet.TLRPC;
import org.indachat.messenger.MessagesController;
import org.indachat.messenger.NotificationCenter;
import org.indachat.messenger.R;
import org.indachat.ui.ActionBar.ActionBar;
import org.indachat.ui.ActionBar.ActionBarMenu;
import org.indachat.ui.ActionBar.AlertDialog;
import org.indachat.ui.ActionBar.Theme;
import org.indachat.ui.ActionBar.ThemeDescription;
import org.indachat.ui.Components.AvatarDrawable;
import org.indachat.ui.Components.BackupImageView;
import org.indachat.ui.ActionBar.BaseFragment;
import org.indachat.ui.Components.EditTextBoldCursor;
import org.indachat.ui.Components.LayoutHelper;

public class CreateInvoiceActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private EditTextBoldCursor amountField;
    private EditTextBoldCursor currencyField;
    private Spinner currencyChoose;

    private int user_id;

    private final static int done_button = 1;

    public CreateInvoiceActivity() {

    }

    @Override
    public boolean onFragmentCreate() {
        //NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        //user_id = getArguments().getInt("user_id", 0);
        //TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(user_id);
        //return user != null && super.onFragmentCreate();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    private void walletIsNotReady(Context context) {
        AlertDialog progressDialog = new AlertDialog(context, 2);
        progressDialog.setMessage(LocaleController.getString("WalletNotReady", R.string.WalletNotReady));
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(dialog -> { this.removeSelfFromStack(); });
        progressDialog.show();
    }

    public void receiveInvoiceMock(String invoice, Context context) {

        String[] invoiceParts = invoice.split("/");

        String amount  = invoiceParts[0];
        String token   = invoiceParts[1];
        String address = invoiceParts[2];

        WalletActivity wallet = WalletActivity.instance;

        if (wallet == null) {
            walletIsNotReady(context);
            return;
        }

        wallet.getAddress("btc", (x)-> {

            if (x.getClass() != String.class) {
                walletIsNotReady(context);
                return;
            }

            wallet.sendTransaction(token, address, amount, (tx)-> {

                //Process TX

            });


        });

    }

    @Override
    public View createView(Context context) {

        WalletActivity wallet = WalletActivity.instance;

        fragmentView = new ScrollView(context);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Invoice", R.string.Invoice));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {

                    String amount = amountField.getText().toString();
                    String token = amountField.getText().toString();

                    wallet.getAddress(token, (address)->{

                        String invoice = String.format("%s/%s/%s", amount, token, address);

                        //SEND INVOICE

                    });
                }
            }
        });



        ActionBarMenu menu = actionBar.createMenu();
        menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));


        if(wallet == null) {
            walletIsNotReady(context);
            return fragmentView;
        }


        wallet.getAddress("btc", (x)-> {

            if (x.getClass() != String.class) {
                walletIsNotReady(context);
                return;
            }

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ((ScrollView) fragmentView).addView(linearLayout, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
            linearLayout.setOnTouchListener((v, event) -> true);

            FrameLayout frameLayout = new FrameLayout(context);
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 24, 24, 0));

            amountField = new EditTextBoldCursor(context);
            amountField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            amountField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            amountField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            amountField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            amountField.setMaxLines(1);
            amountField.setLines(1);
            amountField.setSingleLine(true);
            amountField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
            amountField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            amountField.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            amountField.setHint(LocaleController.getString("Amount", R.string.Amount));
            amountField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            amountField.setCursorSize(AndroidUtilities.dp(20));
            amountField.setCursorWidth(1.5f);
            linearLayout.addView(amountField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 24, 24, 0));
            amountField.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    currencyField.requestFocus();
                    currencyField.setSelection(currencyField.length());
                    return true;
                }
                return false;
            });


            /*
            currencyField = new EditTextBoldCursor(context);
            currencyField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            currencyField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            currencyField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            currencyField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
            currencyField.setMaxLines(1);
            currencyField.setLines(1);
            currencyField.setSingleLine(true);
            currencyField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
            currencyField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            currencyField.setImeOptions(EditorInfo.IME_ACTION_DONE);
            currencyField.setHint(LocaleController.getString("Currency", R.string.Currency));
            currencyField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            currencyField.setCursorSize(AndroidUtilities.dp(20));
            currencyField.setCursorWidth(1.5f);
            linearLayout.addView(currencyField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 24, 16, 24, 0));
            currencyField.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    //doneButton.performClick();
                    return true;
                }
                return false;
            });

             */

            String[] paths = {"BTC", "ETH"};

            currencyChoose = new Spinner(context);

            ArrayAdapter<String>adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, paths);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            currencyChoose.setAdapter(adapter);
            //currencyChoose.setOnItemSelectedListener(context);
            linearLayout.addView(currencyChoose, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 36, 20, 16, 20, 0));


        });


        return fragmentView;
    }



    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            //int mask = (Integer) args[0];

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            //firstNameField.requestFocus();
        }
    }

    @Override
    public ThemeDescription[] getThemeDescriptions() {

        return new ThemeDescription[]{
                new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite),

                new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle),
                new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector),

                new ThemeDescription(amountField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(amountField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText),
                new ThemeDescription(amountField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField),
                new ThemeDescription(amountField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated),
                new ThemeDescription(currencyField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText),
                new ThemeDescription(currencyField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText),
                new ThemeDescription(currencyField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField),
                new ThemeDescription(currencyField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated),


        };
    }
}
