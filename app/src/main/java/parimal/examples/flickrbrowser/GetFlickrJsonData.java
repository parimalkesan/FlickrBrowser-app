package parimal.examples.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.onDownloadComplete{
    private static final String TAG = "GetFlickrJsonData";
    private boolean runningOnSameThread=false;

    private List<Photo> mPhotoList=null;
    private String mBaseURL;
    private  String mLanguage;
    private boolean mMatchAll;

    private final onDataAvailable mCallback;
    interface onDataAvailable{
        void onDataAvailable(List<Photo> data,DownloadStatus status );
    }

    public GetFlickrJsonData(onDataAvailable mCallback,String mBaseURL, String mLanguage, boolean mMatchAll) {
        Log.d(TAG, "GetFlickrJsonData: constructor called");
        this.mBaseURL = mBaseURL;
        this.mLanguage = mLanguage;
        this.mMatchAll = mMatchAll;
        this.mCallback = mCallback;
    }
    void executeOnSameThread(String  searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread=true;
        String destinationUrl=createUri(searchCriteria,mLanguage,mMatchAll);

        GetRawData getRawData=new GetRawData(this);
        getRawData.execute(destinationUrl);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if(mCallback!=null)
        {
            mCallback.onDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUrl=createUri(params[0],mLanguage,mMatchAll);

        GetRawData getRawData=new GetRawData(this);
        getRawData.runInSameThread(destinationUrl);
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll){
        Log.d(TAG, "createUrl: starts");

       // Uri uri= Uri.parse(mBaseURL);
        //Uri.Builder builder=uri.buildUpon();
        //builder=builder.appendQueryParameter("tags",searchCriteria);
        //builder=builder.appendQueryParameter("tagmode",matchAll?"ALL":"ANY");
        //builder=builder.appendQueryParameter("lang",lang);
        //builder=builder.appendQueryParameter("format","json");
        //builder=builder.appendQueryParameter("nojsoncallback","1");

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagmode",matchAll?"ALL":"ANY")
                .appendQueryParameter("lang",lang)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .build().toString();
        }

        public void onDownloadComplete(String data,DownloadStatus status){
            Log.d(TAG, "onDownloadComplete: starts.status= "+status);

            if(status==DownloadStatus.OK){
                mPhotoList=new ArrayList<>();
                try {
                    JSONObject jsonData=new JSONObject(data);
                    JSONArray itemsArray=jsonData.getJSONArray("items");

                    for(int i=0;i<itemsArray.length();i++){
                        JSONObject jsonPhoto=itemsArray.getJSONObject(i);
                        String title=jsonPhoto.getString("title");
                        String author=jsonPhoto.getString("author");
                        String authorId=jsonPhoto.getString("author_id");
                        String tags=jsonPhoto.getString("tags");

                        JSONObject jsonMedia=jsonPhoto.getJSONObject("media");
                        String photoUrl=jsonMedia.getString("m");

                        String link=photoUrl.replaceFirst("_m.","_b.");

                        Photo photoObject=new Photo(title,author,authorId,link,tags,photoUrl);
                        mPhotoList.add(photoObject);

                        Log.d(TAG, "onDownloadComplete: "+photoObject.toString());
                    }
                }
                catch(JSONException jsone){
                    jsone.printStackTrace();
                    Log.d(TAG, "onDownloadComplete: error processing json data"+jsone.getMessage());
                    status=DownloadStatus.FAILED_OR_EMPTY;
                }
            }

            if(runningOnSameThread && mCallback!=null) {
                //now inform the caller that processing is done or return null if error occurs
                mCallback.onDataAvailable(mPhotoList, status);
            }

            Log.d(TAG, "onDownloadComplete: ends");
    }
}
