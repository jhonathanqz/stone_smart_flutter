package com.qztech.stone_smart_flutter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import com.qztech.stone_smart_flutter.core.StoneSmart;
import com.qztech.stone_smart_flutter.payments.PaymentsFragment;
import com.qztech.stone_smart_flutter.payments.PaymentsPresenter;
import com.qztech.stone_smart_flutter.payments.PaymentsUseCase;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class StoneSmartFlutterPlugin
  implements FlutterPlugin, MethodCallHandler {

  private static final String CHANNEL_NAME = "stone_smart_flutter";
  private MethodChannel channel;
  private Context context;
  private StoneSmart stoneSmart;

  public StoneSmartFlutterPlugin() {}

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
    //Get context to application
    context = binding.getApplicationContext();
    channel.setMethodCallHandler(this);
    //Create instance to Stone Smart class
    stoneSmart = new StoneSmart(context, channel);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    //Function responsible for listening to methods called by flutter
    if (call.method.startsWith("payment")) {
      stoneSmart.initPayment(call, result);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    //Dispose plugin
    channel.setMethodCallHandler(null);
    channel = null;
    stoneSmart.dispose();
    stoneSmart = null;
  }
}
