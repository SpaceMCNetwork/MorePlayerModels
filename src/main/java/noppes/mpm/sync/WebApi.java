/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.Minecraft
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.world.entity.player.Player
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.entity.UrlEncodedFormEntity
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.message.BasicNameValuePair
 */
package noppes.mpm.sync;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.player.Player;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.util.GzipUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class WebApi {
    private final String baseUrl = "http://vps.nopapi.nl";
    private final HttpClient client = HttpClientBuilder.create().build();
    private final JsonParser parser = new JsonParser();
    private long playerLastUpdated = 0L;
    private int errors = 0;
    public static final WebApi instance = new WebApi();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public boolean isRunning = false;

    public void run() {
        Minecraft mc = Minecraft.getInstance();
        if (MorePlayerModels.HasServerSide) {
            return;
        }
        if (this.errors > 3 || this.isRunning) {
            return;
        }
        HashMap<String, ModelData> map = new HashMap<String, ModelData>();
        StringBuilder initBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();
        StringBuilder updateBuilderTS = new StringBuilder();
        for (Player player : ClientEventHandler.playerList) {
            ModelData data = ModelData.get(player);
            if (!data.webapiInit) {
                if (initBuilder.length() > 0) {
                    initBuilder.append(";");
                }
                initBuilder.append(player.getUUID());
                map.put(player.getUUID().toString(), data);
                data.webapiInit = true;
                continue;
            }
            if (!data.webapiActive) continue;
            if (updateBuilder.length() > 0) {
                updateBuilder.append(";");
                updateBuilderTS.append(";");
            }
            updateBuilder.append(player.getUUID());
            updateBuilderTS.append(data.lastEdited);
            map.put(player.getUUID().toString(), data);
        }
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        if (initBuilder.length() > 0) {
            urlParameters.add(new BasicNameValuePair("init", initBuilder.toString()));
        }
        if (updateBuilder.length() > 0) {
            urlParameters.add(new BasicNameValuePair("update", updateBuilder.toString()));
            urlParameters.add(new BasicNameValuePair("update_timestamps", updateBuilderTS.toString()));
        }
        ModelData pdata = ModelData.get((Player)mc.player);
        if (pdata.lastEdited > this.playerLastUpdated) {
            this.playerLastUpdated = pdata.lastEdited;
            CompoundTag comp = pdata.writeToNBT();
            comp.remove("EntityClass");
            try {
                urlParameters.add(new BasicNameValuePair("player", pdata.player.getUUID().toString()));
                urlParameters.add(new BasicNameValuePair("player_lastedit", "" + pdata.lastEdited));
                urlParameters.add(new BasicNameValuePair("player_data", GzipUtil.compressToString(pdata.writeToNBT().toString())));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (urlParameters.isEmpty()) {
            return;
        }
        this.isRunning = true;
        this.executor.submit(() -> {
            String data = null;
            try {
                data = this.doRequest(urlParameters);
                if (data.isEmpty()) {
                    return;
                }
                JsonObject obj = (JsonObject)this.parser.parse(data);
                for (Map.Entry ent : obj.entrySet()) {
                    ModelData mdata = (ModelData)map.get(ent.getKey());
                    mdata.readFromNBT(TagParser.parseTag((String)GzipUtil.decompressFromString(((JsonElement)ent.getValue()).getAsString())));
                    mdata.webapiActive = true;
                }
            }
            catch (Exception e) {
                if (data != null) {
                    LogWriter.warn(data);
                }
                LogWriter.except(e);
                ++this.errors;
            }
            finally {
                this.isRunning = false;
            }
        });
    }

    public String doRequest(List<NameValuePair> urlParameters) throws IOException {
        HttpPost post = new HttpPost("http://vps.nopapi.nl");
        urlParameters.add((NameValuePair)new BasicNameValuePair("v", MorePlayerModels.VERSION));
        post.setEntity((HttpEntity)new UrlEncodedFormEntity(urlParameters, "utf-8"));
        HttpResponse response = this.client.execute((HttpUriRequest)post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        post.releaseConnection();
        return result.toString();
    }

    public static void main(String[] args) {
        WebApi api = new WebApi();
        ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add((NameValuePair)new BasicNameValuePair("init", "462374d0-c36d-43dc-a75b-79a913ab9d35;be951074-c7ea-4f02-a725-bf017bc88650"));
        urlParameters.add((NameValuePair)new BasicNameValuePair("update", "362374d0-c36d-43dc-a75b-79a913ab9d35;be951074-c7ea-4f02-a725-bf017bc88650"));
        urlParameters.add((NameValuePair)new BasicNameValuePair("update_timestamps", "0;0"));
        try {
            urlParameters.add((NameValuePair)new BasicNameValuePair("player", "462374d0-c36d-43dc-a75b-79a913ab9d35"));
            urlParameters.add((NameValuePair)new BasicNameValuePair("player_lastedit", "" + System.currentTimeMillis()));
            urlParameters.add((NameValuePair)new BasicNameValuePair("player_data", GzipUtil.compressToString(new ModelData().writeToNBT().toString())));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            String data = api.doRequest(urlParameters);
            System.out.println(data);
            JsonObject obj = (JsonObject)api.parser.parse(data);
            for (Map.Entry ent : obj.entrySet()) {
                System.out.println((String)ent.getKey());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
