package cn.net.sinotour;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayService extends Service {
	private static final String TAG="MusicPlayService";
	private final IBinder mBinder = new LocalBinder();
	/* MediaPlayer对象 */
	private MediaPlayer mMediaPlayer = null;
	private int currentTime = 0;// 歌曲播放进度
	@Override
	public void onCreate() {
		super.onCreate();
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
	}
	/**
	 * 得到当前播放进度
	 */
	public int getCurrent() {
		if (mMediaPlayer.isPlaying()) {
			return mMediaPlayer.getCurrentPosition();
		} else {
			return currentTime;
		}
	}
	
	/**
	 * 跳到输入的进度
	 */
	public void movePlay(int progress) {
		mMediaPlayer.seekTo(progress);
		currentTime = progress;
	}

	/**
	 * 根据歌曲存储路径播放歌曲
	 */
	public void playMusic(FileDescriptor fd,long offset,long length) {
		try {
			/* 重置MediaPlayer */
			mMediaPlayer.reset();
			/* 设置要播放的文件的路径 */

			mMediaPlayer.setDataSource(fd, offset, length);
			/* 准备播放 */
			mMediaPlayer.prepare();
			/* 开始播放 */
			mMediaPlayer.start();
		} catch (IOException e) {
		}
	}
	/**
	 * 根据歌曲存储路径播放歌曲
	 */
	public void playMusic(String path) {
		try {
			/* 重置MediaPlayer */
			mMediaPlayer.reset();
			/* 设置要播放的文件的路径 */

			mMediaPlayer.setDataSource(path);
			/* 准备播放 */
			mMediaPlayer.prepare();
			/* 开始播放 */
			mMediaPlayer.start();
		} catch (IOException e) {
		}
	}
	/**
	 * 歌曲是否真在播放
	 */
	public boolean isPlay() {
		return mMediaPlayer.isPlaying();
	}

	/**
	 * 暂停或开始播放歌曲
	 */
	public void pausePlay() {
		if (mMediaPlayer.isPlaying()) {
			currentTime = mMediaPlayer.getCurrentPosition();
			mMediaPlayer.pause();
		} else {
			mMediaPlayer.start();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}


	/**
	 * 自定义绑定Service类，通过这里的getService得到Service，之后就可调用Service这里的方法了
	 */
	public class LocalBinder extends Binder {
		public MusicPlayService getService() {
			return MusicPlayService.this;
		}
	}

	public MediaPlayer getmMediaPlayer() {
		return mMediaPlayer;
	}

	public void setmMediaPlayer(MediaPlayer mMediaPlayer) {
		this.mMediaPlayer = mMediaPlayer;
	}

	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	// 兼容2.0以前版本
	@Override
	public void onStart(Intent intent, int startId) {
	}

	// 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("Service", "Received start id " + startId + ": " + intent);
		// 如果服务进程在它启动后(从onStartCommand()返回后)被kill掉, 那么让他呆在启动状态但不取传给它的intent.
		// 随后系统会重写创建service，因为在启动时，会在创建新的service时保证运行onStartCommand
		// 如果没有任何开始指令发送给service，那将得到null的intent，因此必须检查它.
		// 该方式可用在开始和在运行中任意时刻停止的情况，例如一个service执行音乐后台的重放

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mMediaPlayer.isPlaying()) {

			mMediaPlayer.release();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
	
		return super.onUnbind(intent);
	}
}
