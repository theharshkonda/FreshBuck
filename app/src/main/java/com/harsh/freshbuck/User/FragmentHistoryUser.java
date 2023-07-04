package com.harsh.freshbuck.User;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsh.freshbuck.R;
import com.harsh.freshbuck.Utilities.AdminHome.Holder;
import com.harsh.freshbuck.Utilities.AdminHome.HomeModel;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;


public class FragmentHistoryUser extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private View view;
    private MaterialBetterSpinner MBSpinner;
    private RecyclerView recyclerView;
    private TextView TVOrderId, TVName, TVMobile, TVDate, TVTime, TVNOItems, TVAmount, TVAddress, TVCity, TVSociety, TVFlat;
    public ArrayList<String> arrayList = new ArrayList<>();
    public ArrayAdapter arrayAdapter;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String uid;
    private FirebaseRecyclerOptions<HomeModel> options;
    private FirebaseRecyclerAdapter<HomeModel, Holder> adapter;
    private LinearLayout linearLayout;

    public FragmentHistoryUser() {
        // Required empty public constructor
    }


    public static FragmentHistoryUser newInstance(String param1, String param2) {
        FragmentHistoryUser fragment = new FragmentHistoryUser();
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

        view = inflater.inflate(R.layout.fragment_history_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        MBSpinner = view.findViewById(R.id.material_spinner_history);
        recyclerView = view.findViewById(R.id.recycleHisUser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TVOrderId = view.findViewById(R.id.textHisOrderIdUser);
        TVName = view.findViewById(R.id.textHisNameUser);
        TVMobile = view.findViewById(R.id.textHisMobileUser);
        TVDate = view.findViewById(R.id.textHisDateUser);
        TVTime = view.findViewById(R.id.textHisTimeUser);
        TVNOItems = view.findViewById(R.id.textHisNoItemsUser);
        TVAmount = view.findViewById(R.id.textHisAmountUser);
        TVAddress = view.findViewById(R.id.textHisAddressUser);
        TVCity = view.findViewById(R.id.textHisCityUser);
        TVSociety = view.findViewById(R.id.textHisSocietyUser);
        TVFlat = view.findViewById(R.id.textHisFlatUser);
        linearLayout = view.findViewById(R.id.historyRecycleViewLayout);

        showDataSpinner(getContext(), uid);

        MBSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                final String Search = MBSpinner.getText().toString();

                if (!(Search.equals("Nothing"))) {
                    DatabaseReference dfFetch = FirebaseDatabase.getInstance().getReference().child("Billing").child(uid).child(Search).child("Bill");
                    dfFetch.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            TVOrderId.setText(snapshot.child("OrderID").getValue(String.class));
                            TVName.setText(snapshot.child("UserName").getValue(String.class));
                            TVMobile.setText(snapshot.child("Mobile").getValue(String.class));
                            TVDate.setText(snapshot.child("Date").getValue(String.class));
                            TVTime.setText(snapshot.child("Time").getValue(String.class));
                            TVNOItems.setText(snapshot.child("NoOfItems").getValue(String.class));
                            String rate = snapshot.child("TotalRate").getValue(String.class);
                            TVAmount.setText(rate + "Rs");
                            TVAddress.setText(snapshot.child("Address").getValue(String.class));
                            TVCity.setText(snapshot.child("City").getValue(String.class));
                            TVSociety.setText(snapshot.child("SocietyName").getValue(String.class));
                            TVFlat.setText(snapshot.child("FlatNo").getValue(String.class));

                            linearLayout.setVisibility(View.VISIBLE);

                            final DatabaseReference cartItem = FirebaseDatabase.getInstance().getReference().child("Billing").child(uid).child(Search).child("ItemList");
                            options = new FirebaseRecyclerOptions.Builder<HomeModel>().setQuery(cartItem, new SnapshotParser<HomeModel>() {

                                @NonNull
                                @Override
                                public HomeModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    return new HomeModel(

                                            snapshot.child("Name").getValue(String.class),
                                            snapshot.child("weight").getValue(String.class),
                                            snapshot.child("rate").getValue(String.class)
                                    );

                                }
                            }).build();
                            adapter = new FirebaseRecyclerAdapter<HomeModel, Holder>(options) {

                                @Override
                                protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull final HomeModel model) {

                                    holder.setTxtUserItemNameCart(model.getCartItemName());
                                    holder.setTxtUserItemRateCart(model.getCartItemRate());
                                    holder.setTxtUserItemWeightCart(model.getCartItemweight());

                                }

                                @NonNull
                                @Override
                                public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history_card, parent, false);

                                    return new Holder(view);
                                }
                            };

                            adapter.startListening();
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {

                    linearLayout.setVisibility(View.GONE);
                    TVOrderId.setText("");
                    TVName.setText("");
                    TVMobile.setText("");
                    TVDate.setText("");
                    TVTime.setText("");
                    TVNOItems.setText("");
                    TVAmount.setText("");
                    TVAddress.setText("");
                    TVCity.setText("");
                    TVSociety.setText("");
                    TVFlat.setText("");

                }
            }
        });

        return view;
    }

    private void showDataSpinner(final Context context, String uid) {

        databaseReference.child("Billing").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    arrayList.add(item.child("Bill").child("OrderID").getValue(String.class));
                }
                arrayList.add("Nothing");
                arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayList);
                MBSpinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}