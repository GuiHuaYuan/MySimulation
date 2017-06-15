/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulationpanel;

import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import gov.nasa.worldwindx.applications.worldwindow.features.AbstractFeaturePanel;
//import simulationpanel.iostruct.*;
import coverage.iostruct.*;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.table.*;

import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import zzlorbittest3.ClientTest;
import zzlorbittest3.PdfReporter;

/**
 *
 * @author ZZL
 */
public class SimulationDialog extends JDialog {

    public WorldWindow wwd;
    private RenderableLayer regionRenderableLayer;//��ͼ��������ʾ��ӵĵ�������������״
    public JTabbedPane tabbedpane;
    public RegionManagerClass regionManager;
    public SatelliteManagerClass satelliteManager;
    private JTextField startTimeText;
    private JTextField endTimeText;

    private JCheckBox weatherConsiderCheckBox;
    private JCheckBox snowConsiderCheckBox;
    private JCheckBox dixingConsiderCheckBox;
    private JTextField plainSwingAngleTextField;
    private JTextField mountainSwingAngleTextField ;
    
    public AccessInput accessInput=null;
    public AccessOutput accessOutput=null;

    public SimulationDialog(WorldWindow wwd, Frame owner) {
        super(owner);
//        wwd.getModel().getLayers().add(regionRenderableLayer);
        regionRenderableLayer=(RenderableLayer)wwd.getModel().getLayers().getLayerByName("������������");
        regionManager = new RegionManagerClass(regionRenderableLayer);
        satelliteManager = new SatelliteManagerClass();

        for (SatelliteInput satelliteInput : satelliteManager.allSatelliteArray) {

            for (SensorInput sensorInput : satelliteInput.sensorInput) {

                for (SensorModeInput smi : sensorInput.sensorMode) {
//                   smi.Mode
                    String str = String.format("%s\t%s\t%f\t%f\t%s\t%f\t%f\t%f\t%f\t%f\t%s\t%f", satelliteInput.ChineseSatName,satelliteInput.id, satelliteInput.leftAngle, satelliteInput.rightAngle, sensorInput.senName, sensorInput.fov, sensorInput.swath, sensorInput.leftAngle, sensorInput.rightAngle, sensorInput.maxAreaPerDay, smi.Mode, smi.resolution);
                    System.out.println(str);

                }

            }
        }

        this.wwd = wwd;
        JPanel SatellitePanel = makeSatellitePanel();
        JScrollPane OutputPanel = makeOutputPanel();
        tabbedpane = new JTabbedPane();
        tabbedpane.addTab("����", SatellitePanel);
        tabbedpane.addTab("����", OutputPanel);
//        tabbedpane.setSelectedIndex(1);

        JPanel GlobalPanel = new JPanel(new BorderLayout());
        GlobalPanel.add(makeBackgroundPanel(), BorderLayout.NORTH);
        GlobalPanel.add(tabbedpane, BorderLayout.CENTER);
        this.getContentPane().add(GlobalPanel);
        this.setTitle("�� ��");
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(333, 800));

