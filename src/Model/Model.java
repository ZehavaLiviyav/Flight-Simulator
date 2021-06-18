package Model;

import Commands.TimeSeries;
import Commands.TimeSeriesAnomalyDetector;
import javafx.beans.property.*;
import javafx.scene.control.Alert;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class Model extends Observable {


    public TimeSeries timeSeries;
    public TimeSeriesAnomalyDetector anomalyDetector;
    public Timer timer = null;
    public IntegerProperty timestep;
    public IntegerProperty m_speed;
    public StringProperty trainPath, algoPath, testPath;


    public DoubleProperty aileron, elevators, rudder, throttle;
    public DoubleProperty joyChange;

    public FloatProperty altimeterValue, headingValue, pitchValue, rollValue, speedValue, yawValue;


    public Model(IntegerProperty timestep) {
        this.timestep = timestep;
        m_speed = new SimpleIntegerProperty(1);

        trainPath = new SimpleStringProperty();
        algoPath = new SimpleStringProperty();
        testPath = new SimpleStringProperty();
        altimeterValue = new SimpleFloatProperty();
        headingValue = new SimpleFloatProperty();
        pitchValue = new SimpleFloatProperty();
        rollValue = new SimpleFloatProperty();
        speedValue = new SimpleFloatProperty();
        yawValue = new SimpleFloatProperty();
        aileron = new SimpleDoubleProperty();
        elevators = new SimpleDoubleProperty();
        rudder = new SimpleDoubleProperty();
        throttle = new SimpleDoubleProperty();
        joyChange = new SimpleDoubleProperty();

    }


    public void play() {

        int s = (int) (1000) / m_speed.getValue();

        if (timer == null) {
            timer = new Timer();
            if (timeSeries != null) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        float[] sArr = timeSeries.data[timestep.getValue()];

                        altimeterValue.setValue(sArr[25]);
                        headingValue.setValue(sArr[36]);
                        pitchValue.setValue(sArr[29]);
                        rollValue.setValue(sArr[28]);
                        speedValue.setValue(sArr[24]);
                        yawValue.setValue(sArr[21]);

                        aileron.setValue(sArr[0]);
                        elevators.setValue(sArr[1]);
                        rudder.setValue(sArr[2]);
                        throttle.setValue(sArr[6]);
                        joyChange.setValue(1);

                        System.out.println("aileron-" + aileron.get());

                        timestep.set(timestep.get() + 1);

                    }
                }, 0, s / 2);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("You did not upload a CSV file");
                alert.showAndWait();
            }
        }

        setChanged();
        notifyObservers();
    }

    public void pause() {
        timer.cancel();
        timer = null;
        setChanged();
        notifyObservers();
    }

    public void stop() {
        timer.cancel();
        timer = null;
        timestep.set(0);
        setChanged();
        notifyObservers();
    }

    public void forward() {

        timestep.set(timestep.get() + 20);
        // לבדוק שאנחנו לא בסוף הטיסה
        setChanged();
        notifyObservers();
    }

    public void backward() {
        int st = timestep.get();
        if (st > 20) {
            timestep.set(st - 20);
        } else {
            timestep.set(0);
        }
        // לעשות ELSE נוסף ולבדוק שאנחנו לא בסוף הטיסה
        setChanged();
        notifyObservers();
    }

    public void fastforward() {

        timestep.set(timestep.get() + 50);
        // לבדוק שאנחנו לא בסוף הטיסה
        setChanged();
        notifyObservers();
    }

    public void fastbackward() {
        int st = timestep.get();
        if (st > 50) {
            timestep.set(st - 50);
        } else {
            timestep.set(0);
        }
        // לעשות ELSE נוסף ולבדוק שאנחנו לא בסוף הטיסה
        setChanged();
        notifyObservers();
    }

    public String[] loadCSV() {
        timeSeries = new TimeSeries(this.trainPath.getValue());
        return timeSeries.name;
    }


//-----------------------------------------------------------------------------------------------//


    public void connect() {
        /*
        PUT THIS IN FLIGHTGEAR SETTINGS
        --generic=socket,in,10,127.0.0.1,5400,tcp,playback_small
        --fdm=null
         */

        Socket fg = null;
        try {
            fg = new Socket("localhost", 5400);
            BufferedReader in = new BufferedReader(new FileReader("reg_flight.csv"));
            PrintWriter out = new PrintWriter(fg.getOutputStream());
            String line;
            while ((line = in.readLine()) != null) {
                out.println(line);
                out.flush();
                Thread.sleep(100);
            }
            out.close();
            in.close();
            fg.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
