package com.droidlogic.launcher.livetv;

import android.database.Cursor;
import android.media.tv.TvContract;


public class Program {

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

    private Program() {
        mChannelId = INVALID_LONG_VALUE;
        mId = INVALID_LONG_VALUE;
        mSearchable = IS_SEARCHABLE;
    }

    public long getId() {
        return mId;
    }

    public String getTile() {
        return mTitle;
    }

    public long getChannelId() {
        return mChannelId;
    }

    public String getPosterArtUrl() {
        return mPosterArtUri;
    }

    private void copyFrom(Program other) {
        if (this == other) {
            return;
        }

        mId = other.mId;
        mPackageName = other.mPackageName;
        mChannelId = other.mChannelId;
        mTitle = other.mTitle;
        mPosterArtUri = other.mPosterArtUri;
        mSearchable = other.mSearchable;
    }

    public static Program fromCursor(Cursor cursor) {
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
                new String[]{
                        TvContract.Programs._ID,
                        TvContract.Programs.COLUMN_PACKAGE_NAME,
                        TvContract.Programs.COLUMN_CHANNEL_ID,
                        TvContract.Programs.COLUMN_TITLE,
                        TvContract.Programs.COLUMN_POSTER_ART_URI,
                        TvContract.Programs.COLUMN_SEARCHABLE,
                };

        return baseColumns;
    }


    public static final class Builder {
        private final Program mProgram;

        public Builder() {
            mProgram = new Program();
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

        public Builder setPosterArtUrl(String url) {
            mProgram.mPosterArtUri = url;
            return this;
        }

        public Program build() {
            Program program = new Program();
            program.copyFrom(mProgram);

            return program;
        }
    }

    @Override
    public String toString() {
        return "Program{" +
                "mId=" + mId +
                ", mPackageName='" + mPackageName + '\'' +
                ", mChannelId=" + mChannelId +
                ", mTitle='" + mTitle + '\'' +
                ", mPosterArtUri='" + mPosterArtUri + '\'' +
                ", mSearchable=" + mSearchable +
                '}';
    }
}
