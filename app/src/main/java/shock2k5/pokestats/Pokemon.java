package shock2k5.pokestats;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kevincoleman on 10/17/16.
 */

public class Pokemon {
    public int baseATK, baseDEF, baseSPA, baseSPD, baseSPE, baseHP, dexNumber, eggSteps, femalePercentage,
            malePercentage, weight;
    public String eggGroup1, eggGroup2, height, name, photoURL, type1, type2;
    public ArrayList<String> eggMoves, lvMoves, tmMoves, transferMoves, tutorMoves;
    public static ArrayList<String> names = null;

    public static Firebase getPokemon(String name){
        return new Firebase("https://pokestatix.firebaseio.com/pokestatix/Pokemon/" + name);
    }

    public static ArrayList<String> getNames(DataSnapshot snapshot){
        if(names != null) return names;
        ArrayList<String> names = new ArrayList<String>();
        Map<String, Object> map = (Map<String, Object>) snapshot.child("Pokemon").getValue();
        names.addAll(map.keySet());

        return names;
    }
}
