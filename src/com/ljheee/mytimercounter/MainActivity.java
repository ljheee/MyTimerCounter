package com.ljheee.mytimercounter;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private AsyTask asyTask;//异步任务
	
	private TextView showTime;
	private TextView resultView;
	private Button startButton;
	private Button pauseButton;
	private Button stopButton;

	private int pc = 0;//暂停时的，计数值
	int ii = 1;
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        showTime = (TextView) findViewById(R.id.show_time);
        resultView = (TextView) findViewById(R.id.result_view);
        startButton = (Button) findViewById(R.id.start_button);
        pauseButton = (Button) findViewById(R.id.pause_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        
        
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				asyTask = new AsyTask();
				asyTask.execute(0);
			}
		});
        stopButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		asyTask.cancel(true);
        	}
        });
       
        pauseButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		resultView.setText("第"+ii+"次计数     pc="+pc);
        		ii++;
        	}
        });
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
		case R.id.action_exit:
			finish();
			break;
		case R.id.action_settings:
			Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    
    /**
     * 异步任务--避免：Service开启新的线程，并利用Handler机制，让Service中新开启的线程和主线程进行了通信，从而利用主线程改变了UI。
     * 没有优先级，只是在前台进程
     * @author ljheee
     *
     */
    private class AsyTask extends AsyncTask<Integer, Integer, Void> {
    	
		private int i;

		/**
		 * 预处理
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startButton.setEnabled(false);
		}

		/**
		 * 其他线程执行，不能做UI改变
		 */
		@Override
		protected Void doInBackground(Integer... value) {
			i = value[0];
			try {      while (!isCancelled()) {
					i++;
					publishProgress(i);//此处：促使UI线程去执行onProgressUpdate（）更新界面
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {e.printStackTrace();}

			return null;
		}
		
		/**
		 * UI线程会执行,更新界面
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pc = values[0];
			showTime.setText("count=" + values[0]);
		}
		
		/**
		 * 异常终止后执行
		 */
		@Override
		protected void onCancelled() {
			super.onCancelled();
			showTime.setText("停止计数count=" + i);
			startButton.setEnabled(true);
		}
		
		/**
		 * （若任务是有限次）正常执行完结束后，执行该方法
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			showTime.setText("停止计数count=" + i);
			startButton.setEnabled(true);
		}
		
    }

}
