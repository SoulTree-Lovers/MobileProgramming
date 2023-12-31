package com.example.jtetris;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private TetrisView myTetView, peTetView;
    private BlockView myBlkView, peBlkView;
    private Button upArrowBtn, leftArrowBtn, rightArrowBtn, downArrowBtn, dropBtn, topLeftBtn, topRightBtn;
    private Button startBtn, pauseBtn, settingBtn, modeBtn, reservedBtn;
    private AlertDialog quitBtn;
    private int dy = 25, dx = 15; // screen size

    private final int reqCode4SettingActivity = 0;
    private String servHostName = "10.27.6.191"; // 로컬 에코 서버 주소 (바뀔 수 있음)
    private int servPortNo = 9999;
    private boolean mirrorMode = false;

    private JTetrisModel myTetModel, peTetModel;
    private Random random;
    private char myCurrBlk, myNextBlk;
    private char peCurrBlk, peNextBlk;

    private GameState gameState = GameState.Initial;
    private GameState savedState;
    private enum GameState {
        Error(-1), Initial(0), Running(1), Paused(2);
        private final int value;
        private GameState(int value) { this.value = value; }
        public int value() { return value; }
        public static GameState stateFromInteger(int value) {
            switch (value) {
                case -1: return Error;
                case 0: return Initial;
                case 1: return Running;
                case 2: return Paused;
                default: return null;
            }
        }
    }
    private enum GameCommand {
        NOP(-1), Quit(0), Start(1), Pause(2), Resume(3), Update(4), Recover(5), Abort(6);
        private final int value;
        private GameCommand(int value) { this.value = value; }
        public int value() { return value; }
    }
    int stateTransMatrix[][] = { // stateTransMatrix[currGameState][GameCommand] --> nextGameState
            { -1, 1, -1, -1, -1, 2, 0 },
            // [Initial][Quit] --> Error
            // [Initial][Start] --> Running
            // [Initial][Pause] --> Error
            // [Initial][Resume] --> Error
            // [Initial][Update] --> Error
            // [Initial][Recover] --> Paused
            // [Initial][Abort] --> Initial
            { 0, -1, 2, -1, 1, -1, 0 },
            // [Running][Quit] --> Initial
            // [Running][Start] --> Error
            // [Running][Pause] --> Paused
            // [Running][Resume] --> Error
            // [Running][Update] --> Running
            // [Running][Recover] --> Error
            // [Running][Abort] --> Initial
            { 0, 1, -1, 1, 2, -1, -1 },
            // [Paused][Quit] --> Initial
            // [Paused][Start] --> Running
            // [Paused][Pause] --> Error
            // [Paused][Resume] --> Running
            // [Paused][Update] --> Paused
            // [Paused][Recover] --> Error
            // [Paused][Abort] --> Error
    };
    private void executeCommand(GameCommand cmd, char key) {
        GameState newState = GameState.stateFromInteger(stateTransMatrix[gameState.value()][cmd.value()]);
        //if (newState == GameState.Error)
        Log.d("MainActivity", "newState="+newState+" <- (gameState="+gameState+",cmd="+cmd+")");
        gameState = newState;
        switch (cmd.value()) {
            case 0: quitGame(); break;
            case 1: startGame(); launchTimer(); break;
            case 2: pauseGame(); break;
            case 3: resumeGame(); launchTimer(); break;
            case 4: updateModel(key); break;
            case 5: recoverGame(); break;
            case 6: abortGame(); break;
            default: Log.d("MainActivity", "unknown command!!!"); break;
        }
    }
    private View.OnClickListener OnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            char key;
            int id = v.getId();
            GameCommand cmd = GameCommand.NOP;
            switch (id) {
                case R.id.startBtn: key = 'N';
                    if (gameState == GameState.Initial) cmd = GameCommand.Start;
//                    else if (gameState == GameState.Running) cmd = GameCommand.Quit; 디버깅 용도
//                    else if (gameState == GameState.Paused) cmd = GameCommand.Quit;
                    else if (gameState == GameState.Running) {
                        // cmd = GameCommand.Quit;
                        savedState = gameState;
                        executeCommand(GameCommand.Pause, 'P');
                        quitBtn.show();
                        return;
                    }
                    else if (gameState == GameState.Paused) {
                        // cmd = GameCommand.Quit;
                        savedState = gameState;
                        quitBtn.show();
                        return;
                    }
                    break;
                case R.id.pauseBtn: key = 'P';
                    if (gameState == GameState.Running) cmd = GameCommand.Pause;
                    else if (gameState == GameState.Paused) cmd = GameCommand.Resume;
                    break;
                case R.id.settingBtn: key = 'S';
                    if (gameState == GameState.Initial) startSettingActivity(reqCode4SettingActivity);
                    return;
                case R.id.modeBtn:
                    mirrorMode = !mirrorMode;
                    if (mirrorMode) modeBtn.setText("2");
                    else modeBtn.setText("1");
                    return;
                case R.id.upArrowBtn: key = 'w'; cmd = GameCommand.Update; break;
                case R.id.leftArrowBtn: key = 'a'; cmd = GameCommand.Update; break;
                case R.id.rightArrowBtn: key = 'd'; cmd = GameCommand.Update; break;
                case R.id.downArrowBtn: key = 's'; cmd = GameCommand.Update; break;
                case R.id.dropBtn: key = ' '; cmd = GameCommand.Update; break;
                default: return;
            }
            executeCommand(cmd, key);
        }
    };
    private  void logMatrix(Matrix m) {
        int dy = m.get_dy();
        int dx = m.get_dx();
        int array[][] = m.get_array();
        for (int y=0; y < dy; y++) {
            for (int x=0; x < dx; x++) {
                if (array[y][x] == 0) System.out.print("□ ");
                else if (array[y][x] == 1) System.out.print("■ ");
                else System.out.print("X ");
            }
            System.out.println();
        }
    }
    private void recoverGame() {
        setButtonsState(); // no argument of flag
        startBtn.setText("Q"); // 'Q' means Quit.
        if (savedState == GameState.Running)
            pauseBtn.setText("P"); // 'P' means Pause.
        else if (savedState == GameState.Paused)
            pauseBtn.setText("R"); // 'R' means Resume.
        //[BEGIN] recover random, TetrisView, and BlockView.
        random = new Random();
        myTetView.init(dy, dx, JTetris.iScreenDw);
        myBlkView.init(JTetris.iScreenDw);
        //logMatrix(myTetModel.getScreen());
        myTetView.accept(myTetModel.getScreen());
        myBlkView.accept(myTetModel.getBlock(myNextBlk));
        myTetView.invalidate();
        myBlkView.invalidate();
        //[END] recover random, TetrisView, and BlockView.
        Toast.makeText(MainActivity.this, "Game Recovered!", Toast.LENGTH_SHORT).show();
    }
    private void updateModel(char key) {
        try {
            JTetris.TetrisState state; // model state
            state = myTetModel.accept(key);
            if (mirrorMode) sendToPeer(key);
            myTetView.accept(myTetModel.getScreen());
            if (state == JTetris.TetrisState.NewBlock){
                myCurrBlk = myNextBlk;
                myNextBlk = (char) ('0' + random.nextInt(JTetris.nBlockTypes));
                state = myTetModel.accept(myCurrBlk);
                if (mirrorMode) sendToPeer(myNextBlk); // send myNextBlk, not myCurrBlk!!
                myTetView.accept(myTetModel.getScreen());
                myBlkView.accept(myTetModel.getBlock(myNextBlk));
                myBlkView.invalidate();
                if (state == JTetris.TetrisState.Finished)
                    executeCommand(GameCommand.Quit, 'Q');
            }
            myTetView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void startGame() {
        setButtonsState(); // no argument of flag
        startBtn.setText("Q"); // 'Q' means Quit.
        pauseBtn.setText("P"); // 'P' means Pause.
        Toast.makeText(MainActivity.this, "Game Started!", Toast.LENGTH_SHORT).show();
        try {
            JTetris.TetrisState state; // model state
            random = new Random();
            myTetModel = new JTetrisModel(dy, dx);
            myTetView.init(dy, dx, JTetris.iScreenDw);
            myBlkView.init(JTetris.iScreenDw);
            myCurrBlk = (char) ('0' + random.nextInt(JTetris.nBlockTypes));
            myNextBlk = (char) ('0' + random.nextInt(JTetris.nBlockTypes));
            state = myTetModel.accept(myCurrBlk);
            myTetView.accept(myTetModel.getScreen());
            myBlkView.accept(myTetModel.getBlock(myNextBlk));
            myTetView.invalidate();
            myBlkView.invalidate();
            if (mirrorMode) {
                sendToPeer('N'); sendToPeer(myCurrBlk); sendToPeer(myNextBlk);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void quitGame() {
        setButtonsState(); // no argument of flag
        startBtn.setText("N"); // 'N' means New Game.
        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
        if (mirrorMode) sendToPeer('Q');
    }
    private void abortGame() {
        setButtonsState(); // no argument of flag
        startBtn.setText("N"); // 'N' means New Game.
        Toast.makeText(MainActivity.this, "Game Aborted!", Toast.LENGTH_SHORT).show();
    }
    private void pauseGame() {
        setButtonsState();
        pauseBtn.setText("R");
        Toast.makeText(MainActivity.this, "Game Paused!", Toast.LENGTH_SHORT).show();
    }
    private void resumeGame() {
        setButtonsState();
        pauseBtn.setText("P");
        Toast.makeText(MainActivity.this, "Game Resumed!", Toast.LENGTH_SHORT).show();
    }
    private final Handler handler4Timer = new Handler(Looper.getMainLooper());
    private Runnable runnableTimer = new Runnable() {
        @Override
        public void run() {
            if (gameState == GameState.Running) {
                updateModel('s');
                Log.d("MainActivity", "ondraws="+myTetView.ondraw_calls);
                handler4Timer.postDelayed(this, 1000); // runnableTimer 객체를 재귀적으로 handler4Timer에게 전송
            }
        }
    };
    private void launchTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) // P means 'Android Pie', aka Android 9.0
            handler4Timer.postDelayed(runnableTimer, 0, 1000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        savedState = gameState;
        Log.d("MainActivity", "onPause(gameState="+gameState+",savedState="+savedState+")");
        if (gameState == GameState.Running)
            executeCommand(GameCommand.Pause, 'P');
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume(gameState="+gameState+",savedState="+savedState+")");
        if (savedState == GameState.Running) {
            savedState = GameState.Paused;
            executeCommand(GameCommand.Resume, 'R');
        }
    }
    boolean buttonState[][] = { // buttonState[currGameState][btnID] --> enable/disable
            { true, false, false }, // [Initial][startBtn] --> enabled
            { true, true, true },   // [Running][anyBtn] --> enabled
            { true, true, false },  // [Paused][startBtn,resumeBtn] --> enabled
    };
    private void setButtonsState() {
        boolean startFlag = buttonState[gameState.value()][0];
        boolean pauseFlag = buttonState[gameState.value()][1];
        boolean defaultFlag = buttonState[gameState.value()][2];
        startBtn.setEnabled(startFlag);
        pauseBtn.setEnabled(pauseFlag);
        if (gameState == GameState.Initial) {
            settingBtn.setEnabled(true);
            modeBtn.setEnabled(true);
        }
        else {
            settingBtn.setEnabled(false);
            modeBtn.setEnabled(false);
        }
        reservedBtn.setEnabled(false);

        upArrowBtn.setEnabled(defaultFlag);
        leftArrowBtn.setEnabled(defaultFlag);
        rightArrowBtn.setEnabled(defaultFlag);
        downArrowBtn.setEnabled(defaultFlag);
        dropBtn.setEnabled(defaultFlag);
        topLeftBtn.setEnabled(false); // always disabled
        topRightBtn.setEnabled(false); // always disabled
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) { // 화면 상태가 변할 때 일시적으로 상태를 저장하는 메소드
        super.onSaveInstanceState(outState);
        Log.d("MainActivity", "onSave(gameState="+gameState+",savedState="+savedState+")");
        outState.putSerializable("myTetModel", myTetModel);
        outState.putInt("savedState", savedState.value());
        outState.putChar("myCurrBlk", myCurrBlk);
        outState.putChar("myNextBlk", myNextBlk);
    }
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        savedState = GameState.stateFromInteger(inState.getInt("savedState")); // 저장된 상태를 가져옴.
        Log.d("MainActivity", "onRestore(gameState="+gameState+",savedState="+savedState+")");
        if (savedState != GameState.Initial) { // 저장된 게임 상태가 Initial이 아니라면, 저장된 모델 복구
            myTetModel = (JTetrisModel) inState.getSerializable("myTetModel"); // 테트리스 모델 복구
            myCurrBlk = inState.getChar("myCurrBlk"); // 현재 블록 복구
            myNextBlk = inState.getChar("myNextBlk"); // 다음 블록 복구
            executeCommand(GameCommand.Recover, 'C'); // 게임 Recover
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = getWindowManager();
        if (wm == null) return;
        int rotation = wm.getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            Log.d("MainActivity", "onCreate(portrait mode)");
            setContentView(R.layout.activity_main);
        }
        else { // rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
            Log.d("MainActivity", "onCreate(landscape mode)");
//            setContentView(R.layout.activity_main_landscape);
        }
        myTetView = (TetrisView) findViewById(R.id.myTetrisView);
        peTetView = (TetrisView) findViewById(R.id.peerTetrisView);
        myBlkView = (BlockView) findViewById(R.id.myBlockView);
        peBlkView = (BlockView) findViewById(R.id.peerBlockView);
        startBtn = (Button) findViewById(R.id.startBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        settingBtn = (Button) findViewById(R.id.settingBtn);
        modeBtn = (Button) findViewById(R.id.modeBtn);
        reservedBtn = (Button) findViewById(R.id.reservedBtn);
        upArrowBtn = (Button) findViewById(R.id.upArrowBtn);
        leftArrowBtn = (Button) findViewById(R.id.leftArrowBtn);
        rightArrowBtn = (Button) findViewById(R.id.rightArrowBtn);
        downArrowBtn = (Button) findViewById(R.id.downArrowBtn);
        dropBtn = (Button) findViewById(R.id.dropBtn);
        topLeftBtn = (Button) findViewById(R.id.topLeftBtn);
        topRightBtn = (Button) findViewById(R.id.topRightBtn);
        quitBtn = (AlertDialog) AlertDialogBtnCreate();

        startBtn.setOnClickListener(OnClickListener);
        pauseBtn.setOnClickListener(OnClickListener);
        settingBtn.setOnClickListener(OnClickListener);
        modeBtn.setOnClickListener(OnClickListener);
        reservedBtn.setOnClickListener(OnClickListener);

        upArrowBtn.setOnClickListener(OnClickListener);
        leftArrowBtn.setOnClickListener(OnClickListener);
        rightArrowBtn.setOnClickListener(OnClickListener);
        downArrowBtn.setOnClickListener(OnClickListener);
        dropBtn.setOnClickListener(OnClickListener);

        setButtonsState(); // no argument of flag
//        startThread(runnable4LBThread); // launch Loopback Thread
        startThread(runnable4RecvThread); // launch RecvThread before SendThread
        startThread(runnable4SendThread); // launch SendThread
    }
    private AlertDialog AlertDialogBtnCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Tetris: ")
                .setMessage("Do you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        executeCommand(GameCommand.Quit, 'Q');
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (savedState == GameState.Running)
                            executeCommand(GameCommand.Resume, 'R');
                    }
                });
        return builder.create();
    }
    private void startSettingActivity(int reqCode) {
        Intent reqmsg = new Intent(MainActivity.this, SettingActivity.class);
        reqmsg.putExtra("servHostName", servHostName);
        reqmsg.putExtra("servPortNo", String.valueOf(servPortNo));
        startActivityResult.launch(reqmsg);
    }
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int rescode = result.getResultCode();
                    if (rescode == Activity.RESULT_OK) {
                        Intent res = result.getData();
                        servHostName = res.getStringExtra("servHostName");
                        String s = res.getStringExtra("servPortNo");
                        servPortNo = Integer.parseInt(s);
                        Log.d("MainActivity", "response=("+servHostName+","+servPortNo+")");
                    }
                    else if (rescode == Activity.RESULT_CANCELED)
                        Log.d("MainActivity", "response=(null)");
                }
            });
    private void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }
    private void sendMessage(Handler h, int type, char key) {
        Message msg = Message.obtain(h, type);
        msg.arg1 = key;
        h.sendMessage(msg);
    }
    private Socket socket = null;
    private final int maxWaitingTime = 3000; // 3 seconds
    private DataOutputStream outStream = null;
    private DataInputStream inStream = null;
    private Object socketReady = new Object();
    private Object inStreamReady = new Object();
    private void _disconnectServer() {
        try {
            if (outStream != null) outStream.close();
            if (inStream != null) inStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        outStream = null;
        inStream = null;
        socket = null;
    }
    private void disconnectServer() {
        synchronized(socketReady) {
            _disconnectServer();
        }
    }
    private boolean connectServer() {
        synchronized(socketReady) {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(servHostName, servPortNo), maxWaitingTime);
                outStream = new DataOutputStream(socket.getOutputStream());
                inStream = new DataInputStream(socket.getInputStream());
                synchronized (inStreamReady) {
                    inStreamReady.notify();
                }
                return true;
            } catch (Exception e) {
                _disconnectServer();
                e.printStackTrace();
                return false;
            }
        }
    }
    public void sendToPeer(char key) {
        sendMessage(handler4SendThread, 0, key);
    }
    private Handler handler4LBThread; // handler for Loopback Thread
    private Handler handler4SendThread = null; // handler for Loopback Thread

    private Runnable runnable4SendThread = new Runnable() { // runnable for SendThread
        @Override
        public void run() {
            Looper.prepare(); // The message loop is ready.
            handler4SendThread = new Handler(Looper.myLooper()) {
                public void handleMessage(Message msg) {
                    try {
                        char key = (char) msg.arg1;
                        Log.d("SendThread", "key='"+key+"'["+(int) key+"]");
                        if (key == 'N') {
                            if (connectServer() == false) {
                                sendToMain('A'); // issue 'Abort' command
                                System.out.println("서버 연결 실패");
                                return;
                            }
                        }

                        synchronized(socketReady) {
                            if (outStream != null) {
                                Log.d("SendThread", "wrote key='" + key + "'[" + (int) key + "]");
                                outStream.writeByte(key);
                                outStream.writeByte('\n');
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Looper.loop(); // The message loop runs.
        }
    };
    private Runnable runnable4RecvThread = new Runnable() { // runnable for RecvThread
        @Override
        public void run() {
            while (true) {
                try {
                    char key;
                    synchronized (inStreamReady) {
                        while (inStream == null) inStreamReady.wait();
                    }
                    while ((key = (char) inStream.readByte()) != 'Q') {
                        if (key != '\n') // skip the newline character
                            sendToMain(key);
                    }
                    sendToMain(key); // key == 'Q'
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                disconnectServer();
            }
        }
    };
//    private Runnable runnable4LBThread = new Runnable() { // runnable for Loopback Thread
//        @Override
//        public void run() {
//            Looper.prepare(); // The message loop is ready.
//            handler4LBThread = new Handler(Looper.myLooper()) {
//                public void handleMessage(Message msg) {
//                    try {
//                        char key = (char) msg.arg1;
//                        Log.d("LBThread", "key='"+key+"'["+(int) key+"]");
//                        sendToMain(key);
//                    } catch (Exception e) { e.printStackTrace(); }
//                }
//            };
//            Looper.loop(); // The message loop runs.
//        }
//    };
    public void sendToMain(char key) {
        sendMessage(handler4MainThread, 0, key);
    }
    private Handler handler4MainThread = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            try {
                char key = (char) msg.arg1;
                Log.d("MainThread", "key='"+key+"'["+(int) key+"]");
                if (key == 'A') { // received 'Abort' command
                    executeCommand(GameCommand.Abort, 'A');
                    return;
                }
                mirrorPeer(key);
            } catch (Exception e) { e.printStackTrace(); }
        }
    };
    private boolean waitingForTwoBlks = false;
    int numOfBlks = -1;
    private void mirrorPeer(char key) { // similar to the logic of onClick()
        if (key == 'Q') {
            Log.d("mirrorPeer", "Quit Game");
            Toast.makeText(MainActivity.this, "Peer Won!", Toast.LENGTH_SHORT).show();
        }
        else if (key == 'N') {
            waitingForTwoBlks = true;
            numOfBlks = 0;
            Log.d("mirrorPeer", "New Game");
        }
        else if (waitingForTwoBlks) {
            if (numOfBlks == 0) {
                peCurrBlk = key;
                numOfBlks++;
                Log.d("mirrorPeer", "1st Block");
            }
            else if (numOfBlks == 1) {
                peNextBlk = key;
                numOfBlks++;
                startPeer();
                waitingForTwoBlks = false;
                Log.d("mirrorPeer", "2nd Block");
            }
        }
        else {
            updatePeer(key);
            Log.d("mirrorPeer", "N-th Block");
        }
    }
    private void startPeer() {
        try {
            JTetris.TetrisState state; // model state
            peTetModel = new JTetrisModel(dy, dx);
            peTetView.init(dy, dx, JTetris.iScreenDw);
            peBlkView.init(JTetris.iScreenDw);
            // peCurrBlk is initialized in mirrorPeer().
            // peNextBlk is initialized in mirrorPeer().
            state = peTetModel.accept(peCurrBlk);
            peTetView.accept(peTetModel.getScreen());
            peBlkView.accept(peTetModel.getBlock(peNextBlk));
            peTetView.invalidate();
            peBlkView.invalidate();
            Log.d("MainThread", "startPeer() called!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updatePeer(char key) {
        try {
            JTetris.TetrisState state; // model state
            if (peTetModel.isBlockIndex(key)) { // key is a block index.
                peCurrBlk = peNextBlk;
                peNextBlk = key;
                state = peTetModel.accept(peCurrBlk);
                peBlkView.accept(peTetModel.getBlock(peNextBlk));
                peBlkView.invalidate();
            }
            else { // key is a movement key.
                state = peTetModel.accept(key);
                peTetView.accept(peTetModel.getScreen());
                peTetView.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}