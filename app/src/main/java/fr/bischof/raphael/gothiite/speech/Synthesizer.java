package fr.bischof.raphael.gothiite.speech;

import android.content.Context;

import java.util.ArrayList;

/**
 * Class that contains the logic to synthesize a text and return parts that have to be played
 * Created by biche on 02/12/2015.
 */
public class Synthesizer {
    private NumberSynthesizer mNumberSynthesizer;
    private String mToSynthesize;
    private int[] mNumberToSynthesize = new int[]{0};

    public Synthesizer(Context context, String toSynthesize) {
        this.mNumberSynthesizer = new NumberSynthesizer(context);
        this.mToSynthesize = toSynthesize;
    }

    public Synthesizer(Context context, String toSynthesize, int... numberToSynthesize) {
        this.mNumberSynthesizer = new NumberSynthesizer(context);
        this.mToSynthesize = toSynthesize;
        this.mNumberToSynthesize = numberToSynthesize;
    }

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
}
