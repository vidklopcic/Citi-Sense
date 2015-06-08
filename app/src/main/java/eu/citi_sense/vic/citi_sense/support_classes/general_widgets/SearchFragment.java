package eu.citi_sense.vic.citi_sense.support_classes.general_widgets;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import eu.citi_sense.vic.citi_sense.R;
import eu.citi_sense.vic.citi_sense.support_classes.map_activity.ActionBarFragment;

public class SearchFragment extends Fragment{
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private AutoCompleteTextView mSearchField;
    private RelativeLayout mFragmentView;
    private ImageView mMicButton;
    private ImageView mMenuButton;
    private ActionBarFragment.MenuClickInterface mInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = (RelativeLayout) inflater.inflate(R.layout.search_fragment, container, false);
        mSearchField = (AutoCompleteTextView) mFragmentView.findViewById(R.id.search_field);
        mMicButton = (ImageView) mFragmentView.findViewById(R.id.mic_btn);
        mMenuButton = (ImageView) mFragmentView.findViewById(R.id.search_menu_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInterface.menuClicked();
            }
        });
        mMicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mInterface = (ActionBarFragment.MenuClickInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MenuClickInterface");
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mSearchField.setText(result.get(0));
                }
                break;
            }

        }
    }

    public void hideSearch() {
        mFragmentView.setVisibility(View.GONE);
    }

    public void showSearch() {
        mFragmentView.setVisibility(View.VISIBLE);
    }
}
