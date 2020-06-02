package com.example.globusproject;

import androidx.annotation.Nullable;

public class ChronometerHelper {

    @Nullable
    private static Long mStartTime;
    private static boolean mRunning;
    @Nullable
    private static Long mPause;
    private static long mProgram_id=0;

    @Nullable
    public Long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(final long startTime) {
        mStartTime = startTime;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(final boolean running) {
        mRunning = running;
    }

    @Nullable
    public Long getPause() {
        return mPause;
    }

    public void setPause(final long pause) {
        mPause = pause;
    }

    public long getProgram_id() {
        return mProgram_id;
    }

    public void setProgram_id(final long program_id) {
        mProgram_id = program_id;
    }
}
