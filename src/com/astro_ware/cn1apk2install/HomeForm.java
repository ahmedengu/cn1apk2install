package com.astro_ware.cn1apk2install;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.components.InfiniteProgress;
import com.codename1.io.*;
import com.codename1.ui.*;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.MultiList;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.util.FailureCallback;
import com.codename1.util.SuccessCallback;

import java.util.*;

public class HomeForm extends com.codename1.ui.Form {
    public HomeForm() {
        this(com.codename1.ui.util.Resources.getGlobalResources());

    }

    public HomeForm(com.codename1.ui.util.Resources resourceObjectInstance) {
        if (Preferences.get("email", "").equals("")) {
            Preferences.clearAll();
            new LoginForm().show();
            return;
        }

        initGuiBuilderComponents(resourceObjectInstance);
        gui_Email.setText(Preferences.get("email", "") + ", Logout");
        pollToList(Requests.pollSync(Preferences.get("email", ""), Preferences.get("password", "")));
        gui_Apps.addPullToRefresh(() -> {
            ConnectionRequest request = Requests.pollAsync(Preferences.get("email", ""), Preferences.get("password", ""));
            request.addResponseListener(evt -> pollToList(request));
        });
    }

    private void onEmailActionEvent(ActionEvent ev) {
        Preferences.clearAll();
        new LoginForm().show();
    }

    private void onPollActionEvent(ActionEvent ev) {
        ConnectionRequest request = Requests.pollSync(Preferences.get("email", ""), Preferences.get("password", ""));
        pollToList(request);
    }

    private void pollToList(ConnectionRequest request) {
        try {

            JSONArray array = new JSONArray(new String(request.getResponseData()));
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
                    entry.put("Line1", (object.getString("appTitle").equals("null")) ? "App" : object.getString("appTitle"));
                    entry.put("Line2", (object.getString("appTitle").equals("null")) ? "inProgress" : object.getJSONArray("otaInstallLinks").getString(0));
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
        if (line2.indexOf("It may be a connection problem") == -1 && line2.indexOf("inProgress") == -1) {
            ConnectionRequest request = new ConnectionRequest(line2) {
                @Override
                protected void postResponse() {

                    Display.getInstance().execute(FileSystemStorage.getInstance().getAppHomePath() + line2.substring(line2.lastIndexOf('/') + 1));
                }


            };
            request.setPost(false);
            request.setDestinationStorage(line2.substring(line2.lastIndexOf('/') + 1));
            request.setFailSilently(true);
            TB.showConnectionProgress("Downloading!", request, null, null);

            NetworkManager.getInstance().addToQueue(request);
        }
    }

    //-- DON'T EDIT BELOW THIS LINE!!!
    private com.codename1.ui.Container gui_Container_1 = new com.codename1.ui.Container(new com.codename1.ui.layouts.BoxLayout(com.codename1.ui.layouts.BoxLayout.Y_AXIS));
    private com.codename1.ui.Button gui_Email = new com.codename1.ui.Button();
    private com.codename1.ui.list.MultiList gui_Apps = new com.codename1.ui.list.MultiList();


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void guiBuilderBindComponentListeners() {
        EventCallbackClass callback = new EventCallbackClass();
        gui_Email.addActionListener(callback);
        gui_Apps.addActionListener(callback);
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

            if (sourceComponent == gui_Email) {
                onEmailActionEvent(ev);
            }
            if (sourceComponent == gui_Apps) {
                onAppsActionEvent(ev);
            }
        }

        public void dataChanged(int type, int index) {
        }
    }

    private void initGuiBuilderComponents(com.codename1.ui.util.Resources resourceObjectInstance) {
        guiBuilderBindComponentListeners();
        setLayout(new com.codename1.ui.layouts.BorderLayout());
        setTitle("Home");
        setName("HomeForm");
        addComponent(com.codename1.ui.layouts.BorderLayout.NORTH, gui_Container_1);
        gui_Container_1.setName("Container_1");
        gui_Container_1.addComponent(gui_Email);
        gui_Email.setText("Email");
        gui_Email.setName("Email");
        addComponent(com.codename1.ui.layouts.BorderLayout.CENTER, gui_Apps);
        gui_Container_1.setName("Container_1");
        gui_Apps.setName("Apps");
    }// </editor-fold>

//-- DON'T EDIT ABOVE THIS LINE!!!
}


