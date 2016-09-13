package com.astro_ware.cn1apk2install;

import com.codename1.components.ToastBar;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Preferences;
import com.codename1.ui.events.ActionEvent;

public class LoginForm extends com.codename1.ui.Form {
    public LoginForm() {
        this(com.codename1.ui.util.Resources.getGlobalResources());
    }

    public LoginForm(com.codename1.ui.util.Resources resourceObjectInstance) {
        initGuiBuilderComponents(resourceObjectInstance);
    }

    private void onLoginActionEvent(ActionEvent ev) {

        ConnectionRequest request = Requests.login(gui_Email.getText(), gui_Password.getText());

        String str = new String(request.getResponseData());

        if (request.getResponseCode() == 200 && str.indexOf("\"Error\":") == -1 && str.indexOf(gui_Email.getText()) != -1) {
            ToastBar.getInstance().showErrorMessage("loggedin");

            Preferences.set("email", gui_Email.getText());
            Preferences.set("password", gui_Password.getText());
            new HomeForm().show();

        } else
            ToastBar.getInstance().showErrorMessage("error");

    }


    //-- DON'T EDIT BELOW THIS LINE!!!
    private com.codename1.ui.Container gui_Container_1 = new com.codename1.ui.Container(new com.codename1.ui.layouts.BoxLayout(com.codename1.ui.layouts.BoxLayout.Y_AXIS));
    private com.codename1.ui.TextField gui_Email = new com.codename1.ui.TextField();
    private com.codename1.ui.TextField gui_Password = new com.codename1.ui.TextField();
    private com.codename1.ui.Button gui_Login = new com.codename1.ui.Button();


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void guiBuilderBindComponentListeners() {
        EventCallbackClass callback = new EventCallbackClass();
        gui_Login.addActionListener(callback);
    }

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

            if (sourceComponent == gui_Login) {
                onLoginActionEvent(ev);
            }
        }

        public void dataChanged(int type, int index) {
        }
    }
    private void initGuiBuilderComponents(com.codename1.ui.util.Resources resourceObjectInstance) {
        guiBuilderBindComponentListeners();
        setLayout(new com.codename1.ui.layouts.FlowLayout());
        setTitle("Login");
        setName("LoginForm");
        ((com.codename1.ui.layouts.FlowLayout) getLayout()).setAlign(com.codename1.ui.Component.CENTER);
        ((com.codename1.ui.layouts.FlowLayout) getLayout()).setValign(com.codename1.ui.Component.CENTER);
        addComponent(gui_Container_1);
        gui_Container_1.setName("Container_1");
        gui_Container_1.addComponent(gui_Email);
        gui_Container_1.addComponent(gui_Password);
        gui_Container_1.addComponent(gui_Login);
        gui_Email.setHint("Email");
        gui_Email.setName("Email");
        gui_Email.setConstraint(com.codename1.ui.TextArea.EMAILADDR);
        gui_Password.setHint("Password");
        gui_Password.setName("Password");
        gui_Password.setConstraint(com.codename1.ui.TextArea.PASSWORD);
        gui_Login.setText("Login");
        gui_Login.setName("Login");
        gui_Container_1.setName("Container_1");
    }// </editor-fold>

//-- DON'T EDIT ABOVE THIS LINE!!!
}
