package tbject.com.smstocalendar;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import tbject.com.smstocalendar.analyzers.SmsEventAnalyzer;

@RunWith(AndroidJUnit4.class)
public class SmsEventAnalyzerTests {

    @Test
    public void smsEventAnalyzer() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        SmsEventAnalyzer smsEventAnalyzer=new SmsEventAnalyzer(context);
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.data_set_possitve)));
        try {
           String line;
            int counterFound=0;
            int totle=0;
            while ((line = reader.readLine()) != null) {
                    line="נקבע תור ליורי לד\"ר ברש בתאריך 10/04/17 בשעה 10:50 בכתובת דרך בן צבי 2 תל אביב . לידיעתך, ניתן לזמן ולבטל תור לד\"ר ברש באתר מכבי ובאפל יקציה";
                // String line="ערב טוב, מחר ב 08:00 נפגש בביתך";
                // String line1="נקבע תור לאמה לד\"ר גורביץ בתאריך 12/03/17 בשעה 16:10 בכתובת חטיבת גולני 8 כפר סבא . לידיעתך, ניתן לזמן ולבטל תור לד\"ר גורביץ באתר מכבי ובאפליקציה.\n";
              // String line2="שירן,הבחינה ביסודות הביטוח ב: 15/03  בשעה 16:00  בהגימנסיה העברית הרצליה – כיתה 6 ו-3 קומה ג\n";
                if (smsEventAnalyzer.analyzeSms(line,"0523403779")) {
                    counterFound++;
                    Log.i("DEBUG","Create smsEvent"+smsEventAnalyzer.getSmsEvent());
                }
                totle++;
            }
            System.out.println("found total smsEvent:"+counterFound+"/"+totle);
        }catch (Exception e){
                e.printStackTrace();
            }




    }

    @Test
    public void contactInformationTest(){
        Context context = InstrumentationRegistry.getTargetContext();
//        String name=AddressAnalyzer.getContactAddress(context,"0522559408");
       // System.out.println(name);

    }
}
