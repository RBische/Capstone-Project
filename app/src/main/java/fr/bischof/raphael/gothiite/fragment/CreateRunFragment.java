package fr.bischof.raphael.gothiite.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.bischof.raphael.gothiite.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateRunFragment extends Fragment {

    public CreateRunFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_run, container, false);
    }
}
