package com.astro_ware.cn1apk2install;
public class LoginForm extends com.codename1.ui.Form {
    public LoginForm() {
        this(com.codename1.ui.util.Resources.getGlobalResources());
    }
    
    public LoginForm(com.codename1.ui.util.Resources resourceObjectInstance) {
        initGuiBuilderComponents(resourceObjectInstance);
    }
    
//-- DON'T EDIT BELOW THIS LINE!!!
    private com.codename1.ui.Container gui_Container_1 = new com.codename1.ui.Container(new com.codename1.ui.layouts.BoxLayout(com.codename1.ui.layouts.BoxLayout.Y_AXIS));
    private com.codename1.ui.TextField gui_Email = new com.codename1.ui.TextField();
    private com.codename1.ui.TextField gui_Password = new com.codename1.ui.TextField();
    private com.codename1.ui.Button gui_Login = new com.codename1.ui.Button();


// <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initGuiBuilderComponents(com.codename1.ui.util.Resources resourceObjectInstance) {
        setLayout(new com.codename1.ui.layouts.FlowLayout());
        setTitle("Login");
        setName("LoginForm");
        ((com.codename1.ui.layouts.FlowLayout)getLayout()).setAlign(com.codename1.ui.Component.CENTER);
        ((com.codename1.ui.layouts.FlowLayout)getLayout()).setValign(com.codename1.ui.Component.CENTER);
        addComponent(gui_Container_1);
        gui_Container_1.setName("Container_1");
        gui_Container_1.addComponent(gui_Email);
        gui_Container_1.addComponent(gui_Password);
        gui_Container_1.addComponent(gui_Login);
        gui_Email.setHint("Email");
        gui_Email.setName("Email");
        gui_Password.setHint("Password");
        gui_Password.setName("Password");
        gui_Login.setText("Login");
        gui_Login.setName("Login");
        gui_Container_1.setName("Container_1");
    }// </editor-fold>

//-- DON'T EDIT ABOVE THIS LINE!!!
}
