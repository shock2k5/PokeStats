package shock2k5.pokestats;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;

/**
 * Created by kevincoleman on 10/21/16.
 */

/**
 * I want to make this class a Thread so I can run this at the splash screen and download the entire
 * databse and catagorize everything on the phone. It's about 1MB of stuff, so it shouldn't be too bad.
 * Don't forget to use HashSets. Probably do this after I get the original working.
 */
public class Calculator extends Thread {

    private DataSnapshot database;
    public Pokemon lPoke, rPoke;
    public String weather;
    Context context;

    public Calculator(){

    }

    public Calculator(DataSnapshot database, Pokemon left, Pokemon right, String weatherIn, Context context){
        this.database = database;
        lPoke = left;
        rPoke = right;
        weather = weatherIn;
    }


    public void calculateDamage() {
        if(lPoke == null || rPoke == null){
            Toast.makeText(context, "Please Choose 2 Pokemon first", Toast.LENGTH_SHORT).show();
        }
    }
}
