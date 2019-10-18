/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.pubFaceTracker;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.android.MessageCallable;
import org.ros.android.MessagePub;
import org.ros.android.RosActivity;
import org.ros.android.MessageSub;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.Locale;
import static android.speech.tts.TextToSpeech.ERROR;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

  //private MessagePub<std_msgs.String> pubString;
  private MessagePub<std_msgs.String> cameraDatatString;
  /*private Button commandButton0;
  private Button commandButton1;
  private Button commandButton2;*/
  private Context mContext;
  private BroadcastReceiver mReceiver = null;
  private final String BROADCAST_MESSAGE = "org.ros.android.pubsubSample.facetracking";
  //private MessagePub<std_msgs.String> pubVoice;
  //private final int REQ_CODE_SPEECH_INPUT = 100;
  //private ImageButton btnSpeak;
  //private TextView txtSpeechInput;

  //boolean camerapulishapk = true;

  /*final String command0Array[] = {  "시작", "움직여","찍어", "스타트" ,"들어" } ;
  final String command1Array[] = { "정지" , "멈춤", "멈춰", "그만", "잠깐", "스탑" } ;
  final String command2Array[] = { "내려", "놓으세요", "놔둬"} ;*/

  //private TextToSpeech tts;              // TTS 변수 선언

  public MainActivity() {
    // The RosActivity constructor configures the notification title and ticker
    // messages.
    super("Pubsub Sample", "Pubsub Sample");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    mContext = getApplicationContext();

    registerReceiver();
  }


  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {

    NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
    nodeConfiguration.setMasterUri(getMasterUri());

    ////////////////publish cameraDatatString //
    cameraDatatString = new MessagePub<std_msgs.String>(mContext);
    cameraDatatString.setTopicName("facedetectdataset");
    cameraDatatString.setMessageType(std_msgs.String._TYPE);
    nodeMainExecutor.execute(cameraDatatString,
            nodeConfiguration.setNodeName("android/facedetectdataset"));

  }


  private void registerReceiver(){

    if(mReceiver != null) return;

    final IntentFilter theFilter = new IntentFilter();
    theFilter.addAction(BROADCAST_MESSAGE);

    this.mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id",0);
        int CenterX = intent.getIntExtra("CenterX",0);
        int CenterY = intent.getIntExtra("CenterY",0);
        int Width = intent.getIntExtra("Width",0);
        int Height = intent.getIntExtra("Height",0);
        int EulerY = intent.getIntExtra("EulerY",0);
        int EulerZ = intent.getIntExtra("EulerZ",0);
        int LeftEyeOpen = intent.getIntExtra("LeftEyeOpen",0);
        int RightEyeOpen = intent.getIntExtra("RightEyeOpen",0);
        int Smile = intent.getIntExtra("Smile",0);
        Log.d("test","Width : "+Width);
        Log.d("test","Height :"+Height);
        Log.d("test","EulerY : "+EulerY);
        Log.d("test","EulerZ : "+EulerZ);
        Log.d("test","LeftEyeOpen : "+LeftEyeOpen);
        Log.d("test","RightEyeOpen : "+RightEyeOpen);
        Log.d("test","Smile : "+Smile);
        if(intent.getAction().equals(BROADCAST_MESSAGE)){
          //Toast.makeText(context, "recevied Data : "+receviedData, Toast.LENGTH_SHORT).show();
          cameraDatatString.message.setData(""+id+","+CenterX+","+CenterY+","+Width+","+Height+","+EulerY+","+EulerZ+","
                  +LeftEyeOpen+","+RightEyeOpen+","+Smile);
          cameraDatatString.publish();
        }
      }
    };

    this.registerReceiver(this.mReceiver, theFilter);

  }

  private void unregisterReceiver() {
    if(mReceiver != null){
      this.unregisterReceiver(mReceiver);
      mReceiver = null;
    }

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

  }

}