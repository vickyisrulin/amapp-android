package org.anoopam.ext.smart.framework;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import org.anoopam.main.R;
import org.anoopam.ext.smart.customviews.SwipeableTextView;
import org.anoopam.ext.smart.exception.InvalidKeyFormatException;
import org.anoopam.ext.smart.exception.NullDataException;
import org.anoopam.ext.smart.exception.WronNumberOfArgumentsException;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class SmartActivity extends AppCompatActivity implements SmartActivityHandler {

    private static final String TAG = "SmartActivity";

    /*Parent Containers*/

    private FrameLayout childViewContainer;
    private FrameLayout drawerContainer;
    private Toolbar toolbar;
    private SwipeableTextView txtNetworkInfo;

    private DrawerLayout drawerLayout;

    private LayoutInflater layoutInflater;
    private ActionBarDrawerToggle mDrawerToggle;
    private OnDrawerStateListener drawerStateListener;

    private WakeLock wakelock;
    private NetworkStateListener networkStateListener;
    private ArrayList<KeyboardStateListener> keyboardStateListeners = new ArrayList<>();
    private KillReceiver clearActivityStack;

    public int width;
    public int height;
    public int orientation;
    private float lastTranslate = 0.0f;

    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    private Snackbar snackbar;
    private CoordinatorLayout snackBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimations();
        }
        super.onCreate(savedInstanceState);

        preOnCreate();
        setContentView(R.layout.smart_activity);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerContainer = (FrameLayout) findViewById(R.id.drawerContainer);
        childViewContainer = (FrameLayout) findViewById(R.id.lytChildViewContainer);
        clearActivityStack = new KillReceiver();
        registerReceiver(clearActivityStack, IntentFilter.create("clearStackActivity", "text/plain"));

        addChildViews();
        addSnackBarContainer();



        // Set a toolbar to replace the action bar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (drawerStateListener != null) {
                        drawerStateListener.onDrawerOpen(drawerView);
                    }

                    // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                    // open I am not going to put anything here)
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (drawerStateListener != null) {
                        drawerStateListener.onDrawerClose(drawerView);
                    }
                    // Code here will execute once drawer is closed
                }



            };

            // Drawer Toggle Object Made
            drawerLayout.setDrawerListener(mDrawerToggle);
            // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();
        }

        addDrawerLayout();

        try {
            if (SmartApplication.REF_SMART_APPLICATION.IS_CRASH_HANDLER_ENABLE)
                CrashReportHandler.attach(this);
        } catch (Throwable e) {
            e.printStackTrace();
            finish();
        }

        initComponents();
        prepareViews();
        setActionListeners();

        if (toolbar != null) {

            manageAppBar(getSupportActionBar(), toolbar, mDrawerToggle);
        }

        postOnCreate();
    }

    private void addDrawerLayout() {

        if (getDrawerLayoutID() != 0) {

            layoutInflater.inflate(getDrawerLayoutID(), drawerContainer);
        } else {
            disableSideMenu();
        }

    }

    protected  void addSnackBarContainer(){

        layoutInflater.inflate(R.layout.snackbar_container,childViewContainer);
        snackBarContainer= (CoordinatorLayout) findViewById(R.id.snackbarPosition);

    }

    protected void closeDrawer() {

        drawerLayout.closeDrawer(drawerContainer);
    }

    private void setNetworkInfoProperties() {

        if (txtNetworkInfo != null) {

            txtNetworkInfo.setVisibility(SmartUtils.isNetworkAvailable() ? View.GONE : View.VISIBLE);
            txtNetworkInfo.setText(getString(R.string.network_not_available));

        }

    }


    protected void disableSideMenu() {

        if (drawerLayout != null && mDrawerToggle != null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }else if (drawerLayout !=null){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }

    public void addChildViews() {

        if (getLayoutView() != null) {
            childViewContainer.addView(getLayoutView());
        } else {
            layoutInflater.inflate(getLayoutID(), childViewContainer);
        }

        txtNetworkInfo = (SwipeableTextView) findViewById(R.id.txtNetworkInfo);
        setNetworkInfoProperties();

        layoutInflater.inflate(R.layout.smart_transparent_frame, childViewContainer);
        childViewContainer.getViewTreeObserver().addOnGlobalLayoutListener(keyboardObserveListener);

    }

    public void setDrawerStateListener(OnDrawerStateListener drawerStateListener) {
        this.drawerStateListener = drawerStateListener;
    }


    protected View getScreenRootView() {
        return childViewContainer;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int color = getResources().getColor(R.color.textSecondary);
//             int alpha = 204; // 80% alpha
        MenuColorizer.colorMenu(this, menu, color, 255);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerContainer)) {

            closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    /**
     * This method will load new activity. If the <b>forgetMe</b> flag is passed
     * <b>false</b> then the current activity will remain in activity stack.
     * Otherwise it will be finished first and then new activity will be loaded.
     *
     * @param clazz    = Class clazz Activity will be loaded.
     * @param current  = Activity current will be the same activity from which this
     *                 function is being called.
     * @param forgetMe = boolean forgetMe will decide whether the current activity
     *                 should remain in activity stack or not.
     */

    public void loadNew(Class<?> clazz, Activity current, boolean forgetMe) {
        Intent intent = new Intent(current, clazz);
        startActivity(intent);
        if (forgetMe) {
            finish();
        }
    }

    public void loadNew(Class<?> clazz, Activity current, int flag) {
        Intent intent = new Intent(current, clazz);
        intent.setFlags(flag);
        startActivity(intent);
    }

    /**
     * This method will load new activity. If the <b>forgetMe</b> flag is passed
     * <b>false</b> then the current activity will remain in activity stack.
     * Otherwise it will be finished first and then new activity will be loaded.
     * This method will also pass String value with a key to next activity.
     *
     * @param clazz      = Class clazz Activity will be loaded.
     * @param current    = Activity current will be the same activity from which this
     *                   function is being called.
     * @param forgetMe   = boolean forgetMe will decide whether the current activity
     *                   should remain in activity stack or not.
     * @param dataTopass = Any number of key-value pair of any type passed to next
     *                   activity with specified String key.<br>
     *                   Note: Key Must Be String ,Non-Empty and NotNull.<br>
     *                   Value can be any type.<br>
     *                   e.g<br>
     *                   <b> {@code}loadNew(NextActivity.Class, CurrentActivity.this
     *                   ,true,"first","hi","second",10,"third",false);
     */
    public void loadNew(Class<?> clazz, Activity current, boolean forgetMe, Object... dataTopass) throws WronNumberOfArgumentsException, InvalidKeyFormatException,
            NullDataException {

        if (dataTopass.length % 2 != 0) {
            throw new WronNumberOfArgumentsException();
        }
        Intent intent = new Intent(current, clazz);

        for (int i = 1; i < dataTopass.length; i += 2) {

            if ((!(dataTopass[i - 1] instanceof String)) || (String.valueOf(dataTopass[i - 1]).length() <= 0) || (dataTopass[i - 1] == null)) {
                throw new InvalidKeyFormatException();
            }

            if (dataTopass[i] == null)
                throw new NullDataException();

            try {
                if (dataTopass[i] instanceof Boolean) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Boolean.parseBoolean(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof boolean[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (boolean[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Byte) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Byte.parseByte((String.valueOf(dataTopass[i]))));
                } else if (dataTopass[i] instanceof byte[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (byte[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Character) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (char) (Character) dataTopass[i]);
                } else if (dataTopass[i] instanceof char[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (char[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Double) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Double.parseDouble((String.valueOf(dataTopass[i]))));
                } else if (dataTopass[i] instanceof double[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (double[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Float) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Float.parseFloat(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof float[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (float[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Integer) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Integer.parseInt(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof int[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (int[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Long) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Long.parseLong(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof long[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (long[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Short) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Short.parseShort(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof short[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (short[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof String) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof String[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (String[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Parcelable) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Parcelable) dataTopass[i]);
                } else if (dataTopass[i] instanceof Parcelable[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Parcelable[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Serializable) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Serializable) dataTopass[i]);
                } else if (dataTopass[i] instanceof Bundle) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Bundle) dataTopass[i]);
                } else if (dataTopass[i] instanceof CharSequence) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (CharSequence) dataTopass[i]);
                }
            } catch (Throwable e) {
            }
        }

        startActivity(intent);

        if (forgetMe) {
            finish();
        }
    }

    /**
     * This method will load new activity. If the <b>forgetMe</b> flag is passed
     * <b>false</b> then the current activity will remain in activity stack.
     * Otherwise it will be finished first and then new activity will be loaded.
     * This method will also pass String value with a key to next activity.
     *
     * @param clazz       = Class clazz Activity will be loaded.
     * @param current     = Activity current will be the same activity from which this
     *                    function is being called.
     * @param requestCode = int request code.
     * @param dataTopass  = Any number of key-value pair of any type passed to next
     *                    activity with specified String key.<br>
     *                    Note: Key Must Be String ,Non-Empty and NotNull.<br>
     *                    Value can be any type.<br>
     *                    e.g<br>
     *                    <b> {@code}loadNew(NextActivity.Class, CurrentActivity.this
     *                    ,true,"first","hi","second",10,"third",false);
     */
    public void loadNewResult(Class<?> clazz, Activity current, int requestCode, Object... dataTopass) throws WronNumberOfArgumentsException, InvalidKeyFormatException,
            NullDataException {

        if (dataTopass.length % 2 != 0) {
            throw new WronNumberOfArgumentsException();
        }
        Intent intent = new Intent(current, clazz);

        for (int i = 1; i < dataTopass.length; i += 2) {

            if ((!(dataTopass[i - 1] instanceof String)) || (String.valueOf(dataTopass[i - 1]).length() <= 0) || (dataTopass[i - 1] == null)) {
                throw new InvalidKeyFormatException();
            }

            if (dataTopass[i] == null)
                throw new NullDataException();

            try {
                if (dataTopass[i] instanceof Boolean) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Boolean.parseBoolean(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof boolean[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (boolean[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Byte) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Byte.parseByte((String.valueOf(dataTopass[i]))));
                } else if (dataTopass[i] instanceof byte[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (byte[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Character) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (char) (Character) dataTopass[i]);
                } else if (dataTopass[i] instanceof char[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (char[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Double) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Double.parseDouble((String.valueOf(dataTopass[i]))));
                } else if (dataTopass[i] instanceof double[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (double[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Float) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Float.parseFloat(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof float[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (float[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Integer) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Integer.parseInt(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof int[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (int[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Long) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Long.parseLong(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof long[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (long[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Short) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), Short.parseShort(String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof short[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (short[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof String) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (String.valueOf(dataTopass[i])));
                } else if (dataTopass[i] instanceof String[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (String[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Parcelable) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Parcelable) dataTopass[i]);
                } else if (dataTopass[i] instanceof Parcelable[]) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Parcelable[]) dataTopass[i]);
                } else if (dataTopass[i] instanceof Serializable) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Serializable) dataTopass[i]);
                } else if (dataTopass[i] instanceof Bundle) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (Bundle) dataTopass[i]);
                } else if (dataTopass[i] instanceof CharSequence) {
                    intent.putExtra(String.valueOf(dataTopass[i - 1]), (CharSequence) dataTopass[i]);
                }
            } catch (Throwable e) {
            }
        }

        startActivityForResult(intent, requestCode);

    }

    /**
     * This method will set the wake lock. Once this method is called device
     * will not sleep until either application is finished or removeWakeLock()
     * method is called.
     */
    @SuppressWarnings("deprecation")
    public void setWakeLock() {

        if (wakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "");
        }

        wakelock.acquire();
    }

    /**
     * This method will remove wake lock.
     */
    public void removeWakeLock() {
        wakelock.release();
    }


   /* @Override
    public void onLowMemory() {

        AQUtility.cleanCacheAsync(this);
        SmartUtils.getOKDialog(this, getString(R.string.alert_title_low_memory), getString(R.string.alert_message_low_memory), getString(R.string.ok), true, new AlertNeutral() {

            @Override
            public void NeutralMathod(DialogInterface dialog, int id) {
            }
        });

    }

*/
    private class NetworkStateListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            setNetworkInfoProperties();
        }
    }

    private final class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }


    @SuppressWarnings("deprecation")
    public int getDeviceWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    @SuppressWarnings("deprecation")
    public int getDeviceHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    public View getSnackBarContainer() {

        return snackBarContainer;
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateListener = new NetworkStateListener();
        registerReceiver(networkStateListener, new IntentFilter("NetworkState"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(clearActivityStack);
    }


    public void setKeyboardStateListener(KeyboardStateListener keyboardStateListener) {
        keyboardStateListeners.add(keyboardStateListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener keyboardObserveListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect r = new Rect();
            childViewContainer.getRootView().getWindowVisibleDisplayFrame(r);
            int screenHeight = childViewContainer.getRootView().getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            final FrameLayout transparentFrame = (FrameLayout) findViewById(R.id.lytTransparentFrame);

            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened

                transparentFrame.setVisibility(View.VISIBLE);

                for (int i = 0; i < keyboardStateListeners.size(); i++) {

                    if (keyboardStateListeners.get(i) != null) {
                        keyboardStateListeners.get(i).onkeyboardOpen();
                    }
                }

                transparentFrame.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (shouldKeyboardHideOnOutsideTouch()) {

                            SmartUtils.hideSoftKeyboard(SmartActivity.this);
                            return true;
                        } else {
                            return false;
                        }

                    }
                });

            } else {
                // keyboard is closed
                for (int i = 0; i < keyboardStateListeners.size(); i++) {

                    if (keyboardStateListeners.get(i) != null) {
                        keyboardStateListeners.get(i).onKeyboardClose();
                    }
                }

                transparentFrame.setVisibility(View.GONE);
                transparentFrame.setOnTouchListener(null);

            }
        }
    };

    public interface OnDrawerStateListener {

        void onDrawerOpen(View drawerView);

        void onDrawerClose(View drawerView);

    }


    protected void setViewTransitionName(View view, String tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTransitionName(tag);
        }

    }

}
