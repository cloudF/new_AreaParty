package com.dkzy.areaparty.phone.bluetoothxie;

import android.os.AsyncTask;

import com.example.connection.DeviceConnection;

public class MouseAndBoardAsyncTask extends AsyncTask<Void, Void, Void> {

    String PCIP;
	private final MouseAndBoardCallBack myCallBack;

	public MouseAndBoardAsyncTask(MouseAndBoardCallBack callBack) {
		myCallBack = callBack;
	}





	@Override
	protected Void doInBackground(Void... arg0) {

		DeviceConnection.getInstance().close();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        PCIP = Constants.getIP_PC();
        if (PCIP!=null){
            DeviceConnection.getInstance().init(PCIP,64788,"1234");


            DeviceConnection.getInstance().connect();
        }


		while (DeviceConnection.getInstance().isConnecting()) {
			try {
				Thread.sleep(100);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

		myCallBack.callback();
	}
}