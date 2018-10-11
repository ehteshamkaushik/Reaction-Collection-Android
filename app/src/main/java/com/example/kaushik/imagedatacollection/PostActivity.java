package com.example.kaushik.imagedatacollection;

import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView userNameTextView = findViewById(R.id.userNameTextViewId);
        final TextView postNoTextView = findViewById(R.id.imageNoTextViewId);
        final Button nextButton = findViewById(R.id.nextButtonId);
        final ImageView postImageView = findViewById(R.id.postImageViewId);
        final ImageButton isLikedImageButton = findViewById(R.id.isLikedImageButtonId);
        final TextView isLikedTextView = findViewById(R.id.isLikedTextViewId);
        final EditText commentEditText = findViewById(R.id.commentEditTextId);
        Button commentButton = findViewById(R.id.commentButtonId);
        final ListView commentListView = findViewById(R.id.commentListViewId);
        final int[] postNo = {1};
        final boolean[] isLiked = {false};
        final List<String> comments = new ArrayList<>();

        final ArrayAdapter<String> commentListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, comments);
        commentListView.setAdapter(commentListAdapter);

        final Intent intent = getIntent();
        final String userName = intent.getExtras().getString("userName");
        userNameTextView.setText(userName);
        initializePost(postNo[0], postNoTextView, postImageView, isLikedTextView);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postNo[0] == 14)
                {
                    nextButton.setText("Finish");
                }
                if (postNo[0] >= 15)
                {
                    Intent intent1 = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(intent1);
                }
                else
                {
                    postNo[0]++;
                    initializePost(postNo[0], postNoTextView, postImageView, isLikedTextView);
                    isLikedImageButton.setImageResource(R.drawable.like_unpessed);
                    comments.clear();
                    commentListAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentListView);
                    isLiked[0] = false;
                }
            }
        });

        isLikedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLiked[0])
                {
                    isLiked[0] = false;
                    isLikedImageButton.setImageResource(R.drawable.like_unpessed);
                    isLikedTextView.setText("You have not liked this post yet");
                }
                else
                {
                    isLiked[0] = true;
                    isLikedImageButton.setImageResource(R.drawable.like_pressed);
                    isLikedTextView.setText("You have liked this post");
                }
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEditText.getText().toString();
                if (!comment.isEmpty()) {
                    comments.add(comment);
                    commentListAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentListView);
                    commentEditText.setText("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    public void initializePost(int postNo, TextView postNoTextView, ImageView postImageView,
                                      TextView isLikedTextView)
    {
        postNoTextView.setText("Post " + postNo);
        final int postId = PostActivity.this.getResources().getIdentifier("post"+ postNo, "drawable", getPackageName());
        postImageView.setImageResource(postId);
        isLikedTextView.setText("You have not liked this post yet");
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PostActivity.this, "Please finish the survey", Toast.LENGTH_LONG).show();
        moveTaskToBack(false);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
