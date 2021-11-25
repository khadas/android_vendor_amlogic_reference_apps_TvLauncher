/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidlogic.launcher.livetv;


import android.content.ContentResolver;
import android.database.Cursor;
import android.media.tv.TvContract.Channels;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/** Static utils for data model classes */
public final class TVModelUtils {
    private static final String TAG = "ModelUtils";
    private static final boolean DEBUG = false;

    private static final int INPUT_ID_LENGTH = 3;

    private static boolean compareInputId(String inputId, String infoInputId) {
        if (null == inputId) {
            return true;
        }
        if (null == infoInputId) {
            return false;
        }

        if (TextUtils.isEmpty(inputId) || TextUtils.isEmpty(infoInputId)) {
            return false;
        }

        if (TextUtils.equals(inputId, infoInputId)) {
            return true;
        }

        String[] inputIdArr = inputId.split("/");
        String[] infoInputIdArr = infoInputId.split("/");
        // InputId is like com.droidlogic.tvinput/.services.Hdmi1InputService/HW5
        if (inputIdArr.length == INPUT_ID_LENGTH && infoInputIdArr.length == INPUT_ID_LENGTH) {
            // For hdmi device inputId could change to com.droidlogic.tvinput/.services.Hdmi2InputService/HDMI200008
            if (inputIdArr[0].equals(infoInputIdArr[0]) && inputIdArr[1].equals(infoInputIdArr[1])) {
                return true;
            }
        }
        return false;
    }


    public static List<Channel> getChannels(ContentResolver resolver, String inputId) {
        List<Channel> channels = new ArrayList<>();
        // TvProvider returns programs in chronological order by default.
        int num = 0;

        Cursor cursor = null;
        try {
            cursor = resolver.query(Channels.CONTENT_URI, Channel.PROJECTION, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                return channels;
            }
            while (cursor.moveToNext()) {
                Channel ch = Channel.fromCursor(cursor);
                if (compareInputId(inputId, ch.getInputId())) {
                    channels.add(ch);

                    num++;
                    if(num >= 5)
                        break;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to get channels", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return channels;
    }

    private TVModelUtils() {}
}
