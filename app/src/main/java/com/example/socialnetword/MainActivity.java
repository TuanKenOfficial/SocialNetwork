package com.example.socialnetword;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;


import com.example.socialnetword.Fragment.GroupsFragment;
import com.example.socialnetword.Fragment.HomeFragment;
import com.example.socialnetword.Fragment.MessagesFragment;
import com.example.socialnetword.Fragment.NotificationFragment;
import com.example.socialnetword.Fragment.ProfileFragment;
import com.example.socialnetword.Fragment.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView; // bottom menu
//    Fragment selectedFragment; // fragment
    NavigationView navigationView; // menu navigation
    DrawerLayout drawerLayout; // drawer
    FirebaseAuth mAuth; //auth
    DatabaseReference ref; // database
    FirebaseUser firebaseUser; // user

    Toolbar toolbar; //toolbar
    ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation); // menu bottom
        navigationView = findViewById(R.id.navigation_view); //menu navigation kéo ra
        drawerLayout =  findViewById(R.id.drawlayout);
        //hiện tên trên thanh toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //hiện 3 gạch trên toolbar
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //bottom menu
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        //Home fragment


        // liên quan dòng 114 bên search users adapter
        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();

            editor.putString("profileid",publisher);
            editor.apply();
            ProfileFragment fragment6 = new ProfileFragment();
            FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
            ft6.replace(R.id.fragment_container,fragment6,"");
            ft6.commit();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    new ProfileFragment()).commit();
        }else {
            HomeFragment fragment1 = new HomeFragment();
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.fragment_container,fragment1,"");
            ft1.commit();
        }


        //navigation view
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UsersMenuSelector(item);
                return false;
            }

        });

    }





    //menu options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void UsersMenuSelector(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                Toast.makeText(this, "Vị trí", Toast.LENGTH_SHORT).show();
                toolbar.setTitle("Vị trí");
                break;

            case R.id.item2:
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                toolbar.setTitle("Theo dõi");
                break;

            case R.id.item3:
                SendUserToPostActivity();
                toolbar.setTitle("Đăng ảnh");
                break;

            case R.id.item4:
                SendUserToGroupActivity();
                toolbar.setTitle("Tạo nhóm chat");
                Toast.makeText(this, "Tạo nhóm", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item5:
//                SendUserToGroupAddActivity();
                toolbar.setTitle("Thêm người vào nhóm");
                Toast.makeText(this, "Thêm người dùng vào nhóm", Toast.LENGTH_SHORT).show();
                break;  

            case R.id.item6:
//                SendUserToGroupAddActivity();
                toolbar.setTitle("Thông tin nhóm");
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item7:
                Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                toolbar.setTitle("Cài đặt");
                break;

            case R.id.item8:
                mAuth.signOut();
                SendUserToLoginActivity();
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                toolbar.setTitle("Đăng xuất");
                break;


        }
    }


     //liên quan item 5
    private void SendUserToGroupAddActivity() {
        Intent postIntent = new Intent(MainActivity.this,GroupChatsActivity.class);
        postIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(postIntent);
    }

    private void SendUserToGroupActivity() {
        Intent postIntent = new Intent(MainActivity.this,CreateGroupActivity.class);
        postIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(postIntent);
        finish();
    }


    private Intent SendUserToLoginActivity() {
        Intent postIntent = new Intent(MainActivity.this,LoginActivity.class);
//        postIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        postIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(postIntent);
        return postIntent;
    }

    private void SendUserToPostActivity() {
        Intent postIntent = new Intent(MainActivity.this,PostsActivity.class);
        postIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(postIntent);
        finish();
    }



    //bottom menu
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = menuItem -> {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                toolbar.setTitle("");
                HomeFragment fragment1 = new HomeFragment();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.fragment_container,fragment1,"");
                ft1.commit();
                return true;



            case R.id.nav_users:
                toolbar.setTitle("Người dùng");
                UsersFragment fragment2 = new UsersFragment();
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.fragment_container,fragment2,"");
                ft2.commit();
               return true;



            case R.id.nav_messages:
                toolbar.setTitle("Chat");
                MessagesFragment fragment3 = new MessagesFragment();
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.fragment_container,fragment3,"");
                ft3.commit();
//                selectedFragment = new MessagesFragment();
                return true;


            case R.id.nav_more:
                showMoreOptions();
                return true;

            case R.id.nav_profile:

                //liên quan bên search users adapter dòng 102
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid" ,FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                ProfileFragment fragment6 = new ProfileFragment();
                FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                ft6.replace(R.id.fragment_container,fragment6,"");
                ft6.commit();
                return true;


        }
        return false;

    };


    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this,bottomNavigationView, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE, 0,0,"Thông báo");
        popupMenu.getMenu().add(Menu.NONE, 1,0,"Nhóm Chats");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == 0){
                    toolbar.setTitle("Thông báo");
                    NotificationFragment fragment4 = new NotificationFragment();
                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.fragment_container,fragment4,"");
                    ft4.commit();
                    return true;

                }
                else if(id == 1){

                    toolbar.setTitle("Nhóm Chats");
                    GroupsFragment fragment5 = new GroupsFragment();
                    FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                    ft5.replace(R.id.fragment_container,fragment5,"");
                    ft5.commit();
                    return true;

                }
                return false;
            }
        });
        popupMenu.show();

    }

    //status bên chats
    private void status (String status){
        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status" ,status);
        ref.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
