package com.example.usama.contactsassignment2;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    private OnFragmentInteractionListener mListener;
    Intent intent;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ArrayList<Contact> arrayList = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            arrayList.add(new Contact(i, "Name: " + i, "Phone: 123456" + i, "Email: example@example.com"));
        }

        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(arrayList);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(v.getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recyclerAdapter);

        //EventBus.getDefault().register(this);

        class MyOnClickListener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.indexOfChild(v);

                intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("name", arrayList.get(itemPosition).getName());
                intent.putExtra("phone", arrayList.get(itemPosition).getPhone());
                intent.putExtra("email", arrayList.get(itemPosition).getEmail());
                startActivity(intent);

            }
        }

        v.setOnClickListener(new MyOnClickListener());

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemSelect(Integer pos);
    }

}
