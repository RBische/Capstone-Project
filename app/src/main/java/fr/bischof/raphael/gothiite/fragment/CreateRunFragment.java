package fr.bischof.raphael.gothiite.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.bischof.raphael.gothiite.R;

/**
 * A fragment containing all necessary things to go for a run
 */
public class CreateRunFragment extends GPSFineLocationFragment {
    @InjectView(R.id.ivGPS)
    public ImageView mIvGPS;

    public CreateRunFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_run, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this,view);
        if (getActivity()!=null&& getActivity() instanceof AppCompatActivity){
            Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar!=null){
                actionBar.setSubtitle(null);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onLocationUpdate(Location location) {
        super.onLocationUpdate(location);
        if (location.hasAccuracy()){
            if (mIvGPS!=null){
                if (location.getAccuracy()>80){
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_low));
                }else if (location.getAccuracy()>30){
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_medium));
                }else{
                    mIvGPS.setImageDrawable(getResources().getDrawable(R.drawable.ic_signal_high));
                }
            }
        }
    }
}
