package shock2k5.pokestats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import java.util.HashSet;
import java.util.Map;

public class DamageCalculatorActivity extends AppCompatActivity {

    private Spinner spnLeftMove1, spnLeftMove2, spnLeftMove3, spnLeftMove4,
        spnRightMove1, spnRightMove2, spnRightMove3, spnRightMove4;
    private Calculator calculator;
    public ArrayList<String> pokeNames;
    public String pokeLeft, pokeRight, lTempName, rTempName;
    private Firebase fireRef;
    private DataSnapshot homeSnap;
    private AutoCompleteTextView leftName, rightName;
    private SeekBar leftHP, leftATK, leftDEF, leftSPA, leftSPD, leftSPE,
        rightHP, rightATK, rightDEF, rightSPA, rightSPD, rightSPE;
    private TextView leftHPEV, leftATKEV, leftDEFEV, leftSPAEV, leftSPDEV, leftSPEEV,
        rightHPEV, rightATKEV, rightDEFEV, rightSPAEV, rightSPDEV, rightSPEEV;
    private ImageView imgLeftPoke, imgRightPoke;
    private TextWatcher watcher;
    private Button btnCalcuate;

    private Pokemon leftPoke, rightPoke;
    private int lHP, lATK, lDEF, lSPA, lSPD, lSPE, rHP, rATK, rDEF, rSPA, rSPD, rSPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damage_calculator);
        Firebase.setAndroidContext(this);

        leftPoke = new Pokemon();
        rightPoke = new Pokemon();

        imgLeftPoke = (ImageView) findViewById(R.id.img_left_poke);
        imgRightPoke = (ImageView) findViewById(R.id.img_right_poke);

        fireRef = new Firebase("https://pokestatix.firebaseio.com/");
        fireRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            /**
             * App just loading up. We want to download the database, but not all of it and
             * update the Pokemon list
             */
            public void onDataChange(final DataSnapshot dataSnapshot) {
                homeSnap = dataSnapshot;
                pokeNames = Pokemon.getNames(dataSnapshot);

                leftName = (AutoCompleteTextView) findViewById(R.id.left_name);
                leftName.setAdapter(new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_list_item_1, pokeNames));
                leftName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        lTempName = charSequence.toString();
                        if(pokeNames.contains(lTempName.toLowerCase())){
                            //Update Image
                            Firebase currPoke = fireRef.child("Pokemon");
                            updateLeft(lTempName, dataSnapshot.child("Pokemon"));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                rightName = (AutoCompleteTextView) findViewById(R.id.right_name);
                rightName.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, pokeNames));
                rightName.addTextChangedListener(new TextWatcher() {
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
        btnCalcuate = (Button) findViewById(R.id.btn_calcuclate);
        btnCalcuate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculator = new Calculator(homeSnap, leftPoke, rightPoke, "clear", getApplicationContext());
                calculator.calculateDamage();
            }
        });
    }

    /**
     * Update Left Pokemon with Stats
     * Update Attacks
     * Update Abilities - The abilities are not in the database.
     * Must add that to the Java web crawler. And name that web crawler
     */
    private void updateLeft(String name, DataSnapshot snap) {
        Map<String, Object> currPoke = (Map<String, Object>) snap.child(name.toLowerCase()).getValue();

        Picasso.with(this).load((String) currPoke.get("photo")).into((ImageView) findViewById(R.id.img_left_poke));

        leftPoke.baseATK = ((Long) currPoke.get("ATK")).intValue();
        leftPoke.baseDEF = ((Long) currPoke.get("DEF")).intValue();
        leftPoke.baseHP = ((Long) currPoke.get("HP")).intValue();
        leftPoke.baseSPA = ((Long) currPoke.get("SPA")).intValue();
        leftPoke.baseSPD = ((Long) currPoke.get("SPD")).intValue();
        leftPoke.baseSPE = ((Long) currPoke.get("SPE")).intValue();

        ArrayList<String> moves = new ArrayList<>();
        if(currPoke.get("eggMoves") != null) moves.addAll(((ArrayList<String>) currPoke.get("eggMoves")));
        if(currPoke.get("lvMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("lvMoves")));
        if(currPoke.get("tutorMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("tutorMoves")));
        if(currPoke.get("transferMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("transferMoves")));
        if(currPoke.get("tmMoves") != null)moves.addAll(((ArrayList<String>) currPoke.get("tmMoves")));

        spnLeftMove1 = (Spinner) findViewById(R.id.spn_left_move_1);
        spnLeftMove1.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove2 = (Spinner) findViewById(R.id.spn_left_move_2);
        spnLeftMove2.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove3 = (Spinner) findViewById(R.id.spn_left_move_3);
        spnLeftMove3.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
        spnLeftMove4 = (Spinner) findViewById(R.id.spn_left_move_4);
        spnLeftMove4.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moves));
    }

    private void updateRight(String name, DataSnapshot snap){
        Map<String, Object> currPoke = (Map<String, Object>) snap.child(name.toLowerCase()).getValue();

        Picasso.with(this).load((String) currPoke.get("photo")).into((ImageView) findViewById(R.id.img_right_poke));

        rightPoke.baseATK = ((Long) currPoke.get("ATK")).intValue();
        rightPoke.baseDEF = ((Long) currPoke.get("DEF")).intValue();
        rightPoke.baseHP = ((Long) currPoke.get("HP")).intValue();
        rightPoke.baseSPA = ((Long) currPoke.get("SPA")).intValue();
        rightPoke.baseSPD = ((Long) currPoke.get("SPD")).intValue();
        rightPoke.baseSPE = ((Long) currPoke.get("SPE")).intValue();

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
                leftHPEV.setText((lHP = i * 4) + "");
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
                leftATKEV.setText((lATK = i * 4) + "");
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
                leftDEFEV.setText((lDEF = i * 4) + "");
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
                leftSPAEV.setText((lSPA = i * 4) + "");
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
                leftSPDEV.setText((lSPD = i * 4) + "");
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
                leftSPEEV.setText((lSPE = i * 4) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                rightHPEV.setText((rHP = i * 4) + "");
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
                rightATKEV.setText((rATK = i * 4) + "");
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
                rightDEFEV.setText((rDEF = i * 4) + "");
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
                rightSPAEV.setText((rSPA = i * 4) + "");
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
                rightSPDEV.setText((rSPD = i * 4) + "");
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
                rightSPEEV.setText((rSPE = i * 4) + "");
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
