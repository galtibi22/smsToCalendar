package tbject.com.smstocalendar;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import tbject.com.smstocalendar.analyzers.SmsEventAnalyzer;

@RunWith(AndroidJUnit4.class)
public class SmsEventAnalyzerTests {

    @Test
    public void initialScreeningKeys() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        SmsEventAnalyzer smsEventAnalyzer=new SmsEventAnalyzer(context);
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.data_set_possitve)));
        try {
            String line;
            int counterFound=0;
            int totle=0;
            while ((line = reader.readLine()) != null) {
           // line="לקוח יקר, משלוח לאומי קארד בדרך אליך מחר ביום ה בין השעות 12-15. נא הכן ת.ז,לבירורים ניתן לפנות למוקד פדקס בטלפון: ";
                    if (smsEventAnalyzer.analyzeSms(line)) {
                        counterFound++;
                        //Log.d("DEBUG","Create smsEvent"+smsEventAnalyzer.getSmsEvent());
                    }
                totle++;
            }
            System.out.println("found total smsEvent:"+counterFound+"/"+totle);
        }catch (Exception e){
                e.printStackTrace();
            }





    }
}
