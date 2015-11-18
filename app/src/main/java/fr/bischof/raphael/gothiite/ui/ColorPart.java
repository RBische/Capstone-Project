package fr.bischof.raphael.gothiite.ui;

/**
 * Used in an InternalView to define parts of the view
 * Created by rbischof on 18/11/2015.
 */
public class ColorPart {
    private int partLength;
    private boolean redColor;

    public ColorPart(int partLength, boolean redColor) {
        this.partLength = partLength;
        this.redColor = redColor;
    }

    public int getPartLength() {
        return partLength;
    }

    public boolean isRedColor() {
        return redColor;
    }
}
