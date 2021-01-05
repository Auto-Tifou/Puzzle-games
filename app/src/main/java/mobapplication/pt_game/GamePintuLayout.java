package mobapplication.pt_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import mobapplication.pt_game.Utils.ImagePiece;
import mobapplication.pt_game.Utils.ImageSqlitterUtil;

/**
 * Created by Administrator on 2019/9/20 0020.
 */

public class GamePintuLayout extends RelativeLayout implements View.OnClickListener{
    private int mColumn = 3;
    //容器的内边距
    private int mPadding;
    //每张小图之间的距离 dp (横向，纵向)
    private int mMagin = 2;
    private ImageView[] mGamePintuItems;
    private int mItemWidth;
    //游戏的图片
    private Bitmap mBitmap;
    private List<ImagePiece>mItemBitmaps;
    private boolean once;
    //游戏面板宽度
    private int mWidth;
    private ImageView mFirst;
    private ImageView mSecond;
    private RelativeLayout mAnimLayout;
    private boolean isAnimin;
    private boolean isGameSuccess;
    private boolean isGameOver;

    public interface GamePintuListener{
        void nextLevel(int nextLevel);
        void timechanged(int currentTime);
        void gameover();
    }

    public GamePintuListener mLister;

    /**
     * 设置接口回调
     * @param mLister
     */
    public void setOnGamePintumLister(GamePintuListener mLister) {
        this.mLister = mLister;
    }
    private int level =1;
    private static final int TIME_CHANGED =0x110;
    private static final int NEXT_LEVERL =0x111;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
          switch (msg.what){
              case TIME_CHANGED:
                  if (isGameSuccess || isGameOver || isPause)
                      return;
                  if (mLister!= null){
                      mLister.timechanged(mTime);
                      }
                  if (mTime == 0){
                  isGameOver = true;
                  mLister.gameover();
                  return;
                  }
                  mTime--;
                  mHandler.sendEmptyMessageDelayed(TIME_CHANGED,1000);
                  break;
              case NEXT_LEVERL:
                  level = level+1;
                  if (mLister!= null){
                      mLister.nextLevel(level);
                  }else {
                      nextLevel();
                  }
                  break;
              default:
          }
        }
    };
    private boolean isTimeEnabled = false;
    private int mTime;

    /**
     * 设置是否开启时间
     * @param timeEnabled
     */
    public void setTimeEnabled(boolean timeEnabled) {
        this.isTimeEnabled = timeEnabled;
    }


    public GamePintuLayout(Context context) {
        this(context,null);
    }

    public GamePintuLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GamePintuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMagin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,
                getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取宽和高中的小值
        mWidth = Math.min(getMeasuredHeight(),getMeasuredWidth());

        if (!once){
            //进行切图，以及排序
            initBitmap();
            //设置ImageView(Item)的宽高等属性
            initItem();

            //判断是否开启时间
            checkTimeEnable();

            once = true;
        }
        setMeasuredDimension(mWidth,mWidth);
    }

    private void checkTimeEnable() {
        if (isTimeEnabled){
            //根据当前等级设置时间
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2,level)*15;
    }

    private void initBitmap() {
    if (mBitmap == null){
        mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image01);

    }
        mItemBitmaps = ImageSqlitterUtil.sqlitImage(mBitmap,mColumn);
        //乱序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random()>0.5 ? 1:-1;
            }
        });
    }

    private void initItem() {
        mItemWidth = (mWidth - mPadding *2 - mMagin*(mColumn - 1))/mColumn;
        mGamePintuItems = new ImageView[mColumn * mColumn];
        //生成item,设置rule
        for (int i = 0;i<mGamePintuItems.length;i++){
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);

            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            mGamePintuItems[i] = item;
            item.setId(i+1);
            //在item的Tag中存储index
            item.setTag(i+"_"+mItemBitmaps.get(i).getIndex());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
            //设置item间横向间隙,通过rightMargin
            // 不是最后一列
            if (i+1 % mColumn != 0 ){
                lp.rightMargin = mMagin;
            }
            //不是第一列
            if (i % mColumn != 0 ){
                lp.addRule(RelativeLayout.RIGHT_OF,mGamePintuItems[i-1].getId());
            }
            //如果不是第一行,设置topMargin和rule
            if ((i+1)>mColumn){
                lp.topMargin = mMagin;
                lp.addRule(RelativeLayout.BELOW,mGamePintuItems[i-mColumn].getId());
            }
            addView(item,lp);
        }
    }
     public void restart(){
         isGameOver = false;
         mColumn--;
         nextLevel();
    }
    private boolean isPause;

    public void pause(){
       isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }
    public void resume(){
        if (isPause){
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    public void nextLevel(){
        this.removeAllViews();
        mAnimLayout = null;
        mColumn++;
        isGameSuccess =false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    /**
     * 获取多个参数的最小值
     */
    private int min(int...params) {
    int min = params[0];
        for (int param:params){
            if (param<min)
                min = param;
        }
        return  min;
    }



    @Override
    public void onClick(View view) {
        //一直点
        if (isAnimin){
            return;
        }
        //两次点击同一个item
        if (mFirst == view){
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if (mFirst == null){
            mFirst = (ImageView)view;
            //透明度 颜色
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        }else {
            mSecond = (ImageView) view;
            //交换我们的item
            exchangeView();
        }
    }


    /**
     * 交换我们的item
     */
    private void exchangeView() {
       mFirst.setColorFilter(null);

        //构造动画层
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);
        lp.leftMargin = mFirst.getLeft()-mPadding;
        lp.topMargin = mFirst.getTop()-mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        first.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth,mItemWidth);
        lp2.leftMargin = mSecond.getLeft()-mPadding;
        lp2.topMargin = mSecond.getTop()-mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画
        TranslateAnimation anim = new TranslateAnimation(0,mSecond.getLeft()-mFirst.getLeft(),
                0,mSecond.getTop()-mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,-mSecond.getLeft()+mFirst.getLeft(),
                0,-mSecond.getTop()+mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        //监听动画
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
                isAnimin = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String)mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                isAnimin = false;
                //判断用户游戏是否成功
                checkSuccess();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 判断用户是否游戏成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;

        for (int i =0;i<mGamePintuItems.length;i++) {
            ImageView imageView = mGamePintuItems[i];
            if (getImageIndexByTag((String)imageView.getTag()) != i) {
                isSuccess = false;
            }
        }if (isSuccess){
            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);
                Log.e("TAG","SUCCESS");
                Toast.makeText(getContext(),"恭喜你，成功过关",Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessage(NEXT_LEVERL);
            }
        }


    /**
     * 根据tag获取id
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag){
        String[]split = tag.split("_");
        return Integer.parseInt(split[0]);
    }
    public int getImageIndexByTag(String tag){
        String[]split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构造动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null){
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}
