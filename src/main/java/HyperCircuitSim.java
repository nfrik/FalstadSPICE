/**
 * Created by NF on 9/4/2016.
 */


// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class HyperCircuitSim {

    final int SETUP_READ_OK = 0;
    final int SETUP_READ_DUMP_WARNING = 1;
    final int SETUP_READ_ERROR = 2;

    double circuitMatrix[][], circuitRightSide[],
            origRightSide[], origMatrix[][];
    RowInfo circuitRowInfo[];
    CircuitMod applet;

    public boolean useFrame;
    int scopeCount;
    Scope scopes[];
    int scopeColCount[];

    String startCircuit = null;
    String startLabel = null;
    String startCircuitText = null;
    String baseURL = "http://circuitmod.sourceforge.net/";

    Class<?> dumpTypes[];

    Vector<CircuitElm> elmList;
    Vector<String> setupList;
    CircuitElm dragElm, menuElm, mouseElm, stopElm;

    Random random;

    Vector<CircuitNode> nodeList;
    CircuitElm voltageSources[];

    Menu circuitsMenu = new Menu("Circuits");

    String stopMessage;

    String savedCircuit;

    boolean circuitNonLinear;

    boolean dumpMatrix;

    SaveOpenDialog saveOpenDialog;

    boolean analyzeFlag;


    double t;
    boolean stoppedCheck = false;
    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    double timeStep;
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;

    int circuitPermute[];


    Scrollbar speedBar;
    Scrollbar currentBar;
    Label powerLabel;
    Scrollbar powerBar;
    int hintType = -1, hintItem1, hintItem2;

    int circuitMatrixSize, circuitMatrixFullSize;

    int voltageSourceCount;

    boolean circuitNeedsMap;

    int pause = 10;

    PopupMenu mainMenu;

    int gridSize, gridMask, gridRound;

    CircuitElm plotXElm, plotYElm;

    int getrand(int x) {
        int q = random.nextInt();
        if (q < 0) q = -q;
        return q % x;
    }

    public void init(String initCircuit) {
        String euroResistor = null;
        String useFrameStr = null;
        boolean printable = false;
        boolean convention = true;

        CircuitElm.initClass(this);

        try {
            baseURL = applet.getDocumentBase().getFile();
            // look for circuit embedded in URL
            String doc = applet.getDocumentBase().toString();
            int in = doc.indexOf('#');
            if (in > 0) {
                String x = null;
                try {
                    x = doc.substring(in + 1);
                    x = URLDecoder.decode(x, "UTF-8");
                    startCircuitText = x;
                } catch (Exception e) {
                    System.out.println("can't decode " + x);
                    e.printStackTrace();
                }
            }
            in = doc.lastIndexOf('/');
            if (in > 0)
                baseURL = doc.substring(0, in + 1);

            String param = applet.getParameter("PAUSE");
            if (param != null)
                pause = Integer.parseInt(param);
            startCircuit = applet.getParameter("startCircuit");
            startLabel = applet.getParameter("startLabel");
            euroResistor = applet.getParameter("euroResistors");
            useFrameStr = applet.getParameter("useFrame");
            String x = applet.getParameter("whiteBackground");
            if (x != null && x.equalsIgnoreCase("true"))
                printable = true;
            x = applet.getParameter("conventionalCurrent");
            if (x != null && x.equalsIgnoreCase("true"))
                convention = false;
        } catch (Exception e) {
        }

        boolean euro = (euroResistor != null && euroResistor.equalsIgnoreCase("true"));
        useFrame = (useFrameStr == null || !useFrameStr.equalsIgnoreCase("false"));
        speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260);
        currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
        powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);

//        if (useFrame)
//            main = this;
//        else
//            main = applet;

        String os = System.getProperty("os.name");
//        isMac = (os.indexOf("Mac ") == 0);
//        ctrlMetaKey = (isMac) ? "\u2318" : "Ctrl";
        String jv = System.getProperty("java.class.version");
        double jvf = new Double(jv).doubleValue();

//        if (jvf >= 48) {
//            muString = "\u03bc";
//            ohmString = "\u03a9";
//            useBufferedImage = true;
//        }

        dumpTypes = new Class[300];
        // these characters are reserved
        dumpTypes['o'] = Scope.class;
        dumpTypes['h'] = Scope.class;
        dumpTypes['$'] = Scope.class;
        dumpTypes['%'] = Scope.class;
        dumpTypes['?'] = Scope.class;
        dumpTypes['B'] = Scope.class;

//        setGrid();
        elmList = new Vector<CircuitElm>();
        setupList = new Vector<String>();
//        undoStack = new Vector<String>();
//        redoStack = new Vector<String>();

        scopes = new Scope[20];
        scopeColCount = new int[20];
        scopeCount = 0;

        random = new Random();

//        getSetupList(circuitsMenu, false);

        // Init SaveOpenDialog
//        saveOpenDialog = new SaveOpenDialog(this);
        savedCircuit = null;
