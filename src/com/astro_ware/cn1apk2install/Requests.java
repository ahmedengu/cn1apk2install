package com.astro_ware.cn1apk2install;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;

/**
 * Created by ahmedengu.
 */
public class Requests {
    public static ConnectionRequest login(String email, String password) {
        ConnectionRequest request = new ConnectionRequest("https://www.codenameone.com/calls?m=login&email=" + email + "&password=" + password, true);
        request.setDisposeOnCompletion(new InfiniteProgress().showInifiniteBlocking());
        NetworkManager.getInstance().addToQueueAndWait(request);
        return request;
    }

    public static ConnectionRequest pollSync(String email, String password) {
        ConnectionRequest request = new ConnectionRequest("https://www.codenameone.com/calls?m=updateBuildStatus&email=" + email + "&password=" + password, true);
        request.setDisposeOnCompletion(new InfiniteProgress().showInifiniteBlocking());

        NetworkManager.getInstance().addToQueueAndWait(request);
        return request;
    }
}
