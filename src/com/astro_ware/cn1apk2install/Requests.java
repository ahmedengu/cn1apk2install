package com.astro_ware.cn1apk2install;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;

/**
 * Created by ahmedengu.
 */
public class Requests {
    public static ConnectionRequest login(String email, String password) {
        ConnectionRequest request = new ConnectionRequest("https://www.codenameone.com/calls?m=login&email=" + email + "&password=" + Util.encodeUrl(password), true);
        request.setDisposeOnCompletion(new InfiniteProgress().showInifiniteBlocking());
        NetworkManager.getInstance().addToQueueAndWait(request);
        return request;
    }

    public static ConnectionRequest pollSync(String email, String password) {
        ConnectionRequest request = new ConnectionRequest("https://www.codenameone.com/calls?m=updateBuildStatus&email=" + email + "&password=" + Util.encodeUrl(password), true);
        request.setDisposeOnCompletion(new InfiniteProgress().showInifiniteBlocking());

        NetworkManager.getInstance().addToQueueAndWait(request);
        return request;
    }

    public static ConnectionRequest pollAsync(String email, String password) {
        ConnectionRequest request = new ConnectionRequest("https://www.codenameone.com/calls?m=updateBuildStatus&email=" + email + "&password=" + Util.encodeUrl(password), true);
        NetworkManager.getInstance().addToQueue(request);
        return request;
    }
}
