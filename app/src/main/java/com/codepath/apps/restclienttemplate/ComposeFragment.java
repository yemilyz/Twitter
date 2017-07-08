package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;
import static com.codepath.apps.restclienttemplate.R.id.btnSubmit;
import static com.codepath.apps.restclienttemplate.R.id.etNewTweet;
import static com.codepath.apps.restclienttemplate.R.id.etTweet;
import static com.codepath.apps.restclienttemplate.R.id.ivProfileImage;
import static com.codepath.apps.restclienttemplate.R.id.tvCharCount;

/**
 * Created by emilyz on 7/7/17.
 */

public class ComposeFragment extends DialogFragment {
    private EditText mNewTweet;
    private TextView tvCharacterCount;

    private TwitterClient client;
    private Tweet tweet;

    public interface ComposeDialogListener {
        public void onFinishComposeDialog(Tweet tweet);
    }

    // Empty constructor required for DialogFragment
    public ComposeFragment() {

    }

    public static ComposeFragment newInstance() {
        ComposeFragment composeFragment = new ComposeFragment();
        return composeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate( R.layout.fragment_compose, container );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        client = TwitterApp.getRestClient();

        mNewTweet = (EditText) view.findViewById( R.id.etTweet );
        Button btnComposeSubmit = (Button) view.findViewById( R.id.btnSubmitting );

        final TextView tvCharacterCount = (TextView) view.findViewById( R.id.tvCharacterCount );
//        EditText etNewTweet = (EditText) view.findViewById(R.id.etNewTweet);
        mNewTweet.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharacterCount.setText( String.valueOf( 140 - s.length() ) );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        } );

        // Set the title
        getDialog().setTitle( "Compose Tweet" );

        mNewTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );

        btnComposeSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( getActivity(), "Submitting tweet", Toast.LENGTH_SHORT ).show();
                if (v.getId() == R.id.btnSubmitting) {
                    Toast.makeText( getActivity(), "Submitting tweet", Toast.LENGTH_SHORT ).show();
                    String newTweetText = mNewTweet.getText().toString();
                    client.sendTweet( newTweetText, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess( statusCode, headers, response );
                            try {
                                // Get the tweet
                                tweet = Tweet.fromJSON( response );
                                ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                                listener.onFinishComposeDialog( tweet );
                                dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess( statusCode, headers, response );
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    tweet = Tweet.fromJSON( response.getJSONObject( i ) );
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dismiss();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            super.onSuccess( statusCode, headers, responseString );
                            dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText( getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT ).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            throwable.printStackTrace();
                            Toast.makeText( getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT ).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            throwable.printStackTrace();
                            Toast.makeText( getActivity(), "Failed to submit tweet", Toast.LENGTH_SHORT ).show();
                            dismiss();
                        }
                    } );
                }

            }
        } );
    }
}


//    public ComposeFragment() {
//        // Empty constructor is required for DialogFragment
//        // Make sure not to add arguments to the constructor
//        // Use `newInstance` instead as shown below
//    }
//
//    public static ComposeFragment newInstance( ) {
//        ComposeFragment composeFragment = new ComposeFragment();
//        return composeFragment;
//    }
//
//    public interface ComposeListener {
//        public void onFinishCompose(Tweet tweet);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_compose, container);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//
//        super.onViewCreated(view, savedInstanceState);
//
//        client = TwitterApp.getRestClient();
//
//        etTweet = (EditText) view.findViewById(R.id.etTweet);
//        btnSubmitting = (Button) view.findViewById( R.id.btnSubmiting );
//        tvCharacterCount = (TextView) view.findViewById( R.id.tvCharacterCount );
//
//
//        final TextWatcher txWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                tvCharacterCount.setText(String.valueOf(140-s.length()));
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        };
//        etTweet.addTextChangedListener(txWatcher);
//
//        getDialog().setTitle("Compose Tweet");
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//
//        btnSubmitting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String newTweet = etTweet.getText().toString();
//                client.sendTweet(etTweet.getText().toString(), new JsonHttpResponseHandler(){
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//
//                            tweet = Tweet.fromJSON( response );
//                            ComposeListener listener = (ComposeListener) getActivity();
//                            listener.onFinishCompose(tweet);
//                            dismiss();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                        super.onSuccess(statusCode, headers, response);
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                tweet = Tweet.fromJSON(response.getJSONObject(i));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        super.onSuccess( statusCode, headers, responseString );
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure( statusCode, headers, responseString, throwable );
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        super.onFailure( statusCode, headers, throwable, errorResponse );
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                        super.onFailure( statusCode, headers, throwable, errorResponse );
//                        dismiss();
//                    }
//                });
//
//            }
//        });
//
//    }
//
//    public void sendBackResult() {
//        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
//        ComposeListener listener = (ComposeListener) getTargetFragment();
//        listener.onFinishCompose(tweet);
//        dismiss();
//    }
//
//}
