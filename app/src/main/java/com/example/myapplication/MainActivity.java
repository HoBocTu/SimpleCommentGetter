package com.example.myapplication;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
    Button butLoad;
    String urlPath = "http://jsonplaceholder.typicode.com/comments";

    RecyclerView recyclerView;
    CardRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initObservers();

    }
    public static Single<Comment[]> getCommentList(){
        return Single.create(new SingleOnSubscribe<Comment[]>() {
            @Override
            public void subscribe(SingleEmitter<Comment[]> emitter) throws Exception {
                String usersJsonStroke = getJsonFromServer(50000);
                JsonParser jsonParser = new JsonParser();
                assert usersJsonStroke != null;
                JsonArray jsonArray = (JsonArray) jsonParser.parse(usersJsonStroke);
                Comment[] comment_list = new Comment[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++){
                    comment_list[i] = parseComment(new JSONObject(jsonArray.get(i).getAsJsonObject().toString()));
                }
                emitter.onSuccess(comment_list);
            }
        });
    }
    private void initUI() {
        butLoad = findViewById(R.id.butLoad);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardRecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }
    private static Comment parseComment(JSONObject commentRoot) throws JSONException {
        Log.d("JSON", commentRoot.toString());
        String postId = commentRoot.getString("postId"),
                Id = commentRoot.getString("id"),
                name = commentRoot.getString("name"),
                email = commentRoot.getString("email"),
                body = commentRoot.getString("body");


        return new Comment(postId, Id, name, email, body);
    }
    private static String getJsonFromServer(int timeout) throws IOException {
        URL url = new URL("http://jsonplaceholder.typicode.com/comments");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();

        int serverResponseCode = connection.getResponseCode();
        switch (serverResponseCode) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String tmpLine;
                while ((tmpLine = br.readLine()) != null) {
                    sb.append(tmpLine).append("\n");
                }
                br.close();
                return sb.toString();
            case 404:
                Log.e("SIte", "page not found!");
                break;
            case 400:
                Log.e("SIte", "Bad request!");
                break;
            case 500:
                Log.e("SIte", "Internal server error");
                break;
        }

        return null;
    }
    private void initObservers() {
        getCompletableLoadButton(butLoad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Completable", "Load button clicked");
                        getCommentList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Comment[]>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d("Single", "OnSubscribe");
                                    }

                                    @Override
                                    public void onSuccess(Comment[] comments) {
                                        Log.d("Single", "Success");
                                        adapter.setItems(new ArrayList<Comment>(Arrays.asList(comments)));
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private Completable getCompletableLoadButton(final Button button) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        emitter.onComplete();
                    }
                });
            }
        });
    }
}
class Comment{
    private String postId;
    private String Id;
    private String name;
    private String email;
    private String body;

    public Comment(String postId, String id, String name, String email, String body) {
        this.postId = postId;
        Id = id;
        this.name = name;
        this.email = email;
        this.body = body;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostId() {
        return postId;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

}