        this.pack();
        this.setLocationRelativeTo(null);

    }

    /**
     * *************************************
     * ���� ѡ�
     ***************************************
     */
    private JPanel makeSatellitePanel() {

        /////////        satellitelistPanel/satellitelistScrollPanel  ��ʼ��    /////////////////
        final JPanel satellitelistPanel = new JPanel();
        satellitelistPanel.setLayout(new GridLayout(0, 1));
        for (SatelliteInput sli : satelliteManager.allSatelliteArray) {
            satellitelistPanel.add(new JCheckBox(sli.satName));
        }
        JPanel satellitelistBorderPanel = new JPanel(new BorderLayout());
        satellitelistBorderPanel.add(satellitelistPanel, BorderLayout.NORTH);
        JScrollPane satellitelistScrollPanel = new JScrollPane(satellitelistBorderPanel);
        satellitelistScrollPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "����"));
        satellitelistScrollPanel.getVerticalScrollBar().setUnitIncrement(20);
        //sliPanel.add(jsp, BorderLayout.CENTER);

        /////////        sensorModelPanel  ��ʼ��    /////////////////
        JPanel sensorModelPanel = new JPanel();
        sensorModelPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "������"));
        sensorModelPanel.setLayout(new GridLayout(0, 1));
        final JRadioButton SelectSensorDirectly = new JRadioButton("ֱ��ѡ��");
        sensorModelPanel.add(SelectSensorDirectly);
        final JRadioButton SelectSensorByClass = new JRadioButton("�����ѡ��");
        JPanel SelectSensorByClassPanel = new JPanel();
        SelectSensorByClassPanel.setLayout(new BoxLayout(SelectSensorByClassPanel, BoxLayout.X_AXIS));
        SelectSensorByClassPanel.add(SelectSensorByClass);
        SelectSensorByClassPanel.add(Box.createGlue());
        SelectSensorByClassPanel.add(new JLabel("�������"));
        final JComboBox classComboBox = new JComboBox();
        classComboBox.addItem("1");
        classComboBox.addItem("2");
        classComboBox.addItem("3");
        classComboBox.addItem("4");
        classComboBox.setSelectedIndex(0);
        classComboBox.setEnabled(false);
        classComboBox.setMaximumSize(new Dimension(1000, 1000));
        SelectSensorByClassPanel.add(classComboBox);
        sensorModelPanel.add(SelectSensorByClassPanel);
        JRadioButton SelectSensorByResolution = new JRadioButton("���ֱ���ѡ��");
        JPanel SelectSensorByResolutionPanel = new JPanel();
        SelectSensorByResolutionPanel.setLayout(new BoxLayout(SelectSensorByResolutionPanel, BoxLayout.X_AXIS));
        SelectSensorByResolutionPanel.add(SelectSensorByResolution);
        SelectSensorByResolutionPanel.add(Box.createGlue());
        SelectSensorByResolutionPanel.add(new JLabel("�ֱ��ʣ�"));
        final JTextField resolutionTextField = new JTextField("5", 4);
        resolutionTextField.setEnabled(false);
        resolutionTextField.setMaximumSize(new Dimension(1000, 1000));//?
        SelectSensorByResolutionPanel.add(resolutionTextField);
        SelectSensorByResolutionPanel.add(new JLabel("  ��"));

        SelectSensorDirectly.setSelected(true);
        SelectSensorDirectly.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classComboBox.setEnabled(false);
                resolutionTextField.setEnabled(false);
            }
        });
        SelectSensorByClass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classComboBox.setEnabled(true);
                resolutionTextField.setEnabled(false);
            }
        });
        SelectSensorByResolution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classComboBox.setEnabled(false);
                resolutionTextField.setEnabled(true);
            }
        });
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(SelectSensorDirectly);
        buttonGroup.add(SelectSensorByClass);
        buttonGroup.add(SelectSensorByResolution);
        sensorModelPanel.add(SelectSensorByResolutionPanel);

        /////////        btnPanel  ��ʼ��    /////////////////
        final SelectSensorDirectlyDialog selectSensorDirectlyDialog = new SelectSensorDirectlyDialog(this);
        JButton btn = new JButton("��һ��");

        /**
         * ************************ ����ѡ��ѡ� ȷ����ť      ***********************************
         */
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ArrayList<SatelliteInput> sliList = new ArrayList<SatelliteInput>();
                //sliList���洢��satelliteManager.allSatelliteArray������ѡ��satellite�Ķ��󣬲�Ҫ����sliList��Ԫ�ص����ݣ�
                //����Ҫ���ģ��Ƚ�sliListͨ��AccessInput�ı��ݺ����õ���������ͨ������filter�������й���
                Component[] satelliteCheckBox = satellitelistPanel.getComponents();
                for (Component cp : satelliteCheckBox) {
                    JCheckBox jc = ((JCheckBox) cp);
                    if (jc.isSelected()) {
                        for (SatelliteInput sli : satelliteManager.allSatelliteArray) {
                            if (jc.getText().equals(sli.satName)) {
                                sliList.add(sli);
                                break;
                            }
                        }
                    }
                }
                if (SelectSensorDirectly.isSelected()) {
                    selectSensorDirectlyDialog.fresh(sliList);
                    selectSensorDirectlyDialog.setVisible(true);
                } else if (SelectSensorByClass.isSelected()) {
                    //              ͨ�����ѡ�񴫸���
                    AccessInput ai = new AccessInput(), ai2;
                    ai.satelliteInput = (SatelliteInput[]) sliList.toArray(new SatelliteInput[sliList.size()]);
                    ai2 = ai.clone();

                    int classIndex = classComboBox.getSelectedIndex() + 1;
                    ProcessResource.SensorFileterByClass(ai2, classIndex);
                    satelliteManager.currentSelectedSatelliteArray = ai2.satelliteInput;

                    satelliteManager.refreshModel();
                    tabbedpane.setSelectedIndex(1);

                } else {
                    //              ͨ���ֱ���ѡ�񴫸���
                    AccessInput ai = new AccessInput(), ai2;
                    ai.satelliteInput = (SatelliteInput[]) sliList.toArray(new SatelliteInput[sliList.size()]);
                    ai2 = ai.clone();
                    double res = 5;
                    try {
                        res = Double.parseDouble(resolutionTextField.getText());
                    } catch (Exception ex) {
                        res = 5;
                        System.out.println("prase double false!");
                    }
                    ProcessResource.SensorFileterByResolution(ai2, res);
                    satelliteManager.currentSelectedSatelliteArray = ai2.satelliteInput;
                    satelliteManager.refreshModel();
                    tabbedpane.setSelectedIndex(1);
                }
            }
        });

        JPanel sliPanel = new JPanel(new BorderLayout());
        sliPanel.add(sensorModelPanel, BorderLayout.NORTH);
        sliPanel.add(satellitelistScrollPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btn);
        Box northBox = Box.createVerticalBox();
        final JCheckBox selectAllCheckBox = new JCheckBox("ȫѡ");
        JPanel selectAllPanel = new JPanel(new GridLayout(1, 1));
        selectAllPanel.add(selectAllCheckBox);
        northBox.add(selectAllPanel);
        northBox.add(Box.createVerticalStrut(20));
        northBox.add(btnPanel);
        sliPanel.add(northBox, BorderLayout.SOUTH);

        selectAllCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] satelliteCheckBox = satellitelistPanel.getComponents();
                for (Component cp : satelliteCheckBox) {
                    JCheckBox jc = ((JCheckBox) cp);
                    jc.setSelected(selectAllCheckBox.isSelected());
                }
            }
        });

        return sliPanel;
    }

    /**
     * ***************************************
     * ���� ѡ� ��5�������
     ***************************************
     */
    private JScrollPane makeOutputPanel() {

        Box settingBox = Box.createVerticalBox();
        settingBox.add(makeSatelliteListPanel());
        settingBox.add(makeGroundTaskRegionPanel());
        settingBox.add(makeTimePanel());
        settingBox.add(makeModelSettingPanel());
        settingBox.add(makeButtonPanel());

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(settingBox, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(outputPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setEnabled(false);

        return scrollPane;
    }
    
    /**
     * ***************************************
     * ��ɫ�������
     ***************************************
     */
    private JPanel makeBackgroundPanel() {
        class ShadePanel extends JPanel {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint p = new GradientPaint(0, 0, new Color(29, 78, 169, 200), 0, getHeight(), new Color(93, 158, 223, 200));
                Paint oldPaint = g2.getPaint();
                g2.setPaint(p);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(oldPaint);
            }
        }
        ShadePanel backgroundPanel = new ShadePanel();
        backgroundPanel.setToolTipText("�滮ʱ��ѡ��Ĳ�ͬ��Լ������ .");
        JLabel lbl = new JLabel();
        lbl.setText("�� ��");
        lbl.setFont(new Font("����", Font.BOLD, 14));
        lbl.setForeground(Color.white);
        backgroundPanel.add(lbl);
        return backgroundPanel;
    }

    /**
     * ***************************************
     * ��ѡ�����б� �� ����ѡ���1����
     ***************************************
     */
    private JPanel makeSatelliteListPanel() {
        Object[] columnString = new String[]{"����", "������", "ģʽ", "�ֱ���"};
        Object[][] obj = new Object[][]{{}};

        satelliteManager.selectedSensorTableModel = new DefaultTableModel(obj, columnString);
        satelliteManager.selectedSensorTableModel.removeRow(0);
        final JTable selectedSensorTable = new JTable(satelliteManager.selectedSensorTableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        selectedSensorTable.getTableHeader().setReorderingAllowed(false);

        Box satelliteBox = Box.createVerticalBox();

        JScrollPane scrollTable = new JScrollPane(selectedSensorTable);
        scrollTable.getVerticalScrollBar().setUnitIncrement(20);
        scrollTable.setPreferredSize(new Dimension(0, 100));
        satelliteBox.add(scrollTable);

        JButton deleteButton = new JButton("ɾ��");
        JPanel deleteButtonPanel = new JPanel();
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = selectedSensorTable.getSelectedRow();
//                System.out.println(selectedIndex);
                if (selectedIndex >= 0) {
                    ArrayList<String> selectSensorList = new ArrayList<String>();
                    if (satelliteManager.currentSelectedSatelliteArray != null) {
                        for (SatelliteInput sli : satelliteManager.currentSelectedSatelliteArray) {
                            for (SensorInput ssi : sli.sensorInput) {
                                for (SensorModeInput smi : ssi.sensorMode) {
                                    selectSensorList.add(smi.SatelliteSensorModeName);
                                }
                            }
                        }
                    }
                    if (selectedIndex < selectSensorList.size()) {
                        selectSensorList.remove(selectedIndex);
                        AccessInput ai = new AccessInput();
                        ai.satelliteInput = satelliteManager.currentSelectedSatelliteArray;
                        ProcessResource.SensorFileterByName(ai, selectSensorList);//ɸѡ��ѡ�������
                        satelliteManager.currentSelectedSatelliteArray = ai.satelliteInput;
                        satelliteManager.refreshModel();
                        tabbedpane.setSelectedIndex(1);
                    } else {
                        System.out.println("Delete False!");
                    }
                }
            }
        });

        deleteButtonPanel.add(deleteButton);
        satelliteBox.add(deleteButtonPanel);

        JPanel satellitePanel = new JPanel(new GridLayout(1, 1));
        satellitePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "��ѡ����"));
        satellitePanel.add(satelliteBox);
        return satellitePanel;
    }
    
    /**
     * ***************************************
     * ���������������� �� ����ѡ���2����
     ***************************************
     */
    private JPanel makeGroundTaskRegionPanel() {

        Box vBox, hBox;

        ///////////         ����ѡ�          ///////////////////
        vBox = Box.createVerticalBox();

        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalStrut(10));
        hBox.add(new JLabel("�����ʶ��"));
        hBox.add(Box.createHorizontalStrut(35));
        final JTextField regionIdentification2 = new JTextField(8);
        regionIdentification2.setMaximumSize(new Dimension(1000, 1000));
        hBox.add(regionIdentification2);
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBox);

        final JComboBox shengComboBox = new JComboBox();
        shengComboBox.addItem("ȫ��");
        for (String str : regionManager.shengMap.values()) {
            shengComboBox.addItem(str);
        }

        final JComboBox shiComboBox = new JComboBox();
        final JComboBox xianComboBox = new JComboBox();

        //                ѡ�� ʡ
        shengComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("sheng");
                String shengNameString = (String) shengComboBox.getSelectedItem();
                shiComboBox.removeAllItems();
                xianComboBox.removeAllItems();
                if (shengNameString.equals("ȫ��")) {

                } else {
                    int shengCode = 0;
                    for (Map.Entry<Integer, String> entry : regionManager.shengMap.entrySet()) {
                        if (entry.getValue().equals(shengNameString)) {
                            shengCode = entry.getKey();
                            break;
                        }
                    }
                    shiComboBox.addItem("ȫ��");
                    for (Map.Entry<Integer, String> entry : regionManager.shiMap.entrySet()) {
                        if (entry.getKey() / 100 == shengCode) {
                            shiComboBox.addItem(entry.getValue());
                        }
                    }
                }
            }
        });

        //                ѡ�� ��
        shiComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("shi");
                if (shiComboBox.getItemCount() <= 0) {
                    return;
                }
                String shengNameString = (String) shengComboBox.getSelectedItem();
                String shiNameString = (String) shiComboBox.getSelectedItem();
                xianComboBox.removeAllItems();
                if (shiNameString.equals("ȫ��")) {

                } else {
                    int shengCode = 0;
                    for (Map.Entry<Integer, String> entry : regionManager.shengMap.entrySet()) {
                        if (entry.getValue().equals(shengNameString)) {
                            shengCode = entry.getKey();
                            break;
                        }
                    }
                    int shiCode = 0;
                    for (Map.Entry<Integer, String> entry : regionManager.shiMap.entrySet()) {
                        if (entry.getKey() / 100 == shengCode && entry.getValue().equals(shiNameString)) {
                            shiCode = entry.getKey();
                            break;
                        }
                    }
                    xianComboBox.addItem("ȫ��");
                    for (Map.Entry<Integer, String> entry : regionManager.xianMap.entrySet()) {
                        if (entry.getKey() / 10000 == shengCode && entry.getKey() / 100 == shiCode) {
                            xianComboBox.addItem(entry.getValue());
                        }
                    }
                }
            }
        });

        Box provBox = Box.createHorizontalBox();
        provBox.add(new JLabel("ʡ��"));
        provBox.add(shengComboBox);
        provBox.add(Box.createHorizontalStrut(20));
        Box cityBox = Box.createHorizontalBox();
        cityBox.add(new JLabel("�У�"));
        cityBox.add(shiComboBox);
        cityBox.add(Box.createHorizontalStrut(20));
        Box countyBox = Box.createHorizontalBox();
        countyBox.add(new JLabel("�أ�"));
        countyBox.add(xianComboBox);
        countyBox.add(Box.createHorizontalStrut(20));

        JPanel ProvCityPanel = new JPanel(new GridLayout(1, 2));
        ProvCityPanel.add(provBox);
        ProvCityPanel.add(cityBox);
        JPanel CountyPanel = new JPanel(new GridLayout(1, 2));
        CountyPanel.add(countyBox);
        CountyPanel.add(new JLabel(""));

        vBox.add(Box.createVerticalStrut(10));
        vBox.add(ProvCityPanel);
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(CountyPanel);

        //�����������
        JButton addCountyButton = new JButton("���");
        //listener
        addCountyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String shengNameString = (String) shengComboBox.getSelectedItem();
                String shiNameString = (String) shiComboBox.getSelectedItem();
                String xianNameString = (String) xianComboBox.getSelectedItem();