//        circuitIsModified = false;


        mainMenu = new PopupMenu();
        mainMenu.add(getClassCheckItem("Add Wire", "WireElm")); //(Dump Data) w
        mainMenu.add(getClassCheckItem("Add Resistor", "ResistorElm")); // r
        mainMenu.add(getClassCheckItem("Add Capacitor", "CapacitorElm")); // c
        mainMenu.add(getClassCheckItem("Add Ground", "GroundElm")); // g

        Menu passMenu = new Menu("Passive Components");
        mainMenu.add(passMenu);
        passMenu.add(getClassCheckItem("Add Inductor", "InductorElm")); // l
        passMenu.add(getClassCheckItem("Add Switch", "SwitchElm")); // s
        passMenu.add(getClassCheckItem("Add Push Switch", "PushSwitchElm"));
        passMenu.add(getClassCheckItem("Add SPDT Switch", "Switch2Elm")); // S
        passMenu.add(getClassCheckItem("Add Potentiometer", "PotElm")); // 174
        passMenu.add(getClassCheckItem("Add Transformer", "TransformerElm")); // T
        passMenu.add(getClassCheckItem("Add Tapped Transformer", "TappedTransformerElm")); // 169
        passMenu.add(getClassCheckItem("Add Transmission Line", "TransLineElm")); // 171
        passMenu.add(getClassCheckItem("Add Relay", "RelayElm")); //178
        passMenu.add(getClassCheckItem("Add Memristor", "MemristorElm"));
        passMenu.add(getClassCheckItem("Add Spark Gap", "SparkGapElm"));

        Menu inputMenu = new Menu("Inputs/Outputs");
        mainMenu.add(inputMenu);
        inputMenu.add(getClassCheckItem("Add Ground", "GroundElm"));
        inputMenu.add(getClassCheckItem("Add Voltage Source (2-terminal)", "DCVoltageElm"));
        inputMenu.add(getClassCheckItem("Add A/C Source (2-terminal)", "ACVoltageElm"));
        inputMenu.add(getClassCheckItem("Add Voltage Source (1-terminal)", "RailElm"));
        inputMenu.add(getClassCheckItem("Add A/C Source (1-terminal)", "ACRailElm"));
        inputMenu.add(getClassCheckItem("Add Square Wave (1-terminal)", "SquareRailElm"));
        inputMenu.add(getClassCheckItem("Add Analog Output", "OutputElm"));
        inputMenu.add(getClassCheckItem("Add Logic Input", "LogicInputElm"));
        inputMenu.add(getClassCheckItem("Add Logic Output", "LogicOutputElm"));
        inputMenu.add(getClassCheckItem("Add Clock", "ClockElm"));
        inputMenu.add(getClassCheckItem("Add A/C Sweep", "SweepElm")); // 170
        inputMenu.add(getClassCheckItem("Add Var. Voltage", "VarRailElm")); // 172
        inputMenu.add(getClassCheckItem("Add Antenna", "AntennaElm"));
        inputMenu.add(getClassCheckItem("Add Current Source", "CurrentElm"));


        Menu activeMenu = new Menu("Active Components");
        mainMenu.add(activeMenu);
        activeMenu.add(getClassCheckItem("Add Diode", "DiodeElm"));
        activeMenu.add(getClassCheckItem("Add Zener Diode", "ZenerElm"));
        activeMenu.add(getClassCheckItem("Add Transistor (bipolar, NPN)", "NTransistorElm"));
        activeMenu.add(getClassCheckItem("Add Transistor (bipolar, PNP)", "PTransistorElm"));
        activeMenu.add(getClassCheckItem("Add Op Amp (- on top)", "OpAmpElm"));
        activeMenu.add(getClassCheckItem("Add Op Amp (+ on top)", "OpAmpSwapElm"));
        activeMenu.add(getClassCheckItem("Add MOSFET (n-channel)", "NMosfetElm"));
        activeMenu.add(getClassCheckItem("Add MOSFET (p-channel)", "PMosfetElm"));
        activeMenu.add(getClassCheckItem("Add JFET (n-channel)", "NJfetElm"));
        activeMenu.add(getClassCheckItem("Add JFET (p-channel)", "PJfetElm"));
        activeMenu.add(getClassCheckItem("Add Analog Switch (SPST)", "AnalogSwitchElm"));
        activeMenu.add(getClassCheckItem("Add Analog Switch (SPDT)", "AnalogSwitch2Elm"));
        activeMenu.add(getClassCheckItem("Add SCR", "SCRElm")); //177
        //activeMenu.add(getClassCheckItem("Add Varactor/Varicap", "VaractorElm"));
        activeMenu.add(getClassCheckItem("Add Tunnel Diode", "TunnelDiodeElm")); //175
        activeMenu.add(getClassCheckItem("Add Triode", "TriodeElm")); //173
        //activeMenu.add(getClassCheckItem("Add Diac", "DiacElm"));
        //activeMenu.add(getClassCheckItem("Add Triac", "TriacElm"));
        //activeMenu.add(getClassCheckItem("Add Photoresistor", "PhotoResistorElm")); // Alpha
        //activeMenu.add(getClassCheckItem("Add Thermistor", "ThermistorElm"));
        activeMenu.add(getClassCheckItem("Add CCII+", "CC2Elm")); //179
        activeMenu.add(getClassCheckItem("Add CCII-", "CC2NegElm"));

        Menu gateMenu = new Menu("Logic Gates");
        mainMenu.add(gateMenu);
        gateMenu.add(getClassCheckItem("Add Inverter", "InverterElm"));
        gateMenu.add(getClassCheckItem("Add NAND Gate", "NandGateElm"));
        gateMenu.add(getClassCheckItem("Add NOR Gate", "NorGateElm"));
        gateMenu.add(getClassCheckItem("Add AND Gate", "AndGateElm"));
        gateMenu.add(getClassCheckItem("Add OR Gate", "OrGateElm"));
        gateMenu.add(getClassCheckItem("Add XOR Gate", "XorGateElm"));
        gateMenu.add(getClassCheckItem("Add ST Inverter", "InverterSTElm")); //186


        Menu chipMenu = new Menu("Chips");
        mainMenu.add(chipMenu);
        chipMenu.add(getClassCheckItem("Add D Flip-Flop", "DFlipFlopElm"));
        chipMenu.add(getClassCheckItem("Add JK Flip-Flop", "JKFlipFlopElm"));
        chipMenu.add(getClassCheckItem("Add 7 Segment LED", "SevenSegElm"));
        chipMenu.add(getClassCheckItem("Add VCO", "VCOElm"));
        chipMenu.add(getClassCheckItem("Add Phase Comparator", "PhaseCompElm"));
        chipMenu.add(getClassCheckItem("Add Counter 4-bit", "CounterElm"));
        chipMenu.add(getClassCheckItem("Add Decade Counter", "DecadeElm"));
        chipMenu.add(getClassCheckItem("Add 555 Timer", "TimerElm"));
        chipMenu.add(getClassCheckItem("Add DAC", "DACElm"));
        chipMenu.add(getClassCheckItem("Add ADC", "ADCElm"));
        chipMenu.add(getClassCheckItem("Add Latch", "LatchElm")); // 168


        Menu displayMenu = new Menu("Display Devices");
        mainMenu.add(displayMenu);
        displayMenu.add(getClassCheckItem("Add Text", "TextElm"));
        displayMenu.add(getClassCheckItem("Add Scope Probe", "ProbeElm"));
        displayMenu.add(getClassCheckItem("Add LED", "LEDElm"));
        displayMenu.add(getClassCheckItem("Add Lamp (beta)", "LampElm"));
        displayMenu.add(getClassCheckItem("Add LED Array", "LEDArrayElm")); //176
        displayMenu.add(getClassCheckItem("Add Matrix 5x7", "Matrix5x7Elm")); // 180


        Menu mychipMenu = new Menu("CD Series");
        mainMenu.add(mychipMenu);
        mychipMenu.add(getClassCheckItem("Add Counter 7-bit (4024)", "CD4024")); // 182
        mychipMenu.add(getClassCheckItem("Add BCD-to-Decimal Decoder (4028)", "CD4028")); // 185
        mychipMenu.add(getClassCheckItem("Add Counter 12-bit (4040)", "CD4040")); // 183
        mychipMenu.add(getClassCheckItem("Add BCD to 7-Segm Decoder (4511)", "CD4511")); // 184
        mychipMenu.add(getClassCheckItem("Add Decade Counter (4017)", "CD4017")); // 189
        //190

        if (useFrame)
