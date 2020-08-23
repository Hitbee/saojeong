package com.example.saojeong.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.saojeong.MainActivity;
import com.example.saojeong.R;
import com.example.saojeong.adapter.FoodAdapter;
import com.example.saojeong.adapter.FullviewAdapter;
import com.example.saojeong.adapter.LikeStoreAdapter;
import com.example.saojeong.auth.TokenCase;
import com.example.saojeong.model.ContactFood;
import com.example.saojeong.model.ContactFullview;
import com.example.saojeong.model.ContactShopOC;
import com.example.saojeong.model.RecyclerDecoration;
import com.example.saojeong.rest.ServiceGenerator;
import com.example.saojeong.rest.dto.SeasonalFoodDto;
import com.example.saojeong.rest.dto.StoreDto;
import com.example.saojeong.rest.service.SeasonalFoodService;
import com.example.saojeong.rest.service.StoreService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class HomeFragment extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FruitFragment fruitFragment;
    private FishFragment fishFragment;
    private VegetableFragment vegetableFragment;
    private HomeFragment homeFragment;
    private RecyclerView recyclerShop;
    private RecyclerView recyclerFood;
    private RecyclerView recyclerFullview;
    private LikeStoreAdapter likeStoreAdapter;
    private FoodAdapter foodAdapter;
    private FullviewAdapter fullviewAdapter;
    List<ContactShopOC> likeStores;
    ArrayList<ContactFood> contactFoods;
    ArrayList<ContactFullview> contactFullviews;

    private StoreService storeService;
    private SeasonalFoodService seasonalFoodService;

    RecyclerDecoration.LeftDecoration leftDecoration = new RecyclerDecoration.LeftDecoration(50);

    private InputMethodManager imm;

    TabHost tabHost;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        storeService = ServiceGenerator.createService(StoreService.class, TokenCase.getToken());
        seasonalFoodService = ServiceGenerator.createService(SeasonalFoodService.class, TokenCase.getToken());

        fragmentManager = getChildFragmentManager();
        transaction = fragmentManager.beginTransaction();

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        fruitFragment = new FruitFragment(); // 과일동 Fragment 선언
        vegetableFragment = new VegetableFragment(); // 채소동 Fragment 선언
        fishFragment = new FishFragment(); // 수산동 Fragment 선언

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        //매장 Recycler View
        recyclerShop = (RecyclerView) rootView.findViewById(R.id.recyclershop_fragment);
        loadStores(this);

        //과일 Recycler View
        recyclerFood = (RecyclerView) rootView.findViewById(R.id.recyclerfruit_fragment);
        setAdapter();

        //채소 Recycler View
        recyclerFood = (RecyclerView) rootView.findViewById(R.id.recyclervegetable_fragment);
        setAdapter();

        //수산 Recycler View
        recyclerFood = (RecyclerView) rootView.findViewById(R.id.recyclerfish_fragment);
        setAdapter();

        loadFoods(this);

        //전체 보기(공지) Recycler View
        recyclerFullview = (RecyclerView) rootView.findViewById(R.id.recyclerfullview_fragment);
        contactFullviews = ContactFullview.createContactsList(20);
        fullviewAdapter = new FullviewAdapter(contactFullviews);
        recyclerFullview.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
        recyclerFullview.setAdapter(fullviewAdapter);

        tabHost = (TabHost) rootView.findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec fruit = tabHost.newTabSpec("과일");
        fruit.setContent(R.id.tabFr);
        fruit.setIndicator("과일"); //Tab Name
        TabHost.TabSpec vegetable = tabHost.newTabSpec("채소");
        vegetable.setContent(R.id.tabVe);
        vegetable.setIndicator("채소"); //Tab Name
        TabHost.TabSpec fish = tabHost.newTabSpec("수산");
        fish.setContent(R.id.tabFi);
        fish.setIndicator("수산"); //Tab Name

        tabHost.addTab(fruit);
        tabHost.addTab(vegetable);
        tabHost.addTab(fish);

        //시작시 Tab Color 지정
        TextView Cfruit = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        Cfruit.setTextColor(Color.parseColor("#ffffff"));
        TextView CVegetable = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        CVegetable.setTextColor(Color.parseColor("#ffd6b7"));
        TextView Cfish = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        Cfish.setTextColor(Color.parseColor("#ffd6b7"));

        //Tab 바꿀 때 마다 색 변경
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    TextView tabcolor = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    if (i == tabHost.getCurrentTab()) {
                        tabcolor.setTextColor(Color.parseColor("#ffffff"));
                    } else
                        tabcolor.setTextColor(Color.parseColor("#ffd6b7"));

                }
            }
        });
        return rootView;
    }

    private void loadStores(HomeFragment homeFragment) {
        storeService.getStarredStoreList().enqueue(new Callback<List<StoreDto>>() {
            @Override
            public void onResponse(Call<List<StoreDto>> call, Response<List<StoreDto>> response) {
                List<StoreDto> body = response.body();
                for (StoreDto dto : body)  { // 디버깅 코드
                    Log.d("RESPONSE CHECK", dto.toString());
                }
                if (response.code() == 201) { // 서버와 통신 성공
                    likeStores = ContactShopOC.createContactsList(body);
                    likeStoreAdapter = new LikeStoreAdapter(Glide.with(homeFragment), likeStores);
                } else { // 서버에서 문제 발생
                    likeStores = ContactShopOC._createContactsList(20);
                    likeStoreAdapter = new LikeStoreAdapter(likeStores);
                }
                recyclerShop.addItemDecoration(leftDecoration);
                recyclerShop.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
                recyclerShop.setAdapter(likeStoreAdapter);
            }

            @Override
            public void onFailure(Call<List<StoreDto>> call, Throwable t) {
                // 안드로이드에서 문제 발생 (네트워크 환경 체크해보기)
                likeStores = ContactShopOC._createContactsList(20);
                likeStoreAdapter = new LikeStoreAdapter(likeStores);
                recyclerShop.addItemDecoration(leftDecoration);
                recyclerShop.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
                recyclerShop.setAdapter(likeStoreAdapter);
            }
        });


    }

    private void loadFoods(HomeFragment homeFragment) {
        seasonalFoodService.getSeasonalFood("fruits", 9).enqueue(new Callback<List<SeasonalFoodDto>>() {
            @Override
            public void onResponse(Call<List<SeasonalFoodDto>> call, Response<List<SeasonalFoodDto>> response) {
                List<SeasonalFoodDto> body = response.body();
                if (response.code() == 201) {
                    contactFoods = ContactFood.createContactsList(20);
                    foodAdapter = new FoodAdapter(contactFoods);
                    recyclerFood.addItemDecoration(leftDecoration);
                    recyclerFood.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
                    recyclerFood.setAdapter(foodAdapter);
                } else {
                    contactFoods = ContactFood.createContactsList(20);
                    foodAdapter = new FoodAdapter(contactFoods);
                    recyclerFood.addItemDecoration(leftDecoration);
                    recyclerFood.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
                    recyclerFood.setAdapter(foodAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<SeasonalFoodDto>> call, Throwable t) {
                contactFoods = ContactFood.createContactsList(20);
                foodAdapter = new FoodAdapter(contactFoods);
                recyclerFood.addItemDecoration(leftDecoration);
                recyclerFood.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
                recyclerFood.setAdapter(foodAdapter);
            }
        });
    }

    private void setAdapter() {
        contactFoods = ContactFood.createContactsList(20);
        foodAdapter = new FoodAdapter(contactFoods);
        recyclerFood.addItemDecoration(leftDecoration);
        recyclerFood.setLayoutManager((new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)));
        recyclerFood.setAdapter(foodAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transaction = fragmentManager.beginTransaction();

        view.findViewById(R.id.iv_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(homeFragment.newInstance());
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // 백스택 모두 지우기
            }
        });

        view.findViewById(R.id.btn_fruit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceHomeFragment(fruitFragment.newInstance());
            }
        });

        view.findViewById(R.id.btn_vegetable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceHomeFragment(vegetableFragment.newInstance());
            }
        });

        view.findViewById(R.id.btn_fish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceHomeFragment(fishFragment.newInstance());
            }
        });
    }

    public void closeKeyBoard(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}