//                System.out.println(strProv+strCity+CountyProv);
                final String str = regionIdentification2.getText().trim();
                if (str.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "����������Ϊ�գ���������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                for (String s : regionManager.getAllIdentification()) {
                    if (str.equals(s)) {
                        JOptionPane.showMessageDialog(null, "�����������ظ�����������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                regionManager.addRegionByCounty(str, shengNameString, shiNameString, xianNameString);
                wwd.redraw();

            }
        });
        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalGlue());
        hBox.add(addCountyButton);
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBox);

        JPanel selectCountyPanel = new JPanel(new BorderLayout());
        selectCountyPanel.add(vBox, BorderLayout.NORTH);

        ///////////         �ļ�ѡ�          ///////////////////
        vBox = Box.createVerticalBox();

        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalStrut(10));
        hBox.add(new JLabel("�����ʶ��"));
        hBox.add(Box.createHorizontalStrut(35));
        final JTextField regionIdentification1 = new JTextField(8);
        regionIdentification1.setMaximumSize(new Dimension(1000, 1000));
        hBox.add(regionIdentification1);
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBox);

        hBox = Box.createHorizontalBox();
        final JTextField shpFilePathTextField;
        shpFilePathTextField = new JTextField(20);
        shpFilePathTextField.setMaximumSize(new Dimension(1000, 1000));
        hBox.add(Box.createHorizontalStrut(5));
        hBox.add(shpFilePathTextField);
        JButton selectShpFileBtn = new JButton("...");
        selectShpFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                System.out.println(regionIdentification1.getText());
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("ѡ��shp�ļ�");
                FileNameExtensionFilter shpFilter = new FileNameExtensionFilter("(*.shp)", "shp");
                jfc.setFileFilter(shpFilter);
