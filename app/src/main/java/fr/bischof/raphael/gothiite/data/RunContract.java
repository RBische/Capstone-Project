package fr.bischof.raphael.gothiite.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the run database.
 * Created by biche on 13/09/2015.
 */
public class RunContract  {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "fr.bischof.raphael.gothiite";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://fr.bischof.raphael.gothiite/runtype/ is a valid path for
    // looking at runtypes data. content://fr.bischof.raphael.gothiite/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_RUN = "run";
    public static final String PATH_LAST_RUN = "lastrun";
    public static final String PATH_RUN_WITH_RUN_TYPE = "runwithruntype";
    public static final String PATH_RUN_INTERVAL = "runInterval";
    public static final String PATH_RUN_TYPE = "runtype";
    public static final String PATH_RUN_TYPE_INTERVAL = "runtypeInterval";
    public static final String PATH_RUN_INTERVAL_WITH_RUN = "runIntervalwithrun";


    /* Inner class that defines the table contents of the run table */
    public static final class RunEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN).build();
        public static final Uri CONTENT_JOINED_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN_WITH_RUN_TYPE).build();
        public static final Uri CONTENT_LAST_RUN_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LAST_RUN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN;

        // Table name
        public static final String TABLE_NAME = "run";

        public static final String COLUMN_AVG_SPEED = "average_speed";
        public static final String COLUMN_VVO2MAX_EQUIVALENT = "vVO2maxEquivalent";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_RUN_TYPE_ID = "runTypeId";

        public static Uri buildRunUri(String id) {
            return Uri.parse(CONTENT_URI+"/"+id);
        }

        public static Uri buildRunsUri() {
            return CONTENT_URI;
        }

        public static Uri buildRunsWithRunTypeUri() {
            return CONTENT_JOINED_URI;
        }

        public static String getRunIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildLastRunUri() {
            return CONTENT_LAST_RUN_URI;
        }
    }

    /* Inner class that defines the table contents of the run interval table */
    public static final class RunIntervalEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN_INTERVAL).build();
        public static final Uri CONTENT_JOINED_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN_INTERVAL_WITH_RUN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_INTERVAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_INTERVAL;

        // Table name
        public static final String TABLE_NAME = "runInterval";

        public static final String COLUMN_DISTANCE_DONE = "distance_done";
        public static final String COLUMN_ORDER = "ordering";
        public static final String COLUMN_START_POSITION_LATITUDE = "start_position_latitude";
        public static final String COLUMN_START_POSITION_LONGITUDE = "start_position_longitude";
        public static final String COLUMN_END_POSITION_LATITUDE = "end_position_latitude";
        public static final String COLUMN_END_POSITION_LONGITUDE = "end_position_longitude";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_RUN_ID = "run_id";

        public static Uri buildRunIntervalUri(String id) {
            return Uri.parse(CONTENT_URI + "/" + id);
        }

        public static Uri buildRunIntervalsWithRunUri(String id) {
            return Uri.parse(CONTENT_JOINED_URI + "/" + id);
        }

        public static Uri buildRunIntervalsUri() {
            return CONTENT_URI;
        }

        public static String getRunIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getRunIntervalIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the run interval table */
    public static final class RunTypeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN_TYPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_TYPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_TYPE;

        // Table name
        public static final String TABLE_NAME = "runType";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DISTANCE_GROWING = "distance_growing";
        public static final String COLUMN_CAN_BE_DELETED = "can_be_deleted";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ICON = "iconRun";

        public static Uri buildRunTypeUri(String id) {
            return Uri.parse(CONTENT_URI+"/"+ id);
        }

        public static Uri buildRunTypesUri() {
            return CONTENT_URI;
        }

        public static String getRunTypeIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the run interval table */
    public static final class RunTypeIntervalEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RUN_TYPE_INTERVAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_TYPE_INTERVAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RUN_TYPE_INTERVAL;

        // Table name
        public static final String TABLE_NAME = "runTypeInterval";

        public static final String COLUMN_ORDER = "ordering";
        public static final String COLUMN_TIME_TO_DO = "time_to_do";
        public static final String COLUMN_DISTANCE_TO_DO = "distance_to_do";
        public static final String COLUMN_SPEED_ESTIMATED = "speed_estimated";
        public static final String COLUMN_EFFORT = "effort";
        public static final String COLUMN_RUN_TYPE_ID = "run_type_id";

        public static Uri buildRunTypeIntervalUri(String id) {
            return Uri.parse(CONTENT_URI+"/"+ id);
        }

        public static Uri buildRunTypeIntervalsUri() {
            return CONTENT_URI;
        }

        public static String getRunTypeIntervalIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
