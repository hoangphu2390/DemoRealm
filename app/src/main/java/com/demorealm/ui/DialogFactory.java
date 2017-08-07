package com.Nappr.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Nappr.adapter.ChildNapTimerAdapter;
import com.Nappr.adapter.DialogNapByAddressAdapter;
import com.Nappr.adapter.PaymentInfoAdapter;
import com.Nappr.adapter.RangeTimerWindowAdapter;
import com.Nappr.adapter.SelectBookingNapAdapter;
import com.Nappr.home.NavigationScreen;
import com.Nappr.listener.HostCancelBookedNapListener;
import com.Nappr.listener.ResultEditPaymentListener;
import com.Nappr.listener.EditTimerListener;
import com.Nappr.listener.InputEmailListener;
import com.Nappr.listener.SelectPaymentListener;
import com.Nappr.listener.SelectTimerListener;
import com.Nappr.pojo.DetailMyNap;
import com.Nappr.pojo.DetailNap.SelectBookingNap;
import com.Nappr.pojo.SearchNap;
import com.Nappr.pojo.StripeCard;
import com.app.Nappr.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.stripe.android.BuildConfig;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kiet Nguyen on 01-Jan-17.
 */

public class DialogFactory {
    private static final String[] arrtime = {"12:00 AM", "12:30 AM", "01:00 AM", "01:30 AM", "02:00 AM", "02:30 AM", "03:00 AM", "03:30 AM",
            "04:00 AM", "04:30 AM", "05:00 AM", "05:30 AM", "06:00 AM", "06:30 AM", "07:00 AM", "07:30 AM", "08:00 AM",
            "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "01:00 PM ", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM",
            "05:30 PM", "06:00 PM", "06:30 PM", "07:00 PM", "07:30 PM", "08:00 PM", "08:30 PM", "09:00 PM", "09:30 PM",
            "10:00 PM", "10:30 PM", "11:00 PM", "11:30 PM", "11:59 PM"};

    private static final String[] arrtime_convert = {"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30",
            "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00",
            "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00",
            "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30",
            "22:00", "22:30", "23:00", "23:30", "23:59"};

    private static int minTime, maxTime;
    private static String start_time, end_time, nap_time_id, payment_id, stripe_number, stripe_exp_month, stripe_exp_year;
    private static LinearLayout layout_booking_nap;
    private static List<SelectBookingNap> selectBookNapCurrent;
    private static List<SelectBookingNap> listSelectBookNap;
    private static SelectBookingNapAdapter selectBookingNapAdapter;
    private static ProgressDialog progressDialog;
    private static Context _context;
    private static ListView lv_edit_nap_timer;
    private static List<String> listTimeConvert;
    private static StripeCard stripeCard;
    private static CardValidCallback cardValidCallback;
    private static CreditCardForm creditCardForm;
    private static Button btn_edit;
    private static ResultEditPaymentListener resultEditPaymentListener;