//            setMenuBar(mb);
            if (initCircuit != null) {
//                loadStartup(initCircuit);
                readSetupFile(initCircuit);
            } else if (startCircuitText != null)
                readSetup(startCircuitText);
            else if (stopMessage == null && startCircuit != null)
                readSetupFile(startCircuit);

        // Set current "saved" circuit
//        savedCircuit = dumpCircuit();
    }

    // Load a circuit (when user clicks on .cmf file).
    void load(String s) {
        if (s != null) {
            // Store the current circuit
            String aux = null;

//            aux = dumpCircuit();

            // Try to read loaded circuit.
            int l = readSetup(s);

            // Check if the read file is ok to load.
            if (l == SETUP_READ_OK || l == SETUP_READ_DUMP_WARNING) {
//                clearUndoRedo();
//                resetTime();
//                circuitIsModified = false;
//                setTitleName(saveOpenDialog.getFileName());
                System.out.println("Circuit was read successfully");
            }

//            // If the circuit had a dump warning, tell the user.
//            if (l == SETUP_READ_DUMP_WARNING) {
//                showSetupReadDumpWarningDialog();
//            }

            // If the circuit can't be load, tell the user and
            // reload current circuit.
            else if (l == SETUP_READ_ERROR) {
//                readSetup(aux);
                System.out.println("Error reading circuit");
//                showSetupReadErrorDialog();
            }
        }
    }

    void loadStartup(String s1) {
        // Get circuit from path s1.
        String s = saveOpenDialog.load(s1);

        // Load circuit
        load(s);
    }

    int readSetup(String text) {
        return readSetup(text, false);
    }

    int readSetup(String text, boolean retain) {
//        titleLabel.setText("untitled");
        return readSetup(text.getBytes(), text.length(), retain);
    }

    void readSetupFile(String str) {
        t = 0;
        System.out.println(str);
        try {
            URL url = new URL(getCodeBase() + "circuits/" + str);
            ByteArrayOutputStream ba = readUrlData(url);
            readSetup(ba.toByteArray(), ba.size(), false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to read " + str + "!");
        }
//        titleLabel.setText(title);
    }

    int readSetup(byte b[], int len, boolean retain) {
        int status = SETUP_READ_OK;

        int i;
        if (!retain) {
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.delete();
            }
            elmList.removeAllElements();
            hintType = -1;
            timeStep = 5e-6;
//            dotsCheckItem.setState(true);
//            smallGridCheckItem.setState(false);
//            powerCheckItem.setState(false);
//            voltsCheckItem.setState(true);
//            showValuesCheckItem.setState(true);
//            setGrid();
            speedBar.setValue(117); // 57
            currentBar.setValue(50);
            powerBar.setValue(50);
            CircuitElm.voltageRange = 5;
            scopeCount = 0;
        }
//        cv.repaint();
        int p;
        for (p = 0; p < len; ) {
            int l;
            int linelen = 0;
            for (l = 0; l != len - p; l++)
                if (b[l + p] == '\n' || b[l + p] == '\r') {
                    linelen = l++;
                    if (l + p < b.length && b[l + p] == '\n')
                        l++;
                    break;
                }
            String line = new String(b, p, linelen);
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                String type = st.nextToken();
                int tint = type.charAt(0);
                try {
                    if (tint == 'o') {
                        Scope sc = new Scope(new CirSim(null));
                        sc.position = scopeCount;
//                        sc.undump(st);
                        scopes[scopeCount++] = sc;
                        break;
                    }
                    if (tint == 'h') {
                        readHint(st);
                        break;
                    }
                    if (tint == '$') {
                        readOptions(st);
                        break;
                    }
                    if (tint == '%' || tint == '?' || tint == 'B') {
                        // ignore afilter-specific stuff
                        break;
                    }
                    if (tint >= '0' && tint <= '9')
                        tint = new Integer(type).intValue();
                    int x1 = new Integer(st.nextToken()).intValue();
                    int y1 = new Integer(st.nextToken()).intValue();
                    int x2 = new Integer(st.nextToken()).intValue();
                    int y2 = new Integer(st.nextToken()).intValue();
                    int f = new Integer(st.nextToken()).intValue();
                    CircuitElm ce = null;
                    Class cls = dumpTypes[tint];
                    if (cls == null) {
                        System.out.println("unrecognized dump type: " + type);
                        status = SETUP_READ_DUMP_WARNING;
                        break;
                    }
                    // find element class
                    Class carr[] = new Class[6];
                    //carr[0] = getClass();
                    carr[0] = carr[1] = carr[2] = carr[3] = carr[4] =
                            int.class;
                    carr[5] = StringTokenizer.class;
                    Constructor cstr = null;
                    cstr = cls.getConstructor(carr);

                    // invoke constructor with starting coordinates
                    Object oarr[] = new Object[6];
                    //oarr[0] = this;
                    oarr[0] = new Integer(x1);
                    oarr[1] = new Integer(y1);
                    oarr[2] = new Integer(x2);
                    oarr[3] = new Integer(y2);
                    oarr[4] = new Integer(f);
                    oarr[5] = st;
                    ce = (CircuitElm) cstr.newInstance(oarr);
                    ce.setPoints();
                    elmList.addElement(ce);
                } catch (java.lang.reflect.InvocationTargetException ee) {
                    ee.getTargetException().printStackTrace();
                    return SETUP_READ_ERROR;
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return SETUP_READ_ERROR;
                }
                break;
            }
            p += l;

        }
