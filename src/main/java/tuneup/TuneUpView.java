/*
 * TuneUpView.java
 */
package tuneup;

import org.jdesktop.application.Action;
import org.jdesktop.application.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The application's main frame.
 */
public class TuneUpView extends FrameView {

    private int MT;

    private ExecutorService service;

    private List<Runnable> task;

    private AtomicInteger requestsMade = new AtomicInteger();

    public TuneUpView(SingleFrameApplication app) {
        super(app);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }

        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }

        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }

        });
        //
        getApplication().getContext().getTaskService().execute(updateAction());
    }

    protected void incRequestsMade() {
        requestsMade.incrementAndGet();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = TuneUpApp.getApplication().getMainFrame();
            aboutBox = new TuneUpAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        TuneUpApp.getApplication().show(aboutBox);
    }

    @Action
    public Task updateAction() {
        return new Task<Void, Void>(getApplication()) {

            @Override
            protected Void doInBackground() throws Exception {
                while (!isCancelled()) {
                    requestsMadeTextField.setText("" + requestsMade.intValue());
                    try {
                        Thread.sleep(1000 * 10);
                    } catch (InterruptedException e) {
                    }
                }
                return null;
            }

        };
    }

    @Action
    public Task startAction() {
        if (null != task) {
            service.shutdownNow();
            for (Runnable r : task) {
                ((TuneUpRunnable) r).stopWork();
            }
            task = null;
            tuneItButton.setText("Tune it, baby!");
        } else {
            //
            try {
                MT = Runtime.getRuntime().availableProcessors() * Integer.valueOf(threadCountTextField.getText());
            } catch (NumberFormatException e) {
                MT = Runtime.getRuntime().availableProcessors() * 8;
            }
            service = Executors.newFixedThreadPool(MT);
            task = new ArrayList<Runnable>();
            if (threadCheckBox.isSelected()) {
                Runnable r = null;
                for (int i = 0; i < MT; i++) {
                    r = new TuneUpRunnable((TuneUpView) TuneUpApp.getApplication().getMainView());
                    task.add(r);
                    service.submit(r);
                }
                service.shutdown();
            }
            if (useSocksCheckBox.isSelected()) {
                final int SECS = 10;
                logTextArea.append("sleeping " + SECS + " seconds to bootstrap tor\n");
                try {
                    Thread.sleep(1000 * SECS);
                } catch (InterruptedException e) {
                    Logger.getLogger(TuneUpView.class.getName()).log(Level.SEVERE, null, e);
                }
                // Set SOCKS options
                System.getProperties().setProperty("socksProxyHost", "localhost");
                System.getProperties().setProperty("socksProxyPort", socksPortTextField.getText());
            }
            // Set text on button
            final String TUNE_KEY = "Tuning...";
            tuneItButton.setText(TUNE_KEY);
        }
        return null;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        socksLabel = new javax.swing.JLabel();
        useSocksCheckBox = new javax.swing.JCheckBox();
        socksPortLabel = new javax.swing.JLabel();
        socksPortTextField = new javax.swing.JTextField();
        actualIpLabel = new javax.swing.JLabel();
        acutalIpTextField = new javax.swing.JTextField();
        requestsMadeLabel = new javax.swing.JLabel();
        requestsMadeTextField = new javax.swing.JTextField();
        tuneItButton = new javax.swing.JButton();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        pauseLabel = new javax.swing.JLabel();
        pauseTextField = new javax.swing.JTextField();
        pauseSecsLabel = new javax.swing.JLabel();
        tunerComboBox = new javax.swing.JComboBox();
        threadCheckBox = new javax.swing.JCheckBox();
        threadCountTextField = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(tuneup.TuneUpApp.class).getContext().getResourceMap(TuneUpView.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        socksLabel.setText(resourceMap.getString("socksLabel.text")); // NOI18N
        socksLabel.setName("socksLabel"); // NOI18N

        useSocksCheckBox.setText(resourceMap.getString("useSocksCheckBox.text")); // NOI18N
        useSocksCheckBox.setName("useSocksCheckBox"); // NOI18N

        socksPortLabel.setText(resourceMap.getString("socksPortLabel.text")); // NOI18N
        socksPortLabel.setName("socksPortLabel"); // NOI18N

        socksPortTextField.setEditable(false);
        socksPortTextField.setText(resourceMap.getString("socksPortTextField.text")); // NOI18N
        socksPortTextField.setName("socksPortTextField"); // NOI18N
        socksPortTextField.setPreferredSize(new java.awt.Dimension(32, 20));

        actualIpLabel.setText(resourceMap.getString("actualIpLabel.text")); // NOI18N
        actualIpLabel.setName("actualIpLabel"); // NOI18N

        acutalIpTextField.setEditable(false);
        acutalIpTextField.setText(resourceMap.getString("acutalIpTextField.text")); // NOI18N
        acutalIpTextField.setName("acutalIpTextField"); // NOI18N
        acutalIpTextField.setPreferredSize(new java.awt.Dimension(90, 20));

        requestsMadeLabel.setText(resourceMap.getString("requestsMadeLabel.text")); // NOI18N
        requestsMadeLabel.setName("requestsMadeLabel"); // NOI18N

        requestsMadeTextField.setEditable(false);
        requestsMadeTextField.setText(resourceMap.getString("requestsMadeTextField.text")); // NOI18N
        requestsMadeTextField.setName("requestsMadeTextField"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(tuneup.TuneUpApp.class).getContext().getActionMap(TuneUpView.class, this);
        tuneItButton.setAction(actionMap.get("startAction")); // NOI18N
        tuneItButton.setText(resourceMap.getString("tuneItButton.text")); // NOI18N
        tuneItButton.setName("tuneItButton"); // NOI18N

        logScrollPane.setName("logScrollPane"); // NOI18N

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setName("logTextArea"); // NOI18N
        logScrollPane.setViewportView(logTextArea);

        pauseLabel.setText(resourceMap.getString("pauseLabel.text")); // NOI18N
        pauseLabel.setName("pauseLabel"); // NOI18N

        pauseTextField.setText(resourceMap.getString("pauseTextField.text")); // NOI18N
        pauseTextField.setName("pauseTextField"); // NOI18N
        pauseTextField.setPreferredSize(new java.awt.Dimension(19, 20));

        pauseSecsLabel.setText(resourceMap.getString("pauseSecsLabel.text")); // NOI18N
        pauseSecsLabel.setName("pauseSecsLabel"); // NOI18N

        tunerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Value 1", "Value 2", "Value 3"}));
        tunerComboBox.setName("tunerComboBox"); // NOI18N

        threadCheckBox.setSelected(true);
        threadCheckBox.setText(resourceMap.getString("threadCheckBox.text")); // NOI18N
        threadCheckBox.setName("threadCheckBox"); // NOI18N

        threadCountTextField.setText(resourceMap.getString("threadCountTextField.text")); // NOI18N
        threadCountTextField.setName("threadCountTextField"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(actualIpLabel)
                                                        .addComponent(requestsMadeLabel)
                                                        .addComponent(socksLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                                                .addComponent(useSocksCheckBox)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(socksPortLabel)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(socksPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(acutalIpTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addComponent(requestsMadeTextField, javax.swing.GroupLayout.Alignment.LEADING))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(pauseLabel))
                                                        .addComponent(tuneItButton))
                                                .addGap(9, 9, 9)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(threadCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(threadCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(tunerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(pauseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(pauseSecsLabel)))
                                                .addGap(58, 58, 58)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(useSocksCheckBox)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(socksPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(socksPortLabel))
                                        .addComponent(socksLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(actualIpLabel)
                                        .addComponent(acutalIpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pauseLabel)
                                        .addComponent(pauseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pauseSecsLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(requestsMadeLabel)
                                        .addComponent(requestsMadeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(threadCheckBox)
                                        .addComponent(threadCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tuneItButton)
                                        .addComponent(tunerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(statusMessageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 240, Short.MAX_VALUE)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusAnimationLabel)
                                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(statusMessageLabel)
                                        .addComponent(statusAnimationLabel)
                                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel actualIpLabel;
    protected javax.swing.JTextField acutalIpTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane logScrollPane;
    protected javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel pauseLabel;
    private javax.swing.JLabel pauseSecsLabel;
    protected javax.swing.JTextField pauseTextField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel requestsMadeLabel;
    protected javax.swing.JTextField requestsMadeTextField;
    private javax.swing.JLabel socksLabel;
    private javax.swing.JLabel socksPortLabel;
    protected javax.swing.JTextField socksPortTextField;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    protected javax.swing.JCheckBox threadCheckBox;
    private javax.swing.JTextField threadCountTextField;
    protected javax.swing.JButton tuneItButton;
    protected javax.swing.JComboBox tunerComboBox;
    protected javax.swing.JCheckBox useSocksCheckBox;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;

    private final Timer busyIconTimer;

    private final Icon idleIcon;

    private final Icon[] busyIcons = new Icon[15];

    private int busyIconIndex = 0;

    private JDialog aboutBox;

}
