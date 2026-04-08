package com.example.ecostayretreat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ecostayretreat.databinding.ActivityMainControllerBinding;
import com.example.ecostayretreat.model.UserModel;
import com.example.ecostayretreat.viewmodel.AuthViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * File: MainController.java
 * Description: The main activity that controls the app's navigation after login.
 * This is the central file for managing the Toolbar, Drawer, and fragment navigation.
 */
public class MainController extends AppCompatActivity {

    private ActivityMainControllerBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // ====================================================================
        // STEP 1: SET UP THE TOOLBAR
        // This is a CRITICAL step. It tells the activity to treat our custom
        // MaterialToolbar as the official app bar. Without this, the hamburger
        // menu icon will not appear.
        // ====================================================================
        setSupportActionBar(binding.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // ====================================================================
        // STEP 2: CONFIGURE THE NAVIGATION DRAWER
        // We list all the main pages of our app here. These are the "top-level"
        // destinations. The NavController will automatically show the hamburger
        // icon on these pages and a "back" arrow on any other page.
        // ====================================================================
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_explore_rooms, R.id.nav_activities,
                R.id.nav_cart, R.id.nav_profile, R.id.nav_bookings,
                R.id.nav_admin_control, R.id.nav_eco_info)
                .setOpenableLayout(drawer)
                .build();

        // ====================================================================
        // STEP 3: FIND THE NAVIGATION CONTROLLER
        // This gets the controller that manages switching between fragments
        // within the main screen area.
        // ====================================================================
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // ====================================================================
        // STEP 4: CONNECT EVERYTHING TOGETHER (THE MOST IMPORTANT PART)
        // These two lines are the "magic" that makes the drawer work.
        // ====================================================================

        // This line connects the Toolbar (with its title and hamburger icon)
        // to the NavController. It automatically updates the title and shows
        // the correct icon.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // This line connects the NavigationView (the drawer menu itself)
        // to the NavController. It handles the clicks on menu items,
        // highlights the selected item, and triggers navigation.
        NavigationUI.setupWithNavController(navigationView, navController);


        // --- Additional setup ---
        setupLogoutHandler();
        checkUserRole();
    }

    /**
     * Checks the current user's role (admin or regular) and adjusts the UI accordingly.
     * This makes the "Admin Control" menu item visible only for admins.
     */
    private void checkUserRole() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null && user.isAdmin()) {
                    binding.navView.getMenu().findItem(R.id.nav_admin_control).setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainController.this, "Failed to check user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up a custom click listener for the logout menu item.
     * This is handled separately because it performs an action (logging out)
     * instead of navigating to a fragment.
     */
    private void setupLogoutHandler() {
        binding.navView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            authViewModel.logout();
            Intent intent = new Intent(MainController.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        });
    }

    /**
     * This method is required to handle clicks on the hamburger icon or back arrow.
     * It tells the NavigationUI to handle the drawer opening/closing or navigating back.
     * @return true if navigation was successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Inflates the menu for the toolbar (e.g., the notification bell).
     * This is separate from the navigation drawer menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        final MenuItem notificationItem = menu.findItem(R.id.action_notifications);
        View actionView = notificationItem.getActionView();
        if (actionView != null) {
            actionView.setOnClickListener(v -> onOptionsItemSelected(notificationItem));
        }
        return true;
    }

    /**
     * Handles clicks on toolbar action items like the notification bell.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Let the NavigationUI try to handle the click first.
        // This ensures that clicks on drawer items in different layouts (e.g., tablets) are handled.
        // If it's not a navigation action, handle it here.
        if (item.getItemId() == R.id.action_notifications) {
            navController.navigate(R.id.action_global_nav_notifications);
            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }
}