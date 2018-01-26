package com.tistory.chebaum.httpurlconnectiontest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivityLog";

    // sendRequest() 를 위해서는 아래의 두개의 변수가 꼭 필요하다!
    private String serverResponse;
    List<ServerChannel> serverStatus=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String head = "http://";
        final String url="172.31.7.11";
        final String webPort=":80";
        final String footer="/vb.htm?getrelayenable=all&getchannels";
        final String id="admin";
        final String pw="9999";

        ((Button)findViewById(R.id.getRequestBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 함수 실행시 List<ServerChannel> serverStatus 객체에 채널 현황이 저장된다. (if null, failed)
                sendRequest(head, url, webPort, footer, id, pw);
            }
        });
    }

    public void sendRequest(String head, String url, String webPort, String footer, String id, String pw) {
        final String basicAuth = "Basic "+ new String(Base64.encode((id+":"+pw).getBytes(),Base64.DEFAULT));
        final String requestStr = head+url+webPort+footer;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL obj = new URL(requestStr);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Authorization", basicAuth);
                    con.setUseCaches(false);


                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    ((TextView) findViewById(R.id.textview)).setText(response);
                    serverResponse = response.toString();
                    Log.d(TAG, response.toString());
                } catch (Exception e) {
                    // 잘못된 값이 입력되었음을 / 연결에 실패했음을 사용자에게 알리고 쓰레드는 종료시킨다.
                    return;
                }
                // TODO 체크!... 항상 HTTP/1.0으로 문자열이 끝날까?
                int getChannelsStartingIdx = serverResponse.indexOf("OK getchannels");
                int getChannelsEndingidx = serverResponse.indexOf("HTTP/1.0");


                String getRelayenableResponse;
                String getChannelsResponse;
                getRelayenableResponse = serverResponse.substring(new String("OK getrelayenable=").length(), getChannelsStartingIdx);
                getChannelsResponse = serverResponse.substring(getChannelsStartingIdx + new String("OK getchannels=").length(), getChannelsEndingidx);

                Log.d(TAG, getRelayenableResponse);
                Log.d(TAG, getChannelsResponse);

                String[] firstStrArr = getRelayenableResponse.split(",");
                String[] secondStrArr = getChannelsResponse.split(",");
                if (firstStrArr.length != secondStrArr.length) {
                    Toast.makeText(getApplicationContext(), "개수 맞지않음", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "response가 비정상임 ******************************************************************");
                    return;
                }

                serverStatus = new ArrayList<>();
                for (int i = 0; i < firstStrArr.length; i++) {
                    int num = Integer.parseInt(firstStrArr[i].substring(0, firstStrArr[i].indexOf(":")));
                    boolean isActive = (firstStrArr[i].substring(firstStrArr[i].indexOf(":") + 1)).equals("0000") ? false : true;
                    serverStatus.add(new ServerChannel(num, isActive, secondStrArr[i]));
                }

                for (ServerChannel ch : serverStatus) {
                    if (ch.isActive()) {
                        String str = "채널번호: " + Integer.toString(ch.getNumber()) + " / 채널이름: " + ch.getName();
                        Log.d("active채널정리결과", str);
                    }
                }
            }
        });

    }

}
