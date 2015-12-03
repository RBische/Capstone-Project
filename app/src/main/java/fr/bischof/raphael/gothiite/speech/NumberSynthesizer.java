package fr.bischof.raphael.gothiite.speech;

import android.content.Context;

import java.util.ArrayList;
import java.util.Objects;

import fr.bischof.raphael.gothiite.R;

/**
 * Class that contains the logic to synthesize a number
 * Created by biche on 02/12/2015.
 */
public class NumberSynthesizer {
    private Context mContext;

    public NumberSynthesizer(Context context) {
        this.mContext = context;
    }

    public String[] getSynthesizeNumber(int number){
        ArrayList<String> arrayListParts = getSynthesizeNumberAsArrayList(number);
        return arrayListParts.toArray(new String[arrayListParts.size()]);
    }

    public ArrayList<String> getSynthesizeNumberAsArrayList(int number){
        String[] parts = synthesizeNumber(number);
        ArrayList<String> arrayListParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.equals("")) {
                arrayListParts.add(part + ".mp3");
            }
        }
        return arrayListParts;
    }

    private String[] synthesizeNumber(int number){
        if(number<1000000){
            String[] numberResources = mContext.getResources().getStringArray(R.array.mp3_numbers);
            if(number<100){
                return numberResources[number].split("\\|");
            }else if (number<1000){
                String[] hundreds = numberResources[((number%1000)/100)+100].split("\\|");
                String[] hundredConnectors = numberResources[100].split("\\|");
                String[] decadePart = numberResources[number%100].split("\\|");
                return generalConcatAll(hundreds,hundredConnectors,decadePart);
            }else if (number<100000&&number%1000<100){
                String[] thousands = numberResources[number/1000].split("\\|");
                String[] thousand = numberResources[111].split("\\|");
                String[] thousandConnectors = numberResources[110].split("\\|");
                String[] decadePart = numberResources[number%100].split("\\|");
                return generalConcatAll(thousands,thousand,thousandConnectors,decadePart);
            }else if (number<100000){
                String[] thousands = numberResources[number/1000].split("\\|");
                String[] thousand = numberResources[111].split("\\|");
                String[] thousandConnectors = numberResources[110].split("\\|");
                String[] hundreds = numberResources[((number%1000)/100)+100].split("\\|");
                String[] hundredConnectors = numberResources[100].split("\\|");
                String[] decadePart = numberResources[number%100].split("\\|");
                return generalConcatAll(thousands,thousand,thousandConnectors,hundreds,hundredConnectors,decadePart);
            }else if (number%1000<100){
                String[] thousandHundreds = numberResources[((number/1000)/100)+100].split("\\|");
                String[] thousandHundredConnectors = numberResources[100].split("\\|");
                String[] thousands = numberResources[(number%100000)/1000].split("\\|");
                String[] thousand = numberResources[111].split("\\|");
                String[] thousandConnectors = numberResources[110].split("\\|");
                String[] decadePart = numberResources[number%100].split("\\|");
                return generalConcatAll(thousandHundreds,thousandHundredConnectors,thousands,thousand,thousandConnectors,decadePart);
            }else{
                String[] thousandHundreds = numberResources[((number/1000)/100)+100].split("\\|");
                String[] thousandHundredConnectors = numberResources[100].split("\\|");
                String[] thousands = numberResources[(number%100000)/1000].split("\\|");
                String[] thousand = numberResources[111].split("\\|");
                String[] thousandConnectors = numberResources[110].split("\\|");
                String[] hundreds = numberResources[((number%1000)/100)+100].split("\\|");
                String[] hundredConnectors = numberResources[100].split("\\|");
                String[] decadePart = numberResources[number%100].split("\\|");
                return generalConcatAll(thousandHundreds,thousandHundredConnectors,thousands,thousand,thousandConnectors,hundreds,hundredConnectors,decadePart);
            }
        }else {
            return new String[]{};
        }
    }

    private String[] generalConcatAll(String[]... jobs) {
        int len = 0;
        for (final String[] job : jobs) {
            len += job.length;
        }

        final String[] result = new String[len];

        int currentPos = 0;
        for (final String[] job : jobs) {
            System.arraycopy(job, 0, result, currentPos, job.length);
            currentPos += job.length;
        }

        return result;
    }
}
