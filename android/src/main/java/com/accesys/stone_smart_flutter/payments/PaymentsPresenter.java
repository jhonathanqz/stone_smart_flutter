package com.accesys.stone_smart_flutter.payments;

import android.content.Context;
import android.util.Log;


import io.flutter.plugin.common.MethodChannel;
import io.reactivex.disposables.Disposable;

public class PaymentsPresenter {

    private PaymentsUseCase mUseCase;
    private PaymentsFragment mFragment;
    private Disposable mSubscribe;
    private Boolean hasAborted = false;
    private int countPassword = 0;

    //@Inject
    public PaymentsPresenter(MethodChannel channel) {
        this.mUseCase = new PaymentsUseCase(channel);
        this.mFragment = new PaymentsFragment(channel);
    }



    public void doTransaction(Context context,
                              String amount,
                              int typeTransaction,
                              int parc,
                              boolean withInterest) {
        mUseCase.transaction(context, amount, typeTransaction, parc, withInterest);
    }

    public void activate(String appName,
                         String stoneCode,
                         Context context) {
        Log.d("print", "*** ATIVANDO PINPAD: " + stoneCode);
        mFragment.onMessage("Ativando terminal");
        mUseCase.initializeAndActivatePinpad(appName, stoneCode, context);
        Log.d("print", "*** FIM ATIVAÇÃO: ");
    }

    public void cancelCurrentTransaction(Context context, String amount) {
        mUseCase.cancelCurrentTransaction(context, amount);
    }

    public void cancelTransaction(Context context, String amount, int typeTransaction) {
        mUseCase.cancelTransaction(context, amount, typeTransaction);
    }



    public void dispose() {
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
    }
}
