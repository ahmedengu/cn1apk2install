package com.astro_ware.cn1apk2install;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Preferences;
import com.codename1.ui.Display;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.MultiList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeForm extends com.codename1.ui.Form {
    //-- DON'T EDIT BELOW THIS LINE!!!
    private com.codename1.ui.Container gui_Container_1 = new com.codename1.ui.Container(new com.codename1.ui.layouts.BoxLayout(com.codename1.ui.layouts.BoxLayout.Y_AXIS));
    private com.codename1.ui.Label gui_Email = new com.codename1.ui.Label();
    private com.codename1.ui.Button gui_Poll = new com.codename1.ui.Button();
    private com.codename1.ui.list.MultiList gui_Apps = new com.codename1.ui.list.MultiList();

    public HomeForm() {
        this(com.codename1.ui.util.Resources.getGlobalResources());
    }

    public HomeForm(com.codename1.ui.util.Resources resourceObjectInstance) {
        initGuiBuilderComponents(resourceObjectInstance);
        gui_Email.setText("Email: " + Preferences.get("email", ""));
        onPollActionEvent(null);
    }

    public void onPollActionEvent(ActionEvent ev) {
        ConnectionRequest request = Requests.pollSync(Preferences.get("email", ""), Preferences.get("password", ""));

        try {
            JSONArray array = new JSONArray(new String(request.getResponseData(), StandardCharsets.UTF_8));
            if (array.length() == 0) {
                ArrayList<Map<String, Object>> data = new ArrayList<>();
                Map<String, Object> entry = new HashMap<>();
                entry.put("Line1", "You have no builds");
                entry.put("Line2", "It may be a connection problem");
                data.add(entry);
                gui_Apps.setModel(new DefaultListModel<>(data));

            } else {
                ArrayList<Map<String, Object>> data = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    Map<String, Object> entry = new HashMap<>();
                    JSONObject object = array.getJSONObject(i);
                    entry.put("Line1", object.getString("appTitle"));
                    entry.put("Line2", object.getJSONArray("otaInstallLinks").getString(0));
                    data.add(entry);
                }

                gui_Apps.setModel(new DefaultListModel<>(data));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onAppsActionEvent(ActionEvent ev) {
        String line2 = ((Map<String, Object>) gui_Apps.getModel().getItemAt(((MultiList) ev.getSource()).getSelectedIndex())).get("Line2").toString();
        if (!line2.contains("It may be a connection problem"))
            Display.getInstance().execute(line2);

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void guiBuilderBindComponentListeners() {
        EventCallbackClass callback = new EventCallbackClass();
        gui_Poll.addActionListener(callback);
        gui_Apps.addActionListener(callback);
    }

    private void initGuiBuilderComponents(com.codename1.ui.util.Resources resourceObjectInstance) {
        guiBuilderBindComponentListeners();
        setLayout(new com.codename1.ui.layouts.BorderLayout());
        setTitle("HomeForm");
        setName("HomeForm");
        addComponent(com.codename1.ui.layouts.BorderLayout.NORTH, gui_Container_1);
        gui_Container_1.setName("Container_1");
        gui_Container_1.addComponent(gui_Email);
        gui_Container_1.addComponent(gui_Poll);
        gui_Email.setText("Email");
        gui_Email.setName("Email");
        gui_Poll.setText("Poll");
        gui_Poll.setName("Poll");
        addComponent(com.codename1.ui.layouts.BorderLayout.CENTER, gui_Apps);
        gui_Container_1.setName("Container_1");
        gui_Apps.setName("Apps");
    }// </editor-fold>

    class EventCallbackClass implements com.codename1.ui.events.ActionListener, com.codename1.ui.events.DataChangedListener {
        private com.codename1.ui.Component cmp;

        public EventCallbackClass(com.codename1.ui.Component cmp) {
            this.cmp = cmp;
        }

        public EventCallbackClass() {
        }

        public void actionPerformed(com.codename1.ui.events.ActionEvent ev) {
            com.codename1.ui.Component sourceComponent = ev.getComponent();
            if (sourceComponent.getParent().getLeadParent() != null) {
                sourceComponent = sourceComponent.getParent().getLeadParent();
            }

            if (sourceComponent == gui_Poll) {
                onPollActionEvent(ev);
            }
            if (sourceComponent == gui_Apps) {
                onAppsActionEvent(ev);
            }
        }

        public void dataChanged(int type, int index) {
        }
    }

//-- DON'T EDIT ABOVE THIS LINE!!!
}
