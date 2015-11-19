package co.mkry.permissions;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

  private static final int RC_WRITE_CALENDAR = 0;
  private static final int RC_MUTIPLE_PERMISSIONS = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    requestMultiplePermissions();
  }

  private void checkPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
        new AlertDialog.Builder(this)
          .setMessage("You need to allow access to the calendar.")
          .setNegativeButton("cancel", null)
          .setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override public void onClick(final DialogInterface dialog, final int which) {
              ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, RC_WRITE_CALENDAR);
            }
          })
          .setCancelable(true)
          .create()
          .show();
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, RC_WRITE_CALENDAR);
      }
    }
  }

  private void requestMultiplePermissions() {
    final String[] permissions = {
      Manifest.permission.WRITE_CALENDAR,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.WRITE_CONTACTS
    };
    ActivityCompat.requestPermissions(this, permissions, RC_MUTIPLE_PERMISSIONS);
  }

  private Uri addEvent() {
    final ContentResolver contentResolver = getContentResolver();
    final ContentValues contentValues = new ContentValues();

    final long startTime = System.currentTimeMillis();

    contentValues.put(CalendarContract.Events.DTSTART, startTime);
    contentValues.put(CalendarContract.Events.DTEND, startTime + 15 * 60 * 1000L);
    contentValues.put(CalendarContract.Events.TITLE, "test event");
    contentValues.put(CalendarContract.Events.DESCRIPTION, "test description");
    contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
    contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
    contentValues.put(CalendarContract.Events.HAS_ALARM, false);

    // insert event to calendar
    final Uri result = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
    if (result != null) {
      Snackbar.make(findViewById(android.R.id.content), "Event added", Snackbar.LENGTH_SHORT).show();
    }
    return result;
  }

  @Override public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
    switch (requestCode) {
      case RC_WRITE_CALENDAR:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // do stuff
        } else {
          Snackbar.make(findViewById(android.R.id.content), "WRITE_CALENDAR Denied", Snackbar.LENGTH_SHORT).show();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        break;
    }
  }
}
