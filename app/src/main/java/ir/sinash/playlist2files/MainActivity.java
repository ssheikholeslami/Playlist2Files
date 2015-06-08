package ir.sinash.playlist2files;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {

    private static final int PICKFILE_RESULT_CODE = 1;

    public Button btnOpenPlaylist;
    private ArrayList<String> listOfFilePaths;
    private String destFolderPath;
    private String playlistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenPlaylist = (Button) findViewById(R.id.btnOpenPlaylist);

        btnOpenPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                new OpenFileDialogFragment().show(getSupportFragmentManager(), "dialog-open-file" );

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("file/*");
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
                //TODO Always "File way", not "Normal Android Way"
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String filePath = data.getData().getPath();

                    StringTokenizer tokenizer = new StringTokenizer(filePath, "/");

                    playlistName = "";


                    while(tokenizer.hasMoreTokens())
                    {
                        playlistName = tokenizer.nextToken();
                    }

                    //m3u8 tokenizer

                    tokenizer = new StringTokenizer(playlistName, ".m3u8");

                    while(tokenizer.hasMoreTokens())
                    {
                        playlistName = tokenizer.nextToken();
                    }

                    Toast.makeText(getApplicationContext(), playlistName, Toast.LENGTH_SHORT).show();

//                    Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_SHORT).show();
                    processPlaylist(filePath);
                }
                break;

        }
    }

    private void processPlaylist(String filePath) {

        File playlistFile =new File(filePath);
        listOfFilePaths = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(playlistFile));
            String line = null;
//            Toast.makeText(getApplicationContext(), playlistFile.getPath(), Toast.LENGTH_SHORT).show();
            while((line = br.readLine())!= null){

                listOfFilePaths.add(line);
//                Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();

            }

            long totalSize = 0;
            for(String path : listOfFilePaths){

                File file = new File(path);
                totalSize += file.length();

            }

            long totalSizeInKBs = totalSize/1024;

//            Toast.makeText(getApplicationContext(), "Total size: " + totalSizeInKBs, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //copy file



        if(isExternalStorageWritable()){

            destFolderPath = Environment.getExternalStorageDirectory().getPath();
            destFolderPath += "/Playlist2Files/"+playlistName+"/";


//            Toast.makeText(getApplicationContext(), destFolderPath, Toast.LENGTH_SHORT).show();

            for (int index = 0; index<listOfFilePaths.size(); index++){

                String sourcePath = listOfFilePaths.get(index);
                String sourceFileName = "";

                StringTokenizer tokenizer = new StringTokenizer(sourcePath, "/");

                //int numOfTokens = tokenizer.countTokens();


                while(tokenizer.hasMoreTokens())
                {
                    sourceFileName = tokenizer.nextToken();
                }


                /**
                for (String token : sourcePath.split("/")) {

                    System.err.println(token);
                }
                */
                String destPath = destFolderPath+"00"+String.valueOf(index+1)+" - "+sourceFileName;
                Toast.makeText(getApplicationContext(), destPath, Toast.LENGTH_SHORT).show();
            }
        }


    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
