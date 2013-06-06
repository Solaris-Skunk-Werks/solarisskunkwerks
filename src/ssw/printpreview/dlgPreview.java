

package ssw.printpreview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import filehandlers.Media;
import dialog.dlgAmmoChooser;
import ssw.gui.ifMechForm;
import Print.PrintMech;
import common.Constants;
import java.util.prefs.Preferences;
import ssw.print.Printer;

public class dlgPreview extends javax.swing.JFrame implements ActionListener {
    private final static double DEFAULT_ZOOM_FACTOR_STEP = .5;
    protected Pageable pageable;
    private Printer printer;
    private Preview preview;
    private ifMechForm Parent;
    private File MechImage = null;
    private File LogoImage = null;
    private Preferences bfbPrefs = Preferences.userRoot().node( Constants.BFBPrefs );
    private Preferences sswPrefs = Preferences.userRoot().node( Constants.SSWPrefs );

    public dlgPreview(String title, JFrame owner, Printer printer, Pageable pageable, double zoom) {
        super(title);
        initComponents();
        this.Parent = (ifMechForm) owner;
        this.printer = printer;
        preview = new Preview(pageable, zoom, spnPreview.getSize());
        spnPreview.setViewportView(preview);

        btnZoomIn.setAction(new ZoomAction("Zoom In", "magnifier-zoom.png", preview, DEFAULT_ZOOM_FACTOR_STEP, false));
        btnZoomOut.setAction(new ZoomAction("Zoom Out", "magnifier-zoom-out.png", preview, -DEFAULT_ZOOM_FACTOR_STEP, false));

        btnBack.setAction(new BrowseAction("Prev", "arrow-180.png", preview, -1));
        btnForward.setAction(new BrowseAction("Next", "arrow.png", preview, 1));

        btnPageWidth.setAction(new ZoomAction("Width", "document-resize.png", preview, preview.getWidthZoom(), true));
        btnPageHeight.setAction(new ZoomAction("Page", "document-resize-actual.png", preview, preview.getHeightZoom(), true));

        chkPrintCanon.setSelected(sswPrefs.getBoolean("UseCanonDots", false));
        chkPrintCharts.setSelected(sswPrefs.getBoolean("UseCharts", false));
        chkRS.setSelected(sswPrefs.getBoolean("UseRS", false));
        cmbPaperSize.setSelectedIndex(sswPrefs.getInt("PaperSize", 0));
        if ( chkRS.isSelected() ) { chkRSActionPerformed(null); }
        
        chkUseHexConversion.setSelected( sswPrefs.getBoolean( "UseMiniConversion", false ) );
        if( chkUseHexConversion.isSelected() ) {
            lblOneHex.setEnabled( true );
            cmbHexConvFactor.setEnabled( true );
            lblInches.setEnabled( true );
            cmbHexConvFactor.setSelectedIndex( sswPrefs.getInt( "MiniConversionRate", 0 ) );
        }

        if ( pageable.getNumberOfPages() <= 2 ) {
            btnBack.setVisible(false);
            btnForward.setVisible(false);
            jSeparator1.setVisible(false);
        }

        spnPreview.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                preview.setViewportSize( e.getComponent().getSize() );
                btnPageWidth.setAction(new ZoomAction("Width", "document-resize.png", preview, preview.getWidthZoom(), true));
                btnPageHeight.setAction(new ZoomAction("Page", "document-resize-actual.png", preview, preview.getHeightZoom(), true));
            }
        });
    }

    public dlgPreview(String title, JFrame owner, Printer printer, Pageable pageable) {
        this(title, owner, printer, pageable, 0.0);
    }

    public dlgPreview(String title, JFrame owner, Printer printer, Printable printable, PageFormat format, int pages, double zoom) {
        this(title, owner, printer, new MyPageable(printable, format, pages), zoom);
    }

    public dlgPreview(String title, JFrame owner, Printer printer, Printable printable, PageFormat format, int pages) {
        this(title, owner, printer, printable, format, pages, 0.0);
    }

    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    private static class MyPageable implements Pageable {
        public MyPageable(Printable printable, PageFormat format, int pages) {
            this.printable = printable;
            this.format = format;
            this.pages = pages;
        }

        public int getNumberOfPages() {
            return pages;
        }

        public Printable getPrintable(int index) {
            if (index >= pages) throw new IndexOutOfBoundsException();
            return printable;
        }

        public PageFormat getPageFormat(int index) {
            if (index >= pages) throw new IndexOutOfBoundsException();
            return format;
        }

        private Printable printable;
        private PageFormat format;
        private int pages;
    }

    private void refresh() {
        setPreferences();
        preview.setPageable(printer.Preview());
        preview.repaint();
    }

    private void setPreferences() {
        sswPrefs.putBoolean(Constants.Format_CanonPattern, chkPrintCanon.isSelected());
        sswPrefs.putBoolean(Constants.Format_Tables, chkPrintCharts.isSelected());
        sswPrefs.putBoolean(Constants.Format_ConvertTerrain, chkUseHexConversion.isSelected());
        //sswPrefs.putBoolean("GenericAmmo", chkGenericAmmo.isSelected());
        sswPrefs.putInt(Constants.Format_TerrainModifier, cmbHexConvFactor.getSelectedIndex());
        sswPrefs.putInt("PaperSize", cmbPaperSize.getSelectedIndex());
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spnPreview = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnBack = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPageWidth = new javax.swing.JButton();
        btnPageHeight = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnChangeAmmo = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPrint = new javax.swing.JButton();
        btnCloseDialog = new javax.swing.JButton();
        pnlPrintOptions = new javax.swing.JPanel();
        chkPrintCharts = new javax.swing.JCheckBox();
        chkUseHexConversion = new javax.swing.JCheckBox();
        lblOneHex = new javax.swing.JLabel();
        cmbHexConvFactor = new javax.swing.JComboBox();
        lblInches = new javax.swing.JLabel();
        chkPrintCanon = new javax.swing.JCheckBox();
        chkRS = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbPaperSize = new javax.swing.JComboBox();
        cmbChartOption = new javax.swing.JComboBox();
        pnlImageOptions = new javax.swing.JPanel();
        chkPrintImage = new javax.swing.JCheckBox();
        btnChooseImage = new javax.swing.JButton();
        chkLogo = new javax.swing.JCheckBox();
        btnChooseLogo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        chkPrintWarrior = new javax.swing.JCheckBox();
        txtGroupName = new javax.swing.JTextField();
        txtWarriorName = new javax.swing.JTextField();
        cmbGunnery = new javax.swing.JComboBox();
        cmbPiloting = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1024, 768));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/arrow-180.png"))); // NOI18N
        btnBack.setText("Prev");
        btnBack.setFocusable(false);
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnBack);

        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/arrow.png"))); // NOI18N
        btnForward.setText("Next");
        btnForward.setFocusable(false);
        btnForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnForward);
        jToolBar1.add(jSeparator1);

        btnPageWidth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document-resize.png"))); // NOI18N
        btnPageWidth.setText("Width");
        btnPageWidth.setFocusable(false);
        btnPageWidth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPageWidth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnPageWidth);

        btnPageHeight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/document-resize-actual.png"))); // NOI18N
        btnPageHeight.setText("Page");
        btnPageHeight.setFocusable(false);
        btnPageHeight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPageHeight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnPageHeight);
        jToolBar1.add(jSeparator3);

        btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/magnifier-zoom.png"))); // NOI18N
        btnZoomIn.setText("Zoom In");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnZoomIn);

        btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/magnifier-zoom-out.png"))); // NOI18N
        btnZoomOut.setText("Zoom Out");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnZoomOut);
        jToolBar1.add(jSeparator2);

        btnChangeAmmo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/ammo.png"))); // NOI18N
        btnChangeAmmo.setText("Ammo");
        btnChangeAmmo.setFocusable(false);
        btnChangeAmmo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChangeAmmo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChangeAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeAmmoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChangeAmmo);
        jToolBar1.add(jSeparator4);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/printer.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCloseDialog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ssw/Images/home.png"))); // NOI18N
        btnCloseDialog.setText("Close");
        btnCloseDialog.setFocusable(false);
        btnCloseDialog.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCloseDialog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseDialogActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCloseDialog);

        pnlPrintOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Print Options"));

        chkPrintCharts.setText("Tables and Movement Grid");
        chkPrintCharts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintChartsActionPerformed(evt);
            }
        });

        chkUseHexConversion.setText("Use Miniatures Scale for Movement");
        chkUseHexConversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUseHexConversionActionPerformed(evt);
            }
        });

        lblOneHex.setText("One Hex equals");
        lblOneHex.setEnabled(false);

        cmbHexConvFactor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        cmbHexConvFactor.setEnabled(false);
        cmbHexConvFactor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHexConvFactorActionPerformed(evt);
            }
        });

        lblInches.setText("Inches");
        lblInches.setEnabled(false);

        chkPrintCanon.setText("Canon Dot Patterns");
        chkPrintCanon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintCanonActionPerformed(evt);
            }
        });

        chkRS.setText("RS Format");
        chkRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRSActionPerformed(evt);
            }
        });

        jLabel5.setText("Paper Size:");
        jPanel3.add(jLabel5);

        cmbPaperSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Letter", "A4" }));
        cmbPaperSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPaperSizeActionPerformed(evt);
            }
        });
        jPanel3.add(cmbPaperSize);

        cmbChartOption.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Canon", "Minimal" }));
        cmbChartOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbChartOptionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPrintOptionsLayout = new javax.swing.GroupLayout(pnlPrintOptions);
        pnlPrintOptions.setLayout(pnlPrintOptionsLayout);
        pnlPrintOptionsLayout.setHorizontalGroup(
            pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkRS))
                    .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                        .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(lblOneHex)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblInches))
                            .addComponent(chkUseHexConversion)
                            .addComponent(chkPrintCanon)
                            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                                .addComponent(chkPrintCharts)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbChartOption, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlPrintOptionsLayout.setVerticalGroup(
            pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkPrintCharts)
                    .addComponent(cmbChartOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPrintCanon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUseHexConversion)
                .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbHexConvFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOneHex))
                    .addGroup(pnlPrintOptionsLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(lblInches)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPrintOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkRS))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlImageOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Image Options"));

        chkPrintImage.setText("Include TRO Pic");
        chkPrintImage.setToolTipText("From Mech file");
        chkPrintImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintImageActionPerformed(evt);
            }
        });

        btnChooseImage.setText("Choose TRO Pic");
        btnChooseImage.setEnabled(false);
        btnChooseImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseImageActionPerformed(evt);
            }
        });

        chkLogo.setText("Include Logo");
        chkLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLogoActionPerformed(evt);
            }
        });

        btnChooseLogo.setText("Choose Logo");
        btnChooseLogo.setEnabled(false);
        btnChooseLogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseLogoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlImageOptionsLayout = new javax.swing.GroupLayout(pnlImageOptions);
        pnlImageOptions.setLayout(pnlImageOptionsLayout);
        pnlImageOptionsLayout.setHorizontalGroup(
            pnlImageOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlImageOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(chkLogo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkPrintImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChooseLogo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChooseImage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlImageOptionsLayout.setVerticalGroup(
            pnlImageOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlImageOptionsLayout.createSequentialGroup()
                .addComponent(chkPrintImage)
                .addGap(6, 6, 6)
                .addComponent(btnChooseImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkLogo)
                .addGap(6, 6, 6)
                .addComponent(btnChooseLogo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Warrior Options"));

        chkPrintWarrior.setSelected(true);
        chkPrintWarrior.setText("Include Warrior Data");
        chkPrintWarrior.setToolTipText("From Mech file");
        chkPrintWarrior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPrintWarriorActionPerformed(evt);
            }
        });

        txtGroupName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtGroupNameFocusLost(evt);
            }
        });

        txtWarriorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtWarriorNameFocusLost(evt);
            }
        });

        cmbGunnery.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbGunnery.setSelectedIndex(4);
        cmbGunnery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGunneryActionPerformed(evt);
            }
        });

        cmbPiloting.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7" }));
        cmbPiloting.setSelectedIndex(5);
        cmbPiloting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPilotingActionPerformed(evt);
            }
        });

        jLabel1.setText("Skills:");

        jLabel2.setText("Name:");

        jLabel3.setText("Unit:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkPrintWarrior, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWarriorName, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtGroupName, txtWarriorName});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(chkPrintWarrior)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbGunnery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPiloting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtWarriorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPrintOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlImageOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlImageOptions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlPrintOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkUseHexConversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUseHexConversionActionPerformed
        if( chkUseHexConversion.isSelected() ) {
            lblOneHex.setEnabled( true );
            cmbHexConvFactor.setEnabled( true );
            lblInches.setEnabled( true );

            printer.setHexConversion( cmbHexConvFactor.getSelectedIndex() + 1 );
        } else {
            lblOneHex.setEnabled( false );
            cmbHexConvFactor.setEnabled( false );
            lblInches.setEnabled( false );
            printer.setHexConversion( 0 );
        }
        refresh();
}//GEN-LAST:event_chkUseHexConversionActionPerformed

    private void chkPrintImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintImageActionPerformed
        btnChooseImage.setEnabled(chkPrintImage.isSelected());
        ((PrintMech)printer.GetMechs().get(0)).setPrintMech(chkPrintImage.isSelected());
        refresh();
}//GEN-LAST:event_chkPrintImageActionPerformed

    private void btnChooseImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseImageActionPerformed
        String defaultDir = "";
        if ( Parent != null ) {defaultDir = sswPrefs.get("LastImagePath", "");}
        Media media = new Media();
        MechImage = media.SelectImage(defaultDir, "Select Image");

        try {
            if ( Parent != null ) {
                sswPrefs.put("LastImage", MechImage.getCanonicalPath());
                sswPrefs.put("LastImagePath", MechImage.getCanonicalPath().replace(MechImage.getName(), ""));
                sswPrefs.put("LastImageFile", MechImage.getName());
            }

            //setImage(MechImage);
            printer.setMechImagePath(MechImage.getCanonicalPath());
            refresh();

        } catch ( Exception e ) {
            //do nothing
        }
}//GEN-LAST:event_btnChooseImageActionPerformed

    private void chkLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLogoActionPerformed
        btnChooseLogo.setEnabled(chkLogo.isSelected());
        ((PrintMech)printer.GetMechs().get(0)).setPrintLogo(chkLogo.isSelected());
        if ( chkLogo.isSelected() ) {
            if ( LogoImage != null ) {
                try {
                    printer.setLogoPath(LogoImage.getCanonicalPath());
                } catch ( IOException ie ) {

                }
            }
        } else {
            printer.setLogoPath("");
        }
        refresh();
}//GEN-LAST:event_chkLogoActionPerformed

    private void btnChooseLogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseLogoActionPerformed
        String defaultDir = "";
        if ( Parent != null ) {defaultDir = sswPrefs.get("LastLogo", "");}
        Media media = new Media();
        LogoImage = media.SelectImage(defaultDir, "Select Logo");

        try {
            if ( Parent != null ) {
                sswPrefs.put("LastLogo", LogoImage.getCanonicalPath());
                sswPrefs.put("LastLogoPath", LogoImage.getCanonicalPath().replace(LogoImage.getName(), ""));
                sswPrefs.put("LastLogoFile", LogoImage.getName());
            }

            //setLogo(LogoImage);
            printer.setLogoPath(LogoImage.getCanonicalPath());
            refresh();

        } catch ( Exception e ) {
            //do nothing
        }
}//GEN-LAST:event_btnChooseLogoActionPerformed

    private void chkPrintCanonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintCanonActionPerformed
        printer.setCanon(chkPrintCanon.isSelected());
        refresh();
    }//GEN-LAST:event_chkPrintCanonActionPerformed

    private void chkPrintChartsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintChartsActionPerformed
        printer.setCharts(chkPrintCharts.isSelected());
        refresh();
    }//GEN-LAST:event_chkPrintChartsActionPerformed

    private void cmbHexConvFactorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHexConvFactorActionPerformed
        printer.setHexConversion( cmbHexConvFactor.getSelectedIndex() + 1 );
        refresh();
    }//GEN-LAST:event_cmbHexConvFactorActionPerformed

    private void btnCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseDialogActionPerformed
        dispose();
}//GEN-LAST:event_btnCloseDialogActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        if ( Parent != null ) {
            sswPrefs.putBoolean("UseCharts", chkPrintCharts.isSelected());
            sswPrefs.putInt("Format_Tables_Option", cmbChartOption.getSelectedIndex());
            sswPrefs.putBoolean( "UseMiniConversion", chkUseHexConversion.isSelected() );
            sswPrefs.putInt( "MiniConversionRate", cmbHexConvFactor.getSelectedIndex() );
            sswPrefs.putBoolean("UseCanonDots", chkPrintCanon.isSelected());
            sswPrefs.putBoolean("UseRS", chkRS.isSelected());
            sswPrefs.putInt("PaperSize", cmbPaperSize.getSelectedIndex());
        }
        refresh();
        printer.Print(false);
        this.setVisible(false);
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnnChangeAmmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnChangeAmmoActionPerformed
        dlgAmmoChooser Ammo = new dlgAmmoChooser( (javax.swing.JFrame) Parent, true, ((PrintMech) printer.GetMechs().firstElement()).CurMech, Parent.GetData() );
        Ammo.setLocationRelativeTo( this );
        if( Ammo.HasAmmo() ) {
            Ammo.setVisible( true );
        } else {
            Media.Messager( this, "This 'Mech has no ammunition to exchange." );
            Ammo.dispose();
        }
        refresh();
    }//GEN-LAST:event_btnnChangeAmmoActionPerformed

    private void chkRSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRSActionPerformed
        chkPrintCanon.setEnabled(!chkRS.isSelected());
        chkPrintCanon.setSelected(true);
        chkPrintCharts.setEnabled(!chkRS.isSelected());
        chkPrintCharts.setSelected(false);
        chkUseHexConversion.setEnabled(!chkRS.isSelected());
        chkUseHexConversion.setSelected(false);
        chkUseHexConversionActionPerformed(evt);
        cmbHexConvFactor.setSelectedIndex(0);
        chkPrintWarrior.setSelected(false);
        printer.setTRO(chkRS.isSelected());
        refresh();
}//GEN-LAST:event_chkRSActionPerformed

    private void btnChangeAmmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeAmmoActionPerformed
        dlgAmmoChooser Ammo = new dlgAmmoChooser( (javax.swing.JFrame) Parent, true, ((PrintMech) printer.GetMechs().firstElement()).CurMech, Parent.GetData() );
        Ammo.setLocationRelativeTo( this );
        if( Ammo.HasAmmo() ) {
            Ammo.setVisible( true );
        } else {
            Media.Messager( this, "This 'Mech has no ammunition to exchange." );
            Ammo.dispose();
        }
        refresh();
}//GEN-LAST:event_btnChangeAmmoActionPerformed

    private void UpdateWarriorData() {
        if ( chkPrintWarrior.isSelected() ) {
            printer.setWarriorData(cmbGunnery.getSelectedIndex(), cmbPiloting.getSelectedIndex(), txtWarriorName.getText(), txtGroupName.getText());
            refresh();
        }
    }

    private void chkPrintWarriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPrintWarriorActionPerformed
        cmbGunnery.setEnabled(chkPrintWarrior.isSelected());
        cmbPiloting.setEnabled(chkPrintWarrior.isSelected());
        txtWarriorName.setEnabled(chkPrintWarrior.isSelected());
        txtGroupName.setEnabled(chkPrintWarrior.isSelected());

        printer.setPrintWarrior(chkPrintWarrior.isSelected());
        UpdateWarriorData();
        refresh();
}//GEN-LAST:event_chkPrintWarriorActionPerformed

    private void cmbGunneryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGunneryActionPerformed
        UpdateWarriorData();
    }//GEN-LAST:event_cmbGunneryActionPerformed

    private void cmbPilotingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPilotingActionPerformed
        UpdateWarriorData();
    }//GEN-LAST:event_cmbPilotingActionPerformed

    private void txtWarriorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtWarriorNameFocusLost
        UpdateWarriorData();
    }//GEN-LAST:event_txtWarriorNameFocusLost

    private void txtGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtGroupNameFocusLost
        UpdateWarriorData();
    }//GEN-LAST:event_txtGroupNameFocusLost

    private void cmbPaperSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPaperSizeActionPerformed
        switch ( cmbPaperSize.getSelectedIndex() )
        {
            case 0:
                printer.setPaperSize(Printer.Letter);
                break;
            case 1:
                printer.setPaperSize(Printer.A4);
        }
        refresh();
    }//GEN-LAST:event_cmbPaperSizeActionPerformed

    private void cmbChartOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbChartOptionActionPerformed
        printer.setChartImageOption(cmbChartOption.getSelectedItem().toString());
        refresh();
    }//GEN-LAST:event_cmbChartOptionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnChangeAmmo;
    private javax.swing.JButton btnChooseImage;
    private javax.swing.JButton btnChooseLogo;
    private javax.swing.JButton btnCloseDialog;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnPageHeight;
    private javax.swing.JButton btnPageWidth;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JCheckBox chkLogo;
    private javax.swing.JCheckBox chkPrintCanon;
    private javax.swing.JCheckBox chkPrintCharts;
    private javax.swing.JCheckBox chkPrintImage;
    private javax.swing.JCheckBox chkPrintWarrior;
    private javax.swing.JCheckBox chkRS;
    private javax.swing.JCheckBox chkUseHexConversion;
    private javax.swing.JComboBox cmbChartOption;
    private javax.swing.JComboBox cmbGunnery;
    private javax.swing.JComboBox cmbHexConvFactor;
    private javax.swing.JComboBox cmbPaperSize;
    private javax.swing.JComboBox cmbPiloting;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblInches;
    private javax.swing.JLabel lblOneHex;
    private javax.swing.JPanel pnlImageOptions;
    private javax.swing.JPanel pnlPrintOptions;
    private javax.swing.JScrollPane spnPreview;
    private javax.swing.JTextField txtGroupName;
    private javax.swing.JTextField txtWarriorName;
    // End of variables declaration//GEN-END:variables

}
