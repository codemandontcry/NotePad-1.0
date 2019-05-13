package com.zc.memo.activity;

import java.util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.zc.memo.R;
import com.zc.memo.item.Note;
import com.zc.memo.manager.NoteManager;
import com.zc.memo.utils.OneShotAlarm;

public class ContentActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Context mContext;

    private TextView tvTitle;
    private TextView tvDate;
    private EditText etContent;
    private ImageView ivEdit;
    private ImageView ivDelete;
    private ImageView ivSettings;
    private ImageView ivalram;
    private TextView av;
    private Note note;
    private String title;
    private Intent intent;
    private LinearLayout layout;

    //alarm clock
    int num=0; //for requestcode
    int BIG_NUM_FOR_ALARM=100;
    String alarm="";
    int alarm_hour=0;
    int alarm_minute=0;
    int alarm_year=0;
    int alarm_month=0;
    int alarm_day=0;
    String alarmtitle;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mContext = this;
        intent = this.getIntent();

        bindViews();

        title = intent.getStringExtra("title");
        //权限申请
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            title = data.getStringExtra("now_title");
            onResume();
        }
        catch (Exception e1){
            title = intent.getStringExtra("title");
        }
    }

    private void bindViews() {
        tvTitle = (TextView) findViewById(R.id.tv_tb_title);
        tvDate = (TextView) findViewById(R.id.date_content);
        etContent = (EditText) findViewById(R.id.editor);
        ivEdit = (ImageView) findViewById(R.id.iv_bb_edit);
        ivDelete = (ImageView) findViewById(R.id.iv_bb_delete);
        ivSettings = (ImageView) findViewById(R.id.iv_bb_settings);
        ivalram = (ImageView) findViewById(R.id.iv_bb_alarm);
        layout= (LinearLayout) this.findViewById(R.id.ContentLayout);
        av=(TextView) findViewById(R.id.date_alarm);
    }
    private void initViews(){

        note = new NoteManager(mContext).get(title);
        tvTitle.setText(note.getTitle());
        alarmtitle = note.getTitle();
        tvDate.setText(note.getCreate_date());
        if(note.getDate_alarm().length()>1) {
            av.setText("Alert at "+note.getDate_alarm()+"!");
        }else {
            av.setVisibility(View.GONE);
        }
        if(note.getBackground()!=null){
            layout.setBackgroundColor(Color.parseColor(note.getBackground()));
        }
        etContent.setText(note.getText());
        etContent.setFocusable(false);
        etContent.setFocusableInTouchMode(false);
        initBottom();
    }

    private void initBottom() {
        //编辑
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CreateActivity.class);
                intent.putExtra("title",title);
                startActivityForResult(intent,1);
                onResume();
            }
        });
        //删除
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认删除")
                        .setMessage("将要删除"+title)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                onResume();

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new NoteManager(mContext).delete(title);
                                finish();
                            }
                        })
                        .create().show();
            }
        });
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this);
                builder.setIcon(R.drawable.fab_settings);
                builder.setTitle("选择一个背景色");
                //    指定下拉列表的显示数据
                final String[] colors = {"护眼色", "薰衣淡紫", "粉粉粉"};
                builder.setItems(colors, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(ContentActivity.this, "选择的背景色为：" + colors[which], Toast.LENGTH_SHORT).show();
                        switch (which){
                            case 0:
                                new NoteManager(mContext).updateBackground(title,"#C7EDCC");
                                onResume();
                                break;
                            case 1:
                                new NoteManager(mContext).updateBackground(title,"#E6E6FA");
                                onResume();
                                break;
                            case 2:
                                new NoteManager(mContext).updateBackground(title,"#FC9D9A");
                                onResume();
                                break;
                            default:
                                break;
                        }

                    }
                });
                builder.show();
            }
        });
        ivalram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm(v);

                onResume();
            }
        });
        av.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(v.getId()==R.id.date_alarm) {
                    //delete the alarm information
                    alarm="";
                    //hide textView
                    av.setVisibility(View.GONE);
                    new NoteManager(mContext).deleteAlarm(title);
                    onResume();
                }
                return true;
            }
        });
    }
    //*********************************set alarm clock***********************************
    public void setAlarm(View v) {
        if(alarm.length()<=1) {
            //if no alarm clock has been set up before
            //show the current time
            Calendar c=Calendar.getInstance();
            alarm_hour=c.get(Calendar.HOUR_OF_DAY);
            alarm_minute=c.get(Calendar.MINUTE);

            alarm_year=c.get(Calendar.YEAR);
            alarm_month=c.get(Calendar.MONTH)+1;
            alarm_day=c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            //show the alarm clock time which has been set up before
            int i=0, k=0;
            while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
            alarm_year=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
            alarm_month=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
            alarm_day=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
            alarm_hour=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            alarm_minute=Integer.parseInt(alarm.substring(k));
        }

        new TimePickerDialog(this,this,alarm_hour,alarm_minute,true).show();
        new DatePickerDialog(this,this,alarm_year,alarm_month-1,alarm_day).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        alarm_year=year;
        alarm_month=monthOfYear+1;
        alarm_day=dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarm_hour=hourOfDay;
        alarm_minute=minute;

        alarm=alarm_year+"/"+alarm_month+"/"+alarm_day+" "+alarm_hour+":"+alarm_minute;
        av.setText("Alert at "+alarm+"!");
        av.setVisibility(View.VISIBLE);

        Log.d("ContentActivity","alarm"+alarm);
        new NoteManager(mContext).updateAlarm(title,alarm);
        loadAlarm(alarm, title, 0);
        Toast.makeText(this,"Alarm will be on at "+alarm+" !",Toast.LENGTH_LONG).show();
    }

    //******************************************************************************************

    //set an alarm clock according to the "alarm"
    private void loadAlarm(String alarm, String title, int days) {
        int alarm_hour=0;
        int alarm_minute=0;
        int alarm_year=0;
        int alarm_month=0;
        int alarm_day=0;

        int i=0, k=0;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_year=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
        alarm_month=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
        alarm_day=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
        alarm_hour=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        alarm_minute=Integer.parseInt(alarm.substring(k));


        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver. Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        Intent intent = new Intent(ContentActivity.this, OneShotAlarm.class);
        intent.putExtra("alarm_title",title);
        PendingIntent sender = PendingIntent.getBroadcast(
                ContentActivity.this, 0, intent, 0);

        // We want the alarm to go off 10 seconds from now.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.add(Calendar.SECOND, 5);

        Calendar alarm_time = Calendar.getInstance();
        alarm_time.set(alarm_year,alarm_month-1,alarm_day,alarm_hour,alarm_minute);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //if(interval==0)
        am.set(AlarmManager.RTC_WAKEUP, alarm_time.getTimeInMillis(), sender);
    }















}
