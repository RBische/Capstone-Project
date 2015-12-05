package fr.bischof.raphael.gothiite.speech;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.Locale;

import fr.bischof.raphael.gothiite.R;

/**
 * Class that contains the logic to synthesize a text and return parts that have to be played
 * Created by biche on 02/12/2015.
 */
public class Synthesizer {
    private NumberSynthesizer mNumberSynthesizer;
    private String mToSynthesize;
    private int[] mNumberToSynthesize = new int[]{0};

    public Synthesizer(Context context,@StringRes int toSynthesize, int... numberToSynthesize) {
        this.mNumberSynthesizer = new NumberSynthesizer(context);
        this.mToSynthesize = getStringFromGoodResources(context,toSynthesize);
        this.mNumberToSynthesize = numberToSynthesize;
    }

    /**
     * Retrieve the strings corresponding to mp3 assets filename depending on the numbers and string resource given in the constructor
     * @return Array of mp3 assets filename
     */
    public String[] getSynthesizedParts(){
        ArrayList<String> parts = new ArrayList<>();
        String[] splittedBaseParts = mToSynthesize.split("\\|");
        int currentNumberIndex = 0;
        for (String splittedBasePart : splittedBaseParts) {
            if (splittedBasePart.equalsIgnoreCase("numberToReplace")) {
                if (mNumberToSynthesize.length > currentNumberIndex) {
                    parts.addAll(mNumberSynthesizer.getSynthesizeNumberAsArrayList(mNumberToSynthesize[currentNumberIndex]));
                    currentNumberIndex++;
                } else {
                    parts.addAll(mNumberSynthesizer.getSynthesizeNumberAsArrayList(0));
                }
            } else {
                parts.add(splittedBasePart);
            }
        }
        return parts.toArray(new String[parts.size()]);
    }

    private String getStringFromGoodResources(Context context,@StringRes int toSynthesize) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString(context.getString(R.string.pref_language_pack_key), context.getString(R.string.pref_language_pack_default));
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        conf.locale = new Locale(language); // whatever you want here
        res.updateConfiguration(conf, null); // second arg null means don't change

        // retrieve resources from desired locale
        String str = res.getString(toSynthesize);

        // restore original locale
        conf.locale = savedLocale;
        res.updateConfiguration(conf, null);
        return str;
    }
}
