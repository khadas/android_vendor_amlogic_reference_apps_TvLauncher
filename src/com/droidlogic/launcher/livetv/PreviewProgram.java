package com.droidlogic.launcher.livetv;

import android.database.Cursor;
import android.media.tv.TvContract;
import android.os.Build;


public class PreviewProgram {

    public static final String[] PROJECTION = getProjection();

    private static final long INVALID_LONG_VALUE = -1;
    private static final int INVALID_INT_VALUE = -1;
    private static final int IS_RECORDING_PROHIBITED = 1;
    private static final int IS_SEARCHABLE = 1;


    private long mId;
    private String mPackageName;
    private long mChannelId;
    private String mTitle;
    private String mPosterArtUri;
    private int mSearchable;
    private String mPreviewVideoUri;
    private String mIntentUri;


    private PreviewProgram() {
        mChannelId = INVALID_LONG_VALUE;
        mId = INVALID_LONG_VALUE;
        mSearchable = IS_SEARCHABLE;
    }

    public String getTile(){
        return mTitle;
    }

    public long getChannelId(){
        return mChannelId;
    }

    public String getPosterArtUrl(){
        return mPosterArtUri;
    }

    private void copyFrom(PreviewProgram other) {
        if (this == other) {
            return;
        }

        mId = other.mId;
        mPackageName= other.mPackageName;
        mChannelId = other.mChannelId;
        mTitle = other.mTitle;
        mPosterArtUri = other.mPosterArtUri;
        mSearchable = other.mSearchable;
    }


    static PreviewProgram fromCursor(Cursor cursor) {
        Builder builder = new Builder();
        int index = 0;
        if (!cursor.isNull(index)) {
            builder.setId(cursor.getLong(index));
        }
        if (!cursor.isNull(++index)) {
            builder.setPackageName(cursor.getString(index));
        }
        if (!cursor.isNull(++index)) {
            builder.setChannelId(cursor.getLong(index));
        }
        if (!cursor.isNull(++index)) {
            builder.setTitle(cursor.getString(index));
        }
        if (!cursor.isNull(++index)) {
            builder.setPosterArtUrl(cursor.getString(index));
        }
        return builder.build();
    }


    private static String[] getProjection() {
        String[] baseColumns =
                new String[] {
                        TvContract.PreviewPrograms._ID,
                        TvContract.PreviewPrograms.COLUMN_PACKAGE_NAME,
                        TvContract.PreviewPrograms.COLUMN_CHANNEL_ID,
                        TvContract.PreviewPrograms.COLUMN_TITLE,
                        TvContract.PreviewPrograms.COLUMN_POSTER_ART_URI,
                        TvContract.PreviewPrograms.COLUMN_SEARCHABLE,
                        TvContract.PreviewPrograms.COLUMN_PREVIEW_VIDEO_URI,
                        TvContract.PreviewPrograms.COLUMN_INTENT_URI,
                        TvContract.PreviewPrograms.COLUMN_TRANSIENT,
                        TvContract.PreviewPrograms.COLUMN_TYPE,
                        TvContract.PreviewPrograms.COLUMN_LIVE,
                        TvContract.PreviewPrograms.COLUMN_BROWSABLE,
                };

        return baseColumns;
    }



    public static final class Builder {
        private final PreviewProgram mProgram;

        public Builder() {
            mProgram = new PreviewProgram();
        }


        private Builder setId(long programId) {
            mProgram.mId = programId;
            return this;
        }


        public Builder setChannelId(long channelId) {
            mProgram.mChannelId = channelId;
            return this;
        }

        public Builder setPackageName(String name) {
            mProgram.mPackageName = name;
            return this;
        }

        public Builder setTitle(String title) {
            mProgram.mTitle = title;
            return this;
        }

        public Builder setPosterArtUrl(String url){
            mProgram.mPosterArtUri = url;
            return this;
        }

        public PreviewProgram build() {
            PreviewProgram program = new PreviewProgram();
            program.copyFrom(mProgram);

            return program;
        }
    }


}
