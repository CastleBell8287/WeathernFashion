package kr.ac.yeonsung.giga.weathernfashion.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.ac.yeonsung.giga.weathernfashion.Fragment.BoardFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.HomeFragment;
import kr.ac.yeonsung.giga.weathernfashion.Fragment.MyInfoFragment;
import kr.ac.yeonsung.giga.weathernfashion.R;

public class MainActivity extends AppCompatActivity {
    LinearLayout main_ly;
    BottomNavigationView bottom_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); //객체 정의
        SettingListener(); //리스너 등록

        //맨 처음 시작할 탭 설정
        bottom_nav.setSelectedItemId(R.id.tab_home);

    }


    private void init() {
        main_ly = findViewById(R.id.main_ly);
        bottom_nav = findViewById(R.id.bottom_nav);
    }

    private void SettingListener() {
        //선택 리스너 등록
        bottom_nav.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, new HomeFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_board: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, new BoardFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_myinfo: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_ly, new MyInfoFragment())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }
}