package com.example.jpoobest.poobestcom.datatype;

import android.os.Bundle;

/**
 * Created by j.poobest on 17/4/2560.
 */

public class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt("value", value);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle saveInstanceState) {
        value = saveInstanceState.getInt("value");

    }

}
