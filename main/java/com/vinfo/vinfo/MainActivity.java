package com.vinfo.vinfo;

/*음성 인식*/
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


import static android.speech.tts.TextToSpeech.ERROR;

enum CASE_TPYE{TYPE_NEWS, TYPE_WIKI, TYPE_WEATHER};

public class MainActivity extends AppCompatActivity {
    //음성 인식 객체
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView listentext=null;
    double latitude=1;
    double longitude=1;

    //음성 합성 객체
    TextToSpeech tts;              // TTS 변수 선언
    TextView resulttext;

    //GPS 객체
    private LocationManager locationManager;
    private LocationListener locationListener;

    //접속할주소
    private final String urlPath = "https://dydrkfl12345.tk/voice";

    //GPS 객체 생성
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            // requestCode 를 임의로 100으로 설정
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        }
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
                Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
            }
        }
        return currentLocation;
    }

    public void speakAllResult()
    {

        resulttext = (TextView) findViewById(R.id.resultText);
        tts.setPitch(1.0f);         // 음성 톤을 2.0배 올려준다.
        tts.setSpeechRate(1.0f);    // 읽는 속도는 기본 설정

        String resultText = resulttext.getText().toString();//.replaceAll("MMM","");
        resultText.replaceAll("동녕상 뉴스", "");
        resultText.replaceAll("동녕상  뉴스", "");

        String[] splitshortspeech = resultText.split("MMM");
        for (int i = 0; i < splitshortspeech.length; i++) {

            if (i == 0)
            { // Use for the first splited text to flush on audio stream
                String[] splitlongspeech = splitshortspeech[i].toString().trim().split("LLL");
                for(int j=0; j<splitlongspeech.length;j++){


                    if(j == 0){

                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_FLUSH, null);
                        tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                    }
                    else if(j==splitlongspeech.length-1){
                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_ADD, null);
                    }
                    else{
                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_ADD, null);
                        tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
            else
            { // add the new test on previous then play the TTS

                String[] splitlongspeech = splitshortspeech[i].toString().trim().split("LLL");
                for(int j=0; j<splitlongspeech.length;j++){
                    if(j == 0){
                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_ADD, null);
                        tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                    }
                    else if(j==splitlongspeech.length-1){
                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_ADD, null);
                    }
                    else{

                        Log.i("TEST", "::::::: " + splitlongspeech[j]);
                        tts.speak(splitlongspeech[j].toString().trim(),TextToSpeech.QUEUE_ADD, null);
                        tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
            tts.playSilence(1, TextToSpeech.QUEUE_ADD, null);

        }
    }

    // GPS 를 받기 위한 매니저와 리스너 설정
    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // TODO 위도, 경도로 하고 싶은 것
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    //GPS 권한 응답에 따른 처리
    boolean canReadLocation = false;
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                Location userLocation = getMyLocation();
                if( userLocation != null ) {
                    // 다음 데이터 //
                    //String lang = mstring.getLangFromSystemLang(getApplicationContext());
                    // todo 사용자의 현재 위치 구하기
                    double latitude = userLocation.getLatitude();
                    double longitude = userLocation.getLongitude();

                }
                canReadLocation = true;
            } else {
                // Permission was denied or request was cancelled
                canReadLocation = false;
            }
        }
    }

    //php Post 클래스
    class HttpTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            // TODO Auto-generated method stub
            try{

                Log.i("TEST", "Sending is Start " );
                //--------------------------
                //   전송 모드 설정 - 기본적인 설정이다
                //--------------------------
                listentext = (TextView) findViewById(R.id.listenText);
                //전달할 인자들
                String sendText= listentext.getText().toString();
                String clearText = new String(sendText.getBytes("utf-8"), "utf-8");

                //Post 방식 - 전달할 인자.
                String data  = URLEncoder.encode("send_text", "UTF-8") + "=" + URLEncoder.encode(clearText, "UTF-8");
                data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(Double.toString(latitude), "UTF-8");
                data += "&" + URLEncoder.encode("lon", "UTF-8") + "=" + URLEncoder.encode(Double.toString(longitude), "UTF-8");


                Log.i("TEST", "Send data " );
                URL url = new URL(urlPath);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                String tmp= null;
                // Read Server Response
                while((line = reader.readLine()) != null) {

                    if(line != null) {
                        sb.append(line);
                        tmp += line;
                    }
                    //break;
                }
                reader.close();

                Log.i("TEST", "Here is OK: " );

                JSONArray jsonArr = new JSONArray("["+sb.toString().trim()+"]");
                JSONObject JS = new JSONObject();
                for(int i=0;i<jsonArr.length();i++){
                    JS = jsonArr.getJSONObject(i);
                }


                Log.i("TEST", "Tap");
                String phpSuccess = JS.getString("success");
                String phpType = JS.getString("type");
                String phpContent = JS.getString("content");
                String phpWeather = JS.getString("main_weather");
                String phpurl = JS.getString("url");

                String allContent = phpSuccess+"::"+ phpType+"::"+ phpContent+"::"+ phpWeather+"::"+phpurl;


                Log.i("TEST", "allContent: " + allContent );


                return allContent;
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();

            }catch(JSONException e){
                e.printStackTrace();
            }
            //오류시 null 반환
            return null;
        }
        //asyonTask 3번째 인자와 일치 매개변수값 -> doInBackground 리턴값이 전달됨
        //AsynoTask 는 preExcute - doInBackground - postExecute 순으로 자동으로 실행됩니다.
        //ui는 여기서 변경
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            //레이아웃 INVISIBLE
            LinearLayout text_Layout = (LinearLayout) findViewById(R.id.resultTextLayout);
            LinearLayout weather_Layout = (LinearLayout) findViewById(R.id.weatherLayout);
            LinearLayout imageButton_Layout = (LinearLayout) findViewById(R.id.ImageButtonLayout);

            //이미지버튼
            ImageButton newsURLImage = (ImageButton) findViewById(R.id.typeimagebutton);

            //결과 텍스트
            resulttext = (TextView) findViewById(R.id.resultText);

            //결과 화면 초기화
            text_Layout.setVisibility(View.INVISIBLE);
            weather_Layout.setVisibility(View.INVISIBLE);
            imageButton_Layout.setVisibility(View.INVISIBLE);


            //통신 실패
            if (value == null) {

                //이미지 버튼 노출
                imageButton_Layout.setVisibility(View.VISIBLE);

                //여기에 X표시 이미지 삽입
                newsURLImage.setImageResource(R.drawable.err); //-> X 이미지 삽입
                resulttext.setText("통신이 제대로 이루어지지 않았습니다.");
            }
            //통신 성공
            else {

                //받은 통신값 분할
                String[] allvalue = value.toString().split("::");

                //음성 입력 진행 이미지 INVISIBLE
                ImageView waitCircleImage = (ImageView) findViewById(R.id.waitCircle);
                waitCircleImage.setVisibility(View.INVISIBLE);

                //정보 저장
                String phpSuccess = allvalue[0];
                String phpType = allvalue[1];
                String phpContent = allvalue[2];
                String phpWeather = allvalue[3];
                final String phpurl = allvalue[4];


                Log.i("TEST", "phpSuccess: " + phpSuccess);
                Log.i("TEST", "phpType: " + phpType);
                Log.i("TEST", "phpContent: " + phpContent);
                Log.i("TEST", "phpWeather: " + phpWeather);
                Log.i("TEST", "phpurl: " + phpurl);

                //반환 Text
                String printNewText = phpContent;

                if (phpSuccess.equals("0")) {


                    //여기에 X표시 이미지 삽입
                    newsURLImage.setImageResource(R.drawable.err); //-> X 이미지 삽입


                    //이미지 버튼 VISIBLE
                    newsURLImage.setVisibility(View.VISIBLE);
                    imageButton_Layout.setVisibility(View.VISIBLE);


                    printNewText = "다시 입력해 주세요";

                } else {

                    phpType = phpType.trim();
                    //뉴스
                    if (phpType.equals("news")) {

                        //ImageUrlButton
                        newsURLImage.setImageResource(R.drawable.news);
                        newsURLImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phpurl));
                                startActivity(intent);
                            }
                        });

                        //뉴스 내용 출력
                        printNewText = phpContent;


                        //이미지 버튼 VISIBLE
                        newsURLImage.setVisibility(View.VISIBLE);
                        imageButton_Layout.setVisibility(View.VISIBLE);


                    }
                    //날씨
                    else if (phpType.equals("weather")) {

                        //레이아웃 INVISIBLE
                        text_Layout.setVisibility(View.INVISIBLE);

                        //음성 출력 내용 전달
                        printNewText = phpContent;

                        //phpContent 분리  ex)sunny:28'C:30%
                        String[] weatherData = phpWeather.split(":");

                        String weather_type = weatherData[0];
                        String temperature = weatherData[1];
                        String humidity = weatherData[2];


                        ImageView weatherPicture = (ImageView) findViewById(R.id.weather_picture);
                        TextView weatherTypeText = (TextView) findViewById(R.id.weather_type_text);
                        TextView weatherTemperatureText = (TextView) findViewById(R.id.weather_temperature_text);
                        TextView weatherHumidityText = (TextView) findViewById(R.id.weather_humidity_text);

                        switch (weather_type.trim()) {
                            case "Clear":
                                weatherPicture.setImageResource(R.drawable.twotone_wb_sunny_black_48dp);
                                weatherTypeText.setText("맑음");
                                weatherTemperatureText.setText(temperature);
                                weatherHumidityText.setText(humidity);

                                weather_Layout.setVisibility(View.VISIBLE);
                                break;

                            case "Clouds":

                                weatherPicture.setImageResource(R.drawable.baseline_wb_cloudy_black_48dp);
                                weatherTypeText.setText("흐림");
                                weatherTemperatureText.setText(temperature);
                                weatherHumidityText.setText(humidity);

                                weather_Layout.setVisibility(View.VISIBLE);

                                break;

                            case "Rain":
                                weatherPicture.setImageResource(R.drawable.baseline_opacity_black_48dp);
                                weatherTypeText.setText("비");
                                weatherTemperatureText.setText(temperature);
                                weatherHumidityText.setText(humidity);

                                weather_Layout.setVisibility(View.VISIBLE);
                                break;

                            case "Snow":
                                weatherPicture.setImageResource(R.drawable.baseline_ac_unit_black_48dp);
                                weatherTypeText.setText("눈");
                                weatherTemperatureText.setText(temperature);
                                weatherHumidityText.setText(humidity);

                                weather_Layout.setVisibility(View.VISIBLE);
                                break;

                            default:

                                Log.i("TEST", "No weatherValue");
                                break;
                        }

                    }
                    //사전 서비스
                    else if (phpType.equals("dictionary")) {

                        //사전 내용 출력
                        printNewText = phpContent;

                        //ImageUrlButton
                        newsURLImage.setImageResource(R.drawable.dictionary);
                        newsURLImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phpurl));
                                startActivity(intent);
                            }
                        });

                        //이미지 버튼 VISIBLE
                        newsURLImage.setVisibility(View.VISIBLE);
                        imageButton_Layout.setVisibility(View.VISIBLE);

                    }
                    //오류
                    else {


                        //X 이미지 삽입
                        newsURLImage.setImageResource(R.drawable.err);

                        //이미지 버튼 VISIBLE
                        newsURLImage.setVisibility(View.VISIBLE);
                        imageButton_Layout.setVisibility(View.VISIBLE);

                        printNewText = "다시 입력해 주세요";

                    }
                }

                //내용 INVISIBLE
                resulttext.setVisibility(View.INVISIBLE);
                resulttext.setText(Html.fromHtml(printNewText));
            }

            //음성출력
            speakAllResult();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //NoTitle Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //음성 인식 모듈 체크
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO
                );
            }
        }

        /*
        //GIF
        ImageView waitCircleImage= (ImageView) findViewById(R.id.waitCircle);
        waitCircleImage.setVisibility(View.INVISIBLE);
        GlideDrawableImageViewTarget gifCircle = new GlideDrawableImageViewTarget(waitCircleImage);
        Glide.with(this).load(R.raw.loadingcircle).into(gifCircle);
        */

        //음성 인식 인텐트
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "utf-8");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(recognitionListener);
        listentext = (TextView) findViewById(R.id.listenText);

        //음성 인식 버튼 이벤트
        ImageButton listenbutton = (ImageButton) findViewById(R.id.listenButton);
        listenbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    //GIF
                    ImageView waitCircleImage= (ImageView) findViewById(R.id.waitCircle);
                    waitCircleImage.setVisibility(View.VISIBLE);
                    GlideDrawableImageViewTarget gifCircle = new GlideDrawableImageViewTarget(waitCircleImage);
                    Glide.with(getApplicationContext()).load(R.raw.loadingheartbeat).into(gifCircle);

                    mRecognizer.startListening(intent);

            }
        });


        //음성 합성 버튼 이벤트
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        Button speakbutton = (Button) findViewById(R.id.speakButton);
        resulttext = (TextView) findViewById(R.id.resultText);

        speakbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //resulttext = (TextView) findViewById(R.id.resultText);
                tts.setPitch(0.6f);         // 음성 톤을 2.0배 올려준다.
                tts.setSpeechRate(1.0f);    // 읽는 속도는 기본 설정

                String resultText = resulttext.getText().toString();//.replaceAll("MMM","");

                String[] splitshortspeech = resultText.split("MMM");
                for (int i = 0; i < splitshortspeech.length; i++) {

                    if (i == 0)
                    { // Use for the first splited text to flush on audio stream
                        String[] splitlongspeech = splitshortspeech[i].toString().trim().split("LLL");
                        for(int j=0; j<splitlongspeech.length;j++){


                            if(j == 0){

                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_FLUSH, null);
                                tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                            }
                            else if(j==splitlongspeech.length-1){
                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_ADD, null);
                            }
                            else{
                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_ADD, null);
                                tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }
                    else
                    { // add the new test on previous then play the TTS

                        String[] splitlongspeech = splitshortspeech[i].toString().trim().split("LLL");
                        for(int j=0; j<splitlongspeech.length;j++){
                            if(j == 0){
                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_ADD, null);
                                tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                            }
                            else if(j==splitlongspeech.length-1){
                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_ADD, null);
                            }
                            else{

                                Log.i("TEST", "::::::: " + splitlongspeech[j]);
                                tts.speak(splitlongspeech[j].toString(),TextToSpeech.QUEUE_ADD, null);
                                tts.playSilence(10, TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                    }
                    tts.playSilence(1, TextToSpeech.QUEUE_ADD, null);

                }

            }
        });


        //TTS Stop post
        Button ttsStopbutton = (Button) findViewById(R.id.ttsStop);
        ttsStopbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
                if(tts != null){
                    tts.stop();
                }
            }
        });

        //GPS Button
        Button gpsbutton = (Button) findViewById(R.id.gpsButton);
        gpsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 사용자의 위치 수신을 위한 세팅 //
                settingGPS();

                // 사용자의 현재 위치 //
                Location userLocation = getMyLocation();

                if (userLocation != null) {
                    // TODO 위치를 처음 얻어왔을 때 하고 싶은 것
                    latitude = userLocation.getLatitude();
                    longitude = userLocation.getLongitude();

                    Toast.makeText(getApplicationContext(), "longitude:"+ longitude +"\n latitude:"+latitude, Toast.LENGTH_LONG).show();
                    TextView lattext = (TextView) findViewById(R.id.lattext);
                    TextView logtext = (TextView) findViewById(R.id.logtext);
                    lattext.setText(String.valueOf(longitude));
                    logtext.setText(String.valueOf(latitude));

                }
                else{
                    userLocation.setLatitude(35.231002);
                    userLocation.setLongitude(129.083508);
                }
            }
        });

        //PHP post
        Button sendPHPButton = (Button) findViewById(R.id.sendButton);
        sendPHPButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                new HttpTask().execute();
            }
        });


    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int i) {
            listentext.setText("너무 늦게 말하면 오류뜹니다");
            //GIF
            ImageView waitCircleImage= (ImageView) findViewById(R.id.waitCircle);
            waitCircleImage.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onResults(Bundle bundle) {


            //GIF
            ImageView waitCircleImage= (ImageView) findViewById(R.id.waitCircle);
            waitCircleImage.setVisibility(View.INVISIBLE);

            //음성 인식 결과
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = bundle.getStringArrayList(key);

            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            listentext.setText(rs[0]);
            //음성 인식 종료

            //완료 문장 처리
            new HttpTask().execute();
        }

        @Override
        public void onPartialResults(Bundle bundle) {


            ArrayList data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            listentext.setText(word);

            Log.i("TEST", "partial_results: " + word);
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}