//                jfc.setFileFilter(new FileNameExtensionFilter("(*.png)", "png"));
                //��ȡ����·��
                FileSystemView fsv = FileSystemView.getFileSystemView();
                File desktopDir = fsv.getHomeDirectory();
                jfc.setCurrentDirectory(desktopDir.getAbsoluteFile());
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String path = jfc.getSelectedFile().getAbsolutePath();
                    shpFilePathTextField.setText(path);
                }
            }
        });
        hBox.add(Box.createHorizontalStrut(10));
        hBox.add(selectShpFileBtn);
        hBox.add(Box.createHorizontalStrut(15));
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(hBox);

        JButton addShpFileBtn = new JButton("���");
        //listener
        addShpFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final String str = regionIdentification1.getText().trim();
                if (str.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "����������Ϊ�գ���������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                for (String s : regionManager.getAllIdentification()) {
                    if (str.equals(s)) {
                        JOptionPane.showMessageDialog(null, "�����������ظ�����������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                String shpFilePath = shpFilePathTextField.getText().trim();
                if (shpFilePath.equals("")) {
                    JOptionPane.showMessageDialog(null, "�ļ�·��Ϊ�գ����������ã�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                regionManager.addRegionByShape(str, shpFilePath);
                wwd.redraw();
            }
        });
        JPanel addShpFlieBtnPanel = new JPanel(new FlowLayout());
        addShpFlieBtnPanel.add(addShpFileBtn);
        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalGlue());
        hBox.add(addShpFileBtn);
        hBox.add(Box.createHorizontalStrut(15));
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(hBox);

        JPanel addShpPanel = new JPanel(new BorderLayout());
        addShpPanel.add(vBox, BorderLayout.NORTH);

        ///////////         ����ѡ�          ///////////////////
        vBox = Box.createVerticalBox();

        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalStrut(10));
        hBox.add(new JLabel("�����ʶ��"));
        hBox.add(Box.createHorizontalStrut(35));
        final JTextField regionIdentification3 = new JTextField(8);
        regionIdentification3.setMaximumSize(new Dimension(1000, 1000));
        hBox.add(regionIdentification3);
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBox);

        hBox = Box.createHorizontalBox();
        JButton drawButton = new JButton("�����");

        //-------------------   ��Ӷ����---------------------//
        final MeasureTool measureTool = new MeasureTool(wwd);
        measureTool.setController(new MeasureToolController());
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_POLYGON);

        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final String str = regionIdentification3.getText().trim();
                if (str.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "����������Ϊ�գ���������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                for (String s : regionManager.getAllIdentification()) {
                    if (str.equals(s)) {
                        JOptionPane.showMessageDialog(null, "�����������ظ�����������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
//                selectedRegionModel.addElement(str);
                if (!measureTool.isArmed()) {
                    measureTool.setArmed(true);
                    wwd.addSelectListener(new SelectListener() {
                        @Override
                        public void selected(SelectEvent event) {
                            if (SelectEvent.RIGHT_CLICK.equals(event.getEventAction())) {
                                if (measureTool.isArmed()) {
                                    measureTool.setArmed(false);
                                    measureTool.getPositions();
                                    ArrayList<LatLon> temp = new ArrayList<>();
                                    temp.addAll(measureTool.getPositions());
                                    measureTool.clear();
                                    regionManager.addRegionByDraw(str, temp);
                                    wwd.removeSelectListener(this);
                                    wwd.redraw();
                                }
                            }
                        }
                    });
                } else {
                    System.out.println("Is Armed!");
                }
            }
        });

        hBox.add(Box.createHorizontalGlue());
        hBox.add(drawButton);
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBox);

        hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("��ʾ����������Ҽ������ɫ�ǵ�ȷ����"));
        hBox.add(Box.createHorizontalGlue());
        vBox.add(Box.createVerticalStrut(25));
        vBox.add(hBox);

        JPanel handDrawPanel = new JPanel(new BorderLayout());
        handDrawPanel.add(vBox, BorderLayout.NORTH);

        ///////////         ��ѡ����ѡ�          ///////////////////
        vBox = Box.createVerticalBox();

        final JList selectedRegionList = new JList(regionManager.selectedRegionModel);
        selectedRegionList.setVisibleRowCount(5);
