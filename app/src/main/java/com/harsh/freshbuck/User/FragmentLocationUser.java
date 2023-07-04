package com.harsh.freshbuck.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsh.freshbuck.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FragmentLocationUser extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view, viewkey;
    private EditText editTextMobile, editTextAddress, editTextCity, editTextSocity, editTextFlat;
    private MaterialButton materialButtonSave;
    private String Mob, Addr, City, Soci, FlatNo;
    private String mobile, address, city, society, flat;
    private String key;
    private Thread threadAddress = null;
    ImageButton arrow;
    LinearLayout hiddenView;
    MaterialCardView cardView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public FragmentLocationUser() {
        // Required empty public constructor
    }


    public static FragmentLocationUser newInstance(String param1, String param2) {
        FragmentLocationUser fragment = new FragmentLocationUser();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_location_i_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        viewkey = view.findViewById(R.id.viewHideKey);
        editTextMobile = view.findViewById(R.id.Address_mobile);
        editTextAddress = view.findViewById(R.id.Address_address);
        editTextCity = view.findViewById(R.id.Address_city);
        editTextSocity = view.findViewById(R.id.Address_socityName);
        editTextFlat = view.findViewById(R.id.Address_FlatNo);
        materialButtonSave = view.findViewById(R.id.button_location_save_user);

        cardView = view.findViewById(R.id.base_cardview);
        arrow = view.findViewById(R.id.arrow_button);
        hiddenView = view.findViewById(R.id.hidden_view);

        key = firebaseAuth.getCurrentUser().getUid();

        threadAddress = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Address");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                mobile = snapshot.child(key).child("Mobile").getValue(String.class);
                                address = snapshot.child(key).child("Address").getValue(String.class);
                                city = snapshot.child(key).child("City").getValue(String.class);
                                society = snapshot.child(key).child("SocietyName").getValue(String.class);
                                flat = snapshot.child(key).child("FlatNo").getValue(String.class);

                                if (!(mobile == null && address == null)) {

                                    editTextMobile.setText(mobile);
                                    editTextAddress.setText(address);
                                    editTextCity.setText(city);
                                    editTextSocity.setText(society);
                                    editTextFlat.setText(flat);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
            }
        });
        threadAddress.start();


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hiddenView.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(cardView,
                            new AutoTransition());
                    hiddenView.setVisibility(View.VISIBLE);
                    arrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });

        materialButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City = "-";
                Soci = "-";
                FlatNo = "-";
                Mob = editTextMobile.getText().toString();
                Addr = editTextAddress.getText().toString();
                City = editTextCity.getText().toString();
                Soci = editTextSocity.getText().toString();
                FlatNo = editTextFlat.getText().toString();

                if (Mob.isEmpty()) {
                    editTextMobile.setError("Enter Mobile Number");
                    editTextMobile.requestFocus();
                } else if (!(isValidMobile(Mob))) {
                    editTextMobile.setError("Enter Valid Mobile Number");
                    editTextMobile.requestFocus();
                } else if (Addr.isEmpty()) {
                    editTextAddress.setError("Enter Address");
                    editTextAddress.requestFocus();
                } else if (!(Mob.isEmpty() && Addr.isEmpty())) {
                    key = firebaseAuth.getCurrentUser().getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Address");
                    databaseReference.child(key).child("ID").setValue(key);
                    databaseReference.child(key).child("Mobile").setValue(Mob);
                    databaseReference.child(key).child("Address").setValue(Addr);
                    databaseReference.child(key).child("City").setValue(City);
                    databaseReference.child(key).child("SocietyName").setValue(Soci);
                    databaseReference.child(key).child("FlatNo").setValue(FlatNo);

                    Toast.makeText(getContext(), "Address Saved" + key, Toast.LENGTH_SHORT).show();
                    TransitionManager.beginDelayedTransition(cardView,new AutoTransition());
                    hiddenView.setVisibility(View.GONE);
                    arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }

            }
        });


        viewkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(getActivity());
            }
        });
        return view;
    }

    public static boolean isValidMobile(String s) {

        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }


}