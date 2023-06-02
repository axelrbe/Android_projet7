package com.openclassroom.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.databinding.ActivityHomeBinding;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build()
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    @SuppressLint("RestrictedApi")
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                showSnackBar("connection_succeed");
                Toast.makeText(this, "connection_succeed", Toast.LENGTH_SHORT).show();

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                userId = currentUser.getUid();
                DocumentReference reference = db.collection("workmates").document(userId);

                Map<String, Object> user = new HashMap<>();
                user.put("name", response.getUser().getName());
                user.put("email", response.getUser().getEmail());
                user.put("profilePicture", response.getUser().getPhotoUri());
                user.put("providerId", response.getUser().getProviderId());

                boolean isNew = response.isNewUser();
                if (isNew) {
                    reference.set(user).addOnSuccessListener(unused -> Log.d("Firestore", "onSuccess: user profile is created for " + userId));
                }

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

    private void showSnackBar(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
