package glass.eclipse;

import glass.eclipse.theme.factory.IGlassParameterFactory;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import kux.glass.IGlassFactory.SGlassParameter;
import kux.utils.CMath;
import bibliothek.gui.dock.common.CControl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class CGlassConfig extends JPanel {
	/***/
	private static final long serialVersionUID = 1L;

	public static enum NGlass {
		UNSELECTED(new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.75D, 0.25D)), 
		SELECTED(new SGlassParameter(0.0D, 0.0D, 0.2D, 0.63D, 0.0D, 0.0D, null, null, null, 0.27D, 0.85D, 0.85D, 0.29D)), 
		FOCUSED(new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.85D, 1.0D)), 
		STRIP(new SGlassParameter(0.25, 0.0, 0.5, 0.0, 0.0, 0.21, new Color(0, 0, 0), new Color(0, 0, 0), new Color(0, 0, 0), 0.0, 1.0, 0.7, 0.2)),
		DISABLED(new SGlassParameter(0.0D, 0.0D, 0.08D, 0.3D, 1.0D, 0.0D, null, null, null, 1.0D, 0.85D, 0.75D, 0.25D)),;
		 
		SGlassParameter p;

		private NGlass(SGlassParameter p) {
			this.p = p;
		}
		
		public SGlassParameter get() {
			return (p);
		}
		
		public void set(SGlassParameter p) {
			this.p = p;
		}
	}
	
	NGlass glass = NGlass.UNSELECTED;
	
	public static  IGlassParameterFactory FACTORY = new IGlassParameterFactory() {
		public SGlassParameter getUnSelectedGlassParameters() {
			return NGlass.UNSELECTED.get();
		}
		
		public SGlassParameter getStripBGGlassParameters() {
			return NGlass.STRIP.get();
		}
		
		public SGlassParameter getSelectedGlassParameters() {
			return NGlass.SELECTED.get();
		}
		
		public SGlassParameter getFocusedGlassParameters() {
			return NGlass.FOCUSED.get();
		}
		
		public SGlassParameter getDisabledGlassParameters(){
			return NGlass.DISABLED.get();
		}
	};
	
	protected void update() {
		panel.setLayout(new BorderLayout(5, 0));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_4.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel_4.setPreferredSize(new Dimension(80, 20));
		lblNewLabel_4.setMaximumSize(new Dimension(80, 20));
		lblNewLabel_4.setMinimumSize(new Dimension(80, 20));
		
		panel.add(lblNewLabel_4, BorderLayout.WEST);
		panel.add(darkness, BorderLayout.CENTER);
		darkness.setValue((int)(glass.p.dDarkness*100));
		panel_1.setLayout(new BorderLayout(5, 0));
		lblIntensity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIntensity.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblIntensity.setPreferredSize(new Dimension(80, 20));
		lblIntensity.setMaximumSize(new Dimension(80, 20));
		lblIntensity.setMinimumSize(new Dimension(80, 20));
		
		panel_1.add(lblIntensity, BorderLayout.WEST);
		panel_1.add(intensity);
		intensity.setValue((int)(glass.p.dIntensity*100));
		panel_2.setLayout(new BorderLayout(5, 0));
		lblPosition.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPosition.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblPosition.setPreferredSize(new Dimension(80, 20));
		lblPosition.setMaximumSize(new Dimension(80, 20));
		lblPosition.setMinimumSize(new Dimension(80, 20));
		
		panel_2.add(lblPosition, BorderLayout.WEST);
		panel_2.add(position);
		position.setValue((int)(glass.p.dReflectionPosition*100));
		panel_3.setLayout(new BorderLayout(5, 0));
		lblConcavity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConcavity.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblConcavity.setPreferredSize(new Dimension(80, 20));
		lblConcavity.setMaximumSize(new Dimension(80, 20));
		lblConcavity.setMinimumSize(new Dimension(80, 20));
		
		panel_3.add(lblConcavity, BorderLayout.WEST);
		panel_3.add(concavity);
		concavity.setValue((int)(glass.p.dConcavity*100));
		panel_4.setLayout(new BorderLayout(5, 0));
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_5.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel_5.setPreferredSize(new Dimension(80, 20));
		lblNewLabel_5.setMaximumSize(new Dimension(80, 20));
		lblNewLabel_5.setMinimumSize(new Dimension(80, 20));
		
		panel_4.add(lblNewLabel_5, BorderLayout.WEST);
		panel_4.add(linear);
		linear.setValue((int)(glass.p.dBackligthLinearIntensity*100));
		panel_5.setLayout(new BorderLayout(5, 0));
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_6.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel_6.setPreferredSize(new Dimension(80, 20));
		lblNewLabel_6.setMaximumSize(new Dimension(80, 20));
		lblNewLabel_6.setMinimumSize(new Dimension(80, 20));
		
		panel_5.add(lblNewLabel_6, BorderLayout.WEST);
		panel_5.add(radial);
		radial.setValue((int)(glass.p.dBackligthRadialIntensity*100));
		panel_6.setLayout(new BorderLayout(5, 0));
		lblColorIntensity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblColorIntensity.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblColorIntensity.setPreferredSize(new Dimension(80, 20));
		lblColorIntensity.setMaximumSize(new Dimension(80, 20));
		lblColorIntensity.setMinimumSize(new Dimension(80, 20));
		
		panel_6.add(lblColorIntensity, BorderLayout.WEST);
		panel_6.add(color_intensity);
		color_intensity.setValue((int)(glass.p.dBackligthColorIntensity*100));
		panel_7.setLayout(new BorderLayout(5, 0));
		lblAmbience.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAmbience.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblAmbience.setPreferredSize(new Dimension(80, 20));
		lblAmbience.setMaximumSize(new Dimension(80, 20));
		lblAmbience.setMinimumSize(new Dimension(80, 20));
		
		panel_7.add(lblAmbience, BorderLayout.WEST);
		panel_7.add(ambience);
		ambience.setValue((int)(glass.p.dAmbientIntensity*100));
		panel_8.setLayout(new BorderLayout(5, 0));
		lblPosition_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPosition_1.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblPosition_1.setPreferredSize(new Dimension(80, 20));
		lblPosition_1.setMaximumSize(new Dimension(80, 20));
		lblPosition_1.setMinimumSize(new Dimension(80, 20));
		
		panel_8.add(lblPosition_1, BorderLayout.WEST);
		panel_8.add(l_position);
		l_position.setValue((int)(glass.p.dLightPosition*100));
		panel_9.setLayout(new BorderLayout(5, 0));
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_7.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel_7.setPreferredSize(new Dimension(80, 20));
		lblNewLabel_7.setMaximumSize(new Dimension(80, 20));
		lblNewLabel_7.setMinimumSize(new Dimension(80, 20));
		
		panel_9.add(lblNewLabel_7, BorderLayout.WEST);
		panel_9.add(l_intensity);
		l_intensity.setValue((int)(glass.p.dLightIntensity*100));
	}
	
	final JSlider darkness = new JSlider();
	final JSlider intensity = new JSlider();
	final JSlider position = new JSlider();
	final JSlider concavity = new JSlider();
	final JSlider linear = new JSlider();
	final JSlider radial = new JSlider();
	final JSlider color_intensity = new JSlider();
	final JSlider ambience = new JSlider();
	final JSlider l_position = new JSlider();
	final JSlider l_intensity = new JSlider();
	
	CControl cControl;
	private final JTextField textField = new JTextField();
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JPanel panel_3 = new JPanel();
	private final JPanel panel_4 = new JPanel();
	private final JPanel panel_5 = new JPanel();
	private final JPanel panel_6 = new JPanel();
	private final JPanel panel_7 = new JPanel();
	private final JPanel panel_8 = new JPanel();
	private final JPanel panel_9 = new JPanel();
	private final JLabel lblNewLabel_4 = new JLabel("Darkness");
	private final JLabel lblIntensity = new JLabel("Intensity");
	private final JLabel lblPosition = new JLabel("Position");
	private final JLabel lblConcavity = new JLabel("Concavity");
	private final JLabel lblNewLabel_5 = new JLabel("Linear");
	private final JLabel lblNewLabel_6 = new JLabel("Radial");
	private final JLabel lblColorIntensity = new JLabel("Color intensity");
	private final JLabel lblAmbience = new JLabel("Ambience");
	private final JLabel lblPosition_1 = new JLabel("Position");
	private final JLabel lblNewLabel_7 = new JLabel("Intensity");
	
	protected void repaintArea() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
					CGlassConfig.this.cControl.getContentArea().paintImmediately(CGlassConfig.this.cControl.getContentArea().getBounds());
			}
		});
		
		
		try {
			textField.setText(glass != null ? "new "+glass.get()+"":"--");
		} catch (Exception e) {
		}
	}
	
	/**
	 * Create the panel.
	 */
	public CGlassConfig(CControl cControl) {
		textField.setBorder(new LineBorder(new Color(0, 0, 0)));
		textField.setEditable(false);
		textField.setColumns(10);
		setLayout(new GridLayout(16, 1, 0, 0));
		this.cControl=cControl;

final JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				glass = (NGlass) comboBox.getSelectedItem();
				update();
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(NGlass.values()));
		add(comboBox);
		
		JLabel lblNewLabel = new JLabel("Background");
		lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
		add(lblNewLabel);
		
		add(panel);
		
		darkness.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = darkness.getValue();
				float f = val/100f;
				glass.p.dDarkness=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_1);
		
		intensity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = intensity.getValue();
				float f = val/100f;
				glass.p.dIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("Reflection");
		lblNewLabel_1.setFont(new Font("SansSerif", Font.BOLD, 13));
		add(lblNewLabel_1);
		
		add(panel_2);
		
		position.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = position.getValue();
				float f = val/100f;
				glass.p.dReflectionPosition=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_3);
		
		concavity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = concavity.getValue();
				float f = val/100f;
				glass.p.dConcavity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		JLabel lblNewLabel_2 = new JLabel("Backlight");
		lblNewLabel_2.setFont(new Font("SansSerif", Font.BOLD, 13));
		add(lblNewLabel_2);
		
		add(panel_4);
		
		linear.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = linear.getValue();
				float f = val/100f;
				glass.p.dBackligthLinearIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_5);
		
		radial.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = radial.getValue();
				float f = val/100f;
				glass.p.dBackligthRadialIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_6);
		
		color_intensity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = color_intensity.getValue();
				float f = val/100f;
				glass.p.dBackligthColorIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_7);
		
		ambience.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = ambience.getValue();
				float f = val/100f;
				glass.p.dAmbientIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		JLabel lblNewLabel_3 = new JLabel("Lightning");
		lblNewLabel_3.setFont(new Font("SansSerif", Font.BOLD, 13));
		add(lblNewLabel_3);
		
		add(panel_8);
		
		l_position.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = l_position.getValue();
				float f = val/100f;
				glass.p.dLightPosition=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(panel_9);
		
		l_intensity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = l_intensity.getValue();
				float f = val/100f;
				glass.p.dLightIntensity=CMath.Round(f, 2);
				repaintArea();
			}
		});
		
		add(textField);

		update();
	}

}
