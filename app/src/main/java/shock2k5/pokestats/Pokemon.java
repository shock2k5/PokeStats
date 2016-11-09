package shock2k5.pokestats;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kevincoleman on 10/17/16.
 */

public class Pokemon {
    public long baseATK, baseDEF, baseSPA, baseSPD, baseSPE, baseHP, dexNumber, eggSteps, level,
        hpEV, atkEV, defEV, spaEV, spdEV, speEV;
    public double weight, femalePercentage, malePercentage;
    public String eggGroup1, eggGroup2, height, name, photoURL, type1, type2, nature;
    public ArrayList<String> eggMoves, lvMoves, tmMoves, transferMoves, tutorMoves, abilities;
    public static ArrayList<String> names = null;



    public static ArrayList<String> getNames(DataSnapshot snapshot){
        if(names != null) return names;
        ArrayList<String> names = new ArrayList<String>();
        Map<String, Object> map = (Map<String, Object>) snapshot.child("Pokemon").getValue();
        Set<String> namesSet =  map.keySet();
        names.addAll(map.keySet());
        return names;
    }

    public Pokemon(){
        this.level = 50;
    }

    public static Pokemon getPokemon(DataSnapshot database, String name){

        HashMap<String, Object> base = (HashMap<String, Object>) database.child("Pokemon").child(name).getValue();
        Pokemon poke = new Pokemon();
        poke.baseATK = (long) base.get("ATK");
        poke.baseDEF = (long) base.get("DEF");
        poke.baseHP = (long) base.get("HP");
        poke.baseSPA = (long) base.get("SPA");
        poke.baseSPD = (long) base.get("SPD");
        poke.baseSPE = (long) base.get("SPE");
        poke.abilities = (ArrayList<String>) base.get("abilities");
        poke.dexNumber = (long) base.get("dexNumber");
        poke.eggGroup1 = (String) ((base.get("egg1") != null) ? base.get("egg1") : "");
        poke.eggGroup2 = (String) ((base.get("egg2") != null) ? base.get("egg2") : "");
        poke.eggSteps = (long) ((base.get("eggSteps") != null) ? base.get("eggSteps") : 0.0);
        poke.femalePercentage = (double) ((base.get("Female") != null) ? base.get("Female") : 0.0);
        poke.malePercentage = (double) ((base.get("Male") != null) ? base.get("Male") : 0.0);
        poke.height = (String) base.get("height");
        poke.eggMoves = (ArrayList<String>) ((base.get("eggMoves") != null) ? base.get("eggMoves") : new ArrayList<>());
        poke.lvMoves = (ArrayList<String>) ((base.get("lvMoves") != null) ? base.get("lvMoves") : new ArrayList<>());
        poke.tmMoves = (ArrayList<String>) ((base.get("tutorMoves") != null) ? base.get("tutorMoves") : new ArrayList<>());
        poke.tutorMoves = (ArrayList<String>) ((base.get("transferMoves") != null) ? base.get("transferMoves") : new ArrayList<>());
        poke.transferMoves = (ArrayList<String>) ((base.get("tmMoves") != null) ? base.get("tmMoves") : new ArrayList<>());
        poke.type1 = (String) base.get("type1");
        poke.type2 = (String) ((base.get("type2") != null) ? base.get("type2") : "");
        poke.weight = (double) base.get("Weight");
        poke.photoURL = (String) base.get("photo");
        poke.hpEV = poke.atkEV = poke.defEV = poke.spaEV = poke.spdEV = poke.speEV = 0;

        return poke;
    }
}
