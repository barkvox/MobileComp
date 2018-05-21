package joshua.barker.com.mobilecomp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";



    EditText etID;
    EditText etDateTime;
    EditText etTemp;
    EditText etCurrent;
    EditText etPWM;

    Button btn_Insert;
    Button btn_Update;
    Button btn_Delete;
    Button btn_LoadAll;

    TextView tvFinalData;

    DatabaseHelper mDatabaseHelper;

    public String mDeviceName;
    public String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic bluetoothGattCharacteristicHM_10;



    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
               // updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
              //  updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //   test_string = ("" + 20);
                //Echo back received data, with something inserted
                final byte[] rxBytes = bluetoothGattCharacteristicHM_10.getValue(); // THIS IS FOR ANDROID TO READ
                //  final byte[] rxBytes = test_string.getBytes();
                final byte[] insertSomething = {(byte)'\n'};
                byte[] txBytes = new byte[insertSomething.length + rxBytes.length];
                System.arraycopy(insertSomething, 0, txBytes, 0, insertSomething.length);
                System.arraycopy(rxBytes, 0, txBytes, insertSomething.length, rxBytes.length);

                if(bluetoothGattCharacteristicHM_10 != null){
                    //DON'T TOUCH THIS!!!!
                    //    bluetoothGattCharacteristicHM_10.setValue(test_string);
                    //    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    //    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10,true);
                }

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        init();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDatabaseHelper = new DatabaseHelper(DatabaseActivity.this);


    }
    private void init(){
        btn_Insert = (Button) findViewById(R.id.btn_Insert);
        btn_Update = (Button) findViewById(R.id.btn_Update);
        btn_Delete = (Button) findViewById(R.id.btn_Delete);
        btn_LoadAll = (Button) findViewById(R.id.btn_LoadAll);

        btn_Insert.setOnClickListener(dbButtonsListener);
        btn_Update.setOnClickListener(dbButtonsListener);
        btn_Delete.setOnClickListener(dbButtonsListener);
        btn_LoadAll.setOnClickListener(dbButtonsListener);


        tvFinalData = (TextView) findViewById(R.id.tvData);
    }
            private View.OnClickListener dbButtonsListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        /*
                        case R.id.btn_Update:
                            long resultUpdate =  mDatabaseHelper.update(Integer.parseInt(getValue(etID)), getValue(etDateTime),getValue(etTemp),getValue(etCurrent),
                                    Double.valueOf(getValue(etPWM)));
                            if(resultUpdate == -1){
                                Toast.makeText(DatabaseActivity.this, "Some error occured while updating", Toast.LENGTH_SHORT).show();
                            }else if(resultUpdate == 1){
                                Toast.makeText(DatabaseActivity.this, "Data Inserted Successfully, ID " + resultUpdate, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(DatabaseActivity.this, "Some error occured, multiple records updated", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.btn_Delete:
                            long resultDelete =  mDatabaseHelper.insert(Integer.parseInt(getValue(etID)), getValue(etDateTime),getValue(etTemp),getValue(etCurrent),
                                    Double.valueOf(getValue(etPWM)));
                            if(resultDelete == 0){
                                Toast.makeText(DatabaseActivity.this, "Some error occured while deleting", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(DatabaseActivity.this, "Data Deleted Successfully, ID", Toast.LENGTH_SHORT).show();
                            }
*/
                        case R.id.btn_LoadAll:
                            Log.d(TAG, "onClick: btnLoadAll");
                            StringBuffer finalData = new StringBuffer();
                            Cursor cursor = mDatabaseHelper.getAllRecords();

                            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                finalData.append(cursor.getInt(cursor.getColumnIndex(mDatabaseHelper.ID)));
                                finalData.append(" - ");
                                finalData.append(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.DATE_TIME)));
                                finalData.append(" - ");
                                finalData.append(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.CURRENT)));
                                finalData.append(" - ");
                                finalData.append(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.TEMP)));
                                finalData.append(" - ");
                                finalData.append(cursor.getString(cursor.getColumnIndex(mDatabaseHelper.PWM)));
                                finalData.append(" \n");
                            }

                            tvFinalData.setText(finalData);
                            break;
                    }
                }
            };
    private String getValue (EditText editText){
        String data = editText.getText().toString().trim();
        return data;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
        mDatabaseHelper.openDB();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
        mDatabaseHelper.closeDB();
    }
}