//        selectedRegionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedRegionScrollList = new JScrollPane(selectedRegionList);
        selectedRegionScrollList.getVerticalScrollBar().setUnitIncrement(20);
        vBox.add(selectedRegionScrollList);

        JButton deleteSelectedRegionButton = new JButton("ɾ��");
        deleteSelectedRegionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List l = selectedRegionList.getSelectedValuesList();
                for (Object obj : l) {
                    regionManager.removeRegion((String) obj);
                }
                wwd.redraw();
            }
        });
        hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalGlue());
        hBox.add(deleteSelectedRegionButton);
        hBox.add(Box.createHorizontalStrut(10));
        vBox.add(Box.createVerticalStrut(10));
        vBox.add(hBox);
        vBox.add(Box.createVerticalGlue());

        JPanel selectedRegionPanel = new JPanel(new BorderLayout());
        selectedRegionPanel.add(vBox, BorderLayout.NORTH);

        ///////************************************////////////////////
        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.addTab("����", selectCountyPanel);
        tabbedPanel.addTab("�ļ�", addShpPanel);
        tabbedPanel.addTab("����", handDrawPanel);
        tabbedPanel.addTab("��ѡ����", selectedRegionPanel);

        JPanel groundTaskRegionPanel = new JPanel(new GridLayout(1, 1));
        groundTaskRegionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "����������������"));
        groundTaskRegionPanel.add(tabbedPanel);
        groundTaskRegionPanel.setPreferredSize(new Dimension(305, 210));

        return groundTaskRegionPanel;
    }

    /**
     * ***************************************
     * ʱ��ѡ���� �� ����ѡ���3����
     ***************************************
     */
    private JPanel makeTimePanel() {

        startTimeText = new JTextField(12);
//        startTimeText.setHorizontalAlignment(JTextField.RIGHT);
        startTimeText.setEditable(false);
        startTimeText.setBackground(Color.white);
        startTimeText.setMaximumSize(startTimeText.getPreferredSize());
        Box startTimeHBox = Box.createHorizontalBox();
        startTimeHBox.add(Box.createHorizontalStrut(15));
        startTimeHBox.add(new JLabel("��ʼ��"));
        startTimeHBox.add(Box.createHorizontalStrut(15));
        DateChooser dateChooser1 = DateChooser.getInstance("yyyy-MM-dd");
        dateChooser1.register(startTimeText);
        startTimeText.setText(dateChooser1.getStrDate());
        startTimeHBox.add(startTimeText);
        startTimeHBox.add(Box.createGlue());

        endTimeText = new JTextField(12);
        endTimeText.setEditable(false);
        endTimeText.setBackground(Color.white);
        endTimeText.setMaximumSize(endTimeText.getPreferredSize());
        Box endTimeHBox = Box.createHorizontalBox();
        endTimeHBox.add(Box.createHorizontalStrut(15));
        endTimeHBox.add(new JLabel("������"));
        endTimeHBox.add(Box.createHorizontalStrut(15));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        DateChooser dateChooser2 = DateChooser.getInstance(cal.getTime(), "yyyy-MM-dd");
        dateChooser2.register(endTimeText);
        endTimeText.setText(dateChooser2.getStrDate());
        endTimeHBox.add(endTimeText);
        endTimeHBox.add(Box.createGlue());

        Box box = Box.createVerticalBox();
        box.add(startTimeHBox);
        box.add(Box.createVerticalStrut(8));
        box.add(endTimeHBox);
        box.add(Box.createVerticalStrut(10));

        JPanel setTimePanel = new JPanel(new GridLayout(1, 1));
        setTimePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "����ʱ�����"));
        setTimePanel.add(box);

        return setTimePanel;
    }


    /**
     * ***************************************
     * ģ�Ͳ������� �� ����ѡ���4����
     ***************************************
     */
    private JPanel makeModelSettingPanel() {
        JPanel modelSettingPanel = new JPanel();
        modelSettingPanel.setLayout(new BoxLayout(modelSettingPanel, BoxLayout.Y_AXIS));
        modelSettingPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "ģ�Ͳ�������"));
        ///
        Box modelParaBox = Box.createVerticalBox();
        modelParaBox.add(Box.createVerticalStrut(3));

        JPanel leftAlignPanel;
        leftAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        weatherConsiderCheckBox = new JCheckBox("���Ƕ�������Ӱ��");
        leftAlignPanel.add(weatherConsiderCheckBox);
        modelParaBox.add(leftAlignPanel);
        modelParaBox.add(Box.createVerticalStrut(3));

        leftAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        snowConsiderCheckBox = new JCheckBox("���ǻ�ѩ���");
        snowConsiderCheckBox.setSelected(true);
        leftAlignPanel.add(snowConsiderCheckBox);
        modelParaBox.add(leftAlignPanel);
        modelParaBox.add(Box.createVerticalStrut(3));

        leftAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dixingConsiderCheckBox = new JCheckBox("���ǵ��ζԲ�ڽ�Ҫ��");//
        dixingConsiderCheckBox.setSelected(true);
        dixingConsiderCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean b=dixingConsiderCheckBox.isSelected();
                plainSwingAngleTextField.setEnabled(b);
                mountainSwingAngleTextField.setEnabled(b);
                
            }

        });
        leftAlignPanel.add(dixingConsiderCheckBox);
        modelParaBox.add(leftAlignPanel);
        modelParaBox.add(Box.createVerticalStrut(3));

        leftAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftAlignPanel.add(new JLabel("            ƽԭ������ڽǲ����ڣ�"));
        plainSwingAngleTextField = new JTextField(3);
        plainSwingAngleTextField.setText("25");
        leftAlignPanel.add(plainSwingAngleTextField);
        modelParaBox.add(leftAlignPanel);

        leftAlignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftAlignPanel.add(new JLabel("            ���������ڽǲ����ڣ�"));
        mountainSwingAngleTextField = new JTextField(3);
        mountainSwingAngleTextField.setText("20");
        leftAlignPanel.add(mountainSwingAngleTextField);
        modelParaBox.add(leftAlignPanel);

        modelParaBox.add(Box.createVerticalStrut(3));
        modelSettingPanel.add(Box.createHorizontalStrut(20));
        modelSettingPanel.add(modelParaBox);

        modelParaBox.add(Box.createVerticalStrut(3));
        modelSettingPanel.add(Box.createHorizontalStrut(20));
        modelSettingPanel.add(modelParaBox);

        return modelSettingPanel;

    }

    /**
     * ***************************************
     * ��ť �� ����ѡ���5����
     ***************************************
     */
    private Thread workingThread;
    private JPanel makeButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        final JButton analyseButton = new JButton("��������");
        analyseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (workingThread == null || workingThread.isAlive() == false) {

                    workingThread = new Thread() {
                        public void run() {

                            //��ȡ�û�������Ϣ������web����
                            accessInput = new AccessInput();

                            HashMap<String, MultiPolygon> map = regionManager.regionIdentification2MultiPolygonMap;
                            if (map.size() <= 0) {
                                JOptionPane.showMessageDialog(null, "δѡ�������������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                analyseButton.setEnabled(true);
                                return;
                            } else {
                                Set<String> identificationSet = regionManager.getAllIdentification();
                                String[] identificationArray = (String[]) identificationSet.toArray(new String[identificationSet.size()]);

                                Geometry geom = map.get(identificationArray[0]);
                                for (int i = 1; i < identificationArray.length; i++) {
                                    geom = geom.union(map.get(identificationArray[i]));
                                }
                                //System.out.println(geom);
                                accessInput.target = geom;
                                accessInput.targetName = "TARGET1";
                            }

                            accessInput.startTime = startTimeText.getText() + " 00:00:00.000";
                            accessInput.endTime = endTimeText.getText() + " 00:00:00.000";
                            //System.out.println(ai.startTime);
                            //System.out.println(ai.endTime);

                            accessInput.satelliteInput = satelliteManager.currentSelectedSatelliteArray;
                            if (accessInput.satelliteInput.length <= 0) {
                                JOptionPane.showMessageDialog(null, "δѡ�����ǣ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                analyseButton.setEnabled(true);
                                return;
                            }

                            accessInput.isDixingConseidered = dixingConsiderCheckBox.isSelected();
                            accessInput.isSnowConseidered = snowConsiderCheckBox.isSelected();
                            accessInput.isWeatherConseidered = weatherConsiderCheckBox.isSelected();
                            try {
                                accessInput.mountainSwingAngle = Float.parseFloat(mountainSwingAngleTextField.getText());
                                if (accessInput.mountainSwingAngle <= 0 || accessInput.mountainSwingAngle > 60) {
                                    JOptionPane.showMessageDialog(null, "�����ڽ�Ϊ0-60֮�䣡", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                    analyseButton.setEnabled(true);
                                    return;
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "�����ڽ��������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                analyseButton.setEnabled(true);
                                return;
                            }
                            try {
                                accessInput.plainSwingAngle = Float.parseFloat(plainSwingAngleTextField.getText());
                                if (accessInput.plainSwingAngle <= 0 || accessInput.plainSwingAngle > 60) {
                                    JOptionPane.showMessageDialog(null, "ƽԭ��ڽ�Ϊ0-60֮�䣡", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                    analyseButton.setEnabled(true);
                                    return;
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "ƽԭ��ڽ��������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                analyseButton.setEnabled(true);
                                return;
                            }

                            System.out.println("����web����");
                            accessOutput = new AccessOutput();
                            ClientTest.GetRes(accessInput.clone(), accessOutput);

                            if (accessOutput.isSuccess == false) {
                                JOptionPane.showMessageDialog(null, "����ʧ�ܣ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                                analyseButton.setEnabled(true);
                                return;
                            }
                            try {
                                byte[] bt = accessOutput.pngArray;
                                ByteArrayInputStream bin = new ByteArrayInputStream(bt);
                                BufferedImage image = ImageIO.read(bin);
                                //BufferedImage image=ImageIO.read(new File("E:\\����\\g.bmp"));
                                //BufferedImage image = ImageIO.read(new File("E:\\����\\8.bmp"));

                                Sector sector = new Sector(Angle.fromDegrees(15 + 0.025), Angle.fromDegrees(55 + 0.025), Angle.fromDegrees(70 - 0.025), Angle.fromDegrees(140 - 0.025));
                                //Sector sector = new Sector(Angle.fromDegrees(15), Angle.fromDegrees(15.4), Angle.fromDegrees(70), Angle.fromDegrees(70.4));  

                                SurfaceImageLayer resultSurfaceImageLayer = (SurfaceImageLayer) wwd.getModel().getLayers().getLayerByName("���򸲸ǽ��");
                                resultSurfaceImageLayer.removeAllRenderables();
                                resultSurfaceImageLayer.addImage("resultSurfaceImageLayer", image, sector);
                                resultSurfaceImageLayer.setEnabled(true);
                                wwd.getModel().getLayers().add(resultSurfaceImageLayer);
                            } catch (Exception ex) {
                                System.out.println("ת��pngͼƬʧ�ܣ�");
                            }
                            analyseButton.setEnabled(true);
                        }
                    };
                    analyseButton.setEnabled(false);
                    workingThread.start();

                }
            }
        });

        JButton outputButton = new JButton("�������");

        class WritePdfAction extends AbstractAction implements RenderingListener {
            BufferedImage image = null;
            String path;
            public WritePdfAction() {
                super("Screen Shot");
            }
            public void actionPerformed(ActionEvent event) {
                if (accessOutput == null || accessOutput.isSuccess == false) {
                    JOptionPane.showMessageDialog(null, "������������޷�������棡", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("ѡ�����pdf�ļ�λ��");
                FileNameExtensionFilter shpFilter = new FileNameExtensionFilter("(*.pdf)", "pdf");
                jfc.setFileFilter(shpFilter);
                //��ȡ����·��
                FileSystemView fsv = FileSystemView.getFileSystemView();
                File desktopDir = fsv.getHomeDirectory();
                jfc.setCurrentDirectory(desktopDir.getAbsoluteFile());
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {         
                    path = jfc.getSelectedFile().getAbsolutePath();
                    wwd.removeRenderingListener(this);
                    wwd.addRenderingListener(this);
                }
            }

            @Override
            public void stageChanged(RenderingEvent event) {
                if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP)) {
                    try {
                        GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                        AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glad.getGLProfile(), false);
                        image = glReadBufferUtil.readPixelsToBufferedImage(glad.getGL(), true);
                        //ImageIO.write(image, "png", new File("E:\\����\\c.png"));
                        PdfReporter pr = new PdfReporter();
                        int len = path.length();
                        if (!path.substring(len - 4, len).equals(".pdf")) {
                            path += ".pdf";
                        }
                        System.out.println(path);
                        pr.report(accessInput, accessOutput, path, image);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        wwd.removeRenderingListener(this);
                    }
                }
            }

        }
        outputButton.addActionListener(new WritePdfAction());
        
        
        Box btnBox1 = Box.createHorizontalBox();
        btnBox1.add(Box.createHorizontalStrut(30));
        btnBox1.add(analyseButton);
        btnBox1.add(Box.createHorizontalGlue());
        btnBox1.add(outputButton);
        btnBox1.add(Box.createHorizontalStrut(30));

        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnBox1);
        buttonPanel.add(Box.createVerticalStrut(30));

        return buttonPanel;
    }

}

