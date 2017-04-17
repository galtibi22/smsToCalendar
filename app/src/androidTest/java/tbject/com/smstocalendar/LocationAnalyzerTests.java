package tbject.com.smstocalendar;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import tbject.com.smstocalendar.services.LocationService;

/**
 * Created by gal.tibi on 12-Apr-17.
 */
@RunWith(AndroidJUnit4.class)

public class LocationAnalyzerTests {

    @Test
    public void locationServiceTest(){
        Context context = InstrumentationRegistry.getTargetContext();
        LocationService locationService=new LocationService(context);
       // boolean success=locationService.checkAddress("גולני 8 כפר סבא");
   //     Log.w("TEST","Status of LocationAnalyzerTest: "+success);
    }
}
