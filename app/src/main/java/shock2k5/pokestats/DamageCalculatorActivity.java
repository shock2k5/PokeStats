package shock2k5.pokestats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DamageCalculatorActivity extends AppCompatActivity {

    private Spinner spnLeftMove1, spnLeftMove2, spnLeftMove3, spnLeftMove4,
        spnRightMove1, spnRightMove2, spnRightMove3, spnRightMove4,
        spnLeftAbility, spnLeftItem, spnleftNature, spnRightAbility,
        spnRightNature, spnRightItem;
    public ArrayList<String> pokeNames, moves, leftAbilities, rightAbilities, moves1;
    public String pokeLeft, pokeRight, lTempName, rTempName;
    private Firebase fireRef;
    private AutoCompleteTextView txtLeftName, txtRightName;
    private SeekBar leftHP, leftATK, leftDEF, leftSPA, leftSPD, leftSPE,
        rightHP, rightATK, rightDEF, rightSPA, rightSPD, rightSPE;
    private TextView leftHPEV, leftATKEV, leftDEFEV, leftSPAEV, leftSPDEV, leftSPEEV,
        rightHPEV, rightATKEV, rightDEFEV, rightSPAEV, rightSPDEV, rightSPEEV,
            txtResult1, txtResult2, txtResult3, txtResult4;
    private ImageView imgLeftPoke, imgRightPoke;
    public static DataSnapshot database;

    private Pokemon leftPoke, rightPoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damage_calculator);
        Firebase.setAndroidContext(this);

        leftPoke = new Pokemon();
        rightPoke = new Pokemon();

        imgLeftPoke = (ImageView) findViewById(R.id.img_left_poke);
        imgRightPoke = (ImageView) findViewById(R.id.img_right_poke);

        spnLeftMove1 = (Spinner) findViewById(R.id.spn_left_move_1);
        spnLeftMove2 = (Spinner) findViewById(R.id.spn_left_move_2);
        spnLeftMove3 = (Spinner) findViewById(R.id.spn_left_move_3);
        spnLeftMove4 = (Spinner) findViewById(R.id.spn_left_move_4);
        spnLeftAbility = (Spinner) findViewById(R.id.left_spn_abilities);

        fireRef = new Firebase("https://pokestatix.firebaseio.com/");
        fireRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            /**
             * App just loading up. We want to download the database, but not all of it and
             * update the Pokemon list
             */
            public void onDataChange(final DataSnapshot dataSnapshot) {
                database = dataSnapshot;
                pokeNames = Pokemon.getNames(dataSnapshot);

                txtLeftName = (AutoCompleteTextView) findViewById(R.id.left_name);
                txtLeftName.setAdapter(new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_list_item_1, pokeNames));
                txtLeftName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        lTempName = charSequence.toString();

                        if(pokeNames.contains(lTempName.toLowerCase())){
                            updateLeft(lTempName, dataSnapshot);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                txtRightName = (AutoCompleteTextView) findViewById(R.id.right_name);
                txtRightName.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, pokeNames));
                txtRightName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        rTempName = charSequence.toString();
                        if(pokeNames.contains(rTempName.toLowerCase())){
                            //Update Image
                            Firebase currPoke = fireRef.child("Pokemon");
                            updateRight(rTempName, dataSnapshot.child("Pokemon"));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        setBars();


    }

    /**
     * This method is called when the name input for the Attacking Pokemon is contained in the
     * Pokedex. This will update the stats, abilities, and moves list for the Attacking mon.
     */
    private void updateLeft(String name, DataSnapshot snap) {
        leftPoke = Pokemon.getPokemon(snap, name);
        Map<String, Object> currPoke = (Map<String, Object>) snap.child("Pokemon").child(name.toLowerCase()).getValue();

        Picasso.with(this).load((String) currPoke.get("photo")).into(imgLeftPoke);

        moves = new ArrayList<>();
        if(currPoke.get("eggMoves") != null) moves.addAll(((ArrayList<String>) currPoke.get("eggMoves")));
        if(currPoke.get("lvMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("lvMoves")));
        if(currPoke.get("tutorMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("tutorMoves")));
        if(currPoke.get("transferMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("transferMoves")));
        if(currPoke.get("tmMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("tmMoves")));

        Collections.sort(moves);
        int size = moves.size();
        for(int i = 0; i < size - 1; i++){
            if(moves.get(i).compareTo(moves.get(i + 1)) == 0){
                moves.remove(i--);
                size--;
            }
        }

        spnLeftMove1.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtResult1.setText(calculateLeftDamage(moves.get(position), true));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnLeftMove2.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtResult2.setText(moves.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnLeftMove3.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtResult3.setText(moves.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnLeftMove4.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtResult4.setText(moves.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        leftAbilities = new ArrayList<String>();

        if(currPoke.get("abilities") != null) leftAbilities.addAll((ArrayList<String>) currPoke.get("abilities"));
        spnLeftAbility.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, leftAbilities));

        //spnLeftAbility.performItemClick(spnLeftAbility, 0, R.id.left_spn_abilities);
    }

    /**
     * This method will calcluate the damage done and output a string of the range of damage that could be done
     * Damage = ((((2 * Level / 5 + 2) * AttackStat * AttackPower / DefenseStat) / 50) + 2) * STAB * Weakness/Resistance * RandomNumber / 100
     * @param attack
     * @param leftAttacker
     * @return
     */
    private String calculateLeftDamage(String attack, boolean leftAttacker) {
        double fullDamage = (((2 * leftPoke.level / 5 + 2) * getAttack(true) * getPower(attack) / getDefense(true)) / 50) + 2 * getStab(attack, true)
                * calculateWeakness(leftAttacker) * weatherBoost();

        return fullDamage * .85 + "% - " + fullDamage + "%";
    }

    private double weatherBoost() {
        return 1.0;
    }

    private double getStab(String attack, boolean isLeftAttacker) {
        String curr = null;
        if(isLeftAttacker) curr = pokeLeft;
        else curr = pokeRight;
        Pokemon mon = Pokemon.getPokemon(database, curr);
        String type = (String) ((Map<String, Object>) database.child("Attacks").child(attack).getValue()).get("type");
        if(mon.type1.equals(type) || mon.type2.equals(type)) return 1.5;
        return 1.0;
    }

    private double getDefense(boolean isLeftAttacker) {
        Pokemon mon = null;
        if(isLeftAttacker) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return ( ( ( 31 + 2 * mon.baseDEF + (mon.defEV) ) * mon.level / 100.0 ) + 5) * calcNatureBoost(mon, "def");
        //TODO Calculate exact Defense stat based on formula (((IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 5) * Nature Value

    }

    private double calcNatureBoost(Pokemon mon, String def) {
        return 0.0;
    }

    private double getPower(String attack) {

        return Double.parseDouble((String) ((Map<String, Object>) database.child("Attacks").child(attack).getValue()).get("hp"));
    }

    private double getAttack(boolean isLeftAttacker) {
        Pokemon mon = null;
        if(isLeftAttacker) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return ( ( ( 31 + 2 * mon.baseATK + (mon.atkEV) ) * mon.level / 100.0 ) + 5) * calcNatureBoost(mon, "atk");

        //TODO (((IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 5) * Nature Value
    }

    private double getHP(boolean isLeft) {
        Pokemon mon = null;
        if(isLeft) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return  ( ( 31 + 2 * mon.baseHP + (mon.hpEV) ) * mon.level / 100.0 ) + 10 + mon.level;

        //TODO  HP = ( (IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 10 + Level
    }

    private double getSpecialAttack(boolean isLeftAttacker) {
        Pokemon mon = null;
        if(isLeftAttacker) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return ( ( ( 31 + 2 * mon.baseSPA + (mon.spaEV) ) * mon.level / 100.0 ) + 5) * calcNatureBoost(mon, "spa");

        //TODO SPA = (((IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 5) * Nature Value
    }

    private double getSpecialDefense(boolean isLeftAttacker) {
        Pokemon mon = null;
        if(isLeftAttacker) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return ( ( ( 31 + 2 * mon.baseSPD + (mon.spdEV) ) * mon.level / 100.0 ) + 5) * calcNatureBoost(mon, "spd");

        //TODO SPD = (((IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 5) * Nature Value
    }

    private double getSpeed(boolean isLeftAttacker) {
        Pokemon mon = null;
        if(isLeftAttacker) Pokemon.getPokemon(database, pokeLeft);
        else Pokemon.getPokemon(database, pokeRight);
        return ( ( ( 31 + 2 * mon.baseSPE + (mon.speEV) ) * mon.level / 100.0 ) + 5) * calcNatureBoost(mon, "spe");

        //TODO SPE = (((IV + 2 * BaseStat + (EV/4) ) * Level/100 ) + 5) * Nature Value
    }


    private double calculateWeakness(boolean leftAttacker) {
        return 1.0;
    }

    public ArrayList<String> getMoves(){
        return moves;
    }

    private void updateRight(String name, DataSnapshot snap){
        Map<String, Object> currPoke = (Map<String, Object>) snap.child(name.toLowerCase()).getValue();

        Picasso.with(this).load((String) currPoke.get("photo")).into(imgRightPoke);

        rightPoke.baseATK = ((Long) currPoke.get("ATK")).intValue();
        rightPoke.baseDEF = ((Long) currPoke.get("DEF")).intValue();
        rightPoke.baseHP = ((Long) currPoke.get("HP")).intValue();
        rightPoke.baseSPA = ((Long) currPoke.get("SPA")).intValue();
        rightPoke.baseSPD = ((Long) currPoke.get("SPD")).intValue();
        rightPoke.baseSPE = ((Long) currPoke.get("SPE")).intValue();

        rightAbilities = new ArrayList<>();

        if(currPoke.get("abilities") != null) rightAbilities.addAll((ArrayList<String>) currPoke.get("abilities"));
    }

    private void setBars() {
        leftHP = (SeekBar) findViewById(R.id.left_hp_bar);
        leftATK = (SeekBar) findViewById(R.id.left_atk_bar);
        leftDEF = (SeekBar) findViewById(R.id.left_def_bar);
        leftSPA = (SeekBar) findViewById(R.id.left_spa_bar);
        leftSPD = (SeekBar) findViewById(R.id.left_spd_bar);
        leftSPE = (SeekBar) findViewById(R.id.left_spe_bar);

        leftHPEV = (TextView) findViewById(R.id.left_hp_num);
        leftATKEV = (TextView) findViewById(R.id.left_atk_num);
        leftDEFEV = (TextView) findViewById(R.id.left_def_num);
        leftSPAEV = (TextView) findViewById(R.id.left_spa_num);
        leftSPDEV = (TextView) findViewById(R.id.left_spd_num);
        leftSPEEV = (TextView) findViewById(R.id.left_spe_num);

        leftHP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftHPEV.setText((leftPoke.hpEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leftATK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftATKEV.setText((leftPoke.atkEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leftDEF.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftDEFEV.setText((leftPoke.defEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leftSPA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftSPAEV.setText((leftPoke.spaEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leftSPD.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftSPDEV.setText((leftPoke.spdEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        leftSPE.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                leftSPEEV.setText((leftPoke.speEV = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txtResult1 = (TextView) findViewById(R.id.txt_move1_dmg);
        txtResult2 = (TextView) findViewById(R.id.txt_move2_dmg);
        txtResult3 = (TextView) findViewById(R.id.txt_move3_dmg);
        txtResult4 = (TextView) findViewById(R.id.txt_move4_dmg);

        rightHP = (SeekBar) findViewById(R.id.right_hp_bar);
        rightATK = (SeekBar) findViewById(R.id.right_atk_bar);
        rightDEF = (SeekBar) findViewById(R.id.right_def_bar);
        rightSPA = (SeekBar) findViewById(R.id.right_spa_bar);
        rightSPD = (SeekBar) findViewById(R.id.right_spd_bar);
        rightSPE = (SeekBar) findViewById(R.id.right_spe_bar);

        rightHPEV = (TextView) findViewById(R.id.right_hp_num);
        rightATKEV = (TextView) findViewById(R.id.right_atk_num);
        rightDEFEV = (TextView) findViewById(R.id.right_def_num);
        rightSPAEV = (TextView) findViewById(R.id.right_spa_num);
        rightSPDEV = (TextView) findViewById(R.id.right_spd_num);
        rightSPEEV = (TextView) findViewById(R.id.right_spe_num);

        rightHP.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightHPEV.setText((rightPoke.baseHP = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rightATK.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightATKEV.setText((rightPoke.baseATK = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rightDEF.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightDEFEV.setText((rightPoke.baseDEF = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rightSPA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightSPAEV.setText((rightPoke.baseSPA = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rightSPD.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightSPDEV.setText((rightPoke.baseSPD = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rightSPE.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rightSPEEV.setText((rightPoke.baseSPE = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_damage_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
