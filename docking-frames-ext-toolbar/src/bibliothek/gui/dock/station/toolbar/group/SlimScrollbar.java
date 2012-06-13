/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * A slim version of a {@link JScrollBar}.
 * 
 * @author Benjamin Sigg
 */
public class SlimScrollbar extends JComponent implements ColumnScrollBar,
		Adjustable{
	private BoundedRangeModel model;
	private Orientation orientation = Orientation.HORIZONTAL;
	private List<AdjustmentListener> listeners = new ArrayList<AdjustmentListener>();

	private boolean hover = false;
	private boolean mousePressed = false;
	private float mouseOffset = 0;

	/**
	 * A factory creating new {@link SlimScrollbar}s.
	 */
	public static final ColumnScrollBarFactory FACTORY = new ColumnScrollBarFactory(){
		@Override
		public ColumnScrollBar create( ToolbarGroupDockStation station ){
			return new SlimScrollbar();
		}
	};

	/**
	 * Creates a new scrollbar
	 */
	public SlimScrollbar(){
		model = new DefaultBoundedRangeModel(0, 0, 0, 1);
		model.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged( ChangeEvent e ){
				AdjustmentEvent event = new AdjustmentEvent(SlimScrollbar.this,
						0, 0, getValue());
				for (AdjustmentListener listener : listeners){
					listener.adjustmentValueChanged(event);
				}
			}
		});
		MouseAdapter adapter = new MouseAdapter(){
			@Override
			public void mousePressed( MouseEvent e ){
				onMouseEvent(e, true);
			}

			@Override
			public void mouseReleased( MouseEvent e ){
				mousePressed = (e.getModifiersEx() & (MouseEvent.BUTTON1_DOWN_MASK
						| MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) != 0;
				onMouseEvent(e, false);
			}

			@Override
			public void mouseDragged( MouseEvent e ){
				onMouseEvent(e, true);
			}

			@Override
			public void mouseEntered( MouseEvent e ){
				onMouseEvent(e, false);
			}

			@Override
			public void mouseExited( MouseEvent e ){
				hover = false;
				repaint();
			}

			@Override
			public void mouseMoved( MouseEvent e ){
				onMouseEvent(e, false);
			}
		};
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	private void onMouseEvent( MouseEvent event, boolean mouseDown ){
		int value = model.getValue();
		int thumb = getThumbSize();
		float track = getTrack();
		int position;
		if (orientation == Orientation.HORIZONTAL){
			position = event.getX();
		} else{
			position = event.getY();
		}

		if (track > 0){
			float range = model.getMaximum() - model.getMinimum();
			float factor = range / track;

			if (mouseDown && !mousePressed){
				mouseOffset = position - getThumbStart();
				if (mouseOffset < 0 || mouseOffset >= thumb){
					mouseOffset = thumb / 2;
				}
				mousePressed = true;
			}
			if (mouseDown && mousePressed){
				setValue((int) ((position - mouseOffset) * factor));
			}
		}

		if (mouseDown){
			hover = true;
		} else if (contains(event.getPoint())){
			hover = value <= position && position <= value + thumb;
		} else{
			hover = false;
		}
		repaint();
	}

	@Override
	public Dimension getMinimumSize(){
		return new Dimension(6, 6);
	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(6, 6);
	}

	@Override
	protected void paintComponent( Graphics g ){
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;

		int value = getThumbStart();
		int thumb = getThumbSize();

		Rectangle2D rec = null;
		final int baseColorValue = 120;
		if (hover){
			if (orientation == Orientation.HORIZONTAL){
				g2D.setPaint(new GradientPaint(new Point(value, 0), new Color(
						baseColorValue + 120, baseColorValue + 120,
						baseColorValue + 120), new Point(value, 6), new Color(
						baseColorValue + 40, baseColorValue + 40,
						baseColorValue + 40)));
				rec = new Rectangle2D.Double(value, 0, thumb, 6);
			} else{
				g2D.setPaint(new GradientPaint(new Point(0, value), new Color(
						baseColorValue + 120, baseColorValue + 120,
						baseColorValue + 120), new Point(6, value), new Color(
						baseColorValue + 40, baseColorValue + 40,
						baseColorValue + 40)));
				rec = new Rectangle2D.Double(0, value, 6, thumb);
			}
		} else{
			if (orientation == Orientation.HORIZONTAL){
				g2D.setPaint(new GradientPaint(new Point(value, 0), new Color(
						baseColorValue + 90, baseColorValue + 90,
						baseColorValue + 90), new Point(value, 6), new Color(
						baseColorValue, baseColorValue, baseColorValue)));
				rec = new Rectangle2D.Double(value, 0, thumb, 6);
			} else{
				g2D.setPaint(new GradientPaint(new Point(0, value), new Color(
						baseColorValue + 90, baseColorValue + 90,
						baseColorValue + 90), new Point(6, value), new Color(
						baseColorValue, baseColorValue, baseColorValue)));
				rec = new Rectangle2D.Double(0, value, 6, thumb);
			}

		}
		g2D.fill(rec);
	}

	private int getThumbStart(){
		float range = model.getMaximum() - model.getMinimum();
		float track = getTrack();

		return (int) (model.getValue() * (track / range));
	}

	private int getThumbSize(){
		float range = model.getMaximum() - model.getMinimum();
		float extent = model.getExtent();

		float track = getTrack();

		int thumb = (int) (track * (extent / range));
		thumb = Math.max(thumb, 20);
		thumb = Math.min(thumb, (int) track);
		return thumb;
	}

	private int getTrack(){
		if (orientation == Orientation.HORIZONTAL){
			return getWidth();
		} else{
			return getHeight();
		}
	}

	@Override
	public void setValues( int required, int available ){
		model.setMaximum(required);
		model.setExtent(available);
	}

	@Override
	public int getValue(){
		return model.getValue();
	}

	@Override
	public Component getComponent(){
		return this;
	}

	@Override
	public void setOrientation( Orientation orientation ){
		this.orientation = orientation;
		revalidate();
	}

	@Override
	public void addAdjustmentListener( AdjustmentListener listener ){
		listeners.add(listener);
	}

	@Override
	public void removeAdjustmentListener( AdjustmentListener listener ){
		listeners.remove(listener);
	}

	@Override
	public int getOrientation(){
		if (orientation == Orientation.HORIZONTAL){
			return HORIZONTAL;
		} else{
			return VERTICAL;
		}
	}

	@Override
	public void setMinimum( int min ){
		model.setMinimum(min);
	}

	@Override
	public int getMinimum(){
		return model.getMinimum();
	}

	@Override
	public void setMaximum( int max ){
		model.setMaximum(max);
	}

	@Override
	public int getMaximum(){
		return model.getMaximum();
	}

	@Override
	public void setUnitIncrement( int u ){
		// ignore
	}

	@Override
	public int getUnitIncrement(){
		// ignore
		return 0;
	}

	@Override
	public void setBlockIncrement( int b ){
		// ignore
	}

	@Override
	public int getBlockIncrement(){
		// ignore
		return 0;
	}

	@Override
	public void setVisibleAmount( int v ){
		// ignore
	}

	@Override
	public int getVisibleAmount(){
		// ignore
		return 0;
	}

	@Override
	public void setValue( int v ){
		model.setValue(v);
	}
}
