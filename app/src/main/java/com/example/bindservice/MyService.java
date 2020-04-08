package com.example.bindservice;
//*BindService綁定介紹 Locale
//*在 Local Service 中我們直接繼承 Binder 而不是 IBinder,因為 Binder 實現了 IBinder 介面，這樣我們可以少做很多工作。
//1.創建一個Binder類別來綁定此Service物件
//2.宣告我的LocaleBinder讓OnBind回傳 => private final IBinder mBinder = new localeBind();
//3.onBind時回傳我的Binder => return mBinder;
//4.Service寫一個產生亂數方法,讓Activity透過ServiceConnection取得Service的方法來玩
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    String TAG ="hank";
    //2.宣告我的LocaleBinder讓OnBind回傳
    private final IBinder mBinder = new LocaleBind();

    //1.綁定此Service的物件
    public class LocaleBind extends Binder { //聲明一個 Binder 類的實現類，供在 onBind() 方法中返回該類的一個實例
           //回傳這個MyService
            MyService getService () { //取得我Service的方法,這邊可以讓Acitvity取得
                Log.v(TAG,"LocaleBind =>" + "getService");
                return MyService.this;
            }

    }
    //Activity傳過來onBind被呼叫接收
    @Override
    public IBinder onBind(Intent intent) {
        //3.回傳我的Binder
        Log.v(TAG,"onBind =>" + "intent:" + intent);
        return mBinder;
    }

    //產生亂數方法
    public int doSomeThing(){
        Log.v(TAG,"Service doSomeThing..");
        return (int)(Math.random()*49+1);
    }

}
