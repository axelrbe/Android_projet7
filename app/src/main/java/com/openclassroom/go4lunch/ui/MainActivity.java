package com.openclassroom.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.databinding.ActivityHomeBinding;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // TODO Faire les test
    // TODO Search workmates fragment

    private static final int RC_SIGN_IN = 123;
    ActivityHomeBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            launchAuthActivity();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                showSnackBar("connection_succeed");
                Toast.makeText(this, "connection_succeed", Toast.LENGTH_SHORT).show();

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                assert currentUser != null;
                userId = currentUser.getUid();

                db.collection("workmates").document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && !document.exists()) {
                                    Map<String, Object> user = new HashMap<>();
                                    assert response != null;
                                    user.put("name", response.getUser().getName());
                                    user.put("email", response.getUser().getEmail());
                                    user.put("profilePicture", response.getUser().getPhotoUri());
                                    user.put("providerId", response.getUser().getProviderId());
                                    user.put("idSelectedRestaurant", "");

                                    boolean isNew = response.isNewUser();
                                    if (isNew) {
                                        db.collection("workmates").document(userId).set(user)
                                                .addOnSuccessListener(unused -> Log.d("usersInfos",
                                                        "onSuccess: user profile is created for " + userId));
                                    }
                                }
                            }
                        });

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);

            } else {
                if (response == null) {
                    showSnackBar("error_authentication_canceled");
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar("error_no_internet");
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar("error_unknown_error");
                        Toast.makeText(this, "error_unknown_error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void launchAuthActivity() {
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build()
                );

        // Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.app_logo)
                        .build(),
                RC_SIGN_IN);
    }

    private void showSnackBar(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
