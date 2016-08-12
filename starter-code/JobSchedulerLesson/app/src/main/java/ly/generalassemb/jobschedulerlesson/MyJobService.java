package ly.generalassemb.jobschedulerlesson;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.PersistableBundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by owlslubic on 8/12/16.
 */
public class MyJobService extends JobService {
    //JobService runs on the Main/UI thread, so let's add an AsyncTask so it doesn't kill our UI
    private AsyncTask<Void, Void, String> mTask;//we want a string returned because we're getting the date and time and adding it to the singleton
    private AsyncTask<Void, Void, String> mDateTask;


    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        final String extra = jobParameters.getExtras().getString("extra");

        //the job info object we create in main activity is passed to this as job parameters (i.e. should it be runnign with wifi? should it have a persistent bundle? etc
        mTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                Calendar calendar = Calendar.getInstance();
                //i wanna SET the calendars date and time
                calendar.setTimeInMillis(System.currentTimeMillis());
                //for date/time formatting:
                SimpleDateFormat timeFormat;//takes in a string that formats the time or date
                switch (extra) {
                    default://initializes either way
                    case "5":
                        timeFormat = new SimpleDateFormat("hh:mm:ss");
                        break;
                    case "7":
                        timeFormat = new SimpleDateFormat("MM/dd/yyyy");
                        break;
                }


                String newText = "New Text " + timeFormat.format(calendar.getTime());//this is the part that automatically formats the time for us
                return newText;
            }

            @Override
            protected void onPostExecute(String string) {//using this because it runs on the UI thread, and we wanna post our date/time there
                super.onPostExecute(string);

                DataSingleton.getInstance().updateMyText(string);
                //tell the job scheduler that my job is done, finish it!
                //job schedules hold on to a wake lock - whcih forces the phone to stay on, keeps all the services running
                //job schedulers job is to improve performace, and wake lock decreases perfomance, whic is why we call job finish - so it knows to let go of the wake lock
                jobFinished(jobParameters, false);//needsReschedule param as TRUE will tell this job to rerun accordig to criteria called BACKOFF criteria
                //backoff criteria can increase linearly(adds the same amount of time over and over - if ti's rescheduled five times, and the backoff is 5 seconds, then its 25 seconds), or exponentially(set it to reschedule 5 times with a backoff time of 2sec, then its 5^2)
            }
        };

        mTask.execute();
        return true;//false tells the jobscheduler this task is QUICK//TRUE is this job may run for longer than it takes to complete this method
    }//returning true tells the system that im gonna explicitly stop the job, false means that its up to the job to end
    //if true, you also have to stop the job manually

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //if the conditions are no longer met, it stops the job, either by killing the whole job, or waiting til you get to the end of the current process
        //so we gotta see if the task is running, cuz if its not, then don't bother!

        if (mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            mTask.cancel(false);//FALSE: cancel gracefully finishes the job, TRUE: cancel immediately loses the data and drops the job right then and there
        }
        return false;
        //TRUE tells it to reschedule itself, using the original conditions - if those conditionsa re not met, it wont run
    }


}

/**
 * DONT FORGET TO ADD ME TO THE MANIFiESTO
 */