package com.zeller.fastlibrary.huangchuang.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ucloud.ucommon.Utils;
import com.zeller.fastlibrary.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UBottomView extends RelativeLayout {
    private static final int SEEKBAR_MAX = 1000;
    private static int DEFAULT_SEEK_PROGRESS = 40 * 1000;

    public static final int MSG_INIT_SEEK_BAR = 1;
    public static final int MSG_UPDATE_SEEK_BAR = 2;
    public static final int MSG_SHOW_FAST_SEEK_BAR_VIEW = 3;
    public static final int MSG_HIDE_FAST_SEEK_BAR_VIEW = 4;

    private Callback callabck;

    @Bind(R.id.img_bt_pause_play)
    ImageButton playPauseButton;

    @Bind(R.id.seekbar)
    SeekBar seekBar;

    @Bind(R.id.fast_seekbar)
    SeekBar fastSeekBar;

    @Bind(R.id.txtv_current_position)
    TextView currentPositionTxtv;

    @Bind(R.id.txtv_duration)
    TextView durationTxtv;

    @Bind(R.id.fast_seek_index_txtv)
    TextView seekingIndexTxtv;

    @Bind(R.id.fast_seek_index_rl)
    ViewGroup seekIndexView;

    @Bind(R.id.img_btn_brightness)
    ImageButton brightnessImgBtn;

    @Bind(R.id.img_btn_volume)
    ImageButton volumeImgBtn;

    private long duration;

    private boolean isInitSeekBar;

    private UEasyPlayer easyPlayer;

    private int lastSeekPosition = -1;

    private int fastSeekToTemp = -1;

    public interface Callback {
        boolean onPlayButtonClick(View view);

        boolean onBrightnessButtonClick(View view);

        boolean onVolumeButtonClick(View view);
    }

    private class UiHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_SEEK_BAR:
                    initVideoProgressSeekBar(msg.arg1, msg.arg2);
                    break;
                case MSG_UPDATE_SEEK_BAR:
                    setVideoSeekbarCurrent(msg.arg1);
                    break;
                case MSG_SHOW_FAST_SEEK_BAR_VIEW:
                    doShowFastSeekIndexBar();
                    break;
                case MSG_HIDE_FAST_SEEK_BAR_VIEW:
                    doHideFastSeekIndexBar();
                    break;
                default:
                    break;
            }
        }
    }

    private Handler uiHandler = new UiHandler();

    public UBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UBottomView(Context context) {
        this(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        playPauseButton.setOnClickListener(playPauseButtonClickListener);
        seekBar.setOnSeekBarChangeListener(seekBarChanageListener);
        brightnessImgBtn.setOnClickListener(brightnessButtonClickListener);
        volumeImgBtn.setOnClickListener(volumeButtonClickListener);
    }

    OnClickListener playPauseButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (callabck != null) {
                callabck.onPlayButtonClick(v);
            }
            easyPlayer.showNavigationBar(0);
        }
    };

    OnClickListener brightnessButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (callabck != null) {
                callabck.onBrightnessButtonClick(v);
            }
            easyPlayer.showNavigationBar(0);
        }
    };

    OnClickListener volumeButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (callabck != null) {
                callabck.onVolumeButtonClick(v);
            }
            easyPlayer.showNavigationBar(0);
        }
    };

    public void togglePlayButtonIcon(int resid) {
        if (playPauseButton != null) {
            playPauseButton.setBackgroundResource(resid);
        }
    }

    public void setCallback(Callback callback) {
        callabck = callback;
    }

    private int getProgressByPosition(int position) {
        if (duration == 0) {
            return 0;
        }
        int pos = 0;
        int max = seekBar.getMax();
        position = (int) (position * 1000L / duration);
        if (max > 0 && max >= position && pos <= duration) {
            pos = position;
        }
        else {
            pos = max;
        }
        return pos;
    }

    private void setVideoSeekbarCurrent(int currposition) {
        int pos = 0;
        if (duration > 0) {
            pos = getProgressByPosition(currposition);
        }
        seekBar.setProgress(pos);
        String content;
        if (currposition > 0) {
            content = Utils.getTimeFormatString(currposition / 1000);
        }
        else {
            if (duration > 0) {
                content = Utils.getTimeFormatString(0);
            }
            else {
                content = "";
            }
        }
        currentPositionTxtv.setText(content);
    }

    private void initVideoProgressSeekBar(int position, long duration) {
        if (position > duration || position < 0) {
            position = 0;
        }
        if (duration > 0) {
            initVideoDuration(duration);
            if (seekBar != null) {
                seekBar.setMax(SEEKBAR_MAX);
                fastSeekBar.setMax(SEEKBAR_MAX);
            }
            setVideoSeekbarCurrent(position);
            isInitSeekBar = true;
        }
        else {
            isInitSeekBar = false;
        }
    }

    private void initVideoDuration(long duration) {
        this.duration = duration;
        String content = Utils.getTimeFormatString((int) duration / 1000);
        DEFAULT_SEEK_PROGRESS = (int) (15 * this.duration / 1000);
        if (durationTxtv != null) {
            durationTxtv.setText(content);
        }
    }

    public void notifyInitVideoProgressBar(int position, int duration) {
        uiHandler.removeMessages(MSG_INIT_SEEK_BAR);
        Message msg = Message.obtain();
        msg.arg1 = position;
        msg.arg2 = duration;
        msg.what = MSG_INIT_SEEK_BAR;
        uiHandler.sendMessage(msg);
    }

    public void notifyUpdateVideoProgressBar(int position) {
        uiHandler.removeMessages(MSG_UPDATE_SEEK_BAR);
        Message msg = Message.obtain();
        msg.arg1 = position;
        msg.what = MSG_UPDATE_SEEK_BAR;
        uiHandler.sendMessage(msg);
    }

    public void notifyShowFaskSeekIndexBar(int delay) {
        uiHandler.removeMessages(MSG_SHOW_FAST_SEEK_BAR_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_FAST_SEEK_BAR_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void doShowFastSeekIndexBar() {
        if (seekIndexView != null) {
            seekIndexView.setVisibility(View.VISIBLE);
        }
    }

    public void onPositionChanaged(int position, int duration) {
        if (!isInitSeekBar) {
            notifyInitVideoProgressBar(position, duration);
        }
        else {
            notifyUpdateVideoProgressBar(position);
        }
    }

    OnSeekBarChangeListener seekBarChanageListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int position = easyPlayer.getDuration() / 1000 * seekBar.getProgress();
            if (easyPlayer.isInPlaybackState()) {
                easyPlayer.seekTo(position);
                fastSeekBar.setProgress(seekBar.getProgress());
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fastSeekBar != null) {
                fastSeekBar.setProgress(progress);
            }
            if (fromUser) {
                easyPlayer.showNavigationBar(0);
            }
        }
    };

    public void setPlayerController(UEasyPlayer controller) {
        easyPlayer = controller;
    }

    private void seekIncrease() {
        int p;
        if (lastSeekPosition == -1) {
            p = easyPlayer.getCurrentPosition();
        }
        else {
            p = lastSeekPosition;
        }
        int seekTo = p + DEFAULT_SEEK_PROGRESS;
        if (seekTo > easyPlayer.getDuration()) {
            seekTo = easyPlayer.getDuration();
        }
        doFastSeek(seekTo);
    }

    public void release() {
        lastSeekPosition = -1;
        fastSeekToTemp = -1;
        isInitSeekBar = false;
    }

    private void seekDecrease() {
        int p;
        if (lastSeekPosition == -1) {
            p = easyPlayer.getCurrentPosition();
        }
        else {
            p = lastSeekPosition;
        }
        int seekTo = p - DEFAULT_SEEK_PROGRESS;
        if (seekTo < 0) {
            seekTo = 0;
        }
        doFastSeek(seekTo);
    }

    private void doFastSeek(int seekTo) {
        if (seekBar != null && fastSeekBar != null) {
            seekingIndexTxtv.setText(Utils.getTimeFormatString((seekTo) / 1000));
            int progress = getProgressByPosition(seekTo);
            fastSeekBar.setProgress(progress);
            fastSeekToTemp = seekTo;
            float pivotX = (fastSeekBar.getWidth() * fastSeekBar.getProgress() / 1000 - seekingIndexTxtv.getWidth() / 2);
            LayoutParams lp = (LayoutParams) seekIndexView.getLayoutParams();
            lp.leftMargin = (int) pivotX;
            seekIndexView.setLayoutParams(lp);
            lastSeekPosition = seekTo;
        }
    }

    public int getLastFastSeekPosition() {
        return fastSeekToTemp;
    }

    public void setLastFastSeekPosition(int value) {
        fastSeekToTemp = value;
    }

    public void setLastSeekPosition(int value) {
        lastSeekPosition = value;
    }

    public void fastSeek(boolean flag) {
        if (flag) {
            seekIncrease();
        }
        else {
            seekDecrease();
        }
    }

    public void doHideFastSeekIndexBar() {
        if (seekIndexView != null) {
            seekIndexView.setVisibility(View.GONE);
        }
    }

    public void notifyHideFaskSeekIndexBar(int delay) {
        uiHandler.removeMessages(MSG_HIDE_FAST_SEEK_BAR_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_FAST_SEEK_BAR_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void setSeekEnable(boolean isSeekEnable) {
        if (fastSeekBar != null) {
            fastSeekBar.setEnabled(isSeekEnable);
            fastSeekBar.setVisibility(isSeekEnable ? View.VISIBLE : View.GONE);
        }
        if (seekBar != null) {
            seekBar.setEnabled(isSeekEnable);
            seekBar.setVisibility(isSeekEnable ? View.VISIBLE : View.GONE);
        }
    }
}
