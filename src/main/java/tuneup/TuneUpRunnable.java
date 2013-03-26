package tuneup;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rbe
 */
public class TuneUpRunnable implements Runnable {

    private TuneUpView view;

    private static final String[] userAgentStrings = {
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.2) Gecko/2008092313 Ubuntu/8.04 (hardy) Firefox/3.1.6",
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.4) Gecko/2008102920 Firefox/3.0.4",
            "Mozilla/6.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:2.0.0.0) Gecko/20061028 Firefox/3.0",
            "Mozilla/5.0 (X11; U; Linux x86_64; es-US; rv:1.9) Gecko/2008061017 Firefox/3.0",
            "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.4) Gecko/20070515 Firefox/2.0.4",
            "Mozilla/5.0 (Windows; Windows XP 5.1; en-US; rv:1.8.1.9) Gecko/20071025 Firefox/2.0.0.9",
            "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.4) Gecko/20070515 Firefox/2.0.4",
            "Mozilla/5.0 (X11; U; Windows NT i686; en-US; rv:1.9.0.1) Gecko/2008070206 Firefox/2.0.0.8",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5.7; it; rv:1.9b4) Gecko/2008030317 Firefox/3.0.2",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; sv-SE; rv:1.9.0.4) Gecko/2008102920 Firefox/3.0.4"};

    private List<String> actualIp = new ArrayList<String>();

    private boolean doWork;

    public TuneUpRunnable(TuneUpView view) {
        this.view = view;
        doWork = true;
    }

    public synchronized void stopWork() {
        doWork = false;
        view.logTextArea.append(Thread.currentThread().getName() + " called stopWork()\n");
    }

    @Override
    public void run() {
        view.logTextArea.append(Thread.currentThread().getName() + " started\n");
        //
        HttpGet checkIpGet = new HttpGet("http://checkip.dyndns.org/");
        String tuner = ((String) view.tunerComboBox.getSelectedItem()).split("-")[0];
        String uri = "http://localhost/?q=" + tuner;
        HttpGet tuneGet = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpClient httpClient = new DefaultHttpClient();
        //
        Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
        Matcher matcher = null;
        //
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM. HH:mm:ss.SSSSS");
        // Start
        int i1 = 0;
        int i2 = 0;
        boolean b = false;
        String ip = null;
        while (doWork) {
            try {
                i1 = (int) (Math.random() * 10.0d);
                i2 = (int) (Math.random() * 10.0d);
                if (view.useSocksCheckBox.isSelected()) {
                    // Check IP via http://checkip.dyndns.org
                    matcher = pattern.matcher(httpClient.execute(checkIpGet, responseHandler));
                    b = matcher.find();
                    ip = matcher.group();
                    if (!actualIp.contains(ip)) {
                        actualIp.add(ip);
                        view.acutalIpTextField.setText(ip);
                        b = true;
                    }
                } else {
                    b = true;
                }
                if (b) {
                    // Wait for a random time
                    if (view.useSocksCheckBox.isSelected()) {
                        Thread.sleep(1000 * i2);
                    }
                    // Tune: set user agent
                    System.getProperties().setProperty("http.agent", userAgentStrings[i1]);
                    // Make THE request
                    try {
                        httpClient.execute(tuneGet, responseHandler);
                        view.incRequestsMade();
                        view.logTextArea.append(Thread.currentThread().getName() + " " + sdf.format(new Date()) + ": " + tuner + " -- " + ip + " as " + System.getProperties().getProperty("http.agent") + "\n");
                    } catch (org.apache.http.conn.HttpHostConnectException e) {
                        view.logTextArea.append(Thread.currentThread().getName() + " ERROR: will retry in 5 seconds: " + e.getMessage() + "\n");
                        try {
                            Thread.sleep(1000 * 5);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
                if (view.useSocksCheckBox.isSelected()) {
                    // Sleep before next try
                    int i = 0;
                    try {
                        i = Integer.valueOf(view.pauseTextField.getText());
                    } catch (NumberFormatException e) {
                    }
                    Thread.sleep(1000 * i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped");
    }

}
