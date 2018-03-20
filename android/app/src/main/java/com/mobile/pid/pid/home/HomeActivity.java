package com.mobile.pid.pid.home;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.pid.pid.R;
import com.mobile.pid.pid.UsuarioLogado;
import com.mobile.pid.pid.home.buscar.BuscarFragment;
import com.mobile.pid.pid.home.feed.FeedFragment;
import com.mobile.pid.pid.home.perfil.PerfilFragment;
import com.mobile.pid.pid.home.turmas.TurmasFragment;
import com.mobile.pid.pid.login.Usuario;

public class HomeActivity extends AppCompatActivity
{
    private BottomNavigationView home_menu;
    private FrameLayout          home_frame;

    private FeedFragment   feedFragment;
    private BuscarFragment buscarFragment;
    private TurmasFragment turmasFragment;
    private PerfilFragment perfilFragment;

    private FirebaseAuth auth;
    private FirebaseUser user_logged;
    private DatabaseReference user_database;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home_menu  = (BottomNavigationView) findViewById(R.id.home_menu);
        home_frame = (FrameLayout) findViewById(R.id.home_frame);

        feedFragment   = new FeedFragment();
        buscarFragment = new BuscarFragment();
        turmasFragment = new TurmasFragment();
        perfilFragment = new PerfilFragment();

        BottomNavigationViewHelper.disableShiftMode(home_menu);

        setFragment(feedFragment);

        home_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menu_feed:
                        setFragment(feedFragment);
                        return true;

                    case R.id.menu_search:
                        setFragment(buscarFragment);
                        return true;
                    case R.id.menu_classes:
                        setFragment(turmasFragment);
                        return true;
                    case R.id.menu_profile:
                        setFragment(perfilFragment);
                        return true;
                    default:
                        return false;
                }

            }
        });

        loadDataUser(); // CARREGAR TODOS OS DADOS DO USUARIO LOGADO
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.home_frame, fragment);
        fragmentTransaction.commit();
    }

    private void loadDataUser() {

        auth = FirebaseAuth.getInstance();
        user_logged = auth.getCurrentUser();
        user_id = user_logged.getUid();

        user_database = FirebaseDatabase.getInstance().getReference().child("usuarios").child(user_id);

        user_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UsuarioLogado.user = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

