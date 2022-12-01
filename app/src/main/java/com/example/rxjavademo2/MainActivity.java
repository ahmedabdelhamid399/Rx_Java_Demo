package com.example.rxjavademo2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    EditText hint;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hint = findViewById(R.id.hint_tv);

        Observable.create(new ObservableOnSubscribe<Object>()
                {
                    //UP_STREAM it is the data the come from observable before passing it to observer

                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception
                    {
                        //Emitter take changes to observable

                        hint.addTextChangedListener(new TextWatcher()
                                //text watcher to track the characters that i type
                        {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                            {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                            {
                                if (charSequence.length() != 0)
                                {
                                    emitter.onNext(charSequence);
                                }//end if
                            }//end onTextChanged()

                            @Override
                            public void afterTextChanged(Editable editable)
                            {

                            }
                        });//end addTextChangedListener()

                    }//end subscribe()

                })
                //doOnNext happens in Observable(UPStream)
                .doOnNext(o -> Log.d(TAG, "UPStream: "+o))
                // #map operator
               /* .map(new Function<Object, Object>()
                {   // map operator takes the data in up stream an do some operations
                    // then send it to the down stream
                    @Override
                    public Object apply(Object o) throws Throwable
                    {
                        return Integer.parseInt(o.toString())*2;
                    }//end apply()
                })//end map
                */

                // #debounce Operator
                    //wait for 2 seconds
                /*.debounce(2, TimeUnit.SECONDS)*/

                // distinctUntilChanged operator
                //this operator won't send data to downStream unitl happens changes
                /*.distinctUntilChanged()*/

                // #flatmap Operator
                .flatMap(new Function<Object, ObservableSource<?>>()
                {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Throwable
                    {
                        return sendDataToAPI(o.toString());
                    }//end apply()
                })//end flatmap Operator

                //to make filter for any data
                //using filter Operator
                //.filter(c->! c.toString().equals("ahmed"))

                .debounce(2, TimeUnit.SECONDS)
                .distinctUntilChanged()

                //subscribe happens in Observer(DownStream)
                .subscribe(c -> Log.d(TAG, "DownStream hit API: "+c))
        ;//end Observable.create()
    }//end onCreate()

    public Observable sendDataToAPI(String data)
    {
        Observable observable = Observable.just("Calling API to send data: " +data);
        observable.subscribe(b-> Log.d(TAG, "sendDataToAPI: "+b));
        return observable;
    }//end sendDataToAPI()
}//end class MainActivity{}