    public static Dialog createConfirmDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_inbox);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btnLeft = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnRight = (Button) dialog.findViewById(R.id.btnAccept);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createInputEmailDialog(final Context context, final InputEmailListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_input_email);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btnLeft = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnRight = (Button) dialog.findViewById(R.id.btnAccept);
        final EditText edtEmail = (EditText) dialog.findViewById(R.id.edt_email);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                if (!Utils.isValidEmail(email)) {
                    Toast.makeText(context, "Email is Invalid", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onInputListener(email);
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createEditPaymentDialog(final Context context, StripeCard card, final ResultEditPaymentListener listener) {
        resultEditPaymentListener = listener;
        _context = context;
        payment_id = String.valueOf(card.id);

        stripe_number = String.valueOf(card.stripe_number);
        stripe_exp_month = String.valueOf(card.stripe_exp_month);
        stripe_exp_year = String.valueOf(card.stripe_exp_year);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_payment);

        btn_edit = (Button) dialog.findViewById(R.id.btn_edit);
        creditCardForm = (CreditCardForm) dialog.findViewById(R.id.credit_card_form);
        creditCardForm.setOnCardValidCallback(cardValidCallback);
        creditCardForm.setCardNumber(card.stripe_number, true);
        creditCardForm.setExpDate(card.stripe_exp_month + "/" + card.stripe_exp_year, true);

        cardValidCallback = new CardValidCallback() {
            @Override
            public void cardValid(CreditCard card) {
                stripeCard = new StripeCard();
                stripeCard.stripe_number = card.getCardNumber();
                stripeCard.stripe_exp_month = String.valueOf(card.getExpMonth());
                stripeCard.stripe_exp_year = String.valueOf(card.getExpYear());
            }
        };

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_edit.setEnabled(false);

                if (Utils.isCheckShowSoftKeyboard(NavigationScreen.self))
                    Utils.hideSoftKeyboard(NavigationScreen.self);
                progressDialog = new ProgressDialog(_context);
                progressDialog.setMessage("Please Wait.....");
                progressDialog.show();
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);

                final String publishableApiKey = BuildConfig.DEBUG ?
                        "pk_test_6pRNASCoBOKtIshFeQd4XMUh" :
                        context.getString(R.string.com_stripe_publishable_key);

                final Card card = new Card(creditCardForm.getCreditCard().getCardNumber(),
                        creditCardForm.getCreditCard().getExpMonth(),
                        creditCardForm.getCreditCard().getExpYear(),
                        creditCardForm.getCreditCard().getSecurityCode());

                Stripe stripe = new Stripe(NavigationScreen.self);
                stripe.createToken(card, publishableApiKey, new TokenCallback() {
                    public void onSuccess(Token token) {
                        progressDialog.dismiss();
                        stripe_number = card.getNumber();
                        stripe_exp_month = String.valueOf(card.getExpMonth());
                        stripe_exp_year = String.valueOf(card.getExpYear());
                        new EditBillingAsyncTask().execute(ProtocolAddress.IPAddress + "v1/payment/update");
                    }

                    public void onError(Exception error) {
                        Toast.makeText(NavigationScreen.self, "Card information invalid or not completed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        btn_edit.setEnabled(true);
                    }
                });
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createConfirmDialog(Context context, String title, String leftBtn, String rightBtn) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_inbox);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.txtHeading);
        Button btnLeft = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnRight = (Button) dialog.findViewById(R.id.btnAccept);

        tvTitle.setText(title);
        btnLeft.setText(leftBtn);
        btnRight.setText(rightBtn);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog showDialogNotification(Context context, String title) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notification);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.txtHeading);
        Button btn_ok = (Button) dialog.findViewById(R.id.btnOK);

        tvTitle.setText(title);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog editTimerDialog(final Context context, final List<SelectBookingNap> listSelectingBookNap,
                                         final int time_id, int pos_min, int pos_max, final EditTimerListener listener) {
        _context = context;
        selectBookNapCurrent = new ArrayList<>();
        listSelectBookNap = new ArrayList<>();
        listTimeConvert = new ArrayList<>();
        addValue_SelectingTime(listSelectingBookNap);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_timer_book_nap);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        Button btnAdd = (Button) dialog.findViewById(R.id.btn_add);
        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btn_close);
        CrystalRangeSeekbar seekbar = (CrystalRangeSeekbar) dialog.findViewById(R.id.rangeSeekbar);
        final TextView txt_mintimerange = (TextView) dialog.findViewById(R.id.txt_mintimerange);
        final TextView txt_maxtimerange = (TextView) dialog.findViewById(R.id.txt_maxtimerange);
        layout_booking_nap = (LinearLayout) dialog.findViewById(R.id.layout_booking_nap);
        lv_edit_nap_timer = (ListView) dialog.findViewById(R.id.lv_edit_nap_timer);


        txt_mintimerange.setText(arrtime[pos_min]);
        txt_maxtimerange.setText(arrtime[pos_max]);
        minTime = pos_min;
        maxTime = pos_max;
        seekbar.setMinValue(pos_min);
        seekbar.setMaxValue(pos_max);

        seekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                minTime = Integer.parseInt(String.valueOf(minValue));
                maxTime = Integer.parseInt(String.valueOf(maxValue));
                txt_mintimerange.setText(arrtime[Integer.parseInt(String.valueOf(minValue))]);
                txt_maxtimerange.setText(arrtime[Integer.parseInt(String.valueOf(maxValue))]);
            }
        });

        if (listSelectingBookNap.size() > 0) {
            layout_booking_nap.setVisibility(View.VISIBLE);
            listSelectBookNap.addAll(listSelectingBookNap);
            selectBookingNapAdapter = new SelectBookingNapAdapter(_context, listSelectBookNap);
            lv_edit_nap_timer.setAdapter(selectBookingNapAdapter);
            Utils.setListViewHeightBasedOnChildren(lv_edit_nap_timer);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectBookNapCurrent.size() == 0) {
                    Toast.makeText(context, "Please add at least one time slot", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onSubmitListener(selectBookNapCurrent);
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_time = arrtime_convert[minTime] + ":00";
                end_time = arrtime_convert[maxTime] + ":00";
                nap_time_id = String.valueOf(time_id);
                if (checkTimeValid(minTime, maxTime))
                    new CheckTimerValidAsync().execute(ProtocolAddress.IPAddress + "v1/nap_time/check-book");
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createInfoRateDialog(Context context, DetailMyNap.BookedUser user) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rate_napper);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        RatingBar rtbCommunication = (RatingBar) dialog.findViewById(R.id.ratingBar_communication);
        RatingBar rdbPuctuality = (RatingBar) dialog.findViewById(R.id.ratingBar_puctuality);
        RatingBar rdbOverall = (RatingBar) dialog.findViewById(R.id.ratingBar_overall);
        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rtbCommunication.setRating(Float.parseFloat(user.communication));
        rdbPuctuality.setRating(Float.parseFloat(user.punctuality));
        rdbOverall.setRating(Float.parseFloat(user.overall));
        rtbCommunication.setEnabled(false);
        rdbPuctuality.setEnabled(false);
        rdbOverall.setEnabled(false);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createChildTimerBookedDialog(Context context, List<DetailMyNap.BookingNap> listBookingNap,
                                                      String date, final HostCancelBookedNapListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_child_timer_booked);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        ListView lvChildTimer = (ListView) dialog.findViewById(R.id.lv_child_nap_timer);
        ChildNapTimerAdapter adapter = new ChildNapTimerAdapter(context, listBookingNap, date,
                new HostCancelBookedNapListener() {
                    @Override
                    public void onCancelBookedNap(DetailMyNap.BookingNap bookingNap) {
                        dialog.dismiss();
                        listener.onCancelBookedNap(bookingNap);
                    }
                });
        lvChildTimer.setAdapter(adapter);
        //Utils.setListViewHeightBasedOnChildren(lvChildTimer);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createListTimeDialog(Context context, List<SearchNap.NapTime> listNapTime, final SelectTimerListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_list_timer);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        ListView lv_range_time = (ListView) dialog.findViewById(R.id.lv_range_time);
        RangeTimerWindowAdapter adapter = new RangeTimerWindowAdapter(NavigationScreen.self, listNapTime,
                new RangeTimerWindowAdapter.WindowNapListener() {
                    @Override
                    public void onOpenWindowNap(SearchNap.NapTime napTime) {
                        listener.onSelectTimer(napTime);
                    }
                });
        lv_range_time.setAdapter(adapter);
        Utils.setListViewHeightBasedOnChildren(lv_range_time);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static Dialog createListNapDialog(Context context, List<SearchNap.NapTime> listNapTime, final SelectTimerListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_show_list_nap);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        ListView lv_nap = (ListView) dialog.findViewById(R.id.lv_nap);
        DialogNapByAddressAdapter adapter = new DialogNapByAddressAdapter(NavigationScreen.self, listNapTime,
                new DialogNapByAddressAdapter.SelectNapListener() {
                    @Override
                    public void onSelectNapTime(SearchNap.NapTime napTime) {
                        listener.onSelectTimer(napTime);
                    }
                });
        lv_nap.setAdapter(adapter);
        // Utils.setListViewHeightBasedOnChildren(lv_nap);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    private static class CheckTimerValidAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(_context);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Please wait..");
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String URL_API = params[0];
            String api_token = PreferenceUtils.getFromPrefs(_context, PreferenceUtils.PREFS_ApiToken, null);
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                multipartBody.addFormDataPart("nap_time_id", nap_time_id)
                        .addFormDataPart("start_time", start_time)
                        .addFormDataPart("end_time", end_time)
                        .addFormDataPart("api_token", api_token);

                RequestBody requestBody = multipartBody.build();
                Request request = new Request.Builder()
                        .url(URL_API)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String message = jsonObject.getString("message");
                    boolean res = jsonObject.getBoolean("result");
                    if (res) {
                        layout_booking_nap.setVisibility(View.VISIBLE);
                        String _start_time = arrtime[minTime];
                        String _end_time = arrtime[maxTime];

                        SelectBookingNap selectBookingNap = new SelectBookingNap();
                        selectBookingNap.start_time = _start_time;
                        selectBookingNap.end_time = _end_time;
                        selectBookingNap.nap_time_id = Integer.parseInt(nap_time_id);
                        selectBookingNap.nap_time_option_id = Integer.parseInt(nap_time_id) + randInt(0, 1000);
                        selectBookNapCurrent.add(selectBookingNap);

                        listSelectBookNap.add(selectBookingNap);
                        selectBookingNapAdapter = new SelectBookingNapAdapter(_context, listSelectBookNap);
                        lv_edit_nap_timer.setAdapter(selectBookingNapAdapter);
                        Utils.setListViewHeightBasedOnChildren(lv_edit_nap_timer);
                        listTimeConvert.add(minTime + "-" + maxTime);
                    } else {
                        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO: handle exception
                }
            } else {
                Toast.makeText(_context, "Server Problem Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean checkTimeValid(int min, int max) {
        int minTime, maxTime, count = 0;
        for (int i = 0; i < listTimeConvert.size(); i++) {
            String[] array = listTimeConvert.get(i).split("-");
            minTime = Integer.parseInt(array[0]);
            maxTime = Integer.parseInt(array[1]);
            if (min >= maxTime || max <= minTime)
                count++;
        }
        if (count == listTimeConvert.size()) return true;
        Toast.makeText(_context, "This time has been selected", Toast.LENGTH_SHORT).show();
        return false;
    }

    private static void addValue_SelectingTime(List<SelectBookingNap> listSelectingBookNap) {
        int minTime, maxTime;
        for (SelectBookingNap bookingNap : listSelectingBookNap) {
            String start_time = bookingNap.start_time;
            String end_time = bookingNap.end_time;
            minTime = index_time(start_time);
            maxTime = index_time(end_time);
            if (minTime != -1 && maxTime != -1)
                listTimeConvert.add(minTime + "-" + maxTime);
        }
    }

    private static int index_time(String time) {
        for (int i = 0; i < arrtime.length; i++) {
            if (arrtime[i].equals(time)) return i;
        }
        return -1;
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static Dialog SelectPaymentDialog(final Context context, List<StripeCard> listCard,
                                             final SelectPaymentListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option_payment_info);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TextView tv_another_account = (TextView) dialog.findViewById(R.id.tv_another_account);
        ListView lv_payment = (ListView) dialog.findViewById(R.id.lv_payment);
        PaymentInfoAdapter adapter = new PaymentInfoAdapter(context, listCard, new PaymentInfoAdapter.SelectPaymentListener() {
            @Override
            public void onSelectPayment(StripeCard card) {
                listener.onSelectPaymentListener(card);
            }
        });
        lv_payment.setAdapter(adapter);


        tv_another_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCreatePaymentListener();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    private static class EditBillingAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(NavigationScreen.self);

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            asyncDialog.setMessage("Please Wait.....");
            asyncDialog.show();
            asyncDialog.setCancelable(false);
            asyncDialog.setCanceledOnTouchOutside(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String responsee = null;
            String api_token = PreferenceUtils.getFromPrefs(NavigationScreen.self, PreferenceUtils.PREFS_ApiToken, "");
            HttpClientWritten client = new HttpClientWritten(params[0], NavigationScreen.self);
            try {
                client.connectForMultipart();
                client.addFormPart("id", payment_id);
                client.addFormPart("api_token", api_token);
                client.addFormPart("stripe_number", stripe_number);
                client.addFormPart("stripe_exp_month", stripe_exp_month);
                client.addFormPart("stripe_exp_year", stripe_exp_year);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                client.finishMultipart();
                responsee = client.getResponse();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return responsee;
        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            asyncDialog.dismiss();
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean res = jsonObject.getBoolean("result");
                    Toast.makeText(NavigationScreen.self, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    if (res) {
                        btn_edit.setEnabled(true);
                        resultEditPaymentListener.onResultEditPayment();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(NavigationScreen.self, "Server problem try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Dialog createDialogError(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_error);

        TextView txtError = (TextView) dialog.findViewById(R.id.txtError);
        txtError.setText(message);

        dialog.findViewById(R.id.btnOkay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
        return dialog;
    }
}
