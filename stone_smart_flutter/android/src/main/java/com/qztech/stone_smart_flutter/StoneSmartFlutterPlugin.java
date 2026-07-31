package com.qztech.stone_smart_flutter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.qztech.stone_smart_flutter.core.StoneSmart;
import com.qztech.stone_smart_flutter.payments.PaymentsFragment;
import com.qztech.stone_smart_flutter.payments.PaymentsPresenter;
import com.qztech.stone_smart_flutter.payments.PaymentsUseCase;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class StoneSmartFlutterPlugin implements FlutterPlugin, MethodCallHandler {

  private static final String CHANNEL_NAME = "stone_smart_flutter";
  private MethodChannel channel;
  private Context context;
  private StoneSmart stoneSmart;

  public StoneSmartFlutterPlugin() {}

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
    context = binding.getApplicationContext();
    channel.setMethodCallHandler(this);
    stoneSmart = new StoneSmart(context, channel);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    // Delegue as chamadas para a classe StoneSmart conforme o método
    if (call.method.startsWith("payment")) {
      stoneSmart.initPayment(call, result);
    } else if (call.method.equals("print")) {
      String text = call.argument("text");
      stoneSmart.print(text, result);
    } else if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
    if (stoneSmart != null) {
      stoneSmart.dispose();
      stoneSmart = null;
    }
  }
}
