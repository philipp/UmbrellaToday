package org.bostonandroid.umbrellatoday;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewAlert extends PreferenceActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.new_alert);
    setContentView(R.layout.new_alert);
    
    Button nextButton = (Button)findViewById(R.id.save_alert);
    nextButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        saveAlert().
          onSuccess(new EitherRunner<Alert>() {
            public void run(Alert a) { 
              finish();
          }}).
          onFailure(new EitherRunner<Alert>() {
            public void run(Alert a) {
              Toast.makeText(getApplicationContext(),
                  "Alert save failed: "+a.errorString(),
                  Toast.LENGTH_LONG);
          }});
      }
    });
  }
  
  private Either<Alert> saveAlert() {
    PreferenceManager pm = getPreferenceManager();
    Alert.Builder alertBuilder = new Alert.Builder();
    Alert alert = alertBuilder.
      alertAt(((TimePreference)pm.findPreference("time")).getTime()).
      repeatDays(((RepeatPreference)pm.findPreference("repeat")).getChoices()).
      autolocate(((CheckBoxPreference)pm.findPreference("detect_location")).isChecked()).
      location(((EditTextPreference)pm.findPreference("location")).getText()).
      build();
    /// TODO: this #save needs to be async.
    return makeAlertFailure(alert, alert.save(getApplicationContext()));
  }
  
  private Either<Alert> makeAlertFailure(Alert a, boolean saved) {
    if (saved) return new Right<Alert>(a);
    else return new Left<Alert>(a);
  }
}