class TB {


    private int position = Component.BOTTOM;

    /**
     * The default UIID that to be used for the {@code ToastBar} component.  This is the
     * style of the box that appears at the bottom of the screen.
     */
    private String defaultUIID = "ToastBar";

    /**
     * The default UIID that is to be used for the text in the {@code ToastBar}.
     */
    private String defaultMessageUIID = "ToastBarMessage";


    //FIXME SH Need to style the {@code ToastBar} so that it looks nicer

    private static TB instance;

    /**
     * Gets reference to the singleton StatusBar instance
     *
     * @return
     */
    public static TB getInstance() {
        if (instance == null) {
            instance = new TB();

        }
        return instance;
    }

    private TB() {

    }


    /**
     * Keeps track of the currently active status messages.
     */
    private final ArrayList<Status> statuses = new ArrayList<Status>();

    /**
     * Gets the default UIID to be used for the style of the {@code ToastBar} component.
     * By default this is "ToastBarComponent".
     *
     * @return the defaultUIID
     */
    public String getDefaultUIID() {
        return defaultUIID;
    }

    /**
     * Sets the defaults UIID to be used for the style of the {@code ToastBar} component.  By default
     * this is "ToastBarComponent"
     *
     * @param defaultUIID the defaultUIID to set
     */
    public void setDefaultUIID(String defaultUIID) {
        this.defaultUIID = defaultUIID;
    }

    /**
     * Gets the default UIID to be used for the style of the {@code ToastBar} text.  By default
     * this is "ToastBarMessage"
     *
     * @return the defaultMessageUIID
     */
    public String getDefaultMessageUIID() {
        return defaultMessageUIID;
    }

    /**
     * Sets the default UIID to be used for the style of the {@code ToastBar} text.  By default this is
     * "ToastBarMessage"
     *
     * @param defaultMessageUIID the defaultMessageUIID to set
     */
    public void setDefaultMessageUIID(String defaultMessageUIID) {
        this.defaultMessageUIID = defaultMessageUIID;
    }

    /**
     * Gets the position of the toast bar on the screen.  Either {@link Component#TOP} or {@link Component#BOTTOM}.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position of the toast bar on the screen.
     *
     * @param position the position to set Should be one of {@link Component#TOP} and {@link Component#BOTTOM}
     */
    public void setPosition(int position) {
        this.position = position;
    }


    /**
     * Represents a single status message.
     */
    public class Status {

        /**
         * This UIID that should be used to style the ToastBar text while this
         * message is being displayed.
         */
        private String messageUIID = defaultMessageUIID;

        /**
         * The UIID that should be used to style the ToastBar component while
         * this message is being displayed.
         */
        private String uiid = defaultUIID;


        /**
         * The start time of the process this status is tracking.
         */
        private final long startTime;

        /**
         * Timer used to "expire" the message after a certain time.
         *
         * @see #setExpires(int)
         */
        private Timer timer;
        /**
         * Timer used to delay the showing of the message.  Useful if you only want
         * to show the message if the task ends up taking a long time.
         *
         * @see #showDelayed(int)
         */
        private Timer showTimer;

        /**
         * The message to be displayed in the {@code ToastBar}.
         */
        private String message;

        /**
         * Optional progress for the task.  (Not tested or implemented yet).
         */
        private int progress = -2;

        /**
         * Optional icon to show in the {@code ToastBar}.  (Not tested or implemented yet).
         */
        private Image icon;

        /**
         * Whether this status message should show an infinite progress indicator. (e.g. spinning beachball).
         */
        private boolean showProgressIndicator;

        private Status() {
            startTime = System.currentTimeMillis();
        }

