package ly.generalassemb.jobschedulerlesson;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements DataSingleton.DataChangeListener {

    TextView oldText, newText;
    private JobScheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataSingleton.getInstance().setListener(this);
        //setting a listener to this singleton so that when something changes, we can do something with that change
        //in this case, onDataChanged is called when there is a change
        oldText = (TextView) findViewById(R.id.textview_1);
        newText = (TextView) findViewById(R.id.textview_2);

        PersistableBundle bundle1 = new PersistableBundle();
        bundle1.putString("extra","5");
        PersistableBundle bundle2 = new PersistableBundle();
        bundle2.putString("extra","7");


        //This is where we will instantiate our JobScheduler and JobInfo objects
        JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()))//making it 1 because it doesnt matter because theres only one job
                //now we set some conditions
                .setPeriodic(5_000)//underscore makes it more readable, has no effect on the actual value
                //this sets the job to run every 5 seconds
                //setting PERIODIC AND other network conditions... IT WILL IGNORE THE OTHERS CONDITIONS
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)//UNMETERED =  only runs on WIFI
                //NETWORK_TYPE_NONE = it doesnt care what type, similar to NETWORK_TYPE_ANY
//                .setRequiresCharging(true)
                .setExtras(bundle1) // this is for multiple jobs in the same job service
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);//doesnt need Context.JOB_SCHE... because the activity IS a context
        jobScheduler.schedule(jobInfo);//BOOM! JOB SCHEDULED

        JobInfo jobInfo2 = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyJobService.class.getName()))
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(7_000)
                .setExtras(bundle2)
                .build();
        jobScheduler.schedule(jobInfo2);



    //lets ake another job

    }

    @Override
    public void onDataChanged(String oldTextString) {
        if (newText.getText().length() != 0) {
            oldText.setText(oldTextString);
        }
        newText.setText(DataSingleton.getInstance().getMyText());
    }
}