/**
 * ***************************************
 * ֱ��ѡ������ �Ի��� 
 **************************************
 */
class SelectSensorDirectlyDialog extends JDialog {

    public JPanel modeGridPanel;
    SimulationDialog mainFrame;
    JDialog thisFrame;

    public SelectSensorDirectlyDialog(SimulationDialog mf) {
        super(mf);
        this.setTitle("ѡ�񴫸���");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setPreferredSize(new Dimension(320, 500));
        this.pack();
        this.setLocationRelativeTo(null);
//        this.setVisible(true);
        mainFrame = mf;
        thisFrame = this;
        init();
    }
    private void init() {
        modeGridPanel = new JPanel(new GridLayout(0, 1));
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(modeGridPanel, BorderLayout.NORTH);
        JScrollPane scrollPanel = new JScrollPane(borderPanel);
        scrollPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("ѡ�񴫸���(����_������_ģʽ_�ֱ���)")));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(20);

        JButton btn = new JButton("��һ��");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /**
                 * ************** ֱ����Ӵ������Ի��� ȷ����ť       **********************
                 */
                ArrayList<String> selectedModeNameList = new ArrayList<String>();
                for (Component cp : modeGridPanel.getComponents()) {
                    JCheckBox jc = (JCheckBox) cp;
                    if (jc.isSelected()) {
                        selectedModeNameList.add(jc.getText());
                    }
                }
                AccessInput oriAi = new AccessInput();
                oriAi.satelliteInput = mainFrame.satelliteManager.allSatelliteArray;
                AccessInput ai = oriAi.clone();
                ProcessResource.SensorFileterByName(ai, selectedModeNameList);//ɸѡ��ѡ�������
                mainFrame.satelliteManager.currentSelectedSatelliteArray = ai.satelliteInput;
                mainFrame.satelliteManager.refreshModel();
                mainFrame.tabbedpane.setSelectedIndex(1);
                thisFrame.setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btn);
        Box box = Box.createVerticalBox();
        final JCheckBox selectAllCheckBox = new JCheckBox("ȫѡ");
        JPanel selectAllPanel = new JPanel(new GridLayout(1, 1));
        selectAllPanel.add(selectAllCheckBox);
        box.add(selectAllPanel);
        box.add(Box.createVerticalStrut(20));
        box.add(buttonPanel);

        JPanel outBorderPanel = new JPanel(new BorderLayout());
        outBorderPanel.add(scrollPanel, BorderLayout.CENTER);
        outBorderPanel.add(box, BorderLayout.SOUTH);
        this.add(outBorderPanel);

        selectAllCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component[] modeCheckBox = modeGridPanel.getComponents();
                for (Component cp : modeCheckBox) {
                    JCheckBox jc = ((JCheckBox) cp);
                    jc.setSelected(selectAllCheckBox.isSelected());
                }
            }
        });
    }

    public void fresh(ArrayList<SatelliteInput> selectedSatelliteList /*��һ�� ������� ��ѡ�������*/) {
        ArrayList<String> oriStringList = new ArrayList<String>();
        //�Ѿ���ѡ��Ĵ����������б��� ������ʾ��ѡ�񴫸���
        for (SatelliteInput sli : mainFrame.satelliteManager.currentSelectedSatelliteArray) {
            for (SensorInput ssi : sli.sensorInput) {
                for (SensorModeInput smi : ssi.sensorMode) {
                    oriStringList.add(smi.SatelliteSensorModeName);
                }
            }

        }

        //������ӱ�ѡ��Ĵ�����  ��ѡ��Ĵ��������ñ�ѡ��
        modeGridPanel.removeAll();
//        modeGridPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("ѡ�񴫸���(����_������_ģʽ_�ֱ���)")));
        for (SatelliteInput sli : selectedSatelliteList) {
            for (SensorInput ssi : sli.sensorInput) {
                for (SensorModeInput smi : ssi.sensorMode) {
                    JCheckBox jc = new JCheckBox(smi.SatelliteSensorModeName);
                    for (String str : oriStringList) {
                        if (jc.getText().equals(str)) {
                            jc.setSelected(true);
                        }
                    }
                    modeGridPanel.add(jc);
                }
            }
        }
    }
}

