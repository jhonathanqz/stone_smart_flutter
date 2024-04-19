package com.qztech.stone_smart_flutter.payments;

import android.content.Context;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.reactivex.disposables.Disposable;
import stone.utils.keys.StoneKeyType;

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
                              boolean withInterest,
                              String qrCodeAuthorization,
                              String qrCodeProviderid,
                              boolean isPrinter) {
        if(qrCodeProviderid == null && qrCodeAuthorization == null){
            mUseCase.initTransaction(context, amount, typeTransaction, parc, withInterest, null, isPrinter);
            return;
        }
        Map<StoneKeyType, String> stoneKeys = new HashMap<StoneKeyType, String>()
        {
            {
                put(StoneKeyType.QRCODE_AUTHORIZATION, "Bearer " + qrCodeAuthorization);
                put(StoneKeyType.QRCODE_PROVIDERID, qrCodeProviderid);
            }
        };
        mUseCase.initTransaction(context, amount, typeTransaction, parc, withInterest, stoneKeys, isPrinter);
    }

    public void printerCurrentTransaction(Context context, boolean isPrinter) {
        mUseCase.printerCurrentTransaction(context, isPrinter);
    }

    public void activate(String appName,
                         String stoneCode,
                         Context context) {
        mFragment.onMessage("Ativando terminal");
        mUseCase.initializeAndActivatePinpad(appName, stoneCode, context);
    }

    public void activateWithCredentials(String appName,
                                        String stoneCode,
                                        String qrCodeAuthorization,
                                        String qrCodeProviderid,
                                        Context context) {
        Map<StoneKeyType, String> stoneKeys = new HashMap<StoneKeyType, String>()
        {
            {
                put(StoneKeyType.QRCODE_AUTHORIZATION, "Bearer " + qrCodeAuthorization);
                put(StoneKeyType.QRCODE_PROVIDERID, qrCodeProviderid);
            }
        };
        mUseCase.initializeAndActivatePinpadWithCredentials(appName, stoneCode, stoneKeys, context);

    }
    public void cancelTransaction(Context context, String amount, int typeTransaction) {
        mUseCase.cancelTransaction(context, amount, typeTransaction);
    }

    public void onReversal(Context context) {
        mUseCase.onReversalTransaction(context);
    }

    public void abortCurrentPosTransaction() {
        mUseCase.abortCurrentPosTransaction();
    }

    public void abortPIXtransaction(Context context){
        mUseCase.abortPIXtransaction(context);
    }



    public void dispose() {
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
    }
}
