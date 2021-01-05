package mobapplication.pt_game;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class MainActivity extends Activity {

    private GamePintuLayout mGamePintuLayout;
    private TextView mLevel,mTime;
    private Button btnlook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGamePintuLayout = (GamePintuLayout)findViewById(R.id.game_pintu);
        mTime = (TextView)findViewById(R.id.id_time);
        mLevel = (TextView)findViewById(R.id.id_level);
        btnlook = (Button)findViewById(R.id.btn_look);

        mGamePintuLayout.setTimeEnabled(true);

        mGamePintuLayout.setOnGamePintumLister(new GamePintuLayout.GamePintuListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏提示")
                        .setMessage("成功过关").setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGamePintuLayout.nextLevel();
                        mLevel.setText(""+nextLevel);
                    }
                }).show();
            }

            @Override
            public void timechanged(int currentTime) {
                mTime.setText(""+currentTime);
            }

            @Override
            public void gameover() {
                new AlertDialog.Builder(MainActivity.this).setTitle("游戏提示")
                        .setMessage("游戏失败！").setPositiveButton("继续挑战", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGamePintuLayout.restart();
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
            }
        });
        btnlook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = new Toast(getApplicationContext());
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view1 = inflater.inflate(R.layout.activity_toast_item , null);
                ImageView imageView = (ImageView) view1.findViewById(R.id.iv_toast);
                imageView.setImageResource(R.drawable.image01);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.setView(view1);
                toast.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGamePintuLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePintuLayout.resume();
    }
}
