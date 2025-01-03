package com.native_code.bp_project02;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.native_code.bp_project02.CustomData.UserModel;
import com.native_code.bp_project02.Native_Profile.Profile;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class Search_Popular extends Fragment {


    private  SearchPopularRecyclerViewAdapter adapter;
    private  EditText NativeSearch_editText;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_popular, container,false);

        NativeSearch_editText=((NativeSearch)getActivity()).editText;

        NativeSearch_editText.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.searchUser(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = NativeSearch_editText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                //adapter.filter(text);


            }
        });

        adapter=new SearchPopularRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.search_popular_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        recyclerView.setAdapter(adapter);



        return rootView;

    }


    class SearchPopularRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;
        List<UserModel> saveList;
        public SearchPopularRecyclerViewAdapter() {



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            saveList=new ArrayList<>();

            db.collection("users")
                    .whereEqualTo("user_kind", "현지인")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            userModels.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc != null) {


                                    userModels.add(doc.toObject(UserModel.class));
                                    saveList.add(doc.toObject(UserModel.class));

                                }
                            }
                            notifyDataSetChanged();
                            Log.d(TAG, "Current data: " + userModels);
                        }
                    });

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).imageurl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);
            ((CustomViewHolder) holder).Region_textView.setText(userModels.get(position).region);
            //((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hash);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getContext(), Profile.class);
                        Log.d(TAG,userModels.get(position).toString());
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });

        }

        @Override
        public int getItemCount() {

            return userModels.size();
        }


        public  void searchUser(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getName() !=null &&saveList.get(i).getName().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }

    }





    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView Nick_textView;
        public TextView Region_textView;
        public TextView Hash_textView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
            Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
            Region_textView=(TextView) view.findViewById(R.id.frienditem_region);
            Hash_textView=(TextView) view.findViewById(R.id.frienditem_hash);
        }
    }

}
