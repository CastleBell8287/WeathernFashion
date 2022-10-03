package kr.ac.yeonsung.giga.weathernfashion.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import kr.ac.yeonsung.giga.weathernfashion.Activities.LoginActivity;
import kr.ac.yeonsung.giga.weathernfashion.Activities.PostActivity;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.BoardRankAdapter;
import kr.ac.yeonsung.giga.weathernfashion.Adapter.DailyWeatherAdapter;
import kr.ac.yeonsung.giga.weathernfashion.R;
import kr.ac.yeonsung.giga.weathernfashion.VO.BoardRank;
import kr.ac.yeonsung.giga.weathernfashion.VO.Weather;
import kr.ac.yeonsung.giga.weathernfashion.methods.API;
import kr.ac.yeonsung.giga.weathernfashion.methods.UserMethods;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    UserMethods userMethods = new UserMethods();
    ArrayList<String> morning = new ArrayList<>(Arrays.asList("07","08","09","10","11","12","13","14","15","16","17","18"));
    ArrayList<String> night = new ArrayList<>(Arrays.asList("00","01","02","03","04","05","06","19","20","21","22","23"));
    API api = new API();
    RecyclerView.Adapter adapter;
    ImageView weather_icon;
    RecyclerView recyclerView;
    LinearLayout MainLayout;
    LinearLayout SubLayout;
    RecyclerView.Adapter rank_adapter;
    RecyclerView rank_recyclerView;
    String dateNow;
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    TextView nowTemp; //현재 온도
    TextView nowWeather; //현재 날씨
    TextView si; //현재 지역
    TextView gu; //현재 지역
    TextView dong; //현재 지역
    TextView min_temp; //최저
    TextView max_temp; //최고
    TextView feel_temp; //체감
    TextView humidity;//습도
    TextView wind_speed;//풍속
    TextView cloud; // 구름
    TextView weatherCode;
    String weatherCodeStr;
    ArrayList<Weather> list = new ArrayList();
    ArrayList<BoardRank> rank_list = new ArrayList();
    String date2;
    DatabaseReference mDatabase;
    Calendar cal = Calendar.getInstance();

    DateFormat df_now = new SimpleDateFormat("HH");



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button logout = view.findViewById(R.id.logout_btn);
        Button go_to_post = view.findViewById(R.id.go_to_post);
        cal.setTime(new Date());
        date2 = df.format(cal.getTime()) ;
        cal.setTime(new Date());
        dateNow = df_now.format(cal.getTime()) ;
        System.out.println("현재시간 " +dateNow);
        weather_icon = view.findViewById(R.id.weather_icon);
        weatherCode = view.findViewById(R.id.weather_code);
        MainLayout = view.findViewById(R.id.MainLayout);
        SubLayout = view.findViewById(R.id.SubLayout);
        nowTemp = view.findViewById(R.id.now_temp);
        si = view.findViewById(R.id.si);
        gu = view.findViewById(R.id.gu);
        dong = view.findViewById(R.id.dong);
        min_temp = view.findViewById(R.id.min_temp);
        max_temp = view.findViewById(R.id.max_temp);
        feel_temp = view.findViewById(R.id.temp_feel);
        humidity = view.findViewById(R.id.now_humidity);
        wind_speed = view.findViewById(R.id.wind_speed);
        cloud = view.findViewById(R.id.now_cloud);

        recyclerView = view.findViewById(R.id.recyclerView) ;
        rank_recyclerView = view.findViewById(R.id.rank_recyclerView) ;
        recyclerView.setHasFixedSize(true);
        rank_recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)) ;
        rank_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)) ;

        getBackgroundColor();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        logout.setOnClickListener(btnListener);
        go_to_post.setOnClickListener(btnListener);
        weather_icon.setOnClickListener(btnListener);

        for(int i = 1; i<20; i++){
            rank_list.add(new BoardRank(R.mipmap.ic_launcher_round,String.valueOf(i)));
            System.out.println(i);
        }
        rank_adapter = new BoardRankAdapter(rank_list);
        rank_recyclerView.setAdapter(rank_adapter);



        new Thread(){
            @Override
            public void run() {
                try {
                    api.getWeatherNow(getActivity(), weather_icon,nowTemp, nowWeather, si, gu, dong, min_temp, max_temp, feel_temp, humidity, wind_speed, cloud, weatherCode);
                    // 이 위치가 아니면 메소드가 실행이 안됩니다 list2에 값은 저장되는데 이 스레드 밖으로 나가면 사라져요 이걸 해결해야할 것 같습니다
                    api.getWeatherList();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //받아온 TimeDate객체 배열을 Adapter에 넣어주면 끄읕
                                adapter = new DailyWeatherAdapter(list);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)) ;
                                recyclerView.setAdapter(adapter);
                                api.getMyAddress(si,gu,dong);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        setCode();
        api.getWeatherIcon(getActivity(), weather_icon, weatherCodeStr); // 날씨아이콘
       return view;
    }

    public void setCode(){
        weatherCodeStr = weatherCode.getText().toString();
        getDailyWeather();
    }

    // 버튼 리스너 (로그아웃, 위치 날씨 설정)
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.go_to_post:
                    Intent intent2 = new Intent(getContext(), PostActivity.class);
                    startActivity(intent2);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.logout_btn:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
                case R.id.weather_icon:
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                api.getWeatherNow(getActivity(), weather_icon,nowTemp, nowWeather, si, gu, dong, min_temp, max_temp, feel_temp, humidity, wind_speed, cloud, weatherCode);

                                api.getMyAddress(si,gu,dong);// 이 위치가 아니면 메소드가 실행이 안됩니다 list2에 값은 저장되는데 이 스레드 밖으로 나가면 사라져요 이걸 해결해야할 것 같습니다
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    api.getToast(getActivity(),"새로고침 중...");
                    break;
            }
        }
    };
    public void getDailyWeather(){
        mDatabase.child("weather").child(date2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot document : snapshot.getChildren()){
                        String temp = document.child("now_Temp").getValue().toString();
                        String sky = document.child("sky").getValue().toString();
                        String pty = document.child("pty").getValue().toString();
                        String time = document.child("time").getValue().toString();
                        Weather weather = new Weather(time,sky,pty,temp);
                        list.add(weather);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter = new DailyWeatherAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
    }

    public void getBackgroundColor(){
        if (morning.contains(dateNow)){
            MainLayout.setBackgroundResource(R.drawable.bg_gradient_morning);
            SubLayout.setBackgroundResource(R.drawable.bg_morning);
            recyclerView.setBackgroundResource(R.drawable.bg_morning);
            rank_recyclerView.setBackgroundResource(R.drawable.bg_morning);
        }else if (night.contains(dateNow)){
            MainLayout.setBackgroundResource(R.drawable.bg_gradient_night);
            SubLayout.setBackgroundResource(R.drawable.bg_night);
            recyclerView.setBackgroundResource(R.drawable.bg_night);
            rank_recyclerView.setBackgroundResource(R.drawable.bg_night);
        }
    }
}