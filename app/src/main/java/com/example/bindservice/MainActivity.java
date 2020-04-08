package com.example.bindservice;
//https://codertw.com/android-%E9%96%8B%E7%99%BC/346510/
//介紹:
//１ Bind Service就像是C/S架構中的服務端，其他組件（比如 Activity）綁定到它（通過 bindService()），可以向它發送請求，可以接受從它返回的響應，它甚至還提供了進程間通信（IPC）功能。
//２ 一個service要想能夠被其他組件綁定，那麼它的 onBind() 方法必須被實現，且必須返回一個 IBinder 對象，然後其他組件可以通過這個 IBinder 對象與該 service 進行通訊。
//３多個client可以綁定至同一個service，但該 service 的onBind() 方法只會在第一個 client 綁定至其的時候被調用，當其他 client 再次綁定到它的時候，並不會調用  onBind() 方法，
// 而是直接返回第一次被調用時產生的那個 IBinder 對象。也就是說，在其生命週期內，onBind() 只會被調用一次。

//如何取得Service:
//應用程式呼叫bindService()與Service綁定前，應用程式需要建立一個serviceConnection物件，呼叫後若Service尚未啟動，Service類別內的onCreate()就會被呼叫，
//使用這種方法啟動Service的應用程式，可以透過onBind()方法取得IBinder物件，接下來就可以透過IBinder物件來取得Service的事件。

//1.一樣直接用套件創建Service再來到Service進行處理
//2.開始建立連線透過bindService,要取得binder裡面的Service物件靠ServiceConnection
//3.玩Service的物件方法每次綁定時都可以玩

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MyService myService;
    private boolean isBound; //是否綁定
    private String TAG = "hank";
    private TextView txtMsg;

    //2.bindService需要過 ServiceConnection 介面來取得建立連線 與 連線意外丟失的回撥
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        //當綁定成功時
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //3.從Service取得Binder,再從中取得MyService物件
            MyService.LocaleBind binder = (MyService.LocaleBind) service; //取得MyService裡的Binder物件
            myService = binder.getService(); //從Binder裡面取得MyService
            isBound = true; //有綁定
            Log.v(TAG, "ServiceConnection => onServiceConnected");
        }

        //當綁定斷開時被呼叫
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false; //未綁定
            Log.v(TAG, "ServiceConnection => onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMsg = findViewById(R.id.txtMsg);
    }

    //1.開始時創建用bindService()
    //abstract bindService(Intent service,erviceConnection conn, @BindServiceFlags int flags)://綁定Service(1,要綁定的IntentService 2.實現ServiceConnection物件 3.flag標誌(除錯/預設))(回傳boolean)
    // 有兩個flag，BIND_DEBUG_UNBIND 與 BIND_AUTO_CREATE，前者用於除錯（詳細內容可以檢視javadoc 上面描述的很清楚），後者預設使用。
    @Override
    protected void onStart() {
        super.onStart();
        //建立bindService
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);//綁定Service(1,要綁定的IntentService 2.實現ServiceConnection物件 3.flag標誌(除錯/預設))
        Log.v(TAG, "onStart()");
    }

    //1.暫停時解除Service綁定
    @Override
    protected void onStop() {
        super.onStop();
        //如果已經暫停了,但仍然有連線,取消連線
        if (isBound) { //如果綁定的話解處綁定
            unbindService(mServiceConnection);
            isBound = false;
        }

        Log.v(TAG, "onStop()");
    }

    //3.如果有綁定,而且Service有進來的話,玩Service的物件方法
    public void callServiceVoid(View view) {
        if (isBound && myService != null) { //如果有綁定,而且Service部為空的話
            int rand = myService.doSomeThing(); //使用Service產生亂數方法
            Log.v(TAG,"MainActivity=>callServiceVoidrand:" + rand);
            txtMsg.setText(""+rand);
        }
    }
}