//        enableItems();
//        if (!retain)
//            handleResize(); // for scopes
        analyzeFlag = true;

        return status;
    }

    public void updateCircuit() {
        CircuitElm realMouseElm;

        if (analyzeFlag) {
            analyzeCircuit();
            analyzeFlag = false;
        }
        setupScopes();
        CircuitElm.selectColor = Color.cyan;
        CircuitElm.whiteColor = Color.white;
        CircuitElm.lightGrayColor = Color.lightGray;

        if (!stoppedCheck) {
            try {
                runCircuit();
            } catch (Exception e) {
                e.printStackTrace();
                analyzeFlag = true;
                return;
            }
        }
        if (!stoppedCheck) {
            long sysTime = System.currentTimeMillis();
            if (lastTime != 0) {
                int inc = (int) (sysTime - lastTime);
                double c = 1.;
                c = java.lang.Math.exp(c / 3.5 - 14.2);
                CircuitElm.currentMult = 1.7 * inc * c;
//                if (!conventionCheckItem.getState())
//                    CircuitElm.currentMult = -CircuitElm.currentMult;
            }
            if (sysTime - secTime >= 1000) {
                framerate = frames;
                steprate = steps;
                frames = 0;
                steps = 0;
                secTime = sysTime;
            }
            lastTime = sysTime;
        } else
            lastTime = 0;
        CircuitElm.powerMult = Math.exp(powerBar.getValue() / 4.762 - 7);


        if (!stoppedCheck && circuitMatrix != null) {
            // Limit to 50 fps (thanks to J�rgen Kl�tzer for this)
            long delay = 1000 / 50 - (System.currentTimeMillis() - lastFrameTime);
            //realg.drawString("delay: " + delay,  10, 90);
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
            }

        }
        lastFrameTime = lastTime;
    }

    boolean converged;
    int subIterations;

    void runCircuit() {
        if (circuitMatrix == null || elmList.size() == 0) {
            circuitMatrix = null;
            return;
        }
        int iter;
        //int maxIter = getIterCount();
        boolean debugprint = dumpMatrix;
        dumpMatrix = false;
        long steprate = (long) (160 * getIterCount());
        long tm = System.currentTimeMillis();
        long lit = lastIterTime;
        if (1000 >= steprate * (tm - lastIterTime))
            return;
        for (iter = 1; ; iter++) {
            int i, j, k, subiter;
            //Perform iteration
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.startIteration();
            }
            steps++;
            final int subiterCount = 5000;
            for (subiter = 0; subiter != subiterCount; subiter++) {
                converged = true;
                subIterations = subiter;
                for (i = 0; i != circuitMatrixSize; i++)
                    circuitRightSide[i] = origRightSide[i];
                if (circuitNonLinear) {
                    for (i = 0; i != circuitMatrixSize; i++)
                        for (j = 0; j != circuitMatrixSize; j++)
                            circuitMatrix[i][j] = origMatrix[i][j];
                }
                for (i = 0; i != elmList.size(); i++) {
                    CircuitElm ce = getElm(i);
                    ce.doStep();
                    ce.calculateCurrent();
                    if ((ce.getDumpClass().getName().compareTo("ResistorElm") == 0)) {
                        if (((ResistorElm) ce).resistance == 10.0) {
                            System.out.println(ce.dump() + " voltage: " + ce.getVoltageDiff() + " current: " + ce.getCurrent() + " t: " + t);
                        }
                    }
                }
                if (stopMessage != null)
                    return;
                boolean printit = debugprint;
                debugprint = false;
                for (j = 0; j != circuitMatrixSize; j++) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        double x = circuitMatrix[i][j];
                        if (Double.isNaN(x) || Double.isInfinite(x)) {
                            stop("nan/infinite matrix!", null);
                            return;
                        }
                    }
                }
                if (printit) {
                    for (j = 0; j != circuitMatrixSize; j++) {
                        for (i = 0; i != circuitMatrixSize; i++)
                            System.out.print(circuitMatrix[j][i] + ",");
                        System.out.print("  " + circuitRightSide[j] + "\n");
                    }
                    System.out.print("\n");
                }
                if (circuitNonLinear) {
                    if (converged && subiter > 0)
                        break;
                    if (!lu_factor(circuitMatrix, circuitMatrixSize,
                            circuitPermute)) {
                        stop("Singular matrix!", null);
                        return;
                    }
                }
                lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
                        circuitRightSide);

                for (j = 0; j != circuitMatrixFullSize; j++) {
                    RowInfo ri = circuitRowInfo[j];
                    double res = 0;
                    if (ri.type == RowInfo.ROW_CONST)
                        res = ri.value;
                    else
                        res = circuitRightSide[ri.mapCol];
                    /*System.out.println(j + " " + res + " " +
              ri.type + " " + ri.mapCol);*/
                    if (Double.isNaN(res)) {
                        converged = false;
                        //debugprint = true;
                        break;
                    }
                    if (j < nodeList.size() - 1) {
                        CircuitNode cn = getCircuitNode(j + 1);
                        for (k = 0; k != cn.links.size(); k++) {
                            CircuitNodeLink cnl = (CircuitNodeLink) cn.links.elementAt(k);
                            cnl.elm.setNodeVoltage(cnl.num, res);
                        }
                    } else {
                        int ji = j - (nodeList.size() - 1);
                        //System.out.println("setting vsrc " + ji + " to " + res);
                        voltageSources[ji].setCurrent(ji, res);
                    }
                }
                if (!circuitNonLinear)
                    break;
            }
            if (subiter > 5)
                System.out.print("converged after " + subiter + " iterations\n");
            if (subiter == subiterCount) {
                stop("Convergence failed!", null);
                break;
            }
            t += timeStep;
            for (i = 0; i != scopeCount; i++)
                scopes[i].timeStep();
            tm = System.currentTimeMillis();
            lit = tm;
            if (iter * 1000 >= steprate * (tm - lastIterTime) ||
                    (tm - lastFrameTime > 500))
                break;
        }
        lastIterTime = lit;
        //System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
    }

    void setupScopes() {
        int i;

        // check scopes to make sure the elements still exist, and remove
        // unused scopes/columns
        int pos = -1;
        for (i = 0; i < scopeCount; i++) {
            if (locateElm(scopes[i].elm) < 0)
                scopes[i].setElm(null);
            if (scopes[i].elm == null) {
                int j;
                for (j = i; j != scopeCount; j++)
                    scopes[j] = scopes[j + 1];
                scopeCount--;
                i--;
                continue;
            }
            if (scopes[i].position > pos + 1)
                scopes[i].position = pos + 1;
            pos = scopes[i].position;
        }
        while (scopeCount > 0 && scopes[scopeCount - 1].elm == null)
            scopeCount--;
//        int h = winSize.height - circuitArea.height;
        pos = 0;
        for (i = 0; i != scopeCount; i++)
            scopeColCount[i] = 0;
        for (i = 0; i != scopeCount; i++) {
//            pos = max(scopes[i].position, pos);
            scopeColCount[scopes[i].position]++;
        }
        int colct = pos + 1;
//        int iw = infoWidth;
        if (colct <= 2)
//            iw = iw * 3 / 2;
//        int w = (winSize.width - iw) / colct;
//        int marg = 10;
//        if (w < marg * 2)
//            w = marg * 2;
            pos = -1;
        int colh = 0;
        int row = 0;
        int speed = 0;
        for (i = 0; i != scopeCount; i++) {
            Scope s = scopes[i];
            if (s.position > pos) {
                pos = s.position;
//                colh = h / scopeColCount[pos];
                row = 0;
                speed = s.speed;
            }
            if (s.speed != speed) {
                s.speed = speed;
                s.resetGraph();
            }
//            Rectangle r = new Rectangle(pos * w, winSize.height - h + colh * row,
//                    w - marg, colh);
            row++;
//            if (!r.equals(s.rect))
//                s.setRect(r);
        }
    }

    int circuitBottom;

    void calcCircuitBottom() {
        int i;
        circuitBottom = 0;
        for (i = 0; i != elmList.size(); i++) {
            Rectangle rect = getElm(i).boundingBox;
            int bottom = rect.height + rect.y;
            if (bottom > circuitBottom)
                circuitBottom = bottom;
        }
    }

    void analyzeCircuit() {
        calcCircuitBottom();
        if (elmList.isEmpty())
            return;
        stopMessage = null;
        stopElm = null;
        int i, j;
        int vscount = 0;
        nodeList = new Vector<CircuitNode>();
        boolean gotGround = false;
        boolean gotRail = false;
        CircuitElm volt = null;

        //System.out.println("ac1");
        // look for voltage or ground element
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce instanceof GroundElm) {
                gotGround = true;
                break;
            }
            if (ce instanceof RailElm)
                gotRail = true;
            if (volt == null && ce instanceof VoltageElm)
                volt = ce;
        }

        // if no ground, and no rails, then the voltage elm's first terminal
        // is ground
        if (!gotGround && volt != null && !gotRail) {
            CircuitNode cn = new CircuitNode();
            Point pt = volt.getPost(0);
            cn.x = pt.x;
            cn.y = pt.y;
            nodeList.addElement(cn);
        } else {
            // otherwise allocate extra node for ground
            CircuitNode cn = new CircuitNode();
            cn.x = cn.y = -1;
            nodeList.addElement(cn);
        }
        //System.out.println("ac2");

        // allocate nodes and voltage sources
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            int inodes = ce.getInternalNodeCount();
            int ivs = ce.getVoltageSourceCount();
            int posts = ce.getPostCount();

            // allocate a node for each post and match posts to nodes
            for (j = 0; j != posts; j++) {
                Point pt = ce.getPost(j);
                int k;
                for (k = 0; k != nodeList.size(); k++) {
                    CircuitNode cn = getCircuitNode(k);
                    if (pt.x == cn.x && pt.y == cn.y)
                        break;
                }
                if (k == nodeList.size()) {
                    CircuitNode cn = new CircuitNode();
                    cn.x = pt.x;
                    cn.y = pt.y;
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    cn.links.addElement(cnl);
                    ce.setNode(j, nodeList.size());
                    nodeList.addElement(cn);
                } else {
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    getCircuitNode(k).links.addElement(cnl);
                    ce.setNode(j, k);
                    // if it's the ground node, make sure the node voltage is 0,
                    // cause it may not get set later
                    if (k == 0)
                        ce.setNodeVoltage(j, 0);
                }
            }
            for (j = 0; j != inodes; j++) {
                CircuitNode cn = new CircuitNode();
                cn.x = cn.y = -1;
                cn.internal = true;
                CircuitNodeLink cnl = new CircuitNodeLink();
                cnl.num = j + posts;
                cnl.elm = ce;
                cn.links.addElement(cnl);
                ce.setNode(cnl.num, nodeList.size());
                nodeList.addElement(cn);
            }
            vscount += ivs;
        }
        voltageSources = new CircuitElm[vscount];
        vscount = 0;
        circuitNonLinear = false;
        //System.out.println("ac3");

        // determine if circuit is nonlinear
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce.nonLinear())
                circuitNonLinear = true;
            int ivs = ce.getVoltageSourceCount();
            for (j = 0; j != ivs; j++) {
                voltageSources[vscount] = ce;
                ce.setVoltageSource(j, vscount++);
            }
        }
        voltageSourceCount = vscount;

        int matrixSize = nodeList.size() - 1 + vscount;
        circuitMatrix = new double[matrixSize][matrixSize];
        circuitRightSide = new double[matrixSize];
        origMatrix = new double[matrixSize][matrixSize];
        origRightSide = new double[matrixSize];
        circuitMatrixSize = circuitMatrixFullSize = matrixSize;
        circuitRowInfo = new RowInfo[matrixSize];
        circuitPermute = new int[matrixSize];
        //int vs = 0;
        for (i = 0; i != matrixSize; i++)
            circuitRowInfo[i] = new RowInfo();
        circuitNeedsMap = false;

        // stamp linear circuit elements
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.stamp();
        }
        //System.out.println("ac4");

        // determine nodes that are unconnected
        boolean closure[] = new boolean[nodeList.size()];
        //boolean tempclosure[] = new boolean[nodeList.size()];
        boolean changed = true;
        closure[0] = true;
        while (changed) {
            changed = false;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                // loop through all ce's nodes to see if they are connected
                // to other nodes not in closure
                for (j = 0; j < ce.getPostCount(); j++) {
                    if (!closure[ce.getNode(j)]) {
                        if (ce.hasGroundConnection(j))
                            closure[ce.getNode(j)] = changed = true;
                        continue;
                    }
                    int k;
                    for (k = 0; k != ce.getPostCount(); k++) {
                        if (j == k)
                            continue;
                        int kn = ce.getNode(k);
                        if (ce.getConnection(j, k) && !closure[kn]) {
                            closure[kn] = true;
                            changed = true;
                        }
                    }
                }
            }
            if (changed)
                continue;

            // connect unconnected nodes
            for (i = 0; i != nodeList.size(); i++)
                if (!closure[i] && !getCircuitNode(i).internal) {
                    System.out.println("node " + i + " unconnected");
                    stampResistor(0, i, 1e8);
                    closure[i] = true;
                    changed = true;
                    break;
                }
        }
        //System.out.println("ac5");

        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            // look for inductors with no current path
            if (ce instanceof InductorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                // first try findPath with maximum depth of 5, to avoid slowdowns
                if (!fpi.findPath(ce.getNode(0), 5) &&
                        !fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " no path");
                    ce.reset();
                }
            }
            // look for current sources with no current path
            if (ce instanceof CurrentElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                if (!fpi.findPath(ce.getNode(0))) {
                    stop("No path for current source!", ce);
                    return;
                }
            }
            // look for voltage source loops
            if ((ce instanceof VoltageElm && ce.getPostCount() == 2) ||
                    ce instanceof WireElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    stop("Voltage source/wire loop with no resistance!", ce);
                    return;
                }
            }
            // look for shorted caps, or caps w/ voltage but no R
            if (ce instanceof CapacitorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " shorted");
                    ce.reset();
                } else {
                    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
                    if (fpi.findPath(ce.getNode(0))) {
                        stop("Capacitor loop with no resistance!", ce);
                        return;
                    }
                }
            }
        }
        //System.out.println("ac6");

        // simplify the matrix; this speeds things up quite a bit
        for (i = 0; i != matrixSize; i++) {
            int qm = -1, qp = -1;
            double qv = 0;
            RowInfo re = circuitRowInfo[i];
			/*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
			       re.dropRow);*/
            if (re.lsChanges || re.dropRow || re.rsChanges)
                continue;
            double rsadd = 0;

            // look for rows that can be removed
            for (j = 0; j != matrixSize; j++) {
                double q = circuitMatrix[i][j];
                if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
                    // keep a running total of const values that have been
                    // removed already
                    rsadd -= circuitRowInfo[j].value * q;
                    continue;
                }
                if (q == 0)
                    continue;
                if (qp == -1) {
                    qp = j;
                    qv = q;
                    continue;
                }
                if (qm == -1 && q == -qv) {
                    qm = j;
                    continue;
                }
                break;
            }
			/*
	    //System.out.println("line " + i + " " + qp + " " + qm + " " + j);
	    if (qp != -1 && circuitRowInfo[qp].lsChanges) {
		System.out.println("lschanges");
		continue;
	    }
	    if (qm != -1 && circuitRowInfo[qm].lsChanges) {
		System.out.println("lschanges");
		continue;
		}
			 */
            if (j == matrixSize) {
                if (qp == -1) {
                    stop("Matrix error", null);
                    return;
                }
                RowInfo elt = circuitRowInfo[qp];
                if (qm == -1) {
                    // we found a row with only one nonzero entry; that value
                    // is a constant
                    int k;
                    for (k = 0; elt.type == RowInfo.ROW_EQUAL && k < 100; k++) {
                        // follow the chain
						/*System.out.println("following equal chain from " +
					   i + " " + qp + " to " + elt.nodeEq);*/
                        qp = elt.nodeEq;
                        elt = circuitRowInfo[qp];
                    }
                    if (elt.type == RowInfo.ROW_EQUAL) {
                        // break equal chains
                        //System.out.println("Break equal chain");
                        elt.type = RowInfo.ROW_NORMAL;
                        continue;
                    }
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        System.out.println("type already " + elt.type + " for " + qp + "!");
                        continue;
                    }
                    elt.type = RowInfo.ROW_CONST;
                    elt.value = (circuitRightSide[i] + rsadd) / qv;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " * " + qv + " = const " + elt.value);
                    i = -1; // start over from scratch
                } else if (circuitRightSide[i] + rsadd == 0) {
                    // we found a row with only two nonzero entries, and one
                    // is the negative of the other; the values are equal
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        //System.out.println("swapping");
                        int qq = qm;
                        qm = qp;
                        qp = qq;
                        elt = circuitRowInfo[qp];
                        if (elt.type != RowInfo.ROW_NORMAL) {
                            // we should follow the chain here, but this
                            // hardly ever happens so it's not worth worrying
                            // about
                            System.out.println("swap failed");
                            continue;
                        }
                    }
                    elt.type = RowInfo.ROW_EQUAL;
                    elt.nodeEq = qm;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " = " + qm);
                }
            }
        }
        //System.out.println("ac7");

        // find size of new matrix
        int nn = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_NORMAL) {
                elt.mapCol = nn++;
                //System.out.println("col " + i + " maps to " + elt.mapCol);
                continue;
            }
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = null;
                // resolve chains of equality; 100 max steps to avoid loops
                for (j = 0; j != 100; j++) {
                    e2 = circuitRowInfo[elt.nodeEq];
                    if (e2.type != RowInfo.ROW_EQUAL)
                        break;
                    if (i == e2.nodeEq)
                        break;
                    elt.nodeEq = e2.nodeEq;
                }
            }
            if (elt.type == RowInfo.ROW_CONST)
                elt.mapCol = -1;
        }
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = circuitRowInfo[elt.nodeEq];
                if (e2.type == RowInfo.ROW_CONST) {
                    // if something is equal to a const, it's a const
                    elt.type = e2.type;
                    elt.value = e2.value;
                    elt.mapCol = -1;
                    //System.out.println(i + " = [late]const " + elt.value);
                } else {
                    elt.mapCol = e2.mapCol;
                    //System.out.println(i + " maps to: " + e2.mapCol);
                }
            }
        }
        //System.out.println("ac8");

		/*System.out.println("matrixSize = " + matrixSize);

	for (j = 0; j != circuitMatrixSize; j++) {
	    System.out.println(j + ": ");
	    for (i = 0; i != circuitMatrixSize; i++)
		System.out.print(circuitMatrix[j][i] + " ");
	    System.out.print("  " + circuitRightSide[j] + "\n");
	}
	System.out.print("\n");*/


        // make the new, simplified matrix
        int newsize = nn;
        double newmatx[][] = new double[newsize][newsize];
        double newrs[] = new double[newsize];
        int ii = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo rri = circuitRowInfo[i];
            if (rri.dropRow) {
                rri.mapRow = -1;
                continue;
            }
            newrs[ii] = circuitRightSide[i];
            rri.mapRow = ii;
            //System.out.println("Row " + i + " maps to " + ii);
            for (j = 0; j != matrixSize; j++) {
                RowInfo ri = circuitRowInfo[j];
                if (ri.type == RowInfo.ROW_CONST)
                    newrs[ii] -= ri.value * circuitMatrix[i][j];
                else
                    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
            }
            ii++;
        }

        circuitMatrix = newmatx;
        circuitRightSide = newrs;
        matrixSize = circuitMatrixSize = newsize;
        for (i = 0; i != matrixSize; i++)
            origRightSide[i] = circuitRightSide[i];
        for (i = 0; i != matrixSize; i++)
            for (j = 0; j != matrixSize; j++)
                origMatrix[i][j] = circuitMatrix[i][j];
        circuitNeedsMap = true;

		/*
	System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
	for (j = 0; j != circuitMatrixSize; j++) {
	    for (i = 0; i != circuitMatrixSize; i++)
		System.out.print(circuitMatrix[j][i] + " ");
	    System.out.print("  " + circuitRightSide[j] + "\n");
	}
	System.out.print("\n");*/

        // if a matrix is linear, we can do the lu_factor here instead of
        // needing to do it every frame
        if (!circuitNonLinear) {
            if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
                stop("Singular matrix!", null);
                return;
            }
        }
    }

    class FindPathInfo {
        static final int INDUCT = 1;
        static final int VOLTAGE = 2;
        static final int SHORT = 3;
        static final int CAP_V = 4;
        boolean used[];
        int dest;
        CircuitElm firstElm;
        int type;

        FindPathInfo(int t, CircuitElm e, int d) {
            dest = d;
            type = t;
            firstElm = e;
            used = new boolean[nodeList.size()];
        }

        boolean findPath(int n1) {
            return findPath(n1, -1);
        }

        boolean findPath(int n1, int depth) {
            if (n1 == dest)
                return true;
            if (depth-- == 0)
                return false;
            if (used[n1]) {
                //System.out.println("used " + n1);
                return false;
            }
            used[n1] = true;
            int i;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                if (ce == firstElm)
                    continue;
                if (type == INDUCT) {
                    if (ce instanceof CurrentElm)
                        continue;
                }
                if (type == VOLTAGE) {
                    if (!(ce.isWire() || ce instanceof VoltageElm))
                        continue;
                }
                if (type == SHORT && !ce.isWire())
                    continue;
                if (type == CAP_V) {
                    if (!(ce.isWire() || ce instanceof CapacitorElm ||
                            ce instanceof VoltageElm))
                        continue;
                }
                if (n1 == 0) {
                    // look for posts which have a ground connection;
                    // our path can go through ground
                    int j;
                    for (j = 0; j != ce.getPostCount(); j++)
                        if (ce.hasGroundConnection(j) &&
                                findPath(ce.getNode(j), depth)) {
                            used[n1] = false;
                            return true;
                        }
                }
                int j;
                for (j = 0; j != ce.getPostCount(); j++) {
                    //System.out.println(ce + " " + ce.getNode(j));
                    if (ce.getNode(j) == n1)
                        break;
                }
                if (j == ce.getPostCount())
                    continue;
                if (ce.hasGroundConnection(j) && findPath(0, depth)) {
                    //System.out.println(ce + " has ground");
                    used[n1] = false;
                    return true;
                }
                if (type == INDUCT && ce instanceof InductorElm) {
                    double c = ce.getCurrent();
                    if (j == 0)
                        c = -c;
                    //System.out.println("matching " + c + " to " + firstElm.getCurrent());
                    //System.out.println(ce + " " + firstElm);
                    if (Math.abs(c - firstElm.getCurrent()) > 1e-10)
                        continue;
                }
                int k;
                for (k = 0; k != ce.getPostCount(); k++) {
                    if (j == k)
                        continue;
                    //System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
                    if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
                        //System.out.println("got findpath " + n1);
                        used[n1] = false;
                        return true;
                    }
                    //System.out.println("back on findpath " + n1);
                }
            }
            used[n1] = false;
            //System.out.println(n1 + " failed");
            return false;
        }
    }

    ByteArrayOutputStream readUrlData(URL url) throws java.io.IOException {
        Object o = url.getContent();
        FilterInputStream fis = (FilterInputStream) o;
        ByteArrayOutputStream ba = new ByteArrayOutputStream(fis.available());
        int blen = 1024;
        byte b[] = new byte[blen];
        while (true) {
            int len = fis.read(b);
            if (len <= 0)
                break;
            ba.write(b, 0, len);
        }
        return ba;
    }

    URL getCodeBase() {
        try {
            if (applet != null)
                return applet.getCodeBase();
            File f = new File(".");
            return new URL("file:" + f.getCanonicalPath() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void getSetupList(Menu menu, boolean retry) {
        Menu stack[] = new Menu[6];
        int stackptr = 0;
        stack[stackptr++] = menu;
        try {
            URL url = new URL(getCodeBase() + "setuplist.txt");
            ByteArrayOutputStream ba = readUrlData(url);
            byte b[] = ba.toByteArray();
            int len = ba.size();
            int p;
            if (len == 0 || b[0] != '#') {
                // got a redirect, try again
                getSetupList(menu, true);
                return;
            }
            for (p = 0; p < len; ) {
                int l;
                for (l = 0; l != len - p; l++)
                    if (b[l + p] == '\n') {
                        l++;
                        break;
                    }
                String line = new String(b, p, l - 1);
                if (line.charAt(0) == '#')
                    ;
                else if (line.charAt(0) == '+') {
                    Menu n = new Menu(line.substring(1));
                    menu.add(n);
                    menu = stack[stackptr++] = n;
                } else if (line.charAt(0) == '-') {
                    menu = stack[--stackptr - 1];
                } else {
                    int i = line.indexOf(' ');
                    if (i > 0) {
                        String title = line.substring(i + 1);
                        boolean first = false;
                        if (line.charAt(0) == '>')
                            first = true;
                        String file = line.substring(first ? 1 : 0, i);
//                        menu.add(getMenuItem(title, "setup " + file));
                        if (first && startCircuit == null) {
                            startCircuit = file;
                            startLabel = title;
                        }
                    }
                }
                p += l;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't read setuplist.txt!");
        }
    }

    public CircuitElm getElm(int n) {
        if (n >= elmList.size())
            return null;
        return elmList.elementAt(n);
    }

    int locateElm(CircuitElm elm) {
        int i;
        for (i = 0; i != elmList.size(); i++)
            if (elm == elmList.elementAt(i))
                return i;
        return -1;
    }

    public CircuitNode getCircuitNode(int n) {
        if (n >= nodeList.size())
            return null;
        return nodeList.elementAt(n);
    }

    void readHint(StringTokenizer st) {
        hintType = new Integer(st.nextToken()).intValue();
        hintItem1 = new Integer(st.nextToken()).intValue();
        hintItem2 = new Integer(st.nextToken()).intValue();
    }

    void readOptions(StringTokenizer st) {
        int flags = new Integer(st.nextToken()).intValue();
//        dotsCheckItem.setState((flags & 1) != 0);
//        smallGridCheckItem.setState((flags & 2) != 0);
//        voltsCheckItem.setState((flags & 4) == 0);
//        powerCheckItem.setState((flags & 8) == 8);
//        showValuesCheckItem.setState((flags & 16) == 0);
        timeStep = new Double(st.nextToken()).doubleValue();
        double sp = new Double(st.nextToken()).doubleValue();
        int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
        //int sp2 = (int) (Math.log(sp)*24+1.5);
        speedBar.setValue(sp2);
        currentBar.setValue(new Integer(st.nextToken()).intValue());
        CircuitElm.voltageRange = new Double(st.nextToken()).doubleValue();
        try {
            powerBar.setValue(new Integer(st.nextToken()).intValue());
        } catch (Exception e) {
        }
//        setGrid();
    }

    double getIterCount() {
        if (speedBar.getValue() == 0)
            return 0;
        //return (Math.exp((speedBar.getValue()-1)/24.) + .5);
        return .1 * Math.exp((speedBar.getValue() - 61) / 24.);
    }

    void stop(String s, CircuitElm ce) {
        stopMessage = s;
        circuitMatrix = null;
        stopElm = ce;
        stoppedCheck = true;
        analyzeFlag = false;
    }

    // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    void stampVCVS(int n1, int n2, double coef, int vs) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, coef);
        stampMatrix(vn, n2, -coef);
    }

    // stamp independent voltage source #vs, from n1 to n2, amount v
    void stampVoltageSource(int n1, int n2, int vs, double v) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn, v);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    void stampVoltageSource(int n1, int n2, int vs) {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    void updateVoltageSource(int n1, int n2, int vs, double v) {
        int vn = nodeList.size() + vs;
        stampRightSide(vn, v);
    }

    void stampResistor(int n1, int n2, double r) {
        double r0 = 1 / r;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            System.out.print("bad resistance " + r + " " + r0 + "\n");
            int a = 0;
            a /= a;
        }
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    void stampConductance(int n1, int n2, double r0) {
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    // current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
    void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
        stampMatrix(cn1, vn1, g);
        stampMatrix(cn2, vn2, g);
        stampMatrix(cn1, vn2, -g);
        stampMatrix(cn2, vn1, -g);
    }

    void stampCurrentSource(int n1, int n2, double i) {
        stampRightSide(n1, -i);
        stampRightSide(n2, i);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    void stampCCCS(int n1, int n2, int vs, double gain) {
        int vn = nodeList.size() + vs;
        stampMatrix(n1, vn, gain);
        stampMatrix(n2, vn, -gain);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    void stampMatrix(int i, int j, double x) {
        if (i > 0 && j > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                RowInfo ri = circuitRowInfo[j - 1];
                if (ri.type == RowInfo.ROW_CONST) {
                    //System.out.println("Stamping constant " + i + " " + j + " " + x);
                    circuitRightSide[i] -= x * ri.value;
                    return;
                }
                j = ri.mapCol;
                //System.out.println("stamping " + i + " " + j + " " + x);
            } else {
                i--;
                j--;
            }
            circuitMatrix[i][j] += x;
        }
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    void stampRightSide(int i, double x) {
        if (i > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                //System.out.println("stamping " + i + " " + x);
            } else
                i--;
            circuitRightSide[i] += x;
        }
    }

    // indicate that the value on the right side of row i changes in doStep()
    void stampRightSide(int i) {
        //System.out.println("rschanges true " + (i-1));
        if (i > 0)
            circuitRowInfo[i - 1].rsChanges = true;
    }

    // indicate that the values on the left side of row i change in doStep()
    void stampNonLinear(int i) {
        if (i > 0)
            circuitRowInfo[i - 1].lsChanges = true;
    }

    // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the lu_solve() routine.
    boolean lu_factor(double a[][], int n, int ipvt[]) {
        double scaleFactors[];
        int i, j, k;

        scaleFactors = new double[n];

        // divide each row by its largest element, keeping track of the
        // scaling factors
        for (i = 0; i != n; i++) {
            double largest = 0;
            for (j = 0; j != n; j++) {
                double x = Math.abs(a[i][j]);
                if (x > largest)
                    largest = x;
            }
            // if all zeros, it's a singular matrix
            if (largest == 0)
                return false;
            scaleFactors[i] = 1.0 / largest;
        }

        // use Crout's method; loop through the columns
        for (j = 0; j != n; j++) {

            // calculate upper triangular elements for this column
            for (i = 0; i != j; i++) {
                double q = a[i][j];
                for (k = 0; k != i; k++)
                    q -= a[i][k] * a[k][j];
                a[i][j] = q;
            }

            // calculate lower triangular elements for this column
            double largest = 0;
            int largestRow = -1;
            for (i = j; i != n; i++) {
                double q = a[i][j];
                for (k = 0; k != j; k++)
                    q -= a[i][k] * a[k][j];
                a[i][j] = q;
                double x = Math.abs(q);
                if (x >= largest) {
                    largest = x;
                    largestRow = i;
                }
            }

            // pivoting
            if (j != largestRow) {
                double x;
                for (k = 0; k != n; k++) {
                    x = a[largestRow][k];
                    a[largestRow][k] = a[j][k];
                    a[j][k] = x;
                }
                scaleFactors[largestRow] = scaleFactors[j];
            }

            // keep track of row interchanges
            ipvt[j] = largestRow;

            // avoid zeros
            if (a[j][j] == 0.0) {
                System.out.println("avoided zero");
                a[j][j] = 1e-18;
            }

            if (j != n - 1) {
                double mult = 1.0 / a[j][j];
                for (i = j + 1; i != n; i++)
                    a[i][j] *= mult;
            }
        }
        return true;
    }

    // Solves the set of n linear equations using a LU factorization
    // previously performed by lu_factor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    void lu_solve(double a[][], int n, int ipvt[], double b[]) {
        int i;

        // find first nonzero b element
        for (i = 0; i != n; i++) {
            int row = ipvt[i];

            double swap = b[row];
            b[row] = b[i];
            b[i] = swap;
            if (swap != 0)
                break;
        }

        int bi = i++;
        for (; i < n; i++) {
            int row = ipvt[i];
            int j;
            double tot = b[row];

            b[row] = b[i];
            // forward substitution using the lower triangular matrix
            for (j = bi; j < i; j++)
                tot -= a[i][j] * b[j];
            b[i] = tot;
        }
        for (i = n - 1; i >= 0; i--) {
            double tot = b[i];

            // back-substitution using the upper triangular matrix
            int j;
            for (j = i + 1; j != n; j++)
                tot -= a[i][j] * b[j];
            b[i] = tot / a[i][i];
        }
    }

    CheckboxMenuItem getCheckItem(String s) {
        CheckboxMenuItem mi = new CheckboxMenuItem(s);
//        mi.addItemListener(this);
//        mi.setActionCommand("");
        return mi;
    }

    CheckboxMenuItem getClassCheckItem(String s, String t) {
        try {
            Class c = Class.forName(t);
            CircuitElm elm = constructElement(c, 0, 0);
            register(c, elm);
            int dt = 0;
            if (elm.needsShortcut() && elm.getDumpClass() == c) {
                dt = elm.getDumpType();
                s += " (" + (char) dt + ")";
            }
            elm.delete();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return getCheckItem(s, t);
    }

    CheckboxMenuItem getCheckItem(String s, String t) {
        CheckboxMenuItem mi = new CheckboxMenuItem(s);
//        mi.addItemListener(this);
//        mi.setActionCommand(t);
        return mi;
    }

    void register(Class c, CircuitElm elm) {
        int t = elm.getDumpType();
        if (t == 0) {
            System.out.println("no dump type: " + c);
            return;
        }
        Class<Scope> dclass = elm.getDumpClass();
        if (dumpTypes[t] == dclass)
            return;
        if (dumpTypes[t] != null) {
            System.out.println("dump type conflict: " + c + " " +
                    dumpTypes[t]);
            return;
        }
        dumpTypes[t] = dclass;
    }

    CircuitElm constructElement(Class c, int x0, int y0) {
        // find element class
        Class carr[] = new Class[2];
        //carr[0] = getClass();
        carr[0] = carr[1] = int.class;
        Constructor cstr = null;
        try {
            cstr = c.getConstructor(carr);
        } catch (NoSuchMethodException ee) {
            System.out.println("caught NoSuchMethodException " + c);
            return null;
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }

        // invoke constructor with starting coordinates
        Object oarr[] = new Object[2];
        oarr[0] = new Integer(x0);
        oarr[1] = new Integer(y0);
        try {
            return (CircuitElm) cstr.newInstance(oarr);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }

    int snapGrid(int x) {
        return (x + gridRound) & gridMask;
    }

}
