package com.lioncode.speed;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.lioncode.speed.com.lioncode.speed.Constants;

import java.util.ArrayList;
import java.util.List;

public class ServerActivity extends ListActivity{

    public static String RESULT_SERVER = "servercode";

    private List<Server> serverList;
    private String[] servernames, serverhosts, serverpings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateServerList();

        ArrayAdapter<Server> adapter = new ServerListArrayAdapter(this, serverList);

        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Server s = serverList.get(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESULT_SERVER, s.getHost());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void populateServerList(){
        serverList = new ArrayList<>();
        servernames = getResources().getStringArray(R.array.server_names);
        serverhosts = getResources().getStringArray(R.array.server_hosts);
        serverpings = getResources().getStringArray(R.array.server_pings);
        for (int i = 0; i < servernames.length; i++){
            serverList.add(new Server(servernames[i], serverhosts[i],
                    Constants.SPEED_TEST_SERVER_URI_DL, Constants.SPEED_TEST_SERVER_URI_UL,
                    serverpings[i]));
        }
    }

    public class Server {
        private String name;
        private String host;
        private String download;
        private String upload;
        private String ping;

        public Server(String name, String host, String download, String upload, String ping){
            this.name = name;
            this.host = host;
            this.download = download;
            this.upload = upload;
            this.ping = ping;
        }
        public String getName() {
            return name;
        }
        public String getHost() {
            return host;
        }
        public String getDownload() {
            return download;
        }
        public String getUpload() {
            return upload;
        }
        public String getPing() {
            return ping;
        }
    }
}
