package com.harsh.freshbuck.Admin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harsh.freshbuck.R;
import com.harsh.freshbuck.Utilities.AdminHome.AdminHolder;
import com.harsh.freshbuck.Utilities.AdminHome.AdminModel;
import com.harsh.freshbuck.Utilities.AdminHome.Holder;
import com.harsh.freshbuck.Utilities.AdminHome.HomeModel;

public class HistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private View view;

    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<AdminModel> options;
    private FirebaseRecyclerAdapter<AdminModel, AdminHolder> adapter;

    private FirebaseRecyclerOptions<HomeModel> optionsinside;
    private FirebaseRecyclerAdapter<HomeModel, Holder> adapterinside;


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

        view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.adminHistoryRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final DatabaseReference fetchAdminBill = FirebaseDatabase.getInstance().getReference().child("AdminData").child("History");
        options = new FirebaseRecyclerOptions.Builder<AdminModel>().setQuery(fetchAdminBill, new SnapshotParser<AdminModel>() {

            @NonNull
            @Override
            public AdminModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new AdminModel(

                        snapshot.child("Bill").child("OrderID").getValue(String.class),
                        snapshot.child("Bill").child("UserName").getValue(String.class),
                        snapshot.child("Bill").child("TotalRate").getValue(String.class),
                        snapshot.child("Bill").child("Date").getValue(String.class),
                        snapshot.child("Bill").child("Mobile").getValue(String.class),
                        snapshot.child("Bill").child("Time").getValue(String.class),
                        snapshot.child("Bill").child("NoOfItems").getValue(String.class),
                        snapshot.child("Bill").child("Address").getValue(String.class),
                        snapshot.child("Bill").child("City").getValue(String.class),
                        snapshot.child("Bill").child("SocietyName").getValue(String.class),
                        snapshot.child("Bill").child("FlatNo").getValue(String.class),
                        snapshot.child("Bill").child("OrderMethod").getValue(String.class)
                );

            }
        }).build();
        adapter = new FirebaseRecyclerAdapter<AdminModel, AdminHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final AdminHolder holder, int position, @NonNull final AdminModel model) {

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (holder.hiddenView.getVisibility() == View.VISIBLE) {

                            TransitionManager.beginDelayedTransition(holder.cardView,
                                    new AutoTransition());
                            holder.hiddenView.setVisibility(View.GONE);
                        } else {

                            TransitionManager.beginDelayedTransition(holder.cardView,
                                    new AutoTransition());
                            holder.hiddenView.setVisibility(View.VISIBLE);
                        }

                    }
                });
                holder.imageViewCheckOrderConfirm.setVisibility(View.GONE);
                holder.textViewOrderId.setText(model.getOrderId());
                holder.textViewUserName.setText(model.getUserName());
                holder.textViewRateAdmin.setText(model.getRate() + "Rs");
                holder.textViewDateAdmin.setText(model.getDate());

                holder.textViewMobile.setText(model.getMobile());
                holder.textViewAddress.setText(model.getAddress());
                holder.textViewTime.setText(model.getTime());
                holder.textViewNoItems.setText(model.getNoItems());
                holder.textViewCity.setText(model.getCity());
                holder.textViewSociety.setText(model.getSocietyName());
                holder.textViewFlatNo.setText(model.getFlatNo());
                holder.textViewOrderMethod.setText(model.getOrderMethod());

                holder.recyclerViewItems.setHasFixedSize(true);
                holder.recyclerViewItems.setLayoutManager(new LinearLayoutManager(getContext()));

                final DatabaseReference cartItem = FirebaseDatabase.getInstance().getReference().child("AdminData").child("History").child(model.getOrderId()).child("ItemList");
                optionsinside = new FirebaseRecyclerOptions.Builder<HomeModel>().setQuery(cartItem, new SnapshotParser<HomeModel>() {

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
                adapterinside = new FirebaseRecyclerAdapter<HomeModel, Holder>(optionsinside) {

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

                adapterinside.startListening();
                holder.recyclerViewItems.setAdapter(adapterinside);

            }

            @NonNull
            @Override
            public AdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_noti_cart, parent, false);

                return new AdminHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

        return view;
    }
}