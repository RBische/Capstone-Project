package fr.bischof.raphael.gothiite.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.UUID;

import fr.bischof.raphael.gothiite.R;
import fr.bischof.raphael.gothiite.data.RunContract;

/**
 * Shows a fragment that allows an user to type a run type interval informations.
 * This fragment automatically saves this RunTypeInterval on validation.
 * Created by biche on 23/09/2015.
 */
public class CreateRunTypeIntervalFragment extends DialogFragment {
    private static final String ARG_RUN_TYPE_ID = "runTypeId";
    private static final String ARG_ORDER = "order";
    private String mRunTypeId;
    private int mOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRunTypeId = getArguments().getString(ARG_RUN_TYPE_ID,"");
        mOrder = getArguments().getInt(ARG_ORDER,0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(getString(R.string.run_type_interval_add));
        final View customView = inflater.inflate(R.layout.fragment_create_run_type_interval, null);
        builder.setView(customView)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText etDistanceToDo = (EditText) customView.findViewById(R.id.etDistanceToDo);
                        EditText etTimeToDo = (EditText) customView.findViewById(R.id.etTimeToDo);
                        double timeToDo;
                        if (!etTimeToDo.getText().toString().equals("")) {
                            timeToDo = Double.parseDouble(etTimeToDo.getText().toString());
                            double distanceToDo = 0;
                            if (!etDistanceToDo.getText().toString().equals("")) {
                                distanceToDo = Double.parseDouble(etDistanceToDo.getText().toString());
                            }
                            boolean isAnEffort = ((ToggleButton) customView.findViewById(R.id.tbEffort)).isChecked();
                            ContentValues valuesToSave = new ContentValues();
                            if (distanceToDo!=0){
                                valuesToSave.put(RunContract.RunTypeIntervalEntry.COLUMN_DISTANCE_TO_DO, distanceToDo);
                            }
                            valuesToSave.put(RunContract.RunTypeIntervalEntry.COLUMN_EFFORT, isAnEffort ? 1 : 0);
                            valuesToSave.put(RunContract.RunTypeIntervalEntry.COLUMN_RUN_TYPE_ID, mRunTypeId);
                            valuesToSave.put(RunContract.RunTypeIntervalEntry.COLUMN_ORDER, mOrder);
                            valuesToSave.put(RunContract.RunTypeIntervalEntry.COLUMN_TIME_TO_DO, timeToDo * 1000);
                            valuesToSave.put(RunContract.RunTypeIntervalEntry._ID, UUID.randomUUID().toString());
                            Uri runTypeIntervalsUri = RunContract.RunTypeIntervalEntry.buildRunTypeIntervalsUri();
                            getActivity().getContentResolver().insert(runTypeIntervalsUri, valuesToSave);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.time_to_do_required), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateRunTypeIntervalFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Helper that creates the dialog
     * @param runTypeID the runTypeId in which the RunTypeInterval will be inserted
     * @param order the index where the RunTypeInterval will be inserted
     * @return Instance of the fragment
     */
    public static CreateRunTypeIntervalFragment newInstance(String runTypeID, int order) {
        CreateRunTypeIntervalFragment fragment = new CreateRunTypeIntervalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RUN_TYPE_ID,runTypeID);
        args.putInt(ARG_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }
}