        /**
         * Directs the status to be cleared (if it isn't already cleared() after a given number of milliseconds.
         *
         * @param millis The maximum number of milliseconds that the status message should be displayed for.
         *               Helpful for error messages that only need to be displayed for a few seconds.
         */
        public void setExpires(int millis) {
            if (millis < 0 && timer != null) {
                timer.cancel();
                timer = null;
            } else if (millis > 0) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Display.getInstance().callSerially(new Runnable() {
                            public void run() {
                                timer = null;
                                Status.this.clear();
                            }
                        });
                    }

                }, millis);
            }
        }

        /**
         * Sets the message that should be displayed in the {@code ToastBar}.
         *
         * @param message
         */
        public void setMessage(String message) {
            this.message = message;

        }

        /**
         * Sets the progress (-1..100) that should be displayed in the progress bar
         * for this status.  When set to -1 it will act as an infinite progress
         *
         * @param progress
         */
        public void setProgress(int progress) {
            this.progress = progress;
            updateStatus();
        }

        /**
         * Shows this status message.  Call this method after making any changes
         * to the status that you want to have displayed.  This will always cause
         * any currently-displayed status to be replaced by this status.
         * <p>If you don't want to show the status immediately, but rather to wait some delay, you can use
         * the {@link #showDelayed(int) } method instead.</p>
         *
         * @see #showDelayed(int)
         */
        public void show() {
            if (showTimer != null) {
                showTimer.cancel();
                showTimer = null;
            }
            ToastBarComponent c = getToastBarComponent();
            if (c != null) {
                c.currentlyShowing = this;
                updateStatus();
                setVisible(true);

            }
        }

        /**
         * Schedules this status message to be shown after a specified number of milliseconds,
         * if it hasn't been cleared or shown first.
         * <p>This is handy if you want to show an status for an operation that usually completes very quickly, but could
         * potentially hang.  In such a case you might decide not to display a status message at all unless the operation
         * takes more than 500ms to complete.</p>
         * <p>
         * <p>If you want to show the status immediately, use the {@link #show() } method instead.</p>
         *
         * @param millis Number of milliseconds to wait before showing the status.
         */
        public void showDelayed(int millis) {
            showTimer = new Timer();
            showTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Display.getInstance().callSerially(new Runnable() {
                        public void run() {
                            if (showTimer != null) {
                                showTimer = null;
                                show();
                            }
                        }
                    });
                }

            }, millis);
        }

        /**
         * Clears this status message. This any pending "showDelayed" requests for this status.
         */
        public void clear() {
            if (showTimer != null) {
                showTimer.cancel();
                showTimer = null;
            }
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            removeStatus(this);
        }

        /**
         * Returns the text that will be displayed for this status.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns the progress of this status.  A value of -1 indicates that the progress
         * bar should not be shown.  Values between 0 and 100 inclusive will be rendered
         * on a progress bar (slider) in the status component.
         *
         * @return the progress
         */
        public int getProgress() {
            return progress;
        }

        /**
         * Gets the icon (may be null) that is displayed with this status.
         *
         * @return the icon
         */
        public Image getIcon() {
            return icon;
        }

        /**
         * Sets the icon that is to be displayed with this status.  Set this to null to not show an icon.
         *
         * @param icon the icon to set
         */
        public void setIcon(Image icon) {
            this.icon = icon;
        }

        /**
         * @return the showProgressIndicator
         */
        public boolean isShowProgressIndicator() {
            return showProgressIndicator;
        }

        /**
         * Sets whether this status message should include an infinite progress indicator (e.g. spinning beach ball).
         *
         * @param showProgressIndicator the showProgressIndicator to set
         */
        public void setShowProgressIndicator(boolean showProgressIndicator) {
            this.showProgressIndicator = showProgressIndicator;
        }

        /**
         * Gets the UIID to use for styling the text of this status message.
         *
         * @return the messageUIID
         */
        public String getMessageUIID() {
            return messageUIID;
        }

        /**
         * Sets the UIID to use for styling the text of this status message.
         *
         * @param messageUIID the messageUIID to set
         */
        public void setMessageUIID(String messageUIID) {
            this.messageUIID = messageUIID;
        }

        /**
         * Gets the UIID that should be used for styling the status component while
         * this status is displayed.
         *
         * @return the uiid
         */
        public String getUiid() {
            return uiid;
        }

        /**
         * Sets the UIID that should be used for styling the status component while
         * this status is displayed.
         *
         * @param uiid the uiid to set
         */
        public void setUiid(String uiid) {
            this.uiid = uiid;
        }

    }

    /**
     * Flag to indicate that the status is updating.  This is used to prevent
     * two status updates from happening at the same time.
     */
    private boolean updatingStatus;

    /**
     * Flag to indicate that a request to update the status was received while
     * updateStatus() was running.
     */
    private boolean pendingUpdateStatus;


    /**
     * Updates the ToastBar UI component with the settings of the current status.
     */
    private void updateStatus() {
        ToastBarComponent c = getToastBarComponent();
        if (c != null) {
            if (updatingStatus) {
                pendingUpdateStatus = true;
                return;
            }
            try {
                updatingStatus = true;
                if (c.currentlyShowing != null && !statuses.contains(c.currentlyShowing)) {
                    c.currentlyShowing = null;
                }
                if (c.currentlyShowing == null || statuses.isEmpty()) {
                    if (!statuses.isEmpty()) {
                        c.currentlyShowing = statuses.get(statuses.size() - 1);
                    } else {
                        setVisible(false);
                        return;
                    }

                }
                Status s = c.currentlyShowing;

                Label l = new Label(s.getMessage() != null ? s.getMessage() : "");

                c.progressLabel.setVisible(s.isShowProgressIndicator());
                if (c.progressLabel.isVisible()) {
                    if (!c.contains(c.progressLabel)) {
                        c.addComponent(BorderLayout.EAST, c.progressLabel);
                    }
                    Image anim = c.progressLabel.getAnimation();
                    if (anim != null && anim.getWidth() > 0) {
                        c.progressLabel.setWidth(anim.getWidth());
                    }
                    if (anim != null && anim.getHeight() > 0) {
                        c.progressLabel.setHeight(anim.getHeight());
                    }
                } else {
                    if (c.contains(c.progressLabel)) {
                        c.removeComponent(c.progressLabel);
                    }
                }
                c.progressBar.setVisible(s.getProgress() >= -1);
                if (s.getProgress() >= -1) {
                    if (!c.contains(c.progressBar)) {
                        c.addComponent(BorderLayout.SOUTH, c.progressBar);
                    }
                    if (s.getProgress() < 0) {
                        c.progressBar.setInfinite(true);
                    } else {
                        c.progressBar.setInfinite(false);
                        c.progressBar.setProgress(s.getProgress());
                    }
                } else {
                    c.removeComponent(c.progressBar);
                }
                c.icon.setVisible(s.getIcon() != null);
                if (s.getIcon() != null && c.icon.getIcon() != s.getIcon()) {
                    c.icon.setIcon(s.getIcon());
                }
                if (s.getIcon() == null && c.contains(c.icon)) {
                    c.removeComponent(c.icon);
                } else if (s.getIcon() != null && !c.contains(c.icon)) {

                    c.addComponent(BorderLayout.WEST, c.icon);
                }
                String oldText = c.label.getText();

                if (!oldText.equals(l.getText())) {


                    if (s.getUiid() != null) {
                        c.setUIID(s.getUiid());
                    } else if (defaultUIID != null) {
                        c.setUIID(defaultUIID);
                    }

                    if (c.isVisible()) {
                        TextArea newLabel = new TextArea();
                        //newLabel.setColumns(l.getText().length()+1);
                        //newLabel.setRows(l.getText().length()+1);
                        newLabel.setFocusable(false);
                        newLabel.setEditable(false);

                        //newLabel.getAllStyles().setFgColor(0xffffff);
                        if (s.getMessageUIID() != null) {
                            newLabel.setUIID(s.getMessageUIID());
                        } else if (defaultMessageUIID != null) {
                            newLabel.setUIID(defaultMessageUIID);
                        } else {
                            newLabel.setUIID(c.label.getUIID());
                        }
                        if (s.getUiid() != null) {
                            c.setUIID(s.getUiid());
                        } else if (defaultUIID != null) {
                            c.setUIID(defaultUIID);
                        }
                        newLabel.setWidth(c.label.getWidth());

                        newLabel.setText(l.getText());

                        Dimension oldTextAreaSize = UIManager.getInstance().getLookAndFeel().getTextAreaSize(c.label, true);
                        Dimension newTexAreaSize = UIManager.getInstance().getLookAndFeel().getTextAreaSize(newLabel, true);

                        c.label.getParent().replaceAndWait(c.label, newLabel, CommonTransitions.createCover(CommonTransitions.SLIDE_VERTICAL, true, 300));
                        c.label = newLabel;

                        if (oldTextAreaSize.getHeight() != newTexAreaSize.getHeight()) {

                            c.label.setPreferredH(newTexAreaSize.getHeight());
                            c.getParent().animateHierarchyAndWait(300);
                        }


                    } else {
                        if (s.getMessageUIID() != null) {
                            c.label.setUIID(s.getMessageUIID());
                        } else if (defaultMessageUIID != null) {
                            c.label.setUIID(defaultMessageUIID);
                        }
                        if (s.getUiid() != null) {
                            c.setUIID(s.getUiid());
                        } else if (defaultUIID != null) {
                            c.setUIID(defaultUIID);
                        }
                        c.label.setText(l.getText());
                        //c.label.setColumns(l.getText().length()+1);
                        //c.label.setRows(l.getText().length()+1);
                        c.label.setPreferredW(c.getWidth());
                        c.revalidate();
                    }
                } else {
                    c.revalidate();
                }
            } finally {
                updatingStatus = false;
                if (pendingUpdateStatus) {
                    pendingUpdateStatus = false;
                    Display.getInstance().callSerially(new Runnable() {
                        public void run() {
                            updateStatus();
                        }
                    });
                }
            }
        }
    }

    /**
     * The actual component for the {@code ToastBar}.  This is added to the layered pane of
     * the top-level form.
     */
    private class ToastBarComponent extends Container {
        private TextArea label;
        private InfiniteProgress progressLabel;
        private Slider progressBar;
        private Label icon;
        private Status currentlyShowing;
        boolean hidden = true;
        Button leadButton = new Button();

        public ToastBarComponent() {
            this.getAllStyles().setBgColor(0x0);
            this.getAllStyles().setBackgroundType(Style.BACKGROUND_NONE);
            this.getAllStyles().setBgTransparency(128);
            setVisible(false);
            label = new TextArea();
            label.setEditable(false);
            label.setFocusable(false);


            progressLabel = new InfiniteProgress();

            progressLabel.setAngleIncrease(4);
            progressLabel.setVisible(false);
            icon = new Label();
            icon.setVisible(false);
            progressBar = new Slider();
            progressBar.setVisible(false);

            leadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (currentlyShowing != null && !currentlyShowing.showProgressIndicator) {
                        currentlyShowing.clear();
                    }
                    TB.this.setVisible(false);
                }
            });
            leadButton.setVisible(false);

            this.setLeadComponent(leadButton);

            setLayout(new BorderLayout());
            addComponent(BorderLayout.WEST, icon);
            addComponent(BorderLayout.CENTER, label);
            addComponent(BorderLayout.SOUTH, progressBar);
            addComponent(BorderLayout.EAST, progressLabel);

            progressBar.setVisible(false);
        }

        @Override
        protected Dimension calcPreferredSize() {
            if (hidden) {
                return new Dimension(Display.getInstance().getDisplayWidth(),
                        0
                );
            } else {
                return super.calcPreferredSize();
                /*
                return new Dimension(Display.getInstance().getDisplayWidth(),
                        Display.getInstance().convertToPixels(10, false)
                );*/
            }
        }
    }

    /**
     * Creates a new Status.
     *
     * @return
     */
    public Status createStatus() {
        Status s = new Status();
        statuses.add(s);
        return s;
    }

    private void removeStatus(Status status) {
        if (status.timer != null) {
            status.timer.cancel();
            status.timer = null;
        }
        statuses.remove(status);
        updateStatus();
    }

    private ToastBarComponent getToastBarComponent() {
        Form f = Display.getInstance().getCurrent();
        if (f != null && !(f instanceof Dialog)) {
            ToastBarComponent c = (ToastBarComponent) f.getClientProperty("ToastBarComponent");
            if (c == null || c.getParent() == null) {
                c = new ToastBarComponent();
                c.hidden = true;
                f.putClientProperty("ToastBarComponent", c);
                Container layered = f.getLayeredPane(this.getClass(), true);
                layered.setLayout(new BorderLayout());
                layered.addComponent(position == Component.TOP ? BorderLayout.NORTH : BorderLayout.SOUTH, c);
                updateStatus();
            }
            return c;
        }
        return null;
    }

    /**
     * Shows or hides the {@code ToastBar}.
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        ToastBarComponent c = getToastBarComponent();
        if (c == null || c.isVisible() == visible) {
            return;
        }
        if (visible) {
            c.setVisible(true);
            c.label.setPreferredH(UIManager.getInstance().getLookAndFeel().getTextAreaSize(c.label, true).getHeight());
            Container layered = c.getParent();
            c.hidden = true;
            layered.revalidate();
            c.hidden = false;
            layered.animateHierarchyAndWait(1000);
            updateStatus();
        } else {
            Form f = c.getComponentForm();
            Container layered = c.getParent();
            c.hidden = true;
            if (Display.getInstance().getCurrent() == f && !f.getMenuBar().isMenuShowing()) {
                layered.animateHierarchyAndWait(1000);
            } else {
                layered.revalidate();
            }
            c.setVisible(false);
        }
    }

    /**
     * Simplifies a common use case of showing an error message with an error icon that fades out after a few seconds
     *
     * @param msg the error message
     */
    public static void showErrorMessage(String msg) {
        showErrorMessage(msg, 3500);
    }

    /**
     * Simplifies a common use case of showing a message with an icon that fades out after a few seconds
     *
     * @param msg     the message
     * @param icon    the material icon to show from {@link com.codename1.ui.FontImage}
     * @param timeout the timeout value in milliseconds
     */
    public static void showMessage(String msg, char icon, int timeout) {
        TB.Status s = TB.getInstance().createStatus();
        Style stl = UIManager.getInstance().getComponentStyle(s.getMessageUIID());
        s.setIcon(FontImage.createMaterial(icon, stl, 4));
        s.setMessage(msg);
        s.setExpires(timeout);
        s.show();
    }

    /**
     * Simplifies a common use case of showing an error message with an error icon that fades out after a few seconds
     *
     * @param icon the material icon to show from {@link com.codename1.ui.FontImage}
     * @param msg  the message
     */
    public static void showMessage(String msg, char icon) {
        showMessage(msg, icon, 3500);
    }

    /**
     * Simplifies a common use case of showing an error message with an error icon that fades out after a few seconds
     *
     * @param msg     the error message
     * @param timeout the timeout value in milliseconds
     */
    public static void showErrorMessage(String msg, int timeout) {
        showMessage(msg, FontImage.MATERIAL_ERROR, timeout);
    }

    /*
     * Shows a progress indicator based on connection request, this is incomplete but it meant to serve as
     * a replacement for the inifinte progress
     *
     * @param message a message to show on the progress indicator
     * @param cr the connection request whose progress should be shown
     * @param onSuccess invoked when the connection request completes, can be null
     * @param onError invoked on case of an error, can be null
     */
    public static void showConnectionProgress(String message, final ConnectionRequest cr,
                                              final SuccessCallback<NetworkEvent> onSuccess, final FailureCallback<NetworkEvent> onError) {
        final TB.Status s = TB.getInstance().createStatus();
        s.setProgress(-1);
        s.setMessage(message);
        s.show();
        final ActionListener[] progListener = new ActionListener[1];
        final ActionListener<NetworkEvent> errorListener = new ActionListener<NetworkEvent>() {
            public void actionPerformed(NetworkEvent evt) {
                s.clear();
                NetworkManager.getInstance().removeErrorListener(this);
                if (progListener[0] != null) {
                    NetworkManager.getInstance().removeProgressListener(progListener[0]);
                }
                if (onError != null) {
                    onError.onError(cr, evt.getError(), evt.getResponseCode(), evt.getMessage());
                }
            }
        };
        NetworkManager.getInstance().addErrorListener(errorListener);
        progListener[0] = new ActionListener<NetworkEvent>() {
            private int soFar;

            public void actionPerformed(NetworkEvent evt) {
                switch (evt.getProgressType()) {
                    case NetworkEvent.PROGRESS_TYPE_INITIALIZING:
                        s.setProgress(-1);
                        break;
                    case NetworkEvent.PROGRESS_TYPE_INPUT:
                    case NetworkEvent.PROGRESS_TYPE_OUTPUT:
                        int currentLength = cr.getContentLength();
                        if (currentLength > 0) {
                            int sentReceived = evt.getSentReceived();
                            float prog = ((float) sentReceived) / ((float) currentLength) * 100f;
                            s.setProgress((int) prog);
                        } else {
                            s.setProgress(-1);
                        }
                }
            }
        };
        cr.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                NetworkManager.getInstance().removeErrorListener(errorListener);
                NetworkManager.getInstance().removeProgressListener(progListener[0]);
                s.clear();
                if (onSuccess != null && (cr.getResponseCode() == 200 || cr.getResponseCode() == 202)) {
                    onSuccess.onSucess(evt);
                }
            }
        });
        NetworkManager.getInstance().addProgressListener(progListener[0]);
    }
}
